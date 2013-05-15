//
//  FacebookProvider.m
//  SignInEverywhere

#import "AccountView.h"
#import "FacebookProvider.h"

@implementation FacebookProvider

- (id)init {
  self = [super init];
  if (self) {
    self.isUserAction = NO;
    // Initialise default permissions.
    self.permissions = [[NSArray alloc] initWithObjects:
                            @"email",
                            @"user_likes",
                            nil];
    // See if we can auto sign-in.
    [FBSession openActiveSessionWithReadPermissions:self.permissions
                                       allowLoginUI:NO
                                  completionHandler:^(FBSession *session,
                                                      FBSessionState state,
                                                      NSError *error) {
                                    [self sessionStateChanged:session
                                                        state:state
                                                        error:error];
                                  }];
  }
  return self;
}

- (NSString *)providerId {
  return @"Facebook";
}

- (UIView *)buttonWithFrame:(CGRect)frame {
  AccountView *av = [[AccountView alloc] initWithFrame:frame];

  if (FBSession.activeSession.isOpen) {
    FBProfilePictureView *uiv = [[FBProfilePictureView alloc]
        initWithProfileID:self.user.id
          pictureCropping:FBProfilePictureCroppingSquare];
    [av setAccount:self.user.name byProvider:[self providerId] withPicture:uiv];
  } else {
    UIImageView *uiv = [[UIImageView alloc] initWithImage:
        [UIImage imageNamed:@"f_logo.png"]];
    [av setAccount:nil byProvider:[self providerId] withPicture:uiv];
  }
  return av;
}

- (void)signOut {
  [FBSession.activeSession closeAndClearTokenInformation];
}

-(void)signIn {
  if (FBSession.activeSession.isOpen && self.user) {
    return [self returnUser];
  }
  self.isUserAction = YES;
  [FBSession openActiveSessionWithReadPermissions:self.permissions
                                     allowLoginUI:YES
                                completionHandler:^(FBSession *session,
                                                    FBSessionState state,
                                                    NSError *error) {
                                    [self sessionStateChanged:session
                                                        state:state
                                                        error:error];
                                }];
}

- (void)disconnect {
  [self signOut];
}

- (BOOL)hasIdentity {
  return (FBSession.activeSession.isOpen && self.user);
}

- (void)returnUser {
  self.isUserAction = NO;
  User *u = [[User alloc] init];
  [u addAuthentication:self.user
            byProvider:[self providerId]
        withIdentifier:self.user.id
               andName:self.user.name];
  [[Authenticator sharedAuth] setUser:u];
}

/*
 * Callback for session changes.
 */
- (void)sessionStateChanged:(FBSession *)session
                      state:(FBSessionState) state
                      error:(NSError *)error {
  switch (state) {
    case FBSessionStateOpen:
      if (!error) {
        [[FBRequest requestForMe] startWithCompletionHandler:
         ^(FBRequestConnection *connection,
           NSDictionary<FBGraphUser> *user,
           NSError *error) {
           if (!error) {
             self.user = user;
             if (self.isUserAction) {
               [self returnUser];
             }
           }
         }];
      }
      break;
    case FBSessionStateClosed:
    case FBSessionStateClosedLoginFailed:
      [FBSession.activeSession closeAndClearTokenInformation];
      break;
    default:
      break;
  }

  if (error) {
    NSLog(@"Error with Facebook Login: %@", error);
  }
}

- (BOOL)openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication
         annotation:(id)annotation {
  // attempt to extract a token from the url
  return [FBSession.activeSession handleOpenURL:url];
}


@end
