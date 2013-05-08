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

  [self setBackgroundColor:[UIColor whiteColor]];
  // Add provider.
  UILabel *providerLabel = [[UILabel alloc]
    initWithFrame:CGRectMake(110.0, 5.0, 200.0, 40.0)];
  [providerLabel setText:provider];
  [self addSubview:providerLabel];

  // Add account.
  if (account) {
    UILabel *accountLabel = [[UILabel alloc]
      initWithFrame:CGRectMake(110.0, 45.0, 200.0, 40.0)];
    [accountLabel setText:account];
    [self addSubview:accountLabel];
  }

  // Add photo to frame.
  CGRect frame = CGRectMake(5.0,
                            5.0,
                            picture.frame.size.width,
                            picture.frame.size.height);
  [picture setFrame:frame];
  [self addSubview:picture];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
