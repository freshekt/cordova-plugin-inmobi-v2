//
//  
//  InMobiPlugin
//
//  Created by Dmitriy Devayev on 10/8/14.
//
//

var cordova = require('cordova');

function InMobi() {
	var self = this;

	

	self.showInterstitialAds = function(arg0, success, error) {
		cordova.exec(success, error, "CDVAdInMobiPlugin", "showInterstitialAds", [arg0]);
	};

	self.loadInterstitialAds =  function(arg0, success, error) {
		cordova.exec(success, error, "CDVAdInMobiPlugin", "loadInterstitialAds", [arg0]);
	};

	self.init =  function(arg0,success, error) {
		cordova.exec(success, error, "CDVAdInMobiPlugin", "init", [arg0]);
	};

	self.setLocation = function(arg0, success, error) {
		cordova.exec(success, error, "CDVAdInMobiPlugin", "setLocation", [arg0]);
	};

	self.loadRewardVideoAd= function (arg0, success, error) {
		error('InMobi method loadRewardVideoAd not implemented')
	};

	self.showRewardVideoAd = function (arg0, success, error) {
		error('InMobi method showRewardVideoAd not implemented')
	};
	self.showBannerAds = function (arg0, success, error) {
		error('InMobi method showBannerAds not implemented')
	};


	self.hideBannerAds = function (arg0,success, error) {
		error('InMobi method hideBannerAds not implemented')
	};
	

}


module.exports = new InMobi();
