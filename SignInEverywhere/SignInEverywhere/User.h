//
//  User.h
//  SignInEverywhere

#import <Foundation/Foundation.h>

@interface User : NSObject

- (void)addAuthentication:(id)authentication
             byProvider:(NSString *)provider
         withIdentifier:(NSString *)identifier
                andName:(NSString *)name;

@property (nonatomic,readonly) NSString *name;
@property (nonatomic) BOOL isSignedIn;

@end
