//
//  TwitterProvider.m
//  SignInEverywhere
//
//  Created by Ian Barber on 21/04/2013.
//  Copyright (c) 2013 Ian Barber. All rights reserved.
//

#import <Accounts/Accounts.h>
#import "Authenticator.h"
#import <Social/Social.h>
#import "TwitterProvider.h"

@implementation TwitterProvider

static NSString * kTwitter = @"twitter";

- (id)init {
  self = [super init];
  if (self) {
    // Create an account store object.
    self.accountStore = [[ACAccountStore alloc] init];
    self.accountType = [self.accountStore accountTypeWithAccountTypeIdentifier:
                           ACAccountTypeIdentifierTwitter];
  }
  return self;
}

- (BOOL)checkState {
  if ([SLComposeViewController isAvailableForServiceType:SLServiceTypeTwitter]) {
    [self signIn];
    return YES;
  } else {
    return NO;
  }
}

- (void)signIn {
  // Request access from the user to use their Twitter accounts.
  [self.accountStore requestAccessToAccountsWithType:self.accountType
                                        options: nil
                                     completion:^(BOOL granted, NSError *error) {
      if(granted) {
        // Get the list of Twitter accounts.
        NSArray *accountsArray = [self.accountStore accountsWithAccountType:
                                      self.accountType];

        if ([accountsArray count] > 0) {
          ACAccount *twitterAccount = [accountsArray objectAtIndex:0];
          User *u = [Authenticator sharedAuth].user;
          [u addAuthentication:twitterAccount
                    byProvider:kTwitter
                withIdentifier:twitterAccount.identifier
                       andName:twitterAccount.username];
          [[Authenticator sharedAuth] setUser:u];
        }
      }
  }];
}

- (void)signOut {
  User *u = [Authenticator sharedAuth].user;
  //[u removeAuthenticationsByProvider:kTwitter];
  [[Authenticator sharedAuth] setUser:u];
}

- (void)disconnect {
  [self signOut];
}

- (BOOL)openURL:(NSURL *)url
sourceApplication:(NSString *)sourceApplication
     annotation:(id)annotation {
  return NO;
}



@end
