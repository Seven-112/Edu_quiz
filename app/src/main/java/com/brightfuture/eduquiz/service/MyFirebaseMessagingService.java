package com.brightfuture.eduquiz.service;


import android.content.Intent;
import android.util.Log;

import com.brightfuture.eduquiz.activity.MainActivity;
import com.brightfuture.eduquiz.helper.MyNotificationManager;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.model.Question;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static ArrayList<Question> questionList;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        //Getting registration token

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + s);

        // Saving reg id to shared preferences
        StoreToken(s);
        Log.d("Instance ID ", FirebaseInstanceId.getInstance().getId());
    }

    private void StoreToken(String token) {
        //save the token in sharedPreferences
        Session.setDeviceToken(token, getApplicationContext());

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {


            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());

                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //fireBase cloud messaging
    private void sendPushNotification(JSONObject json) {
        try {

            Object object = json.get("data");
            if (object instanceof JSONObject) {
                //getting the json data
                System.out.println("==== n_response" + ((JSONObject) object).toString());
                JSONObject data = new JSONObject(((JSONObject) object).toString());

                //parsing json data
                String title = data.getString("title");
                String message = data.getString("message");
                String imageUrl = data.getString("image");
                String type = data.getString("type");
                String typeId = data.getString("type_id");
                String maxLevel = data.getString("maxlevel");
                String no_of = data.getString("no_of");
                //creating MyNotificationManager object
                MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
                //creating an intent for the notification
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("cateId", typeId);
                intent.putExtra("type", type);
                intent.putExtra("maxLevel", maxLevel);
                intent.putExtra("no_of",no_of);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                //if there is no image
                if (imageUrl.equals("null")) {
                    //displaying small notification
                    mNotificationManager.showSmallNotification(title, message, intent);
                } else if (!imageUrl.equals("null")) {
                    //if there is an image
                    //displaying a big notification
                    mNotificationManager.showBigNotification(title, message, imageUrl, intent);
                }
            } /*else if (object instanceof JSONArray) {
                System.out.println("==== n_response jsonArray");
                String jsonData = json.toString();
                Intent registrationComplete = new Intent("get_question");
                registrationComplete.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                registrationComplete.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                registrationComplete.putExtra("questionList", jsonData);
                LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

            }*/


        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
