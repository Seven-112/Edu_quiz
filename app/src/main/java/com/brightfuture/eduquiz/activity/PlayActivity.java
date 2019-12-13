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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
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


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.CircularProgressIndicator;
import com.brightfuture.eduquiz.helper.CircularProgressIndicator2;

import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.helper.TouchImageView;
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

public class PlayActivity extends AppCompatActivity implements OnClickListener {

    public Toolbar toolbar;
    public RelativeLayout titleLayout;
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
    public TextView option_a, option_b, option_c, option_d,
            txtScore, txtTrueQuestion, txtFalseQuestion, coin_count;

    public static TextView btnOpt1, btnOpt2, btnOpt3, btnOpt4, txtQuestion, txtQuestion1, tvAlert;
    public CompleteActivity completeActivity;
    public static Context context;
    public ImageView fifty_fifty, skip_question, resetTimer, audience_poll;

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

    TouchImageView imgQuestion;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ProgressBar imgProgress, rightProgress, wrongProgress, progressBar;
    ImageView imgZoom, imgMic;
    int click = 0;
    int textSize;
    public TextToSpeech textToSpeech;
    RelativeLayout mainLayout;
    public String fromQue;
    public ScrollView mainScroll, queScroll;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mainLayout = findViewById(R.id.play_layout);
        Utils.transparentStatusAndNavigation(PlayActivity.this);
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

        context = PlayActivity.this;
        fromQue = getIntent().getStringExtra("fromQue");

        completeActivity = new CompleteActivity();
        final int[] CLICKABLE = new int[]{R.id.a_layout, R.id.b_layout, R.id.c_layout, R.id.d_layout};

        for (int i : CLICKABLE) {
            findViewById(i).setOnClickListener(this);
        }

