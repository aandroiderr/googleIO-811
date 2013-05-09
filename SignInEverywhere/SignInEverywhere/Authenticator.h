//
//  Authenticator.h
//  SignInEverywhere

#import <Foundation/Foundation.h>
#import "User.h"

@protocol Provider <NSObject>

- (UIView *)buttonWithFrame:(CGRect)frame;
- (NSString *)providerId;
- (void)signIn;
- (void)signOut;
- (void)disconnect;
- (BOOL)openURL:(NSURL *)url
    sourceApplication:(NSString *)sourceApplication
     annotation:(id)annotation;

@end

extern NSString *const kUserStatusNotification;

@interface Authenticator : NSObject

+ (Authenticator *)sharedAuth;

- (BOOL)openURL:(NSURL *)url
    sourceApplication:(NSString *)sourceApplication
           annotation:(id)annotation;

@property (strong,nonatomic) User *user;
@property (strong,nonatomic) NSArray *providers;

@end
