//
//  MainViewController.m
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
#import "LoginViewController.h"
#import "MainViewController.h"
#import "User.h"

@implementation MainViewController

- (id)initWithNibName:(NSString *)nibNameOrNil
               bundle:(NSBundle *)nibBundleOrNil {
  self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
  if (self) {
    self.title = @"Home";
  }
  return self;
}

- (void)viewDidLoad {
  [super viewDidLoad];
  // Do any additional setup after loading the view from its nib.
  [[NSNotificationCenter defaultCenter]
       addObserver:self
          selector:@selector(checkUser:)
              name:kUserStatusNotification
            object:nil];
  [self checkUser:nil];
}

- (void)viewDidUnload {
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void) checkUser:(NSNotification *) notification {
  User * u =[[Authenticator sharedAuth] user];
  NSString *who = @"Anonymous";
  if ([u isSignedIn]) {
    who = u.name;
  }
  [self.welcome setText:[NSString stringWithFormat:@"Welcome %@!", who]];
}

- (IBAction)didTapChoose:(id)sender {
  LoginViewController *vc = [[LoginViewController alloc] initWithDelegate:self];
  [self.navigationController pushViewController:vc animated:YES];
}

- (void)loginViewController:(LoginViewController *)controller
          didChooseProvider:(NSString *)provider {
  [self.navigationController popViewControllerAnimated:YES];
}

@end
