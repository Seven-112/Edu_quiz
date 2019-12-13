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
import android.widget.RadioButton;
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
import com.brightfuture.eduquiz.adapter.TestMcqAdapter1;
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

public class TestMcqActivity extends AppCompatActivity implements OnClickListener {

    public Toolbar toolbar;
    public RelativeLayout titleLayout;
    public RecyclerView rclyt;
    public TextView tvTitle, tvQueNo;
    public static AdRequest adRequest;
    private static int levelNo = 1;
    public static Question question;
    public static int questionIndex = 0,
            btnPosition = 0,
            totalScore = 0,
            count_question_completed = 0,
            score = 0,
            coin = 6,
            level_coin = 6,
            correctQuestion = 0,
            inCorrectQuestion = 0,
            rightAns;
    public TextView option_a, option_b, option_c, option_d,
            txtScore, txtTrueQuestion, txtFalseQuestion, coin_count;

    public static TextView btnOpt1, btnOpt2, btnOpt3, btnOpt4, txtQuestion, txtQuestion1, tvAlert;
    public TestCompletedActivity completeActivity;
    public static Context context;
    public ImageView fifty_fifty, skip_question,rewind_quation, finish, audience_poll;

    public static SharedPreferences settings;
    public RelativeLayout playLayout, checkLayout, lytFifty, lytSkip, lytAudience, lytReset;
    public Button btnTry;
    public CardView layout_A, layout_B, layout_C, layout_D;
    private Animation animation;
    private final Handler mHandler = new Handler();

    public Animation RightSwipe_A, RightSwipe_B, RightSwipe_C, RightSwipe_D, Fade_in, fifty_fifty_anim;
    private CircularProgressIndicator2 progressBarTwo_A, progressBarTwo_B, progressBarTwo_C, progressBarTwo_D;
    public static CircularProgressIndicator progressTimer;
    public static MyCountDownTimer timer;

    public static InterstitialAd interstitial;
    public static ArrayList<String> options;
    public static ArrayList<Review> reviews = new ArrayList<>();
    public MyCountDownTimer timer1;
    public static long leftTime = 0;
    public boolean isDialogOpen = false;
    public ArrayList<Question> questionList;
    public static ArrayList<Question> questionListactual= new ArrayList<>();

