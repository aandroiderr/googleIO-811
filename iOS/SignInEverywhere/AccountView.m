//
//  AccountView.m
//  SignInEverywhere

#import "AccountView.h"

@implementation AccountView

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setAccount:(NSString *)account
        byProvider:(NSString *)provider
       withPicture:(UIView *)picture {

  CGFloat frameWidth = self.frame.size.width - 120.0;
  CGFloat imageSize = self.frame.size.height - 10.0;

  [self setBackgroundColor:[UIColor whiteColor]];
  // Add provider.
  UILabel *providerLabel = [[UILabel alloc]
    initWithFrame:CGRectMake(115.0, 5.0, frameWidth, 30.0)];
  [providerLabel setText:provider];
  [providerLabel setFont:[UIFont boldSystemFontOfSize:17.0]];
  [self addSubview:providerLabel];

  // Add account.
  if (account) {
    UILabel *accountLabel = [[UILabel alloc]
      initWithFrame:CGRectMake(115.0, 30.0, frameWidth, 25.0)];
    [accountLabel setText:account];
    [self addSubview:accountLabel];
  }

  // Add photo to frame.
  CGRect frame = CGRectMake(5.0,
                            5.0,
                            imageSize,
                            imageSize);
  [picture setFrame:frame];
  [self addSubview:picture];
}

@end
