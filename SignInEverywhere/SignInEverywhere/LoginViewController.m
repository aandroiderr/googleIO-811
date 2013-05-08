//
//  LoginViewController.m
//  SignInEverywhere

#import "AppDelegate.h"
#import "Authenticator.h"
#import "LoginViewController.h"

@interface LoginViewController ()

@end

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
  
  // We need to loop over all our registered providers,
  // get the status out of them, and display our account chooser.
  NSInteger pIndex = 0;
  CGFloat y = 5.0;
  for (id<Provider>provider in [[Authenticator sharedAuth] providers]) {
    UIButton *pButton = [provider buttonWithFrame:CGRectMake(5, y, 310.0, 110.0)];
    [pButton addTarget:self
                action:@selector(didTapProvider:)
      forControlEvents:UIControlEventTouchUpInside];
    [pButton setTag:pIndex++];
    [self.view addSubview:pButton];
    y += 115;
  }
}

- (IBAction)didTapProvider:(id)sender {
  UIButton *btn = (UIButton *)sender;
  id<Provider> provider = [[[Authenticator sharedAuth] providers]
                              objectAtIndex:btn.tag];
  if (provider) {
    [provider signIn];
    if (self.delegate) {
      [self.delegate loginViewController:self
                       didChooseProvider:[provider providerId]];
    }
  }
}

@end
