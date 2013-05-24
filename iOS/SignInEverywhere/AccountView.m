//
//  AccountView.m
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
