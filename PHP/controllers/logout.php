<?php

class LogoutController {
  private $auth;
  
  public function __construct(Authenticator $auth) {
    $this->auth = $auth;
  }
  
  public function handle($path, $unsafe_request) {
    $_SESSION = array();
    header("Location: /");
    exit;
  }
}