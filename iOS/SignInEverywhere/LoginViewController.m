//
//  LoginViewController.m
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
#import "LoginViewController.h"

static NSString* const kCellTag = @"loginview";
static const NSInteger kCellViewTag = 3938;
static const CGFloat kCellHeight = 110.0;

@implementation LoginViewController

- (id)initWithDelegate:(id<LoginDelegate>)delegate {
  self = [super init];
  if (self) {
    self.delegate = delegate;
  }
  return self;
}

- (void)viewDidLoad {
  [super viewDidLoad];

  self.title = @"Choose Account";  
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
  return 1;
}

- (NSInteger)tableView:(UITableView *)tableView
 numberOfRowsInSection:(NSInteger)section {
  return [[[Authenticator sharedAuth] providers] count];
}

- (CGFloat)tableView:(UITableView *)tableView
    heightForRowAtIndexPath:(NSIndexPath *)indexPath {
  return kCellHeight;
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView
           editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
  return UITableViewCellEditingStyleDelete;
}

- (void)tableView:(UITableView *)tableView
    commitEditingStyle:(UITableViewCellEditingStyle)editingStyle
    forRowAtIndexPath:(NSIndexPath *)indexPath{
  if (editingStyle == UITableViewCellEditingStyleDelete) {
    self.selectedRow = [indexPath row];
    [self showSignOutDisconnect];
  }
}

-(NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath {
  return @"Sign Out";
}


- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath {
  id<Provider> p = [[[Authenticator sharedAuth] providers]
                       objectAtIndex:[indexPath row]];
  UIView *pButton = [p buttonWithFrame:CGRectMake(0,
                                                    0,
                                                    tableView.frame.size.width,
                                                    kCellHeight)];
  [pButton setTag:kCellViewTag];
  UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kCellTag];
  if (!cell) {
    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle
                                   reuseIdentifier:kCellTag];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
  } else {
    [[[cell contentView] viewWithTag:kCellViewTag] removeFromSuperview];
  }
  [[cell contentView] addSubview:pButton];
  return cell;
}

- (BOOL)tableView:(UITableView *)tableView
    canEditRowAtIndexPath:(NSIndexPath *)indexPath {
  id<Provider> p = [[[Authenticator sharedAuth] providers]
                    objectAtIndex:[indexPath row]];
  return [p hasIdentity];
}

- (void)tableView:(UITableView *)tableView
    didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
  id<Provider> provider = [[[Authenticator sharedAuth] providers]
                              objectAtIndex:[indexPath row]];
  if (provider) {
    [provider signIn];
    if (self.delegate) {
      [self.delegate loginViewController:self
                       didChooseProvider:[provider providerId]];
    }
  }
}

- (void)showSignOutDisconnect {
  UIAlertView *message = [[UIAlertView alloc] initWithTitle:@"Sign Out"
                                                    message:nil
                                                   delegate:self
                                          cancelButtonTitle:@"Cancel"
                                          otherButtonTitles:@"Sign Out",
                                                            @"Disconnect",
                                                            nil];
  [message show];
}

- (void)alertView:(UIAlertView *)alertView
didDismissWithButtonIndex:(NSInteger)buttonIndex {
  if (self.selectedRow < 0) {
    return;
  }
  id<Provider> provider = [[[Authenticator sharedAuth] providers]
                           objectAtIndex:self.selectedRow];
  switch (buttonIndex) {
    case 1:
      [provider signOut];
      break;
    case 2:
      [provider disconnect];
      break;
  }
  [self.tableview reloadData];
  self.selectedRow = -1;
}


@end
