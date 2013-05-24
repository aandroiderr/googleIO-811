Sample from 2013-05-16. Demonstration apps/code from Ian Barber's Google I/O 2013 talk "How to Offer Google+ Sign-In Alongside Other Social Sign-In Services": http://www.youtube.com/watch?v=N_mofJwgtJ4

PHP
----------------
Simple sign-in from an identity provider. Uses Google+ Sign-In on the web, and Github oAuth 2.0:

https://developers.google.com/+/web/signin/
http://developer.github.com/v3/oauth/

Uses Twitter Bootstrap for the template: http://twitter.github.io/bootstrap/

See instructions in PHP/README.md

Android
----------------
Stores a local account user in a SQLite database, and allows attaching multiple IDPs. Uses the services from Google Play Services, the Facebook SDK, and Scribe for accessing LinkedIn:

https://developers.google.com/+/mobile/android/
http://developers.facebook.com/android/
https://github.com/fernandezpablo85/scribe-java

You'll need to set the Facebook app ID, and LinkedIn app ID and secret in the Strings file. 

iOS
----------------
Simple account chooser, with app state independent from IDP state. Uses the Google+ iOS SDK, the Facebook iOS SDK, and the Social framework for Twitter.

https://developers.google.com/+/mobile/ios/
http://developers.facebook.com/ios/
https://dev.twitter.com/docs/adding-social-framework

You'll need to setup a Google+ client ID and a Facebook app ID in the AppDelegate.m and the SignInEverywhere-Info.plist. You'll also need to create custom URL handlers for Google+ and Facebook (see the current ones as examples).

You'll also need to drop in the frameworks and bundles from the Google+ and Facebook SDKs.