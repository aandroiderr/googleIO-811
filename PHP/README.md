Link google-api-php-client:
// TODO

Create a config.ini file in this directory: 
```
[googleplus]
clientid = YOUR_CLIENT_ID
clientsecret = YOUR_CLIENT_SECRET
redirect_uri = http://localhost:8080/callback

[github]
clientid = YOUR_CLIENT_ID
clientsecret = YOUR_CLIENT_SECRET
redirect_uri = http://localhost:8080/callback
```

Run With:
php -S localhost:8080 router.php