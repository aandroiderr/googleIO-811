//
//  MainViewController.h
//  SignInEverywhere

#import "LoginViewController.h"
#import <UIKit/UIKit.h>

@interface MainViewController : UIViewController <
  LoginDelegate
>

@property (nonatomic,strong) IBOutlet UILabel *welcome;
@property (nonatomic,strong) IBOutlet UIButton *choose;

- (IBAction)didTapChoose:(id)sender;

@end
