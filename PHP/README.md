Link google-api-php-client to a v0.6.2 of the Google PHP API Client
https://code.google.com/p/google-api-php-client/downloads/detail?name=google-api-php-client-0.6.2.tar.gz

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