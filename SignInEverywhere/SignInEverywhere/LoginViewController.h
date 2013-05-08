//
//  LoginViewController.h
//  SignInEverywhere
//

#import <UIKit/UIKit.h>

@class LoginViewController;

@protocol LoginDelegate <NSObject>
- (void)loginViewController:(LoginViewController *)controller
          didChooseProvider:(NSString *)provider;
@end

@interface LoginViewController : UIViewController

- (id)initWithDelegate:(id<LoginDelegate>)delegate;
- (IBAction)didTapProvider:(id)sender;

@property (nonatomic,weak) id<LoginDelegate> delegate;

@end
