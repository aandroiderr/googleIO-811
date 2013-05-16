//
//  TwitterProvider.m
//  SignInEverywhere
//
//  Created by Ian Barber on 21/04/2013.
//  Copyright (c) 2013 Ian Barber. All rights reserved.
//

#import <Accounts/Accounts.h>
#import "AccountView.h"
#import "Authenticator.h"
#import <Social/Social.h>
#import "TwitterProvider.h"

@implementation TwitterProvider

static NSString *kTwitterImageUrl = @"https://api.twitter.com/1/users/profile_image?screen_name=%@&size=bigger";

- (id)init {
  self = [super init];
  if (self) {
    // Create an account store object.
    self.accountStore = [[ACAccountStore alloc] init];
    self.accountType = [self.accountStore accountTypeWithAccountTypeIdentifier:
                           ACAccountTypeIdentifierTwitter];
    self.isUserAction = NO;
    if ([SLComposeViewController isAvailableForServiceType:SLServiceTypeTwitter]) {
      [self retrieveUser];
    }
  }
  return self;
}

- (NSString *)providerId {
  return @"Twitter";
}

- (UIView *)buttonWithFrame:(CGRect)frame {
  AccountView *av = [[AccountView alloc] initWithFrame:frame];

  if (self.user) {
    NSString *imUrl = [NSString stringWithFormat:kTwitterImageUrl,
                          self.user.username];
    UIImage *img = [[UIImage alloc] initWithData:
                    [NSData dataWithContentsOfURL:
                     [NSURL URLWithString:imUrl]]];
    UIImageView *uiv = [[UIImageView alloc] initWithImage:img];

    [av setAccount:self.user.username
        byProvider:[self providerId]
       withPicture:uiv];
  } else {
    UIImageView *uiv = [[UIImageView alloc] initWithImage:
                        [UIImage imageNamed:@"twitter.png"]];
    [av setAccount:nil byProvider:[self providerId] withPicture:uiv];
  }
  return av;
}

- (BOOL)hasIdentity {
  return self.user != nil;
}

- (void)signIn {
  if(self.user) {
    return [self returnUser];
  }
  self.isUserAction = true;
  [self retrieveUser];
}

- (void)retrieveUser {
  // Request access from the user to use their Twitter accounts.
  [self.accountStore requestAccessToAccountsWithType:self.accountType
                                             options: nil
                                          completion:^(BOOL granted, NSError *error) {
      if(granted) {
        // Get the list of Twitter accounts.
        NSArray *accountsArray = [self.accountStore accountsWithAccountType:
                                  self.accountType];

        if ([accountsArray count] > 0) {
          self.user = [accountsArray objectAtIndex:0];
          if(self.isUserAction) {
            [self returnUser];
          }
        }
      }
    }];
}

- (void)returnUser {
  self.isUserAction = NO;
  User *u = [[User alloc] init];
  [u addAuthentication:self.user
            byProvider:[self providerId]
        withIdentifier:self.user.identifier
               andName:self.user.username];
  [[Authenticator sharedAuth] setUser:u];
}

- (void)signOut {
  return;
}

- (void)disconnect {
  return;
}

- (BOOL)openURL:(NSURL *)url
sourceApplication:(NSString *)sourceApplication
     annotation:(id)annotation {
  return NO;
}



@end
