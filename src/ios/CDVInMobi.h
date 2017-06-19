//
//  CDVInMobi.h
//  InMobiPlugin
//
//  Created by Josef Fröhle on 2017-06-19.
//
//

#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

#import <InMobiSDK/InMobiSDK.h>

@interface CDVAdInMobiPlugin : CDVPlugin
- (void)showAdInterstitial:(CDVInvokedUrlCommand*)command;
@property (nonatomic, strong) IMInterstitial *adInterstitial;
@end