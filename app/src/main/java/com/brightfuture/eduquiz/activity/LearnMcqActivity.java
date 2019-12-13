package com.brightfuture.eduquiz.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.adapter.TestMcqAdapter;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.CircularProgressIndicator;
import com.brightfuture.eduquiz.helper.CircularProgressIndicator2;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.helper.TouchImageView;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.model.Question;
import com.brightfuture.eduquiz.model.Review;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.brightfuture.eduquiz.activity.MainActivity.bookmarkDBHelper;
import static com.brightfuture.eduquiz.activity.MainActivity.rewardedVideoAd;

public class LearnMcqActivity extends AppCompatActivity implements OnClickListener {

    public Toolbar toolbar;
    public RelativeLayout titleLayout;
    public RecyclerView rclyt;
    public TextView tvTitle, tvQueNo;
    public static AdRequest adRequest;
    private static int levelNo = 1;
    public Question question;
    public int questionIndex = 0,
            btnPosition = 0,
            totalScore = 0,
            count_question_completed = 0,
            score = 0,
            coin = 6,
            level_coin = 6,
            correctQuestion = 0,
            inCorrectQuestion = 0,
            rightAns;

    public static TextView  tvAlert;
    public CompleteActivity completeActivity;
    public static Context context;
    public ImageView fifty_fifty, finish, audience_poll;

    public static SharedPreferences settings;
    public RelativeLayout playLayout, checkLayout, lytFifty, lytSkip, lytAudience, lytReset;
    public Button btnTry;
    private final Handler mHandler = new Handler();


    public static InterstitialAd interstitial;
    public static ArrayList<String> options;
    public static ArrayList<Review> reviews = new ArrayList<>();

    public static long leftTime = 0;
    public boolean isDialogOpen = false;
    public static ArrayList<Question> questionList=new ArrayList<>();

    ProgressBar progressBar;

