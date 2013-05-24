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

require_once "Provider.php";
require_once "google-api-php-client/src/Google_Client.php";
require_once "google-api-php-client/src/contrib/Google_PlusService.php";

class GooglePlusUser implements User {
  private $data;
  const REVOKE_URL = "https://accounts.google.com/o/oauth2/revoke?token=%s";
  
  public function __construct($data) {
    $this->data = $data;
  }
  
  public function getProvider() {
    return "googleplus";
  }
  
  public function getId() {
    return $this->data['user']['id'];
  }
  
  public function getName() {
    return $this->data['user']['displayName'];
  }
  
  public function hasFeature($feature) {
    // Just an example!
    if($feature == Feature::APPACTIVITES) {
      return true;
    }
    return false;
  }
  
  public function signOut() {
    $_SESSION[GooglePlus::TAG] = array();
    $_SESSION[GooglePlus::TAG]['signedOut'] = true;
  }
  
  public function disconnect() {
    $token = json_decode($this->data['token']);
    $access_token = $token->access_token;
    file_get_contents(sprintf(self::REVOKE_URL, $access_token));
    $this->signOut();
  }
}

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
    if(isset($_SESSION[self::TAG]) && $_SESSION[self::TAG]['signedOut']) {
      $additional = "
      var signedOut = true;
      window.___gcfg = {
        isSignedOut: true
      };
      ";
    } else {
      $additional = "
      var signedOut = false;
      ";
    }
    return <<<EOS
    <script type="text/javascript">
    {$additional}
    function signInCallback(authResult) {
      if (authResult['code'] && !signedOut) {
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
      if(signedOut) {
        // Allow second click.
        signedOut = false;
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
    $this->callback->onSignedIn(new GooglePlusUser($_SESSION[self::TAG]));
    return true;
  }
  
  public function checkState() {
    // Check expiry on access token. 
    if(isset($_SESSION[self::TAG]['token']) && 
      $this->checkExpiry()) {
      $this->callback->onSignedIn(new GooglePlusUser($_SESSION[self::TAG]));
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