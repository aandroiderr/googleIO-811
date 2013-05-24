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