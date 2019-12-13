package com.brightfuture.eduquiz.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.adapter.SlidingImage_Adapter;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.BookmarkDBHelper;
import com.brightfuture.eduquiz.helper.DBHelper;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.model.ImageModel;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CommunityActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences settings;


    public static RewardedVideoAd rewardedVideoAd;
    public static DBHelper dbHelper;
    public static BookmarkDBHelper bookmarkDBHelper;
    public String type;
    public TextView tvTitle, tvQueNo;
    public RelativeLayout titleLayout;
    public View devider;
    public RelativeLayout lytLeaderBoard, lytProfile, lytSignOut, lytBattle, lytPlay, lytSetting, bottomLyt,lytTest,lytLearn,lytPlayquiz,lytdiscuss;
    public LinearLayout lytMidScreen;
    public Button btnBookmark, btnInstruction;
    public String status = "0";
    public TextView tvAlert;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageList = new ArrayList<>();
    public ProgressBar progressBar;

    @Override
    protected void onNewIntent(Intent intent) {

    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        devider = findViewById(R.id.divider);
        titleLayout = findViewById(R.id.titleLayout);
        tvTitle = findViewById(R.id.tvTitle);
        tvQueNo = findViewById(R.id.tvQueNo);
        progressBar= findViewById(R.id.progressBar);


        Utils.transparentStatusAndNavigation(CommunityActivity.this);
        try {
            dbHelper = new DBHelper(getApplicationContext());
            bookmarkDBHelper = new BookmarkDBHelper(getApplicationContext());
            dbHelper.createDatabase();
            bookmarkDBHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }


        lytTest=findViewById(R.id.lytTestmcq);
        lytLearn=findViewById(R.id.lytLearnmcq);
        lytProfile = findViewById(R.id.lytProfile);
        lytSignOut = findViewById(R.id.lytSignOut);
        lytBattle = findViewById(R.id.lytBattle);
        lytPlay = findViewById(R.id.lytPlay);
        lytSetting = findViewById(R.id.lytSetting);
        lytMidScreen = findViewById(R.id.midScreen);
        bottomLyt = findViewById(R.id.bottomLayout);
        tvAlert = findViewById(R.id.tvAlert);
        lytPlayquiz=findViewById(R.id.lytPlayquiz);
        lytdiscuss=findViewById(R.id.lytdiscuss);


        lytSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.CheckVibrateOrSound(CommunityActivity.this);
                Intent playQuiz = new Intent(CommunityActivity.this, SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        //battle button only shown when user already login

        lytBattle.setOnClickListener(this);
        lytPlay.setOnClickListener(this);

        lytProfile.setOnClickListener(this);
        lytSignOut.setOnClickListener(this);
        lytPlayquiz.setOnClickListener(this);
        lytdiscuss.setOnClickListener(this);


        settings = getSharedPreferences(Session.SETTING_Quiz_PREF, 0);
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getApplicationContext());
        PlayActivity.loadRewardedVideoAd(CommunityActivity.this);
        type = getIntent().getStringExtra("type");
        System.out.println("=== == type " + type);
       /* if (Utils.isNetworkAvailable(CommunityActivity.this)) {
            if (Session.isLogin(getApplicationContext())) {
                GetUserStatus();
            } else {
                lytSignOut.setVisibility(View.GONE);
            }
        }*/
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lytBattle: //add question
                Utils.btnClick(view, CommunityActivity.this);
                if (!Session.isLogin(CommunityActivity.this)) {
                    Intent intentLogin = new Intent(CommunityActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                } else {
                    Intent playQuiz = new Intent(CommunityActivity.this, CommunityAllQuestionActivity.class);
                    startActivity(playQuiz);
                }
                break;
            case R.id.lytPlay: // ask question
                Utils.btnClick(view, CommunityActivity.this);
                Intent playQuiz = new Intent(CommunityActivity.this, AddQuestionActivity.class);
                startActivity(playQuiz);
                break;
            case R.id.btnBookmark:
                Utils.btnClick(view, CommunityActivity.this);
                Intent intent = new Intent(CommunityActivity.this, BookmarkList.class);
                startActivity(intent);
                break;
            case R.id.btnInstruction:
                Utils.btnClick(view, CommunityActivity.this);
                Intent infoIntent = new Intent(CommunityActivity.this, InstructionActivity.class);
                startActivity(infoIntent);
                break;
            case R.id.lytLeaderBoard:
                Utils.btnClick(view, CommunityActivity.this);
                if (!Session.isLogin(CommunityActivity.this)) {
                    Intent intentLogin = new Intent(CommunityActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                } else {
                    Intent intent1 = new Intent(CommunityActivity.this, LeaderBoardActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.lytTestmcq:
                Utils.btnClick(view, CommunityActivity.this);
                if (!Session.isLogin(CommunityActivity.this)) {
                    Intent intentLogin = new Intent(CommunityActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                } else {
                    Intent intent1 = new Intent(CommunityActivity.this, LeaderBoardActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.lytProfile:
                Utils.btnClick(view, CommunityActivity.this);
                if (!Session.isLogin(CommunityActivity.this)) {
                    Intent intentLogin = new Intent(CommunityActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                } else {
                    UpdateProfile();
                }
                break;
            case R.id.lytSignOut:
                Utils.btnClick(view, CommunityActivity.this);
               // SignOutWarningDialog();
                break;
            case R.id.lytPlayquiz:
                Utils.btnClick(view, CommunityActivity.this);
                Intent intent1 = new Intent(CommunityActivity.this, PlayQuizActivity.class);
                intent1.putExtra("type", type);
                startActivity(intent1);
                break;
            case R.id.lytdiscuss: //discuss
                Utils.btnClick(view, CommunityActivity.this);
                Intent intent3 = new Intent(CommunityActivity.this, TopicListActivity.class);

                startActivity(intent3);
                break;

        }
    }

    public void RemoveGameRoomId() {
        final DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference(Constant.DB_GAME_ROOM);
        final String FCM_USER_ID = FirebaseAuth.getInstance().getUid();
        String roomKey = gameRef.child(FCM_USER_ID).getKey();
        if (FCM_USER_ID.equals(roomKey))
            gameRef.child(roomKey).removeValue();
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : data.getChildren()) {
                        if (FCM_USER_ID.equals(dataSnapshot1.getKey())) {
                            gameRef.child(dataSnapshot1.getRef().getParent().getKey()).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void startGame() {

        Intent intent = new Intent(CommunityActivity.this, CategoryActivity.class);
        startActivity(intent);

    }

    public void UpdateProfile() {
        Intent intent = new Intent(CommunityActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void SignOutWarningDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CommunityActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.logout_warning));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Session.clearUserSession(CommunityActivity.this);
                LoginManager.getInstance().logOut();
                LoginActivity.mAuth.signOut();
                Intent intentLogin = new Intent(CommunityActivity.this, LoginActivity.class);
                startActivity(intentLogin);
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    // Instance ID token to your app server.
    private void sendTokenToServer() {

        final String token = Session.getDeviceToken(getApplicationContext());
        System.out.println("token : " + token);
        if (token != null) {
            FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String fcm_id = dataSnapshot.child(Constant.FCM_ID).getValue().toString();
                    if (!fcm_id.equals(token)) {
                        postTokenToServer(token);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    //send registration token to server
    public void postTokenToServer(final String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                if (!token.equals("token"))
                                    Session.setPreviousFCM(token, getApplicationContext());
                                FirebaseDatabase.getInstance().getReference("user")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("fcm_id").setValue(token);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.updateFcmId, "1");
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
                params.put(Constant.fcmId, token);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    @Override
    protected void onPause() {
        AppController.StopSound();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.playSound();
        if (Session.isLogin(CommunityActivity.this)) {
            RemoveGameRoomId();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.bookmark).setVisible(false);
        menu.findItem(R.id.report).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.setting:
                Utils.CheckVibrateOrSound(CommunityActivity.this);
                Intent playQuiz = new Intent(CommunityActivity.this, SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void testMCQ(View view) {
        Utils.CheckVibrateOrSound(CommunityActivity.this);
        Intent intent = new Intent(CommunityActivity.this, TestMcqListActivity.class);
        startActivity(intent);

    }

    public void learnMCQ(View view) {
        Utils.CheckVibrateOrSound(CommunityActivity.this);
        Intent intent = new Intent(CommunityActivity.this, LearnMcqListActivity.class);
        startActivity(intent);
    }

    public void getPosts() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.VISIBLE);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("===== == que res  " + jsonObject.toString());
                            boolean error = jsonObject.getBoolean(Constant.ERROR);

                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    ImageModel image= new ImageModel();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                   image.setUrl(object.getString("image"));
                                   image.setPost(object.getString("post_name"));

                                    imageList.add(image);

                                }


                               progressBar.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressBar.setVisibility(View.GONE);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.get_all_posts, "1");

                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    public void GetUserStatus() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject jsonobj = obj.getJSONObject("data");

                                if (jsonobj.getString(Constant.status).equals(Constant.DE_ACTIVE)) {
                                    Session.clearUserSession(getApplicationContext());
                                    FirebaseAuth.getInstance().signOut();
                                    LoginManager.getInstance().logOut();
                                    Intent intentLogin = new Intent(CommunityActivity.this, LoginActivity.class);
                                    startActivity(intentLogin);
                                    finish();
                                } else {
                                    lytSignOut.setVisibility(View.VISIBLE);
                                    sendTokenToServer();
                                    RemoveGameRoomId();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.GET_USER_BY_ID, "1");
                params.put(Constant.ID, Session.getUserData(Session.USER_ID, getApplicationContext()));

                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void Home(View view) {
        Intent playQuiz = new Intent(CommunityActivity.this, MainActivity.class);
        startActivity(playQuiz);
    }
}
