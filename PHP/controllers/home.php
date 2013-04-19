<?php

class HomeController {
  private $auth;
  
  public function __construct(Authenticator $auth) {
    $this->auth = $auth;
  }
  
  public function handle($path, $unsafe_request) {
    // Redirect to login if not logged in.
    if($this->auth->getUser() == null) {
      header("Location: /login");
      exit;
    }
    
    $user = $this->auth->getUser();
    $content = "<p><a href='/logout'>Logout</a></p>";
    $content .= sprintf("<h1>Hi %s</h1>", $user['name']);
    $content .= print_r($user, true);
    include_once "views/container.php";
  }
}