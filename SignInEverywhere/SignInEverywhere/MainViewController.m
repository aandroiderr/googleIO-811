//
//  MainViewController.m
//  SignInEverywhere

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
  // TODO: Stop spinner.
  User * u =[[Authenticator sharedAuth] user];
  NSString *who = @"Anonymous";
  if ([u isSignedIn]) {
    who = u.name;
  }
  [self.welcome setText:[NSString stringWithFormat:@"Welcome %@!", who]];
}

- (IBAction)didTapChoose:(id)sender {
  // TODO: Start a spinner
  LoginViewController *vc = [[LoginViewController alloc] initWithDelegate:self];
  [self.navigationController pushViewController:vc animated:YES];
}

- (void)loginViewController:(LoginViewController *)controller
          didChooseProvider:(NSString *)provider {
  [self.navigationController popViewControllerAnimated:YES];
}

@end
