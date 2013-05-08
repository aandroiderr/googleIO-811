<?php
require_once "Provider.php";

class Github implements Provider {
  // How often should we revalidate access.
  const REVAL_EVERY = 86400; // 1 day.
  // Tag for storing session data.
  const TAG = "GitHub";
  // URL to redirect the user to.
  const AUTH_URL = "https://github.com/login/oauth/authorize/?";
  // URL to retrieve the token from.
  const TOKEN_URL = "https://github.com/login/oauth/access_token";
  // URL to retrieve the user info.
  const USER_URL = "https://api.github.com/user?access_token=%s";
  
  private $client_id;
  private $redirect_uri;
  private $client_secret; 
  private $callback;
  
  public function __construct($client_id, $client_secret, $redir) {
    $this->client_id = $client_id;
    $this->client_secret = $client_secret;
    $this->redirect_uri = $redir . '/github';
  }
  
  public function getId() {
    return "github";
  }
  
  /**
   * Options: redirect-uri, scope
   */
  public function getMarkup($options = array()) {
    $url = $this->getAuthUrl($options);
    return sprintf('<a href="%s">Sign In With Github</a>', $url);
  }
  
  public function getScript() {
    return "";
  }
  
  public function validate($unsafe_request) {
    if(!isset($_SESSION[self::TAG.'state']) || 
      $unsafe_request['state'] != $_SESSION[self::TAG.'state'] ||
      isset($unsafe_request['error'])) {
      return false;
    }

    $data = array(
      "client_id" => $this->client_id,
      "client_secret" => $this->client_secret,
      "code" => $unsafe_request['code']
    );
    $opts = array( 'http'=>array(
      'method'=>"POST",
      'header' => "Content-Type:  application/x-www-form-urlencoded\r\n"
                    . "Accept: application/json\r\n",
      'content'=>http_build_query($data)
    ));
    $context = stream_context_create($opts);
    $response = file_get_contents(self::TOKEN_URL, false, $context);
    $token = json_decode($response);
    
    if(isset($token->access_token)) {
      $json = file_get_contents(sprintf(self::USER_URL, $token->access_token));
      $user = json_decode($json);
      $_SESSION[self::TAG] = array(
        'id' => $user->id,
        'provider' => $this->getId(),
        'name' => $user->name,
        'token' => $token->access_token,
        'user' => $user, 
        "retrieved" => time());
    }
    
    $this->callback->callback($_SESSION[self::TAG]);
    return true;
  }
  
  public function checkState() {
    if(!isset($_SESSION[self::TAG]['token'])) {
      return false;
    }
    if((time() - $_SESSION[self::TAG]['retrieved']) > self::REVAL_EVERY) {
      // TODO:!
      $response = file_get_contents(get_validate_url());
      $tokeninfo = json_decode($response);
      if(!isset($tokeninfo->id)) {
        // Invalid token.
        $_SESSION[self::TAG] = array();
        return false;
      }
    }
    $this->callback->callback($_SESSION[self::TAG]);
    return true;
  }
  
  public function setCallback($callback) {
    $this->callback = $callback;
  }
  
  private function getAuthUrl($options) {
    $bytes = openssl_random_pseudo_bytes(8);
    $_SESSION[self::TAG.'state'] = $state = bin2hex($bytes);
    $params = array_merge(array(
      'client_id' => $this->client_id,
      'state' => $state,
      'redirect_uri' => $this->redirect_uri
    ), $options);
    return self::AUTH_URL . http_build_query($params);
  }
}