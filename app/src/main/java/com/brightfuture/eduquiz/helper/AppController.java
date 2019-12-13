package com.brightfuture.eduquiz.helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatDelegate;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;


public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();

    private static Context mContext;
    private static String mAppUrl;
    public static MediaPlayer player;
    public static Activity currentActivity;

    private RequestQueue mRequestQueue;

    private static AppController mInstance;
    private com.android.volley.toolbox.ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        setContext(getApplicationContext());
        mAppUrl = Constant.PLAY_STORE_URL + mContext.getPackageName();
        setTelephoneListener();
        player = new MediaPlayer();
        mediaPlayerInitializer();
        //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static void mediaPlayerInitializer() {
        try {
            player = MediaPlayer.create(getAppContext(), R.raw.snd_bg);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setLooping(true);
            player.setVolume(1f, 1f);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static String getAppUrl() {
        return mAppUrl;
    }

    private static void setContext(Context context) {
        mContext = context;
    }

    public static Context getAppContext() {
        return mContext;
    }


    public static void playSound() {
        try {
            if (Session.getMusicEnableDisable(mContext) && !player.isPlaying()) {
                player.start();
            } else {
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
            mediaPlayerInitializer();
            player.start();
        }
    }

    public static void StopSound() {
        if (player.isPlaying()) {
            player.pause();
        }

    }

    private void setTelephoneListener() {
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    StopSound();
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    StopSound();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

       /* TelephonyManager telephoneManager = (TelephonyManager) getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephoneManager != null) {
            telephoneManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }*/
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

 /*   public com.android.volley.toolbox.ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new BitmapCache());
        }
        return this.mImageLoader;
    }*/
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
