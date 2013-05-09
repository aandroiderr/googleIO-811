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

@end
