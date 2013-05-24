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

class Authenticator {
  private $providers;
  private $user;
  
  public function __construct() {
    $this->user = null;
  }
  
  public function addProvider(Provider $provider) {
    $this->providers[$provider->getId()] = $provider;
    $provider->setCallback($this);
    $provider->checkState();
  }
  
  public function listProviders() {
    return $this->providers;
  }
  
  public function getUser() {
    return $this->user;
  }
  
  public function handleResponse($id, $unsafe_request) {
    return $this->providers[$id]->validate($unsafe_request);
  }
  
  public function onSignedIn($auth) {
    if($auth) {
      $this->user = $auth;
    }
  }
}