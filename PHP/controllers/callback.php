<?php
/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
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