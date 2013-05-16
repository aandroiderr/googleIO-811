<?php

class LogoutController {
  private $auth;
  
  public function __construct(Authenticator $auth) {
    $this->auth = $auth;
  }
  
  public function handle($path, $unsafe_request) {
    if($path == "/disconnect") {
      $this->auth->getUser()->disconnect();
    }
    $this->auth->getUser()->signOut();
    header("Location: /");
    exit;
  }
}