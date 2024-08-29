package com.epriest.cherryCamera.util;

import android.app.Activity;
import android.content.Context;

import com.epriest.cherryCamera.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.BuildConfig;

public class ccFirebase {

    static public void setAdMob(Activity act) {
//        String AD_APP_ID = act.getString(R.string.banner_ad_app_id);
//        String AD_UNIT_ID = act.getString(R.string.banner_ad_unit_id);

        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(act.getApplicationContext(), initializationStatus -> {
                    });
                })
                .start();

        // Create a new ad view.
        AdView mAdView = (AdView) act.findViewById(R.id.adView);
        mAdView.removeAllViews();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
//        String id = act.getString(R.string.banner_ad_app_id);
//        MobileAds.initialize(act.getApplicationContext(), id);
//        AdView mAdView = (AdView) act.findViewById(R.id.adView);
//        AdRequest adRequest;
//        if(BuildConfig.DEBUG) {
//            adRequest = new AdRequest.Builder()
//                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                    .build();
//        }else{
//            adRequest = new AdRequest.Builder().build();
//        }
//        mAdView.loadAd(adRequest);


}