    TouchImageView imgQuestion;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ProgressBar imgProgress, rightProgress, wrongProgress, progressBar;
    ImageView imgZoom, imgMic;
    int click = 0;
    int textSize;
    public TextToSpeech textToSpeech;
    RelativeLayout mainLayout;
    public String fromQue ,cat_name;
    public static String sub_cat_name;
    public int sub_cat_id,qstn_cnt;
    TextView questionNo;
    public ScrollView mainScroll, queScroll;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mcq);
        mainLayout = findViewById(R.id.play_layout);
        Utils.transparentStatusAndNavigation(TestMcqActivity.this);
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

        context = TestMcqActivity.this;
        fromQue = getIntent().getStringExtra("fromQue");
        sub_cat_id = getIntent().getIntExtra("sub_cat_id", 0);
        sub_cat_name = getIntent().getStringExtra("sub_cat_name");
        cat_name = getIntent().getStringExtra("cat_name");
        qstn_cnt = getIntent().getIntExtra("qstn_cnt", 0);
        Constant.MAX_QUESTION_PER_LEVEL = qstn_cnt;


        completeActivity = new TestCompletedActivity();
        textSize = Integer.valueOf(Session.getSavedTextSize(TestMcqActivity.this));
        Session.removeSharedPreferencesData(TestMcqActivity.this);
        settings = getSharedPreferences(Session.SETTING_Quiz_PREF, 0);


        resetAllValue();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static void ChangeTextSize(int size) {


    }

    public void resetAllValue() {
        reviews.clear();
        rclyt=findViewById(R.id.rc_question);

//        levelNo = MainActivity.dbHelper.GetLevelById(Constant.CATE_ID, Constant.SUB_CAT_ID);
        playLayout = findViewById(R.id.innerLayout);
        titleLayout = findViewById(R.id.titleLayout);
        tvTitle = findViewById(R.id.tvTitle);
        titleLayout.setVisibility(View.VISIBLE);
        tvQueNo = findViewById(R.id.tvQueNo);
        progressTimer = findViewById(R.id.progressBarTwo);
        progressBar = findViewById(R.id.progressBar);
        coin_count = findViewById(R.id.coin_count);

        tvAlert = findViewById(R.id.tvNoConnection);
        checkLayout = findViewById(R.id.checkLayout);
        lytFifty = findViewById(R.id.lytFifty);
        lytSkip = findViewById(R.id.lytSkip);
        lytAudience = findViewById(R.id.lytAudience);
        lytReset = findViewById(R.id.lytReset);
        btnTry = findViewById(R.id.btnTry);

        fifty_fifty = findViewById(R.id.fifty_fifty);
        skip_question = findViewById(R.id.next);
       rewind_quation = findViewById(R.id.prev);
        finish = findViewById(R.id.finish);
        audience_poll = findViewById(R.id.audience_poll);

        coin_count.setText(String.valueOf(coin));
        tvAlert.setText(getString(R.string.msg_no_internet));

        ChangeTextSize(textSize);
        totalScore = Session.getScore(getApplicationContext());
        count_question_completed = Session.getCountQuestionCompleted(getApplicationContext());
        coin = Session.getPoint(getApplicationContext());
        txtScore = findViewById(R.id.txtScore);
        txtScore.setText(String.valueOf(score));
        coin_count.setText(String.valueOf(coin));


        if (Utils.isNetworkAvailable(TestMcqActivity.this)) {
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
                Utils.btnClick(view, TestMcqActivity.this);
               shareClicked();
            }
        });
        finish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Utils.btnClick(view, TestMcqActivity.this);
                submitTestDialog();
            }
        });
        fifty_fifty.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Utils.btnClick(view, TestMcqActivity.this);
                viewMoreDialog();

            }
        });

        btnTry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAlert.getText().equals(getString(R.string.no_enough_question)))
                    BackButtonMethod();
                else {
                    if (Utils.isNetworkAvailable(TestMcqActivity.this)) {
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
        Utils.TotalQuestion = Constant.MAX_QUESTION_PER_LEVEL;
        Utils.CoreectQuetion = correctQuestion;
        Utils.WrongQuation = inCorrectQuestion;
        timer.cancel();
        Session.setRightAns(getApplicationContext(), rightAns);
        Session.setScore(getApplicationContext(), totalScore);
        Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);

        if (correctQuestion >= Constant.correctAnswer && levelNo == Utils.RequestlevelNo) {
       /*     levelNo = levelNo + 1;
            if (MainActivity.dbHelper.isExist(Constant.CATE_ID, Constant.SUB_CAT_ID)) {
                MainActivity.dbHelper.UpdateLevel(Constant.CATE_ID, Constant.SUB_CAT_ID, levelNo);
            } else {
                MainActivity.dbHelper.insertIntoDB(Constant.CATE_ID, Constant.SUB_CAT_ID, levelNo);
            }*/

        }
        int total = Constant.MAX_QUESTION_PER_LEVEL;
        int percent = (correctQuestion * 100) / total;

        if (percent >= 30) {
            Session.setLevelComplete(TestMcqActivity.this, true);
            if (Session.isLogin(TestMcqActivity.this))
                UpdateScore(String.valueOf(score));

        } else {
            Session.setLevelComplete(TestMcqActivity.this, false);

        }


        if (percent >= 30 && percent <= 40) {
            coin = coin + Constant.giveOneCoin;
            level_coin = Constant.giveOneCoin;

        } else if (percent >= 40 && percent <= 50) {
            coin = coin + Constant.giveTwoCoins;
            level_coin = Constant.giveTwoCoins;

        } else if (percent >= 60 && percent < 80) {
            coin = coin + Constant.giveThreeCoins;
            level_coin = Constant.giveThreeCoins;

        } else if (percent >= 80) {
            coin = coin + Constant.giveFourCoins;
            level_coin = Constant.giveFourCoins;

        }
        Utils.level_coin = level_coin;
        Session.setPoint(getApplicationContext(), coin);
        coin_count.setText(String.valueOf(coin));


        saveScore();
        Intent intent = new Intent(TestMcqActivity.this, TestCompletedActivity.class);
        intent.putExtra("fromQue", fromQue);
        startActivity(intent);
        ((TestMcqActivity) context).finish();
        blankAllValue();
        Session.removeSharedPreferencesData(TestMcqActivity.this);


    }


    @Override
    public void onClick(View v) {

    }






    /**/
    public void viewMoreDialog() {




        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TestMcqActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.end_test_view_more));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        // Setting OK Button
        alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                ((TestMcqActivity) context).finish();

            }
        });

        alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
                Constant.LeftTime = leftTime;
                if (Constant.LeftTime != 0) {
                    timer = new MyCountDownTimer(Constant.LeftTime, 1000);
                    timer.start();

                }
            }
        });
        // Showing Alert Message
        alertDialog.show();

    }

    /**/
    public void submitTestDialog() {




            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TestMcqActivity.this);
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.end_msg));
            alertDialog.setCancelable(false);
            final AlertDialog alertDialog1 = alertDialog.create();
            // Setting OK Button
            alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    levelCompleted();
                    //((TestMcqActivity) context).finish();

                }
            });

            alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog1.dismiss();
                    Constant.LeftTime = leftTime;
                    if (Constant.LeftTime != 0) {
                        timer = new MyCountDownTimer(Constant.LeftTime, 1000);
                        timer.start();

                    }
                }
            });
            // Showing Alert Message
            alertDialog.show();

    }
    public void PlayAreaLeaveDialog() {


        if (!tvAlert.getText().equals(getResources().getString(R.string.no_enough_question))) {
            if (timer != null) {
                timer.cancel();
            }
            if (timer1 != null) {
                timer1.cancel();

            }
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TestMcqActivity.this);
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.exit_msg));
            alertDialog.setCancelable(false);
            final AlertDialog alertDialog1 = alertDialog.create();
            // Setting OK Button
            alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    if (timer != null) {
                        timer.cancel();
                    }
                    if (timer1 != null) {
                        timer1.cancel();

                    }
                    reviews.clear();
                    leftTime = 0;
                    Constant.LeftTime = 0;
                    if (textToSpeech != null) {
                        textToSpeech.shutdown();
                    }
                    ((TestMcqActivity) context).finish();

                }
            });

            alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog1.dismiss();
                    Constant.LeftTime = leftTime;
                    if (Constant.LeftTime != 0) {
                        timer = new MyCountDownTimer(Constant.LeftTime, 1000);
                        timer.start();

                    }
                }
            });
            // Showing Alert Message
            alertDialog.show();
        } else {
            ((TestMcqActivity) context).finish();
        }
    }




    //add attended question in ReviewList
    public static void AddReview1(Question question1, RadioButton tvBtnOpt, boolean isRight) {

        if(reviews.isEmpty()) {
            reviews.add(new Review(question1.getId(),
                    question1.getQuestion(),
                    question1.getImage(),
                    question1.getTrueAns(),
                    tvBtnOpt.getText().toString(),
                    question1.getOptions(),
                    question1.getNote(),
                            isRight)
                    );
        }
        else{
            int i=0;
            boolean isPresent=false;
            Review n =new Review(question1.getId(),
                    question1.getQuestion(),
                    question1.getImage(),
                    question1.getTrueAns(),
                    tvBtnOpt.getText().toString(),
                    question1.getOptions(),
                    question1.getNote(),
                    isRight);
            for (Review r:reviews

                 ) {
                if(r.getQueId()==question1.getId())
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

    }


    public static int isAlreadyAnswered(Question q)
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
                if(r.getQueId()==q.getId())
                {
                    isPresent=true;
                    return i;


                }
                i++;
            }

        }


        return -1;
    }

    public static void addScore(int i, boolean isCorrect) {



        if(i == -1)
        {

            if(isCorrect) {
                correctQuestion++;
                //  txtTrueQuestion.setText(String.valueOf(correctQuestion));
                //   rightProgress.setProgress(correctQuestion);
                // totalScore = totalScore + 5;
                count_question_completed = count_question_completed + 5;
                score = score + 2;
                //txtScore.setText(String.valueOf(score));
                //rightAns = Session.getRightAns(getApplicationContext());
                rightAns++;
            }
            else
            {
                inCorrectQuestion++;
                score=score-2;
            }
            //Session.setRightAns(getApplicationContext(), rightAns);
           // Session.setScore(getApplicationContext(), totalScore);
            //Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);

        }
        else
        {
            boolean existingans=reviews.get(i).isRight();

            if(existingans && isCorrect)
            {
                //dont do anything
                inCorrectQuestion++;
            }
            else if(!existingans && isCorrect)
            {
                correctQuestion++;
                //  txtTrueQuestion.setText(String.valueOf(correctQuestion));
                //   rightProgress.setProgress(correctQuestion);
                // totalScore = totalScore + 5;
                count_question_completed = count_question_completed + 5;
                score = score + 2;
                //txtScore.setText(String.valueOf(score));
             //   rightAns = Session.getRightAns(getApplicationContext());
                rightAns++;
           ////     Session.setRightAns(getApplicationContext(), rightAns);
              //  Session.setScore(getApplicationContext(), totalScore);
                //Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);
            }
            else if(existingans && !isCorrect)
            {
                correctQuestion--;
                inCorrectQuestion++;
                //  txtTrueQuestion.setText(String.valueOf(correctQuestion));
                //   rightProgress.setProgress(correctQuestion);
                // totalScore = totalScore + 5;
                count_question_completed = count_question_completed - 5;
                score = score - 2;
                //txtScore.setText(String.valueOf(score));
                //rightAns = Session.getRightAns(getApplicationContext());
                rightAns--;
                //Session.setRightAns(getApplicationContext(), rightAns);
                //Session.setScore(getApplicationContext(), totalScore);
                //Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);
            }

        }
    }


    /*
     * Save score in Preferences
     */
    private void saveScore() {

        Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);
        Session.setScore(getApplicationContext(), totalScore);
        Session.setLastLevelScore(getApplicationContext(), score);

    }














    public void BackButtonMethod() {
        CheckSound();
        PlayAreaLeaveDialog();
    }

    public void CheckSound() {
        if (Session.getSoundEnableDisable(TestMcqActivity.this))
            Utils.backSoundonclick(TestMcqActivity.this);

        if (Session.getVibration(TestMcqActivity.this))
            Utils.vibrate(TestMcqActivity.this, Utils.VIBRATION_DURATION);
    }

    public void SettingButtonMethod() {
        CheckSound();
        if (timer1 != null)
            timer1.cancel();

        if (timer != null)
            timer.cancel();

        Intent intent = new Intent(TestMcqActivity.this, SettingActivity.class);
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
                                questionList = new ArrayList<>();
                                questionList.clear();
                                questionListactual.clear();
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

                                if (questionList.size() >= Constant.MAX_QUESTION_PER_LEVEL) {


                                    for(int i=0;i<qstn_cnt;i++)
                                    {
                                        questionListactual.add(questionList.get(i));
                                    }

                                    if (timer != null)
                                        timer.cancel();
                                    if (timer1 != null)
                                        timer1.cancel();

                                    timer = new MyCountDownTimer(Constant.TIME_PER_QUESTION*qstn_cnt, Constant.COUNT_DOWN_TIMER);
                                    timer.start();
                                    //notify adapter
                                    TestMcqAdapter1 adapter = new TestMcqAdapter1(questionListactual,TestMcqActivity.this);
                                    // Attach the adapter to the recyclerview to populate items
                                    rclyt.setAdapter(adapter);
                                    // Set layout manager to position the items
                                    rclyt.setLayoutManager(new LinearLayoutManager(TestMcqActivity.this));
                                    rclyt.setItemViewCacheSize(100);
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

        tvQueNo.setText("");
        checkLayout.setVisibility(View.VISIBLE);
        tvAlert.setText(getString(R.string.no_enough_question));
        btnTry.setText(getString(R.string.go_back));
        progressBar.setVisibility(View.GONE);
        playLayout.setVisibility(View.GONE);

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
                                Toast.makeText(TestMcqActivity.this, message, Toast.LENGTH_SHORT).show();
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
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, TestMcqActivity.this));
                params.put(Constant.SCORE, score);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    //Show dialog for rewarded ad
    //if user has not enough coin to use lifeline
    public void ShowRewarded(final Context context) {
        if (timer != null) {
            timer.cancel();
        }
        if (timer1 != null) {
            timer1.cancel();
        }
        if (!Utils.isNetworkAvailable(TestMcqActivity.this)) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Internet Connection Error!");
            dialog.setMessage("Internet Connection Error! Please connect to working Internet connection");
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (leftTime != 0) {
                        timer1 = new MyCountDownTimer(leftTime, 1000);
                        timer1.start();
                    }
                    isDialogOpen = false;
                }
            });
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (leftTime != 0) {
                        timer1 = new MyCountDownTimer(leftTime, 1000);
                        timer1.start();
                    }
                    isDialogOpen = false;
                }
            });
            dialog.show();
            isDialogOpen = true;

        } else {
            isDialogOpen = true;
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
            dialog.setView(dialogView);
            TextView skip = (TextView) dialogView.findViewById(R.id.skip);
            TextView watchNow = (TextView) dialogView.findViewById(R.id.watch_now);
            final AlertDialog alertDialog = dialog.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            alertDialog.setCancelable(false);
            skip.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    isDialogOpen = false;
                    if (leftTime != 0) {
                        timer1 = new MyCountDownTimer(leftTime, 1000);
                        timer1.start();
                    }
                }
            });
            watchNow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showRewardedVideo();
                    alertDialog.dismiss();
                    isDialogOpen = false;
                }
            });
        }
    }

    public void showRewardedVideo() {

        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        } else if (!rewardedVideoAd.isLoaded()) {
            loadRewardedVideoAd(TestMcqActivity.this);
            if (rewardedVideoAd.isLoaded()) {
                rewardedVideoAd.show();
            }
        }
    }

    public static void loadRewardedVideoAd(Context context) {

        if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(context.getResources().getString(R.string.admob_Rewarded_Video_Ads), new AdRequest.Builder().build());
        }
    }

    RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {
        }

        @Override
        public void onRewardedVideoCompleted() {

        }

        @Override
        public void onRewardedVideoAdOpened() {
        }

        @Override
        public void onRewardedVideoStarted() {
        }

        @Override
        public void onRewardedVideoAdClosed() {
            loadRewardedVideoAd(TestMcqActivity.this);
        }

        @Override
        public void onRewarded(RewardItem reward) {
            // Reward the user.
            coin = coin + 4;
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            if (Utils.isNetworkAvailable(TestMcqActivity.this)) {
                if (interstitial.isLoaded()) {
                    interstitial.show();
                    coin = coin + 4;
                } else {
                    interstitial = new InterstitialAd(TestMcqActivity.this);
                    interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
                    AdRequest adRequest = new AdRequest.Builder().build();
                    interstitial.loadAd(adRequest);
                    if (interstitial.isLoaded()) {
                        interstitial.show();
                        coin = coin + 4;
                    }
                }
            }
        }


    };


    public class MyCountDownTimer extends CountDownTimer {

        private MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;
            System.out.println("===  timer  " + millisUntilFinished);
            int progress = (int) (millisUntilFinished / 1000);

            if (progressTimer == null)
                progressTimer = findViewById(R.id.progressBarTwo);
            else
                progressTimer.setCurrentProgress(progress);

            //when left last 5 second we show progress color red
            if (millisUntilFinished <= 6000)
                progressTimer.SetTimerAttributes(Color.RED, Color.parseColor(Constant.PROGRESS_BG_COLOR), Color.RED, Constant.PROGRESS_TEXT_SIZE);
            else
                progressTimer.SetTimerAttributes(Color.parseColor(Constant.PROGRESS_COLOR), Color.parseColor(Constant.PROGRESS_BG_COLOR), Color.WHITE, Constant.PROGRESS_TEXT_SIZE);

        }

        @Override
        public void onFinish() {
            levelCompleted();
           /* if (questionIndex >= Constant.MAX_QUESTION_PER_LEVEL) {
                levelCompleted();
            } else {
                mHandler.postDelayed(mUpdateUITimerTask, 100);
                questionIndex++;
            }*/

        }
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

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {

        if (Constant.LeftTime != 0) {
            timer1 = new MyCountDownTimer(Constant.LeftTime, Constant.COUNT_DOWN_TIMER);
            timer1.start();
        }

        coin_count.setText(String.valueOf(coin));
        super.onResume();

    }

    @Override
    public void onPause() {

        Constant.LeftTime = leftTime;
        if (timer != null)
            timer.cancel();
        if (timer1 != null)
            timer1.cancel();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        leftTime = 0;
        if (timer != null)
            timer.cancel();
        if (timer1 != null)
            timer1.cancel();
        if (textToSpeech != null)
            textToSpeech.shutdown();

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
        final String sharetext = "I am giving test on " + sub_cat_name + " on "  + getString(R.string.app_name);
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, "" + sharetext + " " + Constant.PLAY_STORE_URL + getPackageName());
        startActivity(Intent.createChooser(share, "Share " + getString(R.string.app_name) + "!"));
    }
}