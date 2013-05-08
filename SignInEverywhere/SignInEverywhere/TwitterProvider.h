//
//  TwitterProvider.h
//  SignInEverywhere
//
//  Created by Ian Barber on 21/04/2013.
//  Copyright (c) 2013 Ian Barber. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Authenticator.h"

@interface TwitterProvider : NSObject <Provider>

@property (strong,nonatomic) ACAccountStore* accountStore;
@property (strong,nonatomic) ACAccountType *accountType;

@end
