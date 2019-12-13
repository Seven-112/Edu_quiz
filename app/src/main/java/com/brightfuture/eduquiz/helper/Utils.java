package com.brightfuture.eduquiz.helper;

import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Vibrator;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.brightfuture.eduquiz.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Utils {
    public static AdRequest adRequest;
    public static InterstitialAd interstitial;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static AlertDialog alertDialog;
    private static Vibrator sVibrator;
    public static int TotalQuestion = 1;
    public static int CoreectQuetion = 1;
    public static int WrongQuation = 1;
    public static int level_coin = 1;
    public static final long VIBRATION_DURATION = 100;

    public static int RequestlevelNo = 1;
    public static final boolean DEFAULT_SOUND_SETTING = true;
    public static final boolean DEFAULT_VIBRATION_SETTING = true;
    public static final boolean DEFAULT_MUSIC_SETTING = false;
    public static final boolean DEFAULT_LAN_SETTING = true;
    public static final String FONTS = "fonts/";

    public static void backSoundonclick(Context mContext) {
        try {
            int resourceId = R.raw.click;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setrightAnssound(Context mContext) {
        try {
            int resourceId = R.raw.right;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setwronAnssound(Context mContext) {
        try {
            int resourceId = R.raw.wrong;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void vibrate(Context context, long duration) {
        if (sVibrator == null) {
            sVibrator = (Vibrator) context
                    .getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (sVibrator != null) {
            if (duration == 0) {
                duration = 50;
            }
            sVibrator.vibrate(duration);
        }
    }


    public static boolean isNetworkAvailable(Activity activity) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }




        return false;
    }

    public static void loadAd(Context context) {

        interstitial = new InterstitialAd(context);
        interstitial.setAdUnitId(context.getString(R.string.admob_interstitial_id));
        adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);
    }

    public static void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        } else {
            adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);
            interstitial.show();
        }
    }

    public static void CheckVibrateOrSound(Context context) {

        if (Session.getSoundEnableDisable(context)) {
            backSoundonclick(context);
        }
        if (Session.getVibration(context)) {
            vibrate(context, Utils.VIBRATION_DURATION);
        }
    }


    public static void btnClick(View view, Activity activity) {
        Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        CheckVibrateOrSound(activity);
    }

    static class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }
    public static void transparentStatusAndNavigation(Activity context) {

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true, context);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false, context);
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
            //context.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(final int bits, boolean on, Activity context) {
        Window win = context.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
