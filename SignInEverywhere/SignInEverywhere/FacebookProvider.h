//
//  FacebookProvider.h
//  SignInEverywhere

#import <FacebookSDK/FacebookSDK.h>
#import <Foundation/Foundation.h>
#import "Authenticator.h"

@interface FacebookProvider : NSObject <Provider>

@property (nonatomic,strong) NSArray* permissions;
@property (nonatomic,strong) NSDictionary<FBGraphUser> *user;
@property (nonatomic) Boolean isUserAction;

@end
