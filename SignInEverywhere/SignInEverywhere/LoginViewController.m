//
//  LoginViewController.m
//  SignInEverywhere

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