    int click = 0;
    int textSize;
    public TextToSpeech textToSpeech;
    RelativeLayout mainLayout;
    public String fromQue ,sub_cat_name,cat_name;
    public int sub_cat_id,qstn_cnt;
    public ScrollView mainScroll, queScroll;
    TextView questionNo;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_mcq);
        mainLayout = findViewById(R.id.play_layout);
        Utils.transparentStatusAndNavigation(LearnMcqActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        context = LearnMcqActivity.this;
        fromQue = getIntent().getStringExtra("fromQue");
        sub_cat_id = getIntent().getIntExtra("sub_cat_id",0);
        sub_cat_name = getIntent().getStringExtra("sub_cat_name");
        cat_name = getIntent().getStringExtra("cat_name");
        qstn_cnt = getIntent().getIntExtra("qstn_cnt", 0);
        Constant.MAX_QUESTION_PER_LEVEL=qstn_cnt;


        completeActivity = new CompleteActivity();


        textSize = Integer.valueOf(Session.getSavedTextSize(LearnMcqActivity.this));
        Session.removeSharedPreferencesData(LearnMcqActivity.this);
        settings = getSharedPreferences(Session.SETTING_Quiz_PREF, 0);


        resetAllValue();



    //    rewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);


        try {
            interstitial = new InterstitialAd(LearnMcqActivity.this);
            interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
            adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdOpened() {
                    //when ads show , we have to stop timer

                }

                @Override
                public void onAdClosed() {
                    //after ad close we restart timer
                    // timer1= new timer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static void ChangeTextSize(int size) {


    }

    public void resetAllValue() {
//        levelNo = MainActivity.dbHelper.GetLevelById(Constant.CATE_ID, Constant.SUB_CAT_ID);
        rclyt=findViewById(R.id.rclyt);
        progressBar=findViewById(R.id.progressBar);
        playLayout = findViewById(R.id.play_layout);
        titleLayout = findViewById(R.id.titleLayout);
        tvTitle = findViewById(R.id.tvTitle);
        titleLayout.setVisibility(View.VISIBLE);

        tvAlert = findViewById(R.id.tvNoConnection);
        checkLayout = findViewById(R.id.checkLayout);
        lytFifty = findViewById(R.id.lytFifty);
        lytSkip = findViewById(R.id.lytSkip);
        lytAudience = findViewById(R.id.lytAudience);
        lytReset = findViewById(R.id.lytReset);
        btnTry = findViewById(R.id.btnTry);

        fifty_fifty = findViewById(R.id.fifty_fifty);

        finish = findViewById(R.id.finish);
        audience_poll = findViewById(R.id.audience_poll);


        ChangeTextSize(textSize);



        if (Utils.isNetworkAvailable(LearnMcqActivity.this)) {
            getQuestionsFromJson();

        } else {
            tvAlert.setText(getString(R.string.msg_no_internet));
            playLayout.setVisibility(View.GONE);
            checkLayout.setVisibility(View.VISIBLE);

        }
        tvTitle.setText(cat_name+" : "+sub_cat_name);
        //timer = new timer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);

        audience_poll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.btnClick(view, LearnMcqActivity.this);
                shareClicked();
            }
        });
        finish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Utils.btnClick(view, LearnMcqActivity.this);
                submitTestDialog();
            }
        });
        fifty_fifty.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Utils.btnClick(view, LearnMcqActivity.this);
                viewMoreDialog();

            }
        });

        btnTry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAlert.getText().equals(getString(R.string.no_enough_question)))
                    BackButtonMethod();
                else {
                    if (Utils.isNetworkAvailable(LearnMcqActivity.this)) {
                        getQuestionsFromJson();

                    } else {
                        tvAlert.setText(getString(R.string.msg_no_internet));
                        playLayout.setVisibility(View.GONE);
                        checkLayout.setVisibility(View.VISIBLE);

                    }
                }
            }
        });

    }


    public void levelCompleted() {

        Intent intent = new Intent(LearnMcqActivity.this, LearningCompletedActivity.class);
        intent.putExtra("sub_cat_id", sub_cat_id);
        intent.putExtra("sub_cat_name", sub_cat_name);
        intent.putExtra("cat_name", cat_name);
        intent.putExtra("qstn_cnt", qstn_cnt);
        startActivity(intent);
        ((LearnMcqActivity) context).finish();
        blankAllValue();
        Session.removeSharedPreferencesData(LearnMcqActivity.this);


    }


    @Override
    public void onClick(View v) {

    }


    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            if (getApplicationContext() != null) {


            }
        }
    };

    public void increaseQuestionIndex() {
        questionIndex++;

    }

    /**/
    public void viewMoreDialog() {




        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LearnMcqActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.end_learn_view_more));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        // Setting OK Button
        alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                ((LearnMcqActivity) context).finish();

            }
        });

        alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
                Constant.LeftTime = leftTime;

            }
        });
        // Showing Alert Message
        alertDialog.show();

    }

    /**/
    public void submitTestDialog() {




        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LearnMcqActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.end_learn_msg));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        // Setting OK Button
        alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                levelCompleted();
                ((LearnMcqActivity) context).finish();

            }
        });

        alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
                Constant.LeftTime = leftTime;

            }
        });
        // Showing Alert Message
        alertDialog.show();

    }
    public void PlayAreaLeaveDialog() {


        if (!tvAlert.getText().equals(getResources().getString(R.string.no_enough_question))) {

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LearnMcqActivity.this);
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.exit_msg));
            alertDialog.setCancelable(false);
            final AlertDialog alertDialog1 = alertDialog.create();
            // Setting OK Button
            alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed

                    reviews.clear();
                    leftTime = 0;
                    Constant.LeftTime = 0;
                    if (textToSpeech != null) {
                        textToSpeech.shutdown();
                    }
                    ((LearnMcqActivity) context).finish();

                }
            });

            alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog1.dismiss();
                    Constant.LeftTime = leftTime;

                }
            });
            // Showing Alert Message
            alertDialog.show();
        } else {
            ((LearnMcqActivity) context).finish();
        }
    }


    //add attended question in ReviewList
    public void AddReview(Question question, TextView tvBtnOpt, boolean isRight) {

        if(reviews.isEmpty()) {
            reviews.add(new Review(question.getId(),
                    question.getQuestion(),
                    question.getImage(),
                    question.getTrueAns(),
                    tvBtnOpt.getText().toString(),
                    question.getOptions(),
                    question.getNote(),
                    isRight)
            );
        }
        else{
            int i=0;
            boolean isPresent=false;
            Review n =new Review(question.getId(),
                    question.getQuestion(),
                    question.getImage(),
                    question.getTrueAns(),
                    tvBtnOpt.getText().toString(),
                    question.getOptions(),
                    question.getNote(),
                    isRight);
            for (Review r:reviews

            ) {
                if(r.getQueId()==question.getId())
                {
                    reviews.set(i,n);
                    isPresent=true;
                    break;

                }
                i++;
            }
            if(!isPresent)
            {
                reviews.add(n);
            }
        }

        leftTime = 0;
        Constant.LeftTime = 0;
        mHandler.postDelayed(mUpdateUITimerTask, 1000);

    }


    private int isAlreadyAnswered()
    {
        boolean isPresent=false;

        if(reviews.isEmpty()) {
            return  -1;
        }
        else
        {
            int i=0;


            for (Review r:reviews

            ) {
                if(r.getQueId()==question.getId())
                {
                    isPresent=true;
                    return i;


                }
                i++;
            }

        }


        return -1;
    }

    private void addScore(int i, boolean isCorrect) {



        if(i == -1)
        {

            correctQuestion++;
            //  txtTrueQuestion.setText(String.valueOf(correctQuestion));
            //   rightProgress.setProgress(correctQuestion);
            // totalScore = totalScore + 5;
            count_question_completed = count_question_completed + 5;
            score = score + 5;
           // txtScore.setText(String.valueOf(score));
            rightAns = Session.getRightAns(getApplicationContext());
            rightAns++;
            Session.setRightAns(getApplicationContext(), rightAns);
            Session.setScore(getApplicationContext(), totalScore);
            Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);

        }
        else
        {
            boolean existingans=reviews.get(i).isRight();

            if(existingans && isCorrect)
            {
                //dont do anything
            }
            else if(!existingans && isCorrect)
            {
                correctQuestion++;
                //  txtTrueQuestion.setText(String.valueOf(correctQuestion));
                //   rightProgress.setProgress(correctQuestion);
                // totalScore = totalScore + 5;
                count_question_completed = count_question_completed + 5;
                score = score + 5;
             //   txtScore.setText(String.valueOf(score));
                rightAns = Session.getRightAns(getApplicationContext());
                rightAns++;
                Session.setRightAns(getApplicationContext(), rightAns);
                Session.setScore(getApplicationContext(), totalScore);
                Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);
            }
            else if(existingans && !isCorrect)
            {
                correctQuestion--;
                //  txtTrueQuestion.setText(String.valueOf(correctQuestion));
                //   rightProgress.setProgress(correctQuestion);
                // totalScore = totalScore + 5;
                count_question_completed = count_question_completed - 5;
                score = score - 5;
               // txtScore.setText(String.valueOf(score));
                rightAns = Session.getRightAns(getApplicationContext());
                rightAns--;
                Session.setRightAns(getApplicationContext(), rightAns);
                Session.setScore(getApplicationContext(), totalScore);
                Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);
            }

        }
    }


    private void saveScore() {

        Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);
        Session.setScore(getApplicationContext(), totalScore);
        Session.setLastLevelScore(getApplicationContext(), score);

    }

    //play sound when answer is correct
    public void rightSound() {
        if (Session.getSoundEnableDisable(LearnMcqActivity.this))
            Utils.setrightAnssound(LearnMcqActivity.this);

        if (Session.getVibration(LearnMcqActivity.this))
            Utils.vibrate(LearnMcqActivity.this, Utils.VIBRATION_DURATION);

    }

    //play sound when answer is incorrect
    private void playWrongSound() {
        if (Session.getSoundEnableDisable(LearnMcqActivity.this))
            Utils.setwronAnssound(LearnMcqActivity.this);

        if (Session.getVibration(LearnMcqActivity.this))
            Utils.vibrate(LearnMcqActivity.this, Utils.VIBRATION_DURATION);

    }

    //set progress again after next question

    //previous Question

    //Skip Question lifeline

    //Fifty Fifty Lifeline


    //AudiencePoll Lifeline method
    //Show alert dialog when lifeline already used in current level

    public void BackButtonMethod() {
        CheckSound();
        PlayAreaLeaveDialog();
    }

    public void CheckSound() {
        if (Session.getSoundEnableDisable(LearnMcqActivity.this))
            Utils.backSoundonclick(LearnMcqActivity.this);

        if (Session.getVibration(LearnMcqActivity.this))
            Utils.vibrate(LearnMcqActivity.this, Utils.VIBRATION_DURATION);
    }

    public void SettingButtonMethod() {
        CheckSound();


        Intent intent = new Intent(LearnMcqActivity.this, SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
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

                                if (questionList.size() >0) {
                                    //notify adapter
                                    Constant.MAX_QUESTION_PER_LEVEL=questionList.size();
                                    TestMcqAdapter adapter = new TestMcqAdapter(questionList,LearnMcqActivity.this);
                                    // Attach the adapter to the recyclerview to populate items
                                    rclyt.setAdapter(adapter);
                                    // Set layout manager to position the items
                                    rclyt.setLayoutManager(new LinearLayoutManager(LearnMcqActivity.this));
                                    playLayout.setVisibility(View.VISIBLE);
                                    checkLayout.setVisibility(View.GONE);
                                } else {

                                    NotEnoughQuestion();
                                }
                                progressBar.setVisibility(View.GONE);
                            } else {

                                NotEnoughQuestion();
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
                params.put(Constant.getQuestion, "1");
                params.put(Constant.subCategoryId, "" +sub_cat_id);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void NotEnoughQuestion() {

 //       tvQueNo.setText("");
        checkLayout.setVisibility(View.VISIBLE);
        tvAlert.setText(getString(R.string.no_enough_question));
        btnTry.setText(getString(R.string.go_back));
        progressBar.setVisibility(View.GONE);
        playLayout.setVisibility(View.GONE);

    }

    //filter current level question
    public static ArrayList<Question> filter(ArrayList<Question> models, String query) {
        query = query.toLowerCase();

        final ArrayList<Question> filteredModelList = new ArrayList<>();
        for (Question model : models) {
            final String text = "" + model.getLevel();
            if (text.equals(query)) {
                filteredModelList.add(model);
            }
        }

        return filteredModelList;
    }

    public void blankAllValue() {
        questionIndex = 0;
        totalScore = 0;
        count_question_completed = 0;
        score = 0;
        correctQuestion = 0;
        inCorrectQuestion = 0;
    }

    public void UpdateScore(final String score) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");
                            if (error) {
                                Toast.makeText(LearnMcqActivity.this, message, Toast.LENGTH_SHORT).show();
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
                params.put(Constant.setMonthlyLeaderboard, "1");
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, LearnMcqActivity.this));
                params.put(Constant.SCORE, score);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    //Show dialog for rewarded ad
    //if user has not enough coin to use lifeline



    public static void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        } else {
            adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);
            interstitial.show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onPause() {


        super.onPause();
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.report).setVisible(false);
        if (checkLayout.getVisibility() == View.VISIBLE)
            menu.findItem(R.id.bookmark).setVisible(false);
        else {
            menu.findItem(R.id.bookmark).setVisible(false);

            final MenuItem menuItem = menu.findItem(R.id.bookmark);
            menuItem.setTitle("unmark");
            if (question != null) {
                int isfav = bookmarkDBHelper.getBookmarks(question.getId());

                if (isfav == question.getId()) {
                    menuItem.setIcon(R.drawable.ic_mark);
                    menuItem.setTitle("mark");
                } else {
                    menuItem.setIcon(R.drawable.ic_unmark);
                    menuItem.setTitle("unmark");
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.bookmark:

                if (menuItem.getTitle().equals("unmark")) {
                    String solution = question.getNote();
                    MainActivity.bookmarkDBHelper.insertIntoDB(question.getId(),
                            question.getQuestion(),
                            question.getTrueAns(),
                            solution,
                            question.getImage(),
                            options.get(0).trim(),
                            options.get(1).trim(),
                            options.get(2).trim(),
                            options.get(3).trim());
                    menuItem.setIcon(R.drawable.ic_mark);
                    menuItem.setTitle("mark");
                } else {
                    MainActivity.bookmarkDBHelper.delete_id(question.getId());
                    menuItem.setIcon(R.drawable.ic_unmark);
                    menuItem.setTitle("unmark");
                }
                return true;
            case R.id.setting:
                SettingButtonMethod();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {

        PlayAreaLeaveDialog();

    }
    public void shareClicked() {
        final String sharetext = "I am learning " + sub_cat_name + " on "  + getString(R.string.app_name);
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, "" + sharetext + " " + Constant.PLAY_STORE_URL + getPackageName());
        startActivity(Intent.createChooser(share, "Share " + getString(R.string.app_name) + "!"));
    }
}