//
//  CDVInMobi.java
//  InMobiPlugin
//
//  Created by Josef Fröhle on 2017-06-19.
//
//
package com.deineagentur.cordova.ad;

import java.util.Map;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.sdk.InMobiSdk;

public class CDVAdInMobiPlugin extends CordovaPlugin {

  private InMobiInterstitial interstitialAd;
  private InterstitialAdListener2 mInterstitialListener;

  private boolean initialized = false;
  private boolean mCanShowAd = false;

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (action.equals("showAdInterstitial")) {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          showAdInterstitial(callbackContext, args.getString(0);
        }
      });
      return true;
    }
    return false;
  }

  private void showAdInterstitial(CallbackContext callbackContext, String pId) {
    Log.d("InMobi", "InMobi plugin called");
    if (!this.initialized) {
      long placementId = Long.parseLong(pId);
      InterstitialAdListener2 mInterstitialListener = new InMobiInterstitial.InterstitialAdListener2() {
        // implementation for other events
        // onAdReceived, onAdLoaFailed, etc
        @Override
        public void onAdLoadSucceeded(InMobiInterstitial inMobiInterstitial) {
          Log.d(TAG, "Ad can now be shown!");
          this.mCanShowAd = true;
          this.onShowAdInterstitial(null);
        }
      };
      InMobiInterstitial interstitialAd = new InMobiInterstitial(cordova.getActivity(), placementId, mInterstitialListener);

      this.initialized = true;
    }

    interstitialAd.load();
  }

  public void onShowAdInterstitial(View view) {
    if (interstitialAd != null && this.mCanShowAd) {
      interstitialAd.show();
    }
  }

}