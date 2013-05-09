//
//  GooglePlusProvider.m
//  SignInEverywhere
//

#import "AccountView.h"
#import "GooglePlusProvider.h"
#import "GooglePlus/GPPURLHandler.h"
#import "GoogleOpenSource/GTLQueryPlus.h"
#import "GoogleOpenSource/GTLServicePlus.h"
#import "User.h"

@implementation GooglePlusProvider

- (id)initWithClientId:(NSString *)clientId {
  self = [super init];
  if (self) {
    self.isUserAction = false;
    [[GPPSignIn sharedInstance] setClientID:clientId];
    [[GPPSignIn sharedInstance] setDelegate:self];
    [[GPPSignIn sharedInstance] trySilentAuthentication];
  }
  return self;
}

- (NSString*)providerId {
  return @"Google+";
}

- (UIView *)buttonWithFrame:(CGRect)frame {
  AccountView *av = [[AccountView alloc] initWithFrame:frame];

  if (self.user) {
    NSString *imUrl = [self.user.image.url
                       stringByReplacingOccurrencesOfString:@"sz=50"
                       withString:@"sz=200"];
    UIImage *img = [[UIImage alloc] initWithData:
                        [NSData dataWithContentsOfURL:
                            [NSURL URLWithString:imUrl]]];
    UIImageView *uiv = [[UIImageView alloc] initWithImage:img];

    [av setAccount:self.user.displayName
        byProvider:[self providerId]
       withPicture:uiv];
  } else {
    UIImageView *uiv = [[UIImageView alloc] initWithImage:
                        [UIImage imageNamed:@"gpp_sign_in_dark_icon_normal@2x.png"]];
    [av setAccount:nil byProvider:[self providerId] withPicture:uiv];
  }
  return av;
}

- (void)signOut {
  [[GPPSignIn sharedInstance] signOut];
}

- (void)signIn {
  if (self.user) {
    return [self returnUser];
  }
  self.isUserAction = true;
  [[GPPSignIn sharedInstance] authenticate];
}

-(void)disconnect {
  [[GPPSignIn sharedInstance] disconnect];
}

- (BOOL)openURL:(NSURL *)url
sourceApplication:(NSString *)sourceApplication
     annotation:(id)annotation {
  return [GPPURLHandler handleURL:url
                sourceApplication:sourceApplication
                       annotation:annotation];
}

- (void)finishedWithAuth:(GTMOAuth2Authentication *)auth
                   error:(NSError *)error {
  if (error) {
    NSLog(@"Google Error: %@", error);
  } else {
    GTLQueryPlus *request = [GTLQueryPlus queryForPeopleGetWithUserId:@"me"];
    [[[GPPSignIn sharedInstance] plusService] executeQuery:request
        completionHandler:^(GTLServiceTicket *ticket,
                            GTLPlusPerson *person,
                            NSError *ierror) {
          if (!error) {
            self.user = person;
            if (self.isUserAction) {
              [self returnUser];
            }
          }
        }];
  }
}

- (void)returnUser {
  self.isUserAction = NO;
  User *u = [[User alloc] init];
  [u addAuthentication:self.user
            byProvider:[self providerId]
        withIdentifier:self.user.identifier
               andName:self.user.displayName];
  [[Authenticator sharedAuth] setUser:u];
}


@end
