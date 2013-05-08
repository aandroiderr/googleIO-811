//
//  Authenticator.m
//  SignInEverywhere

#import "Authenticator.h"

NSString *const kUserStatusNotification =
@"com.google.samples.devrel.SignInEverywhere:UserStatusNotification";

@implementation Authenticator

+ (Authenticator *)sharedAuth {
  static Authenticator *sharedInstance = nil;
  if (!sharedInstance) {
    sharedInstance = [[Authenticator alloc] init];
  }
  return sharedInstance;
}

- (void)setUser:(User *)user {
  _user = user;
  [[NSNotificationCenter defaultCenter]
      postNotificationName:kUserStatusNotification
                    object:self];
}

- (BOOL)openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication
         annotation:(id)annotation {
  for (id<Provider>p in self.providers) {
    if ([p openURL:url
        sourceApplication:sourceApplication
        annotation:annotation]) {
      return YES;
    }
  }
  return NO;
}

@end
