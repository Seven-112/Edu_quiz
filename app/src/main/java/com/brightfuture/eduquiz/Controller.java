package com.brightfuture.eduquiz;

import android.app.Application;

import com.brightfuture.eduquiz.helper.AudienceNetworkInitializeHelper;
import com.facebook.ads.AudienceNetworkAds;

public class Controller extends Application  {

    @Override
    public void onCreate() {
        super.onCreate();

        if (AudienceNetworkAds.isInAdsProcess(this)) {
            return;
        } // else execute default application initialization code


        AudienceNetworkInitializeHelper.initialize(this);


    }


}

