<?php
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
} else if(strpos($url, '/logout') === 0) {
    require_once "controllers/logout.php";
    $controller = new LogoutController($authenticator);    
} else {
  require_once "controllers/home.php";
  $controller = new HomeController($authenticator);
}

$controller->handle($url, $_REQUEST);