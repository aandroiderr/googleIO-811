//
//  AccountView.h
//  SignInEverywhere
//
//  Created by Ian Barber on 06/05/2013.
//  Copyright (c) 2013 Ian Barber. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AccountView : UIView

- (void)setAccount:(NSString *)account
        byProvider:(NSString *)provider
       withPicture:(UIView *)picture;

@end
