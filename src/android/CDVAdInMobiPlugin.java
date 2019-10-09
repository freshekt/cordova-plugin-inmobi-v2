package com.deineagentur.cordova.ad;

import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.inmobi.sdk.InMobiSdk;

import androidx.annotation.NonNull;

public class CDVAdInMobiPlugin extends CordovaPlugin {

    private InMobiInterstitial interstitialAd;
    private InterstitialAdEventListener mInterstitialListener;
    public static final String EVENT_AD_LOADED = "inMobiOnAdLoaded";
    public static final String EVENT_AD_FAILLOAD = "inMobiOnAdFailLoad";
    public static final String EVENT_AD_PRESENT = "inMobiOnAdPresent";
    public static final String EVENT_AD_RECEIVED = "inMobiOnAdReceived";
    public static final String EVENT_AD_LEAVEAPP = "inMobiOnAdLeaveApp";
    public static final String EVENT_AD_DISMISS = "inMobiOnAdDismiss";
    public static final String EVENT_AD_CLICKED = "inMobiOnAdClicked";
    public static final String EVENT_AD_LOGGING_IMPRESSION = "inMobiOnAdLoggingImpression";
    public static final String EVENT_AD_REWARDED_VIDEO_COMPLETED = "inMobiOnAdRewardedVideoCompleted";
    public static final String EVENT_AD_REWARDED_VIDEO_CLOSED = "inMobiOnAdRewardedVideoClosed";
    public static final String AD_OBJECT_INTERSTITIAL = "interstitial";
    public static final String AD_OBJECT_BANNER = "banner";
    public static final String AD_OBJECT_REWARDED_VIDEO = "rewarded_video";
    public static final String TAG = "InMobi";

