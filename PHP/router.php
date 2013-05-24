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

require_once 'Authenticator.php';
require_once 'Github.php';
require_once 'GooglePlus.php';
require_once 'config.php';

session_start();
$config = parse_ini_file("config.ini", true); 

/* Authenticator */
$authenticator = new Authenticator();
$gplus = new GooglePlus($config['googleplus']['clientid'], $config['googleplus']['clientsecret']);
$authenticator->addProvider($gplus);
$github = new Github($config['github']['clientid'], 
  $config['github']['clientsecret'], 
  $config['github']['redirect_uri']);
$authenticator->addProvider($github);

/* Routing */
$controller = null;
$url = $_SERVER['PHP_SELF'];
if(strpos($url, '/static/') === 0)  {
  return false;
} else if(strpos($url, '/callback') === 0) {
  require_once "controllers/callback.php";
  $controller = new CallbackController($authenticator);
} else if(strpos($url, '/login') === 0) {
    require_once "controllers/login.php";
    $controller = new LoginController($authenticator);
} else if(strpos($url, '/logout') === 0 || strpos($url, '/disconnect') === 0) {
    require_once "controllers/logout.php";
    $controller = new LogoutController($authenticator);    
} else {
  require_once "controllers/home.php";
  $controller = new HomeController($authenticator);
}

$controller->handle($url, $_REQUEST);