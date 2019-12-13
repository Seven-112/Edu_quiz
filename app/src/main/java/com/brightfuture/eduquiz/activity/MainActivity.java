package com.brightfuture.eduquiz.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.adapter.MainMcqAdapter;
import com.brightfuture.eduquiz.adapter.SlidingImage_Adapter;
import com.brightfuture.eduquiz.adapter.TestMcqAdapter;
import com.brightfuture.eduquiz.adapter.TestMcqAdapter1;
import com.brightfuture.eduquiz.helper.BookmarkDBHelper;
import com.brightfuture.eduquiz.helper.DBHelper;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.model.ImageModel;
import com.brightfuture.eduquiz.model.Question;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences settings;
    private RecyclerView recyclerView;
    public static ArrayList<Question> questionList=new ArrayList<>();
    public static RewardedVideoAd rewardedVideoAd;
    public static DBHelper dbHelper;
    public static BookmarkDBHelper bookmarkDBHelper;
    public String type;
    public TextView tvTitle, tvQueNo;
    public RelativeLayout titleLayout;
    public View devider;
    public RelativeLayout lytLeaderBoard, lytProfile, lytSignOut, lytBattle, lytPlay, lytSetting, bottomLyt,lytTest,lytLearn,lytPlayquiz;
    public LinearLayout lytMidScreen;
    public Button btnBookmark, btnInstruction;
    public String status = "0";
    public TextView tvAlert,tvQuestion;
    RadioButton tvAns1,tvAns2,tvAns3,tvAns4;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageList = new ArrayList<>();
    public ProgressBar progressBar;

    @Override
    protected void onNewIntent(Intent intent) {

    }

    private String[] urls = new String[] {"https://demonuts.com/Demonuts/SampleImages/W-03.JPG", "https://demonuts.com/Demonuts/SampleImages/W-08.JPG", "https://demonuts.com/Demonuts/SampleImages/W-10.JPG",
            "https://demonuts.com/Demonuts/SampleImages/W-13.JPG", "https://demonuts.com/Demonuts/SampleImages/W-17.JPG", "https://demonuts.com/Demonuts/SampleImages/W-21.JPG"};

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devider = findViewById(R.id.divider);
        titleLayout = findViewById(R.id.titleLayout);
        tvTitle = findViewById(R.id.tvTitle);
        tvQueNo = findViewById(R.id.tvQueNo);
        progressBar= findViewById(R.id.progressBar);

       getPosts();
        getQuestionsFromJson();
        Utils.transparentStatusAndNavigation(MainActivity.this);
        try {
            dbHelper = new DBHelper(getApplicationContext());
            bookmarkDBHelper = new BookmarkDBHelper(getApplicationContext());
            dbHelper.createDatabase();
            bookmarkDBHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnBookmark = findViewById(R.id.btnBookmark);
        btnInstruction = findViewById(R.id.btnInstruction);
    //    lytLeaderBoard = findViewById(R.id.lytLeaderBoard);
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
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        tvQuestion=findViewById(R.id.tvQuestion);
        tvAns1=(RadioButton)findViewById(R.id.tvAns1);
        tvAns2=(RadioButton)findViewById(R.id.tvAns2);
        tvAns3=(RadioButton)findViewById(R.id.tvAns3);
        tvAns4=(RadioButton)findViewById(R.id.tvAns4);
        getSingleQuestion();

        lytSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.CheckVibrateOrSound(MainActivity.this);
                Intent playQuiz = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });

        //battle button only shown when user already login

        lytBattle.setOnClickListener(this);
        lytPlay.setOnClickListener(this);
        btnBookmark.setOnClickListener(this);
        btnInstruction.setOnClickListener(this);

        lytProfile.setOnClickListener(this);
        lytSignOut.setOnClickListener(this);
        lytPlayquiz.setOnClickListener(this);


        settings = getSharedPreferences(Session.SETTING_Quiz_PREF, 0);
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getApplicationContext());
        PlayActivity.loadRewardedVideoAd(MainActivity.this);
        type = getIntent().getStringExtra("type");
        System.out.println("=== == type " + type);
        if (type!=null) {
            if (type.equals("category")) {

                Constant.TotalLevel = Integer.valueOf(getIntent().getStringExtra("maxLevel"));
                Constant.CATE_ID = Integer.valueOf(getIntent().getStringExtra("cateId"));
                if (getIntent().getStringExtra("no_of").equals("0")) {
                    Intent intent = new Intent(MainActivity.this, LevelActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, SubcategoryActivity.class);
                    startActivity(intent);
                }
            }
        }
        if (Utils.isNetworkAvailable(MainActivity.this)) {
            if (Session.isLogin(getApplicationContext())) {
                GetUserStatus();
            } else {
                lytSignOut.setVisibility(View.GONE);
            }
        }
    }

    private void getSingleQuestion() {
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
                                //questionList = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Question question = new Question();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    question.setId(Integer.parseInt(object.getString(Constant.ID)));
                                    question.setQuestion(object.getString(Constant.QUESTION));
                                    //question.setQuestion("ભારતના મુખ્યમંત્રી કોણ છે?");
                                    question.setImage(object.getString(Constant.IMAGE));
                                    question.addOption(object.getString(Constant.OPTION_A));
                                    question.addOption(object.getString(Constant.OPTION_B));
                                    question.addOption(object.getString(Constant.OPTION_C));
                                    question.addOption(object.getString(Constant.OPTION_D));
                                    String rightAns = object.getString("answer");
                                    question.setAnsOption(rightAns);
                                    if (rightAns.equalsIgnoreCase("A")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_A));
                                    } else if (rightAns.equalsIgnoreCase("B")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_B));
                                    } else if (rightAns.equalsIgnoreCase("C")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_C));
                                    } else {
                                        question.setTrueAns(object.getString(Constant.OPTION_D));
                                    }
                                    question.setLevel(object.getString(Constant.LEVEL));
                                    question.setNote(object.getString(Constant.NOTE));

                                   tvQuestion.setText(question.getQuestion());
                                   tvAns1.setText(question.getOptions().get(0));
                                    tvAns2.setText(question.getOptions().get(1));
                                    tvAns3.setText(question.getOptions().get(2));
                                    tvAns4.setText(question.getOptions().get(3));
                                }


                                progressBar.setVisibility(View.GONE);
                            } else {


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
                params.put(Constant.getOneRandomQuestion, "1");
                params.put(Constant.subCategoryId, "" +3);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void init() {
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(MainActivity.this,imageList));

        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES = urls.length;

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

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
                                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lytBattle:
                Utils.btnClick(view, MainActivity.this);
                if (!Session.isLogin(MainActivity.this)) {
                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                } else {
                    Intent playQuiz = new Intent(MainActivity.this, GetOpponentActivity.class);
                    startActivity(playQuiz);
                }
                break;
            case R.id.lytPlay:
                Utils.btnClick(view, MainActivity.this);
                startGame();
                break;
            case R.id.btnBookmark:
                Utils.btnClick(view, MainActivity.this);
                Intent intent = new Intent(MainActivity.this, BookmarkList.class);
                startActivity(intent);
                break;
            case R.id.btnInstruction:
                Utils.btnClick(view, MainActivity.this);
                Intent infoIntent = new Intent(MainActivity.this, InstructionActivity.class);
                startActivity(infoIntent);
                break;
            case R.id.lytLeaderBoard:
                Utils.btnClick(view, MainActivity.this);
                if (!Session.isLogin(MainActivity.this)) {
                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                } else {
                    Intent intent1 = new Intent(MainActivity.this, LeaderBoardActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.lytTestmcq:
                Utils.btnClick(view, MainActivity.this);
                if (!Session.isLogin(MainActivity.this)) {
                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                } else {
                    Intent intent1 = new Intent(MainActivity.this, LeaderBoardActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.lytProfile:
                Utils.btnClick(view, MainActivity.this);
                if (!Session.isLogin(MainActivity.this)) {
                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                } else {
                    UpdateProfile();
                }
                break;
            case R.id.lytSignOut:
                Utils.btnClick(view, MainActivity.this);
                Intent intent2 = new Intent(MainActivity.this, CommunityActivity.class);

                startActivity(intent2);
               // SignOutWarningDialog();
                break;
            case R.id.lytPlayquiz:
                Utils.btnClick(view, MainActivity.this);
                Intent intent1 = new Intent(MainActivity.this, PlayQuizActivity.class);
                intent1.putExtra("type", type);
                startActivity(intent1);
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

        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        startActivity(intent);

    }

    public void UpdateProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void SignOutWarningDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.logout_warning));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Session.clearUserSession(MainActivity.this);
                LoginManager.getInstance().logOut();
                LoginActivity.mAuth.signOut();
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
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
        if (Session.isLogin(MainActivity.this)) {
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
                Utils.CheckVibrateOrSound(MainActivity.this);
                Intent playQuiz = new Intent(MainActivity.this, SettingActivity.class);
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
        Utils.CheckVibrateOrSound(MainActivity.this);
        Intent intent = new Intent(MainActivity.this, TestMcqListActivity.class);
        startActivity(intent);

    }

    public void learnMCQ(View view) {
        Utils.CheckVibrateOrSound(MainActivity.this);
        Intent intent = new Intent(MainActivity.this, LearnMcqListActivity.class);
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
                                init();

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

    public void getQuestionsFromJson() {
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
                                //questionList = new ArrayList<>();
                                questionList.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Question question = new Question();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    question.setId(Integer.parseInt(object.getString(Constant.ID)));
                                    question.setQuestion(object.getString(Constant.QUESTION));
                                    //question.setQuestion("ભારતના મુખ્યમંત્રી કોણ છે?");
                                    question.setImage(object.getString(Constant.IMAGE));
                                    question.addOption(object.getString(Constant.OPTION_A));
                                    question.addOption(object.getString(Constant.OPTION_B));
                                    question.addOption(object.getString(Constant.OPTION_C));
                                    question.addOption(object.getString(Constant.OPTION_D));
                                    question.setCat_id(Integer.parseInt(object.getString(Constant.category)));
                                    question.setSubcat_id(Integer.parseInt(object.getString(Constant.subCategoryId)));
                                    question.setCategory_name(object.getString(Constant.CATEGORY_NAME));
                                    question.setSubcategory_name(object.getString(Constant.SUB_CATE_NAME));
                                    String rightAns = object.getString("answer");
                                    question.setAnsOption(rightAns);
                                    if (rightAns.equalsIgnoreCase("A")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_A));
                                    } else if (rightAns.equalsIgnoreCase("B")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_B));
                                    } else if (rightAns.equalsIgnoreCase("C")) {
                                        question.setTrueAns(object.getString(Constant.OPTION_C));
                                    } else {
                                        question.setTrueAns(object.getString(Constant.OPTION_D));
                                    }
                                    question.setLevel(object.getString(Constant.LEVEL));
                                    question.setNote(object.getString(Constant.NOTE));

                                    if (question.getOptions().size() == 4) {
                                        questionList.add(question);
                                        Collections.shuffle(questionList);

                                    }
                                }

                                if (questionList.size() >= 0) {
                                    //notify adapter
                                    MainMcqAdapter adapter = new MainMcqAdapter(questionList,MainActivity.this);
                                    // Attach the adapter to the recyclerview to populate items
                                    recyclerView.setAdapter(adapter);
                                    // Set layout manager to position the items
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                                } else {


                                }
                                progressBar.setVisibility(View.GONE);
                            } else {


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
                params.put(Constant.getRandQuestion, "1");
                params.put(Constant.subCategoryId, "" +3);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void viewMore(View view) {
        Utils.CheckVibrateOrSound(MainActivity.this);
        Intent intent = new Intent(MainActivity.this, TestMcqListActivity.class);
        startActivity(intent);
    }

    public void testShare(View view) {
        shareClicked("");
    }

    public void learnMore(View view) {
        Utils.CheckVibrateOrSound(MainActivity.this);
        Intent intent = new Intent(MainActivity.this, LearnMcqListActivity.class);
        startActivity(intent);
    }

    public void learnShare(View view) {
        shareClicked("learn");
    }


    public void shareClicked(String type) {
        final String sharetext;
        if(type!="") {
            sharetext = "I am learning  on " + getString(R.string.app_name);
        }
        else
            sharetext = "I am solving MCQs on "  + getString(R.string.app_name);
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, "" + sharetext + " " + Constant.PLAY_STORE_URL + getPackageName());
        startActivity(Intent.createChooser(share, "Share " + getString(R.string.app_name) + "!"));
    }
}