    private boolean initialized = false;
    private boolean mCanShowAd = false;
    private boolean isLoading = false;
    private boolean doSendLocationEachRequest = false;
    private Timer time;

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {


        switch (action){
            case "init":
                cordova.getThreadPool().execute(new Runnable() {
                    @SuppressLint("MissingPermission")
                    public void run() {
                        try {
                            JSONObject opts = args.optJSONObject(0);
                            JSONObject params = new JSONObject();
                            if(opts.has(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE))
                              params.put(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE, opts.getBoolean(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE));
                            if(opts.has("gdpr"))
                             params.put("gdpr",opts.getString("gdpr"));
                            doSendLocationEachRequest =  opts.has("send_location_each_request") && opts.getBoolean("send_location_each_request");

                            InMobiSdk.init(cordova.getActivity(), opts.getString("account_id"), params);
                            InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);

                            LocationServices.getFusedLocationProviderClient(cordova.getActivity())
                                    .getLastLocation().addOnSuccessListener(cordova.getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        InMobiSdk.setLocation(location);
                                    }
                                }
                            });

                            callbackContext.success("inMob initializesd successfully");
                        } catch (Exception err) {
                            callbackContext.error(err.getMessage());
                        }
                    }
                });
                return true;
            case "loadInterstitialAds":
                final String arg = args.getString(0);
                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        loadAdInterstitial(callbackContext, arg);
                    }
                });
                return true;

            case "showInterstitialAds":
                if (mCanShowAd) {
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            onShowAdInterstitial(null);
                            callbackContext.success("inMob interstitial Ads Showing");
                        }
                    });
                } else
                    callbackContext.error(
                            "First initialize the inMob interstitial ads ");
                return true;
            case "setLocation":
                try {
                    JSONObject opts = args.optJSONObject(0);
                    Location targetLocation = new Location("");
                    targetLocation.setLatitude(opts.getDouble("lat"));
                    targetLocation.setLongitude(opts.getDouble("lng"));
                    InMobiSdk.setLocation(targetLocation);
                    callbackContext.success("inMob location was successfully");
                } catch (Exception ex){
                    callbackContext.error("inMob location wrong parameter ");
                }
                return true;
                default:

                    return false;



        }



  }

  @SuppressLint("MissingPermission")
  private void loadAdInterstitial(final CallbackContext callbackContext, String pId) {

    Log.d("InMobi", "InMobi plugin called");

      long placementId = Long.parseLong(pId);
      if(!initialized){
      InterstitialAdEventListener mInterstitialListener = new InterstitialAdEventListener() {


          // implementation for other events
        // onAdReceived, onAdLoaFailed, etc
        @Override
        public void onAdLoadSucceeded(InMobiInterstitial inMobiInterstitial) {
            super.onAdLoadSucceeded(inMobiInterstitial);
            time.cancel();
          Log.d(TAG, "onAdLoadSucceeded can now be shown!");
          mCanShowAd = true;
          isLoading = false;
          callbackContext.success("InMobi interstitial Ads is loaded and ready to be displayed!");
          fireAdEvent(EVENT_AD_LOADED,AD_OBJECT_INTERSTITIAL,"success");
        }



          @Override
        public void onAdLoadFailed(InMobiInterstitial inMobiInterstitial, InMobiAdRequestStatus inMobiAdRequestStatus) {
              super.onAdLoadFailed(inMobiInterstitial, inMobiAdRequestStatus);
              isLoading = false;
                    Log.d(TAG, "Unable to load interstitial ad (error message: " +
                    inMobiAdRequestStatus.getMessage());


              switch(inMobiAdRequestStatus.getStatusCode().ordinal()) {
                  case 1:
                  case 2:
                  case 3:
                  case 13:
                  case 14:
                  case 19:
                  case 20:
                  case 21:
                      callbackContext.error("InMobi interstitial Ads failed to load: " + inMobiAdRequestStatus.getMessage());
                      fireAdEvent(EVENT_AD_FAILLOAD,AD_OBJECT_INTERSTITIAL,inMobiAdRequestStatus.getMessage());
                      break;

                  case 5:
                  case 6:
                  case 9:
                      time.cancel();
                      time = new Timer();
                      Calendar calendar = Calendar.getInstance();
                      calendar.add(Calendar.SECOND,1);
                      time.schedule(new TimerTask(){

                          @Override
                          public void run() {
                                loadAdInterstitial(callbackContext,pId);
                          }
                      },calendar.getTime());
                      Log.d(TAG,inMobiAdRequestStatus.getMessage());

                      break;
                  default:
                      Log.d(TAG,inMobiAdRequestStatus.getMessage());
                      return;
              }

        }


          @Override
        public void onAdReceived(InMobiInterstitial inMobiInterstitial) {
              super.onAdReceived(inMobiInterstitial);
            Log.d(TAG, "onAdReceived");
            isLoading = false;
            callbackContext.success("InMobi interstitial Ads is received!");
            fireAdEvent(EVENT_AD_RECEIVED,AD_OBJECT_INTERSTITIAL,"success");
        }

          @Override
          public void onAdClicked(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
              super.onAdClicked(inMobiInterstitial, map);
              Log.d(TAG, "onAdClicked " + map.size());
              fireAdEvent(EVENT_AD_CLICKED,AD_OBJECT_INTERSTITIAL,"success");
          }


        @Override
        public void onAdWillDisplay(InMobiInterstitial inMobiInterstitial) {
            super.onAdWillDisplay(inMobiInterstitial);
             Log.d(TAG, "onAdWillDisplay " + inMobiInterstitial);

        }

        @Override
        public void onAdDisplayed(InMobiInterstitial inMobiInterstitial) {
            super.onAdDisplayed(inMobiInterstitial);
            Log.d(TAG, "onAdDisplayed " + inMobiInterstitial);
            callbackContext.success("InMobi interstitial Ads displayed.");
            fireAdEvent(EVENT_AD_PRESENT,AD_OBJECT_INTERSTITIAL,"success");
        }



          @Override
        public void onAdDisplayFailed(InMobiInterstitial inMobiInterstitial) {
              super.onAdDisplayFailed(inMobiInterstitial);
             Log.d(TAG, "onAdDisplayFailed " + "FAILED");
        }

        @Override
        public void onAdDismissed(InMobiInterstitial inMobiInterstitial) {
            super.onAdDismissed(inMobiInterstitial);
            Log.d(TAG, "onAdDismissed " + inMobiInterstitial);
            callbackContext.success("InMobi interstitial Ads dismissed");
            fireAdEvent(EVENT_AD_DISMISS,AD_OBJECT_INTERSTITIAL,"success");
        }

        @Override
        public void onUserLeftApplication(InMobiInterstitial inMobiInterstitial) {
            super.onUserLeftApplication(inMobiInterstitial);
            Log.d(TAG, "onUserLeftApplication " + inMobiInterstitial);
            fireAdEvent(EVENT_AD_LEAVEAPP,AD_OBJECT_INTERSTITIAL,"success");

        }

          @Override
          public void onRewardsUnlocked(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
              super.onRewardsUnlocked(inMobiInterstitial, map);
              Log.d(TAG, "onRewardsUnlocked " + map.size());
          }


      };


       interstitialAd = new InMobiInterstitial(cordova.getActivity(), placementId, mInterstitialListener);
    }

     if(!isLoading){
         if(doSendLocationEachRequest) {
             LocationServices.getFusedLocationProviderClient(cordova.getActivity())
                     .getLastLocation().addOnSuccessListener(cordova.getActivity(), new OnSuccessListener<Location>() {
                 @Override
                 public void onSuccess(Location location) {
                     if (location != null) {
                         InMobiSdk.setLocation(location);
                     }
                     if(!isLoading) {
                         interstitialAd.load();
                         isLoading = true;
                     }
                 }
             }).addOnFailureListener(cordova.getActivity(), new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     if(!isLoading) {
                         interstitialAd.load();
                         isLoading = true;
                     };
                 }
             });
         } else {
             interstitialAd.load();
             isLoading = true;
         }

     }

      initialized = true;
  }

  public void onShowAdInterstitial(View view) {
    if (interstitialAd != null && this.mCanShowAd) {
      interstitialAd.show();
    }
  }


	public void fireEvent(String eventName, String jsonData) {

    String	js = "javascript:cordova.fireDocumentEvent('" + eventName + "'";
      if(jsonData != null) {
        js += "," + jsonData;
      }
      js += ");";

    webView.loadUrl(js);

  }

  protected void fireAdEvent(String event, String adType, String message) {
      String json = String.format("{\'adNetwork\':\'InmobiAd\',\'adType\':\'%s\',\'adEvent\':\'%s\',\'message\':\'%s\'}", new Object[]{ adType, event,message});
      this.fireEvent(event, json);
  }


}