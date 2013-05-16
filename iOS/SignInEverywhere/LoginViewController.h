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

@interface LoginViewController : UIViewController <
  UITableViewDataSource,
  UITableViewDelegate,
  UIAlertViewDelegate
>

- (id)initWithDelegate:(id<LoginDelegate>)delegate;

@property (nonatomic,weak) id<LoginDelegate> delegate;
@property (nonatomic,strong) IBOutlet UITableView* tableview;
@property (nonatomic) NSInteger selectedRow;

@end
