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

	self.init = function() {
		cordova.exec(function(){}, function(){}, "CDVAdInMobiPlugin", "init", []);
	};

	self.showAdInterstitial = function() {
		cordova.exec(function(){}, function(){}, "CDVAdInMobiPlugin", "showAdInterstitial", []);
	};
}

module.exports = new InMobi();
