<?php
require_once "Provider.php";
require_once "google-api-php-client/src/Google_Client.php";
require_once "google-api-php-client/src/contrib/Google_PlusService.php";

class GooglePlus implements Provider {
  // Tag for storing session data.
  const TAG = "Google+";
  
  private $client_id;
  private $callback;
  private $gclient;
  private $plus;
  
  public function __construct($client_id, $client_secret) {
    $this->client_id = $client_id;
    $this->gclient = new Google_Client();
    $this->gclient->setClientId($client_id);
    $this->gclient->setClientSecret($client_secret);
    $this->gclient->setRedirectUri('postmessage');
    $this->plus = new Google_PlusService($this->gclient);
  }
  
  public function getId() {
    return "googleplus";
  }
  
  public function getMarkup() {
    return sprintf('<span
        class="g-signin"
        data-callback="signInCallback"
        data-clientid="%s"
        data-cookiepolicy="single_host_origin">
      </span>', $this->client_id);
  }
  
  public function getScript() {
    $bytes = openssl_random_pseudo_bytes(8);
    $_SESSION[self::TAG . 'state'] = $state = bin2hex($bytes);
    return <<<EOS
    <script type="text/javascript">
    function signInCallback(authResult) {
      if (authResult['code']) {
        // Hide the sign-in button now that the user is authorized, for example:
        $('.g-signin').attr('style', 'display: none');

        // Send the code to the server
        $.ajax({
          type: 'POST',
          url: '/callback/googleplus',
          contentType: 'application/x-www-form-urlencoded; charset=utf-8',
          success: function(result) {
            window.location = '/';
          },
          processData: false,
          data: "code="+authResult['code']+"&state={$state}"
        });
      } 
    }
    </script>
EOS;
  }
  
  public function validate($unsafe_request) {
    // Exchange code for token.
    if(!isset($_SESSION[self::TAG . 'state']) || 
      $unsafe_request['state'] != $_SESSION[self::TAG . 'state'] ||
      !isset($unsafe_request['code'])) {
      return false;
    }
    
    $this->gclient->authenticate($unsafe_request['code']);
    unset($_SESSION[self::TAG . 'state']);
    
    // Retrieve the user. 
    $_SESSION[self::TAG] = array();
    $_SESSION[self::TAG]['token'] = $this->gclient->getAccessToken();
    $_SESSION[self::TAG]['user'] = $user = $this->plus->people->get("me");
    $_SESSION[self::TAG]['id'] = $user['id'];
    $_SESSION[self::TAG]['name'] = $user['displayName'];
    $this->callback->onSignedIn($_SESSION[self::TAG]);
    return true;
  }
  
  public function checkState() {
    // Check expiry on access token. 
    if(isset($_SESSION[self::TAG]['token']) && 
      $this->checkExpiry()) {
      $this->callback->onSignedIn($_SESSION[self::TAG]);
      return true;
    }
    return false;
  }
  
  public function setCallback($callback) {
    $this->callback = $callback;
  }
  
  /* 
   * Test to see if the token will still be valid. 
   * If not, the JS will restart the flow.
   */
  private function checkExpiry() {
    $token = json_decode($_SESSION[self::TAG]['token']);
    return time() < ($token->created + $token->expires_in);
  }
}