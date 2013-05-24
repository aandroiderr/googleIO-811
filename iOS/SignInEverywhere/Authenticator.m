//
//  Authenticator.m
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
