<?php

class CallbackController {
  private $auth;
  
  public function __construct(Authenticator $auth) {
    $this->auth = $auth;
  }
  
  public function handle($path, $unsafe_request) {
    // Have provider verify.
    $parts = explode("/", $path);
    if($this->auth->handleResponse($parts[2], $unsafe_request)) {
      $this->redirectHome();
    } else {
      http_response_code(401);
      $content = "<p><a href='/'>Home</a></p>";
      $content .=  "<p>Verification Failed!</p>";
      include_once "views/container.php";
    }
  }
  
  private function redirectHome() {
    header("Location: /");
    exit;
  }
}