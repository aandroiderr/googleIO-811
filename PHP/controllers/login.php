<?php

class LoginController {
  private $auth;
  
  public function __construct(Authenticator $auth) {
    $this->auth = $auth;
  }
  
  public function handle($path, $unsafe_request) {
    // Redirect to login if not logged in.
    if($this->auth->getUser() != null) {
      header("Location: /");
      exit;
    }
    
    $head = $content = "";  
    foreach($this->auth->listProviders() as $p) {
      $content .= sprintf('<div id="%s">%s</div>', $p->getId(), $p->getMarkup());
      $head .= $p->getScript();
    }
    include_once "views/container.php";
  }
}