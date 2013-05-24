//
//  AppDelegate.m
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

#import "AppDelegate.h"
#import "Authenticator.h"
#import "FacebookProvider.h"
#import "GooglePlusProvider.h"
#import "MainViewController.h"
#import "TwitterProvider.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
  // Override point for customization after application launch.
  id<Provider> googleProvider = [[GooglePlusProvider alloc]
      initWithClientId:@"644347805375.apps.googleusercontent.com"];
  id<Provider> facebookProvider = [[FacebookProvider alloc] init];
  id<Provider> twitterProvider = [[TwitterProvider alloc] init];
  [Authenticator sharedAuth].providers = [NSArray arrayWithObjects:
                                          googleProvider,
                                          facebookProvider,
                                          twitterProvider,
                                          nil];

  MainViewController *viewController = [[MainViewController alloc]
                         initWithNibName:@"MainViewController"
                         bundle:nil];
  UINavigationController *navController = [[UINavigationController alloc]
      initWithRootViewController:viewController];
  self.window.rootViewController = navController;
  [self.window makeKeyAndVisible];
  return YES;
}

- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication
         annotation:(id)annotation {
  return [[Authenticator sharedAuth] openURL:url
                                 sourceApplication:sourceApplication
                                        annotation:annotation];
}


@end
