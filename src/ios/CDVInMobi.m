//
//  CDVInMobi.m
//  InMobiPlugin
//
//  Created by Josef Fröhle on 2017-06-19.
//
//

#import "CDVInMobi.h"
#import <Cordova/CDV.h>

#import <InMobiSDK/InMobiSDK.h>

@implementation CDVAdInMobiPlugin
- (void)showAdInterstitial:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* placementId = [command.arguments objectAtIndex:0];
    
    
    [self.commandDelegate runInBackground:^{
        NSLog(@"InMobi plugin called");
        
        self.adInterstitial = [[IMInterstitial alloc] initWithPlacementId:placementId];
        self.adInterstitial.delegate = self;
        [self.adInterstitial load];
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

/*Indicates that the interstitial is ready to be shown */
- (void)interstitialDidFinishLoading:(IMInterstitial *)adInterstitial {
    NSLog(@"interstitialDidFinishLoading");
    [ad showFromViewController:self withAnimation:kIMInterstitialAnimationTypeCoverVertical];
}
- (void)interstitialDidReceiveAd:(IMInterstitial *)adInterstitial {
    NSLog(@"Loaded interstitial ad");
}
@end
