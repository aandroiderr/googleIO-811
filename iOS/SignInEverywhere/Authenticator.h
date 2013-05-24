//
//  Authenticator.h
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

#import <Foundation/Foundation.h>
#import "User.h"

@protocol Provider <NSObject>

- (UIView *)buttonWithFrame:(CGRect)frame;
- (NSString *)providerId;
- (BOOL)hasIdentity;
- (void)signIn;
- (void)signOut;
- (void)disconnect;
- (BOOL)openURL:(NSURL *)url
    sourceApplication:(NSString *)sourceApplication
     annotation:(id)annotation;

@end

extern NSString *const kUserStatusNotification;

@interface Authenticator : NSObject

+ (Authenticator *)sharedAuth;

- (BOOL)openURL:(NSURL *)url
    sourceApplication:(NSString *)sourceApplication
           annotation:(id)annotation;

@property (strong,nonatomic) User *user;
@property (strong,nonatomic) NSArray *providers;

@end