        textToSpeech = new TextToSpeech(PlayActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.setPitch(1.1f);

                }
            }
        });
        textSize = Integer.valueOf(Session.getSavedTextSize(PlayActivity.this));
        Session.removeSharedPreferencesData(PlayActivity.this);
        RightSwipe_A = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_right_a);
        RightSwipe_B = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_right_b);
        RightSwipe_C = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_right_c);
        RightSwipe_D = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_right_d);
        Fade_in = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fade_out);
        fifty_fifty_anim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fifty_fifty);
        settings = getSharedPreferences(Session.SETTING_Quiz_PREF, 0);


        resetAllValue();


        mainScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                v.findViewById(R.id.queScroll).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        queScroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        rewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);

        progressTimer.setMaxProgress(Constant.CIRCULAR_MAX_PROGRESS);
        progressTimer.setCurrentProgress(Constant.CIRCULAR_MAX_PROGRESS);
        try {
            interstitial = new InterstitialAd(PlayActivity.this);
            interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
            adRequest = new AdRequest.Builder().build();
            interstitial.loadAd(adRequest);
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdOpened() {
                    //when ads show , we have to stop timer
                    if (timer != null) {
                        timer.cancel();
                    }
                    if (timer1 != null) {
                        timer1.cancel();

                    }
                }

                @Override
                public void onAdClosed() {
                    //after ad close we restart timer
                    // timer1= new timer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
                    if (!tvAlert.getText().equals(getString(R.string.no_enough_question)))
                        timer.start();
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

        if (btnOpt1 != null)
            btnOpt1.setTextSize(size);
        if (btnOpt2 != null)
            btnOpt2.setTextSize(size);
        if (btnOpt3 != null)
            btnOpt3.setTextSize(size);
        if (btnOpt4 != null)
            btnOpt4.setTextSize(size);
        if (txtQuestion != null)
            txtQuestion.setTextSize(size);
        if (txtQuestion1 != null)
            txtQuestion1.setTextSize(size);
    }

    public void resetAllValue() {
        levelNo = MainActivity.dbHelper.GetLevelById(Constant.CATE_ID, Constant.SUB_CAT_ID);
        playLayout = findViewById(R.id.innerLayout);
        titleLayout = findViewById(R.id.titleLayout);
        tvTitle = findViewById(R.id.tvTitle);
        titleLayout.setVisibility(View.VISIBLE);
        tvQueNo = findViewById(R.id.tvQueNo);
        progressTimer = findViewById(R.id.progressBarTwo);
        mainScroll = findViewById(R.id.mainScroll);
        queScroll = findViewById(R.id.queScroll);
        progressBar = findViewById(R.id.progressBar);
        coin_count = findViewById(R.id.coin_count);
        imgProgress = findViewById(R.id.imgProgress);
        rightProgress = findViewById(R.id.rightProgress);
        wrongProgress = findViewById(R.id.wrongProgress);
        imgQuestion = findViewById(R.id.imgQuestion);

        tvAlert = findViewById(R.id.tvNoConnection);
        checkLayout = findViewById(R.id.checkLayout);
        lytFifty = findViewById(R.id.lytFifty);
        lytSkip = findViewById(R.id.lytSkip);
        lytAudience = findViewById(R.id.lytAudience);
        lytReset = findViewById(R.id.lytReset);
        btnTry = findViewById(R.id.btnTry);

        btnOpt1 = findViewById(R.id.btnOpt1);
        btnOpt2 = findViewById(R.id.btnOpt2);
        btnOpt3 = findViewById(R.id.btnOpt3);
        btnOpt4 = findViewById(R.id.btnOpt4);

        option_a = findViewById(R.id.option_a);
        option_b = findViewById(R.id.option_b);
        option_c = findViewById(R.id.option_c);
        option_d = findViewById(R.id.option_d);


        imgMic = findViewById(R.id.imgMic);
        imgZoom = findViewById(R.id.imgZoom);
        fifty_fifty = findViewById(R.id.fifty_fifty);
        skip_question = findViewById(R.id.skip_quation);
        resetTimer = findViewById(R.id.reset_timer);
        audience_poll = findViewById(R.id.audience_poll);

        coin_count.setText(String.valueOf(coin));
        tvAlert.setText(getString(R.string.msg_no_internet));
        txtTrueQuestion = findViewById(R.id.txtTrueQuestion);
        txtTrueQuestion.setText("0");
        txtFalseQuestion = findViewById(R.id.txtFalseQuestion);
        txtFalseQuestion.setText("0");
        txtQuestion = findViewById(R.id.txtQuestion);
        txtQuestion1 = findViewById(R.id.txtQuestion1);
        layout_A = findViewById(R.id.a_layout);
        layout_B = findViewById(R.id.b_layout);
        layout_C = findViewById(R.id.c_layout);
        layout_D = findViewById(R.id.d_layout);

        ChangeTextSize(textSize);
        progressBarTwo_A = findViewById(R.id.progress_A);
        progressBarTwo_B = findViewById(R.id.progress_B);
        progressBarTwo_C = findViewById(R.id.progress_C);
        progressBarTwo_D = findViewById(R.id.progress_D);
        progressBarTwo_A.SetAttributes(Color.parseColor(Constant.AUD_PROGRESS_COLOR), Color.parseColor(Constant.AUD_PROGRESS_COLOR), Color.parseColor(Constant.AUD_PROGRESS_BG_COLOR), Color.WHITE, Constant.AUD_PROGRESS_TEXT_SIZE);
        progressBarTwo_B.SetAttributes(Color.parseColor(Constant.AUD_PROGRESS_COLOR), Color.parseColor(Constant.AUD_PROGRESS_COLOR), Color.parseColor(Constant.AUD_PROGRESS_BG_COLOR), Color.WHITE, Constant.AUD_PROGRESS_TEXT_SIZE);
        progressBarTwo_C.SetAttributes(Color.parseColor(Constant.AUD_PROGRESS_COLOR), Color.parseColor(Constant.AUD_PROGRESS_COLOR), Color.parseColor(Constant.AUD_PROGRESS_BG_COLOR), Color.WHITE, Constant.AUD_PROGRESS_TEXT_SIZE);
        progressBarTwo_D.SetAttributes(Color.parseColor(Constant.AUD_PROGRESS_COLOR), Color.parseColor(Constant.AUD_PROGRESS_COLOR), Color.parseColor(Constant.AUD_PROGRESS_BG_COLOR), Color.WHITE, Constant.AUD_PROGRESS_TEXT_SIZE);

        animation = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.right_ans_anim); // Change alpha from fully visible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
        totalScore = Session.getScore(getApplicationContext());
        count_question_completed = Session.getCountQuestionCompleted(getApplicationContext());
        coin = Session.getPoint(getApplicationContext());
        txtScore = findViewById(R.id.txtScore);
        txtScore.setText(String.valueOf(score));
        coin_count.setText(String.valueOf(coin));

        rightProgress.setMax(Constant.MAX_QUESTION_PER_LEVEL);
        wrongProgress.setMax(Constant.MAX_QUESTION_PER_LEVEL);

        if (Utils.isNetworkAvailable(PlayActivity.this)) {
            getQuestionsFromJson();

        } else {
            tvAlert.setText(getString(R.string.msg_no_internet));
            playLayout.setVisibility(View.GONE);
            checkLayout.setVisibility(View.VISIBLE);

        }
        tvTitle.setText(getString(R.string.level_txt) + Utils.RequestlevelNo);
        //timer = new timer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
        imgMic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(questionList.get(questionIndex).getQuestion(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        audience_poll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.btnClick(view, PlayActivity.this);
                AudiencePollLifeline();
            }
        });
        resetTimer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Utils.btnClick(view, PlayActivity.this);
                ResetTimerLifeline();
            }
        });
        skip_question.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Utils.btnClick(view, PlayActivity.this);
                SkipQuestionMethod();
            }
        });
        fifty_fifty.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Utils.btnClick(view, PlayActivity.this);
                FiftyFiftyLifeline();
            }
        });

        btnTry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAlert.getText().equals(getString(R.string.no_enough_question)))
                    BackButtonMethod();
                else {
                    if (Utils.isNetworkAvailable(PlayActivity.this)) {
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

    public void QuestionBookmark() {
    /*if (imgBookmark.getTag().equals("unmark")) {
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
        imgBookmark.setImageResource(R.drawable.ic_mark);
        imgBookmark.setTag("mark");
    } else {
        MainActivity.bookmarkDBHelper.delete_id(question.getId());
        imgBookmark.setImageResource(R.drawable.ic_unmark);
        imgBookmark.setTag("unmark");
    }*/
    }

    private void nextQuizQuestion() {
        if (timer != null)
            timer.cancel();
        if (timer1 != null)
            timer1.cancel();

        timer = new MyCountDownTimer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
        timer.start();

        Constant.LeftTime = 0;
        leftTime = 0;
        setAgain();
        if (questionIndex >= Constant.MAX_QUESTION_PER_LEVEL) {
            levelCompleted();
        }
        invalidateOptionsMenu();
        layout_A.setBackgroundResource(R.drawable.answer_bg);
        layout_B.setBackgroundResource(R.drawable.answer_bg);
        layout_C.setBackgroundResource(R.drawable.answer_bg);
        layout_D.setBackgroundResource(R.drawable.answer_bg);
        layout_A.clearAnimation();
        layout_B.clearAnimation();
        layout_C.clearAnimation();
        layout_D.clearAnimation();
        System.out.println("+++  " + tvAlert.getText());

        layout_A.setClickable(true);
        layout_B.setClickable(true);
        layout_C.setClickable(true);
        layout_D.setClickable(true);
        btnOpt1.startAnimation(RightSwipe_A);
        btnOpt2.startAnimation(RightSwipe_B);
        btnOpt3.startAnimation(RightSwipe_C);
        btnOpt4.startAnimation(RightSwipe_D);
        txtQuestion1.startAnimation(Fade_in);
        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            int temp = questionIndex;
            imgQuestion.resetZoom();
            tvQueNo.setText(++temp + "/" + Constant.MAX_QUESTION_PER_LEVEL);

            if (!question.getImage().isEmpty()) {
                imgZoom.setVisibility(View.VISIBLE);
                txtQuestion1.setVisibility(View.VISIBLE);
                txtQuestion.setVisibility(View.GONE);
                imgQuestion.setImageUrl(question.getImage(), imageLoader);
                imgQuestion.setVisibility(View.VISIBLE);
                imgProgress.setVisibility(View.GONE);
                imgZoom.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        click++;
                        if (click == 1)
                            imgQuestion.setZoom(1.25f);
                        else if (click == 2)
                            imgQuestion.setZoom(1.50f);
                        else if (click == 3)
                            imgQuestion.setZoom(1.75f);
                        else if (click == 4) {
                            imgQuestion.setZoom(2.00f);
                            click = 0;
                        }
                    }
                });
            } else {
                imgZoom.setVisibility(View.GONE);
                imgQuestion.setVisibility(View.GONE);
                txtQuestion1.setVisibility(View.GONE);
                txtQuestion.setVisibility(View.VISIBLE);
            }

            txtQuestion.setText(Html.fromHtml(question.getQuestion()));
            txtQuestion1.setText(Html.fromHtml(question.getQuestion()));
            options = new ArrayList<String>();
            options.addAll(question.getOptions());
            Collections.shuffle(options);

            btnOpt1.setText(Html.fromHtml(options.get(0).trim()));
            btnOpt2.setText(Html.fromHtml(options.get(1).trim()));
            btnOpt3.setText(Html.fromHtml(options.get(2).trim()));
            btnOpt4.setText(Html.fromHtml(options.get(3).trim()));

        }
    }

    public void levelCompleted() {
        Utils.TotalQuestion = Constant.MAX_QUESTION_PER_LEVEL;
        Utils.CoreectQuetion = correctQuestion;
        Utils.WrongQuation = inCorrectQuestion;
        timer.cancel();

        if (correctQuestion >= Constant.correctAnswer && levelNo == Utils.RequestlevelNo) {
            levelNo = levelNo + 1;
            if (MainActivity.dbHelper.isExist(Constant.CATE_ID, Constant.SUB_CAT_ID)) {
                MainActivity.dbHelper.UpdateLevel(Constant.CATE_ID, Constant.SUB_CAT_ID, levelNo);
            } else {
                MainActivity.dbHelper.insertIntoDB(Constant.CATE_ID, Constant.SUB_CAT_ID, levelNo);
            }

        }
        int total = Constant.MAX_QUESTION_PER_LEVEL;
        int percent = (correctQuestion * 100) / total;

        if (correctQuestion >= Constant.correctAnswer) {
            Session.setLevelComplete(PlayActivity.this, true);
            if (Session.isLogin(PlayActivity.this))
                UpdateScore(String.valueOf(score));

        } else {
            Session.setLevelComplete(PlayActivity.this, false);

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
        Intent intent = new Intent(PlayActivity.this, CompleteActivity.class);
        intent.putExtra("fromQue", fromQue);
        startActivity(intent);
        ((PlayActivity) context).finish();
        blankAllValue();
        Session.removeSharedPreferencesData(PlayActivity.this);


    }


    @Override
    public void onClick(View v) {
        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
            layout_C.setClickable(false);
            layout_D.setClickable(false);
            if (progressBarTwo_A.getVisibility() == (View.VISIBLE)) {
                progressBarTwo_A.setVisibility(View.GONE);
                progressBarTwo_B.setVisibility(View.GONE);
                progressBarTwo_C.setVisibility(View.GONE);
                progressBarTwo_D.setVisibility(View.GONE);
                option_a.setVisibility(View.VISIBLE);
                option_b.setVisibility(View.VISIBLE);
                option_c.setVisibility(View.VISIBLE);
                option_d.setVisibility(View.VISIBLE);
            }
            Constant.LeftTime = 0;
            String trueAns = question.getTrueAns().trim();
            switch (v.getId()) {
                case R.id.a_layout:
                    AddReview(question, btnOpt1);
                    if (btnOpt1.getText().toString().equalsIgnoreCase(trueAns)) {
                        addScore();
                        layout_A.setBackgroundResource(R.drawable.right_gradient);
                        layout_A.startAnimation(animation);

                    } else if (!btnOpt1.getText().toString().equalsIgnoreCase(trueAns)) {
                        layout_A.setBackgroundResource(R.drawable.wrong_gradient);
                        WrongQuestion();

                        if (btnOpt2.getText().toString().equals(trueAns)) {
                            layout_B.startAnimation(animation);
                            layout_B.setBackgroundResource(R.drawable.right_gradient);

                        } else if (btnOpt3.getText().toString().equals(trueAns)) {
                            layout_C.setBackgroundResource(R.drawable.right_gradient);
                            layout_C.startAnimation(animation);

                        } else if (btnOpt4.getText().toString().equals(trueAns)) {
                            layout_D.setBackgroundResource(R.drawable.right_gradient);
                            layout_D.startAnimation(animation);

                        }

                    }
                    increaseQuestionIndex();
                    break;

                case R.id.b_layout:
                    AddReview(question, btnOpt2);
                    if (btnOpt2.getText().toString().equalsIgnoreCase(trueAns)) {
                        addScore();
                        layout_B.setBackgroundResource(R.drawable.right_gradient);
                        layout_B.startAnimation(animation);

                    } else if (!btnOpt2.getText().toString().equalsIgnoreCase(trueAns)) {

                        layout_B.setBackgroundResource(R.drawable.wrong_gradient);
                        WrongQuestion();

                        if (btnOpt1.getText().toString().equals(trueAns)) {
                            layout_A.startAnimation(animation);
                            layout_A.setBackgroundResource(R.drawable.right_gradient);

                        } else if (btnOpt3.getText().toString().equals(trueAns)) {
                            layout_C.setBackgroundResource(R.drawable.right_gradient);
                            layout_C.startAnimation(animation);

                        } else if (btnOpt4.getText().toString().equals(trueAns)) {
                            layout_D.setBackgroundResource(R.drawable.right_gradient);
                            layout_D.startAnimation(animation);

                        }

                    }
                    increaseQuestionIndex();
                    break;
                case R.id.c_layout:
                    AddReview(question, btnOpt3);
                    if (btnOpt3.getText().toString().equalsIgnoreCase(trueAns)) {

                        addScore();
                        layout_C.setBackgroundResource(R.drawable.right_gradient);
                        layout_C.startAnimation(animation);


                    } else if (!btnOpt3.getText().toString().equalsIgnoreCase(trueAns)) {
                        layout_C.setBackgroundResource(R.drawable.wrong_gradient);
                        WrongQuestion();

                        if (btnOpt1.getText().toString().equals(trueAns)) {
                            layout_A.startAnimation(animation);
                            layout_A.setBackgroundResource(R.drawable.right_gradient);


                        } else if (btnOpt2.getText().toString().equals(trueAns)) {
                            layout_B.startAnimation(animation);
                            layout_B.setBackgroundResource(R.drawable.right_gradient);


                        } else if (btnOpt4.getText().toString().equals(trueAns)) {
                            layout_D.setBackgroundResource(R.drawable.right_gradient);
                            layout_D.startAnimation(animation);

                        }

                    }
                    increaseQuestionIndex();

                    break;
                case R.id.d_layout:
                    AddReview(question, btnOpt4);
                    if (btnOpt4.getText().toString().equalsIgnoreCase(trueAns)) {
                        layout_D.setBackgroundResource(R.drawable.right_gradient);
                        layout_D.startAnimation(animation);

                        addScore();
                    } else if (!btnOpt4.getText().toString().equalsIgnoreCase(trueAns)) {
                        layout_D.setBackgroundResource(R.drawable.wrong_gradient);
                        WrongQuestion();

                        if (btnOpt1.getText().toString().equals(trueAns)) {
                            layout_A.startAnimation(animation);
                            layout_A.setBackgroundResource(R.drawable.right_gradient);

                        } else if (btnOpt2.getText().toString().equals(trueAns)) {
                            layout_B.startAnimation(animation);
                            layout_B.setBackgroundResource(R.drawable.right_gradient);

                        } else if (btnOpt3.getText().toString().equals(trueAns)) {
                            layout_C.setBackgroundResource(R.drawable.right_gradient);
                            layout_C.startAnimation(animation);
                        }
                    }
                    increaseQuestionIndex();
                    break;
            }

        }
    }


    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            if (getApplicationContext() != null) {
                if (checkLayout.getVisibility() != View.VISIBLE)
                    nextQuizQuestion();

            }
        }
    };

    public void increaseQuestionIndex() {
        questionIndex++;
        if (timer != null) {
            timer.cancel();
        }
        if (timer1 != null) {
            timer1.cancel();

        }
    }

    /**/
    public void PlayAreaLeaveDialog() {


        if (!tvAlert.getText().equals(getResources().getString(R.string.no_enough_question))) {
            if (timer != null) {
                timer.cancel();
            }
            if (timer1 != null) {
                timer1.cancel();

            }
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlayActivity.this);
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
                    ((PlayActivity) context).finish();

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
            ((PlayActivity) context).finish();
        }
    }


    //add attended question in ReviewList
    public void AddReview(Question question, TextView tvBtnOpt) {
        reviews.add(new Review(question.getId(),
                question.getQuestion(),
                question.getImage(),
                question.getTrueAns(),
                tvBtnOpt.getText().toString(),
                question.getOptions(),
                question.getNote()));

        leftTime = 0;
        Constant.LeftTime = 0;
        mHandler.postDelayed(mUpdateUITimerTask, 1000);
        txtScore.setText(String.valueOf(score));
    }


    private void addScore() {
        rightSound();
        correctQuestion++;
        txtTrueQuestion.setText(String.valueOf(correctQuestion));
        rightProgress.setProgress(correctQuestion);
        totalScore = totalScore + 5;
        count_question_completed = count_question_completed + 5;
        score = score + 5;
        txtScore.setText(String.valueOf(score));
        rightAns = Session.getRightAns(getApplicationContext());
        rightAns++;
        Session.setRightAns(getApplicationContext(), rightAns);
        Session.setScore(getApplicationContext(), totalScore);
        Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);
    }

    private void WrongQuestion() {
        setAgain();
        playWrongSound();
        saveScore();
        inCorrectQuestion++;
        totalScore = totalScore - 2;
        count_question_completed = count_question_completed - 2;
        score = score - 2;
        txtFalseQuestion.setText(String.valueOf(inCorrectQuestion));
        wrongProgress.setProgress(inCorrectQuestion);
        txtScore.setText(String.valueOf(score));
    }

    /*
     * Save score in Preferences
     */
    private void saveScore() {

        Session.setCountQuestionCompleted(getApplicationContext(), count_question_completed);
        Session.setScore(getApplicationContext(), totalScore);
        Session.setLastLevelScore(getApplicationContext(), score);

    }

    //play sound when answer is correct
    public void rightSound() {
        if (Session.getSoundEnableDisable(PlayActivity.this))
            Utils.setrightAnssound(PlayActivity.this);

        if (Session.getVibration(PlayActivity.this))
            Utils.vibrate(PlayActivity.this, Utils.VIBRATION_DURATION);

    }

    //play sound when answer is incorrect
    private void playWrongSound() {
        if (Session.getSoundEnableDisable(PlayActivity.this))
            Utils.setwronAnssound(PlayActivity.this);

        if (Session.getVibration(PlayActivity.this))
            Utils.vibrate(PlayActivity.this, Utils.VIBRATION_DURATION);

    }

    //set progress again after next question
    private void setAgain() {
        if (progressBarTwo_A.getVisibility() == (View.VISIBLE)) {
            progressBarTwo_A.setVisibility(View.GONE);
            progressBarTwo_B.setVisibility(View.GONE);
            progressBarTwo_C.setVisibility(View.GONE);
            progressBarTwo_D.setVisibility(View.GONE);
        }

    }

    //Skip Question lifeline
    public void SkipQuestionMethod() {
        CheckSound();

        if (!Session.isSkipUsed(PlayActivity.this)) {
            if (coin >= 4) {
                if (timer1 != null)
                    timer1.cancel();
                if (timer != null)
                    timer.cancel();

                leftTime = 0;
                Constant.LeftTime = 0;

                coin = coin - 4;
                coin_count.setText(String.valueOf(coin));
                questionIndex++;
                layout_A.setBackgroundResource(R.drawable.answer_bg);
                layout_B.setBackgroundResource(R.drawable.answer_bg);
                layout_C.setBackgroundResource(R.drawable.answer_bg);
                layout_D.setBackgroundResource(R.drawable.answer_bg);

                nextQuizQuestion();
                Session.setSkip(PlayActivity.this);
            } else
                ShowRewarded(PlayActivity.this);
        } else
            AlreadyUsed();

    }

    //Fifty Fifty Lifeline
    public void FiftyFiftyLifeline() {
        CheckSound();
        if (!Session.isFiftyFiftyUsed(PlayActivity.this)) {
            if (coin >= 4) {
                btnPosition = 0;
                coin = coin - 4;
                coin_count.setText(String.valueOf(coin));
                if (btnOpt1.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 1;
                if (btnOpt2.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 2;
                if (btnOpt3.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 3;
                if (btnOpt4.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 4;

                if (btnPosition == 1) {
                    layout_B.startAnimation(fifty_fifty_anim);
                    layout_C.startAnimation(fifty_fifty_anim);
                    layout_B.setClickable(false);
                    layout_C.setClickable(false);

                } else if (btnPosition == 2) {
                    layout_C.startAnimation(fifty_fifty_anim);
                    layout_D.startAnimation(fifty_fifty_anim);
                    layout_C.setClickable(false);
                    layout_D.setClickable(false);

                } else if (btnPosition == 3) {
                    layout_D.startAnimation(fifty_fifty_anim);
                    layout_A.startAnimation(fifty_fifty_anim);
                    layout_D.setClickable(false);
                    layout_A.setClickable(false);

                } else if (btnPosition == 4) {
                    layout_A.startAnimation(fifty_fifty_anim);
                    layout_B.startAnimation(fifty_fifty_anim);
                    layout_A.setClickable(false);
                    layout_B.setClickable(false);
                }
                Session.setFifty_Fifty(PlayActivity.this);
            } else
                ShowRewarded(PlayActivity.this);

        } else
            AlreadyUsed();
    }

    //Reset Timer lifeline method
    public void ResetTimerLifeline() {
        CheckSound();
        if (!Session.isResetUsed(PlayActivity.this)) {
            if (coin >= 4) {

                coin = coin - 4;
                coin_count.setText(String.valueOf(coin));

                if (timer1 != null)
                    timer1.cancel();

                Constant.LeftTime = 0;
                leftTime = 0;
                if (timer != null) {
                    timer.cancel();
                    timer.start();
                } else {
                    timer.start();
                }
                Session.setReset(PlayActivity.this);
            } else
                ShowRewarded(PlayActivity.this);
        } else
            AlreadyUsed();

    }

    //AudiencePoll Lifeline method
    public void AudiencePollLifeline() {
        CheckSound();
        if (!Session.isAudiencePollUsed(PlayActivity.this)) {
            if (coin >= 4) {
                btnPosition = 0;
                coin = coin - 4;
                coin_count.setText(String.valueOf(coin));
                int min = 45;
                int max = 70;
                Random r = new Random();
                int A = r.nextInt(max - min + 1) + min;
                int remain1 = 100 - A;
                int B = r.nextInt(((remain1 - 10)) + 1);
                int remain2 = remain1 - B;
                int C = r.nextInt(((remain2 - 5)) + 1);
                int D = remain2 - C;
                progressBarTwo_A.setVisibility(View.VISIBLE);
                progressBarTwo_B.setVisibility(View.VISIBLE);
                progressBarTwo_C.setVisibility(View.VISIBLE);
                progressBarTwo_D.setVisibility(View.VISIBLE);

                if (btnOpt1.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 1;
                if (btnOpt2.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 2;
                if (btnOpt3.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 3;
                if (btnOpt4.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 4;


                if (btnPosition == 1) {
                    progressBarTwo_A.setCurrentProgress(A);
                    progressBarTwo_B.setCurrentProgress(B);
                    progressBarTwo_C.setCurrentProgress(C);
                    progressBarTwo_D.setCurrentProgress(D);

                } else if (btnPosition == 2) {
                    progressBarTwo_B.setCurrentProgress(A);
                    progressBarTwo_C.setCurrentProgress(C);
                    progressBarTwo_D.setCurrentProgress(D);
                    progressBarTwo_A.setCurrentProgress(B);

                } else if (btnPosition == 3) {
                    progressBarTwo_C.setCurrentProgress(A);
                    progressBarTwo_B.setCurrentProgress(C);
                    progressBarTwo_D.setCurrentProgress(D);
                    progressBarTwo_A.setCurrentProgress(B);

                } else if (btnPosition == 4) {
                    progressBarTwo_D.setCurrentProgress(A);
                    progressBarTwo_B.setCurrentProgress(C);
                    progressBarTwo_C.setCurrentProgress(D);
                    progressBarTwo_A.setCurrentProgress(B);

                }
                option_a.setVisibility(View.VISIBLE);
                option_b.setVisibility(View.VISIBLE);
                option_c.setVisibility(View.VISIBLE);
                option_d.setVisibility(View.VISIBLE);
                Session.setAudiencePoll(PlayActivity.this);
            } else
                ShowRewarded(PlayActivity.this);
        } else
            AlreadyUsed();

    }

    //Show alert dialog when lifeline already used in current level
    public void AlreadyUsed() {

        if (timer != null)
            timer.cancel();
        if (timer1 != null)
            timer1.cancel();

        final AlertDialog.Builder dialog = new AlertDialog.Builder(PlayActivity.this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lifeline_dialog, null);
        dialog.setView(dialogView);

        TextView ok = (TextView) dialogView.findViewById(R.id.ok);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        alertDialog.setCancelable(false);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                if (leftTime != 0) {
                    timer1 = new MyCountDownTimer(leftTime, 1000);
                    timer1.start();
                }
            }
        });

    }

    public void BackButtonMethod() {
        CheckSound();
        PlayAreaLeaveDialog();
    }

    public void CheckSound() {
        if (Session.getSoundEnableDisable(PlayActivity.this))
            Utils.backSoundonclick(PlayActivity.this);

        if (Session.getVibration(PlayActivity.this))
            Utils.vibrate(PlayActivity.this, Utils.VIBRATION_DURATION);
    }

    public void SettingButtonMethod() {
        CheckSound();
        if (timer1 != null)
            timer1.cancel();

        if (timer != null)
            timer.cancel();

        Intent intent = new Intent(PlayActivity.this, SettingActivity.class);
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
                                    nextQuizQuestion();
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
                params.put(Constant.getQuestionByLevel, "1");

                params.put(Constant.Level, String.valueOf(Utils.RequestlevelNo));
                if (fromQue.equals("cate"))
                    params.put(Constant.category, "" + Constant.CATE_ID);
                else if (fromQue.equals("subCate"))
                    params.put(Constant.subCategoryId, "" + Constant.SUB_CAT_ID);
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
                                Toast.makeText(PlayActivity.this, message, Toast.LENGTH_SHORT).show();
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
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, PlayActivity.this));
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
        if (!Utils.isNetworkAvailable(PlayActivity.this)) {
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
            loadRewardedVideoAd(PlayActivity.this);
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
            loadRewardedVideoAd(PlayActivity.this);
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
            if (Utils.isNetworkAvailable(PlayActivity.this)) {
                if (interstitial.isLoaded()) {
                    interstitial.show();
                    coin = coin + 4;
                } else {
                    interstitial = new InterstitialAd(PlayActivity.this);
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
            if (questionIndex >= Constant.MAX_QUESTION_PER_LEVEL) {
                levelCompleted();
            } else {
                mHandler.postDelayed(mUpdateUITimerTask, 100);
                questionIndex++;
            }

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
}