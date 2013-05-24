//
//  FacebookProvider.m
//  SignInEverywhere
//
// Copyright 2013 Google Inc. All Rights Reserved.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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
