//
//  AppDelegate.m
//  SignInEverywhere

#import "AppDelegate.h"
#import "Authenticator.h"
#import "FacebookProvider.h"
#import "GooglePlusProvider.h"
#import "MainViewController.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
  // Override point for customization after application launch.
  id<Provider> googleProvider = [[GooglePlusProvider alloc]
      initWithClientId:@"644347805375.apps.googleusercontent.com"];
  id<Provider> facebookProvider = [[FacebookProvider alloc] init];
  [Authenticator sharedAuth].providers = [NSArray arrayWithObjects:
                                          googleProvider,
                                          facebookProvider,
                                          nil];

  MainViewController *viewController = [[MainViewController alloc]
                         initWithNibName:@"MainViewController"
                         bundle:nil];
  UINavigationController *navController = [[UINavigationController alloc]
      initWithRootViewController:viewController];
  self.window.rootViewController = navController;
  [self.window makeKeyAndVisible];
  return YES;
}

- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication
         annotation:(id)annotation {
  return [[Authenticator sharedAuth] openURL:url
                                 sourceApplication:sourceApplication
                                        annotation:annotation];
}


@end
