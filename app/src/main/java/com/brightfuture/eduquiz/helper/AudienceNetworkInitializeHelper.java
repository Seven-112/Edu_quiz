package com.brightfuture.eduquiz.helper;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.AudienceNetworkAds;

public class AudienceNetworkInitializeHelper implements AudienceNetworkAds.InitListener {


    @Override
    public void onInitialized(AudienceNetworkAds.InitResult initResult) {
        Log.d(AudienceNetworkAds.TAG, initResult.getMessage());

    }

    public static void initialize(Context context) {
        AudienceNetworkAds
                .buildInitSettings(context)
                .withInitListener(new AudienceNetworkInitializeHelper())
                .initialize();
    }
}
