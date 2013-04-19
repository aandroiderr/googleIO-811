<?php

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
  
  public function handleResponse($id, $request) {
    return $this->providers[$id]->validate($request);
  }
  
  public function response($auth) {
    if($auth) {
      $this->user = $auth;
    }
  }
}