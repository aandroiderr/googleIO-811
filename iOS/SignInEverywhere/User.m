//
//  User.m
//  SignInEverywhere

#import "User.h"

@implementation User

- (id)init {
  self = [super init];
  if (self) {
    self.isSignedIn = NO;
  }
  return self;
}

- (void)addAuthentication:(id)authentication
               byProvider:(NSString *)provider
           withIdentifier:(NSString *)identifier
                  andName:(NSString *)name {
  self.isSignedIn = true;
  _name = name;
}

@end