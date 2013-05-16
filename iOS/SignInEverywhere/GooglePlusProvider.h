//
//  GooglePlusProvider.h
//  SignInEverywhere

#import "Authenticator.h"
#import <Foundation/Foundation.h>
#import "GooglePlus/GPPSignIn.h"
#import "GoogleOpenSource/GTLPlusPerson.h"

@interface GooglePlusProvider : NSObject <
  GPPSignInDelegate,
  Provider
>

- (id)initWithClientId:(NSString*)clientId;

@property (nonatomic,strong) GTLPlusPerson *user;
@property (nonatomic) Boolean isUserAction;

@end
