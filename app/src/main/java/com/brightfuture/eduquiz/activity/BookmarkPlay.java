package com.brightfuture.eduquiz.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.CircularProgressIndicator;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.helper.TouchImageView;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.model.Bookmark;

import java.util.ArrayList;
import java.util.Collections;

public class BookmarkPlay extends AppCompatActivity implements View.OnClickListener {
    public Animation RightSwipe_A, RightSwipe_B, RightSwipe_C, RightSwipe_D, Fade_in;
    public int questionIndex = 0,
            correctQuestion = 0,
            inCorrectQuestion = 0;

    public TextView txtQuestion, txtQuestion1,
            btnOpt1, btnOpt2, btnOpt3, btnOpt4, txtTrueQuestion, txtFalseQuestion, tvTitle, tvQueNo;
    public RelativeLayout playLayout, checkLayout;
    public Button btnTry;
    public  CardView layout_A, layout_B, layout_C, layout_D;
    private final Handler mHandler = new Handler();
    public static CircularProgressIndicator progressTimer;
    public static MyCountDownTimer myCountDownTimer;
    public static ArrayList<String> options;
    public MyCountDownTimer myCountDownTimer1;
    public static long leftTime = 0;
    public  ArrayList<Bookmark> questionList;
    public  Bookmark question;
    public  TouchImageView imgQuestion;
    public  ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public  ProgressBar imgProgress, rightProgress, wrongProgress;
    public  ImageView imgZoom;
    int click = 0;
    private Animation animation;
    public TextView tvNoConnection;
    public Button btnAnswer;
    public String trueOption;
    public Toolbar toolbar;
    public RelativeLayout titleLayout;
    public ScrollView mainScroll, queScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_play);
        RelativeLayout mainLayout = findViewById(R.id.play_layout);
        Utils.transparentStatusAndNavigation(BookmarkPlay.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
        final int[] CLICKABLE = new int[]{R.id.a_layout, R.id.b_layout, R.id.c_layout, R.id.d_layout};

        for (int i : CLICKABLE) {
            findViewById(i).setOnClickListener(this);
        }

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackButtonMethod();
            }
        });
        titleLayout = findViewById(R.id.titleLayout);
        titleLayout.setVisibility(View.VISIBLE);
        tvQueNo = findViewById(R.id.tvQueNo);
        tvTitle = findViewById(R.id.tvTitle);
        questionList = BookmarkList.bookmarks;
        tvTitle.setText(getString(R.string.bookmark_play1));
        RightSwipe_A = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_right_a);
        RightSwipe_B = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_right_b);
        RightSwipe_C = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_right_c);
        RightSwipe_D = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_right_d);
        Fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        playLayout = findViewById(R.id.innerLayout);
        playLayout.setVisibility(View.GONE);
        progressTimer = findViewById(R.id.progressBarTwo);
        progressTimer.setMaxProgress(Constant.CIRCULAR_MAX_PROGRESS);
        progressTimer.setCurrentProgress(Constant.CIRCULAR_MAX_PROGRESS);

        mainScroll = findViewById(R.id.mainScroll);
        queScroll=findViewById(R.id.queScroll);
        imgProgress = findViewById(R.id.imgProgress);
        rightProgress = findViewById(R.id.rightProgress);
        wrongProgress = findViewById(R.id.wrongProgress);
        imgQuestion = findViewById(R.id.imgQuestion);

        checkLayout = findViewById(R.id.checkLayout);
        btnTry = findViewById(R.id.btnTry);
        btnAnswer = findViewById(R.id.btnAnswer);
        tvNoConnection = findViewById(R.id.tvNoConnection);
        btnOpt1 = findViewById(R.id.btnOpt1);
        btnOpt2 = findViewById(R.id.btnOpt2);
        btnOpt3 = findViewById(R.id.btnOpt3);
        btnOpt4 = findViewById(R.id.btnOpt4);

        imgZoom = findViewById(R.id.imgZoom);

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


        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_ans_anim); // Change alpha from fully visible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the

        rightProgress.setMax(questionList.size());
        wrongProgress.setMax(questionList.size());

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
        if (Utils.isNetworkAvailable(BookmarkPlay.this)) {

            Collections.shuffle(questionList);
            playLayout.setVisibility(View.VISIBLE);
            nextQuizQuestion();
            checkLayout.setVisibility(View.GONE);
        } else {
            playLayout.setVisibility(View.GONE);
            checkLayout.setVisibility(View.VISIBLE);
        }
        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (question.getAnswer().equals(options.get(0).trim())) {
                    trueOption = "A";
                } else if (question.getAnswer().equals(options.get(1).trim())) {
                    trueOption = "B";
                } else if (question.getAnswer().equals(options.get(2).trim())) {
                    trueOption = "C";
                } else if (question.getAnswer().equals(options.get(3).trim())) {
                    trueOption = "D";
                }
                btnAnswer.setText(getString(R.string.true_ans) + trueOption);
            }
        });
    }

    private void nextQuizQuestion() {


        myCountDownTimer = new MyCountDownTimer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
            myCountDownTimer.start();
        } else {
            myCountDownTimer.start();
        }
        if (myCountDownTimer1 != null) {
            myCountDownTimer1.cancel();
        }
        Constant.LeftTime = 0;
        leftTime = 0;
        if (questionIndex >= questionList.size()) {
            CompleteQuestions();
        }
        btnAnswer.setText("Show Answer");
        layout_A.setBackgroundResource(R.drawable.answer_bg);
        layout_B.setBackgroundResource(R.drawable.answer_bg);
        layout_C.setBackgroundResource(R.drawable.answer_bg);
        layout_D.setBackgroundResource(R.drawable.answer_bg);
        layout_A.clearAnimation();
        layout_B.clearAnimation();
        layout_C.clearAnimation();
        layout_D.clearAnimation();


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
            tvQueNo.setText(++temp + "/" + questionList.size());
            if (!question.getImageUrl().isEmpty()) {
                imgZoom.setVisibility(View.VISIBLE);
                txtQuestion1.setVisibility(View.VISIBLE);
                txtQuestion.setVisibility(View.GONE);
                imgQuestion.setImageUrl(question.getImageUrl(), imageLoader);
                imgQuestion.setVisibility(View.VISIBLE);
                imgProgress.setVisibility(View.GONE);
                imgZoom.setOnClickListener(new View.OnClickListener() {
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

    public void PlayAreaLeaveDialog(final Activity context) {
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
        }
        if (myCountDownTimer1 != null) {
            myCountDownTimer1.cancel();

        }
        Constant.LeftTime = leftTime;

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Message
        alertDialog.setMessage(context.getResources().getString(R.string.exit_msg));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                ((MainActivity) context).getSupportFragmentManager().popBackStack();
                leftTime = 0;
                Constant.LeftTime = 0;
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
                if (Constant.LeftTime != 0) {
                    myCountDownTimer = new MyCountDownTimer(Constant.LeftTime, 1000);
                    myCountDownTimer.start();

                }
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            if (getApplicationContext() != null) {
                nextQuizQuestion();
            }
        }
    };

    public void BackButtonMethod() {
        onBackPressed();
        if (myCountDownTimer1 != null) {
            myCountDownTimer1.cancel();
        }
        // TODO Auto-generated method stub
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();

        }
        Constant.LeftTime = 0;
        leftTime = 0;
    }

    public void CheckSound() {
        if (Session.getSoundEnableDisable(getApplicationContext())) {
            Utils.backSoundonclick(getApplicationContext());
        }
        if (Session.getVibration(getApplicationContext())) {
            Utils.vibrate(getApplicationContext(), Utils.VIBRATION_DURATION);
        }
    }

    public void SettingButtonMethod() {
        CheckSound();
        if (myCountDownTimer1 != null) {
            myCountDownTimer1.cancel();
        }
        // TODO Auto-generated method stub
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();

        }
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
    }


    public class MyCountDownTimer extends CountDownTimer {

        private MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;

            int progress = (int) (millisUntilFinished / 1000);

            if (progressTimer == null) {
                progressTimer = (CircularProgressIndicator) findViewById(R.id.progressBarTwo);
            } else {
                progressTimer.setCurrentProgress(progress);
            }
            //when left last 5 second we show progress color red
            if (millisUntilFinished <= 6000) {
                progressTimer.SetTimerAttributes(Color.RED, Color.parseColor(Constant.PROGRESS_BG_COLOR), Color.RED, Constant.PROGRESS_TEXT_SIZE);
            } else {
                progressTimer.SetTimerAttributes(Color.parseColor(Constant.PROGRESS_COLOR), Color.parseColor(Constant.PROGRESS_BG_COLOR), Color.WHITE, Constant.PROGRESS_TEXT_SIZE);
            }
        }

        @Override
        public void onFinish() {
            if (questionIndex >= questionList.size()) {
                CompleteQuestions();

            } else {

                //WrongQuestion();
                mHandler.postDelayed(mUpdateUITimerTask, 100);
                questionIndex++;
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
            layout_C.setClickable(false);
            layout_D.setClickable(false);
            Constant.LeftTime = 0;
            switch (v.getId()) {
                case R.id.a_layout:

                    if (btnOpt1.getText().toString().trim().equalsIgnoreCase(question.getAnswer().trim())) {
                        questionIndex++;

                        layout_A.setBackgroundResource(R.drawable.right_gradient);
                        layout_A.startAnimation(animation);
                        layout_B.setBackgroundResource(R.drawable.answer_bg);
                        layout_C.setBackgroundResource(R.drawable.answer_bg);
                        layout_D.setBackgroundResource(R.drawable.answer_bg);
                        addScore();
                    } else if (!btnOpt1.getText().toString().trim().equalsIgnoreCase(question.getAnswer().trim())) {
                        WrongQuestion();
                        layout_A.setBackgroundResource(R.drawable.wrong_gradient);
                        String trueAns = question.getAnswer().trim();
                        if (btnOpt2.getText().toString().trim().equals(trueAns)) {
                            layout_B.startAnimation(animation);
                            layout_B.setBackgroundResource(R.drawable.right_gradient);
                            layout_C.setBackgroundResource(R.drawable.answer_bg);
                            layout_D.setBackgroundResource(R.drawable.answer_bg);


                        } else if (btnOpt3.getText().toString().trim().equals(trueAns)) {
                            layout_C.setBackgroundResource(R.drawable.right_gradient);
                            layout_C.startAnimation(animation);
                            layout_B.setBackgroundResource(R.drawable.answer_bg);
                            layout_D.setBackgroundResource(R.drawable.answer_bg);

                        } else if (btnOpt4.getText().toString().trim().equals(trueAns)) {
                            layout_D.setBackgroundResource(R.drawable.right_gradient);
                            layout_D.startAnimation(animation);
                            layout_B.setBackgroundResource(R.drawable.answer_bg);
                            layout_C.setBackgroundResource(R.drawable.answer_bg);
                        }


                        questionIndex++;
                    }

                    if (myCountDownTimer != null) {
                        myCountDownTimer.cancel();
                    }
                    if (myCountDownTimer1 != null) {
                        myCountDownTimer1.cancel();
                        leftTime = 0;
                    }
                    mHandler.postDelayed(mUpdateUITimerTask, 1000);
                    break;

                case R.id.b_layout:

                    if (btnOpt2.getText().toString().trim().equalsIgnoreCase(question.getAnswer().trim())) {
                        questionIndex++;

                        layout_B.setBackgroundResource(R.drawable.right_gradient);
                        layout_B.startAnimation(animation);
                        layout_A.setBackgroundResource(R.drawable.answer_bg);
                        layout_C.setBackgroundResource(R.drawable.answer_bg);
                        layout_D.setBackgroundResource(R.drawable.answer_bg);
                        addScore();
                    } else if (!btnOpt2.getText().toString().trim().equalsIgnoreCase(question.getAnswer().trim())) {
                        WrongQuestion();
                        String trueAns = question.getAnswer().trim();
                        layout_B.setBackgroundResource(R.drawable.wrong_gradient);


                        if (btnOpt1.getText().toString().trim().equals(trueAns)) {
                            layout_A.startAnimation(animation);
                            layout_A.setBackgroundResource(R.drawable.right_gradient);
                            layout_C.setBackgroundResource(R.drawable.answer_bg);
                            layout_D.setBackgroundResource(R.drawable.answer_bg);

                        } else if (btnOpt3.getText().toString().trim().equals(trueAns)) {
                            layout_C.setBackgroundResource(R.drawable.right_gradient);
                            layout_C.startAnimation(animation);
                            layout_A.setBackgroundResource(R.drawable.answer_bg);
                            layout_D.setBackgroundResource(R.drawable.answer_bg);

                        } else if (btnOpt4.getText().toString().trim().equals(trueAns)) {
                            layout_D.setBackgroundResource(R.drawable.right_gradient);
                            layout_D.startAnimation(animation);
                            layout_A.setBackgroundResource(R.drawable.answer_bg);
                            layout_C.setBackgroundResource(R.drawable.answer_bg);
                        }

                        questionIndex++;
                    }

                    if (myCountDownTimer != null) {
                        myCountDownTimer.cancel();
                    }
                    if (myCountDownTimer1 != null) {
                        myCountDownTimer1.cancel();
                        leftTime = 0;
                    }
                    mHandler.postDelayed(mUpdateUITimerTask, 1000);
                    break;
                case R.id.c_layout:

                    if (btnOpt3.getText().toString().trim().equalsIgnoreCase(question.getAnswer().trim())) {
                        questionIndex++;

                        layout_C.setBackgroundResource(R.drawable.right_gradient);
                        layout_C.startAnimation(animation);
                        layout_A.setBackgroundResource(R.drawable.answer_bg);
                        layout_B.setBackgroundResource(R.drawable.answer_bg);
                        layout_D.setBackgroundResource(R.drawable.answer_bg);
                        addScore();
                    } else if (!btnOpt3.getText().toString().trim().equalsIgnoreCase(question.getAnswer().trim())) {
                        layout_C.setBackgroundResource(R.drawable.wrong_gradient);
                        String trueAns = question.getAnswer().trim();
                        WrongQuestion();

                        if (btnOpt1.getText().toString().trim().equals(trueAns)) {
                            layout_A.startAnimation(animation);
                            layout_A.setBackgroundResource(R.drawable.right_gradient);
                            layout_B.setBackgroundResource(R.drawable.answer_bg);
                            layout_D.setBackgroundResource(R.drawable.answer_bg);

                        } else if (btnOpt2.getText().toString().trim().equals(trueAns)) {
                            layout_B.startAnimation(animation);
                            layout_B.setBackgroundResource(R.drawable.right_gradient);
                            layout_A.setBackgroundResource(R.drawable.answer_bg);
                            layout_D.setBackgroundResource(R.drawable.answer_bg);

                        } else if (btnOpt4.getText().toString().trim().equals(trueAns)) {
                            layout_D.setBackgroundResource(R.drawable.right_gradient);
                            layout_D.startAnimation(animation);
                            layout_A.setBackgroundResource(R.drawable.answer_bg);
                            layout_B.setBackgroundResource(R.drawable.answer_bg);
                        }
                        questionIndex++;
                    }
                    if (myCountDownTimer != null) {
                        myCountDownTimer.cancel();
                    }
                    if (myCountDownTimer1 != null) {
                        myCountDownTimer1.cancel();
                        leftTime = 0;
                    }
                    mHandler.postDelayed(mUpdateUITimerTask, 1000);
                    break;
                case R.id.d_layout:

                    // AnswerButtonClickMethod(layout_D, btnOpt4);
                    if (btnOpt4.getText().toString().trim().equalsIgnoreCase(question.getAnswer().trim())) {
                        layout_D.setBackgroundResource(R.drawable.right_gradient);
                        layout_D.startAnimation(animation);
                        questionIndex++;
                        layout_A.setBackgroundResource(R.drawable.answer_bg);
                        layout_B.setBackgroundResource(R.drawable.answer_bg);
                        layout_C.setBackgroundResource(R.drawable.answer_bg);
                        addScore();

                    } else if (!btnOpt4.getText().toString().trim().equalsIgnoreCase(question.getAnswer().trim())) {
                        WrongQuestion();
                        layout_D.setBackgroundResource(R.drawable.wrong_gradient);
                        String trueAns = question.getAnswer().trim();
                        if (btnOpt1.getText().toString().trim().equals(trueAns)) {
                            layout_A.startAnimation(animation);
                            layout_A.setBackgroundResource(R.drawable.right_gradient);
                            layout_B.setBackgroundResource(R.drawable.answer_bg);
                            layout_C.setBackgroundResource(R.drawable.answer_bg);

                        } else if (btnOpt2.getText().toString().trim().equals(trueAns)) {
                            layout_B.startAnimation(animation);
                            layout_B.setBackgroundResource(R.drawable.right_gradient);
                            layout_A.setBackgroundResource(R.drawable.answer_bg);
                            layout_C.setBackgroundResource(R.drawable.answer_bg);

                        } else if (btnOpt3.getText().toString().trim().equals(trueAns)) {
                            layout_C.setBackgroundResource(R.drawable.right_gradient);
                            layout_C.startAnimation(animation);
                            layout_A.setBackgroundResource(R.drawable.answer_bg);
                            layout_B.setBackgroundResource(R.drawable.answer_bg);

                        }
                        questionIndex++;
                    }
                    if (myCountDownTimer != null) {
                        myCountDownTimer.cancel();
                    }
                    if (myCountDownTimer1 != null) {
                        myCountDownTimer1.cancel();

                    }
                    mHandler.postDelayed(mUpdateUITimerTask, 1000);
                    break;
            }

        }
    }


    @Override
    public void onResume() {

        if (leftTime != 0) {
            myCountDownTimer1 = new MyCountDownTimer(leftTime, Constant.COUNT_DOWN_TIMER);
            myCountDownTimer1.start();

        }
        super.onResume();


    }

    public void CompleteQuestions() {

        playLayout.setVisibility(View.GONE);
        checkLayout.setVisibility(View.VISIBLE);
        tvNoConnection.setText(getString(R.string.all_complete_msg));

    }

    private void addScore() {

        correctQuestion++;
        txtTrueQuestion.setText(String.valueOf(correctQuestion));
        rightProgress.setProgress(correctQuestion);

    }

    private void WrongQuestion() {

        inCorrectQuestion++;
        txtFalseQuestion.setText(String.valueOf(inCorrectQuestion));
        wrongProgress.setProgress(inCorrectQuestion);

    }

    @Override
    public void onPause() {
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
        }
        if (myCountDownTimer1 != null) {
            myCountDownTimer1.cancel();
        }
        super.onPause();
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
        //  menu.findItem(R.id.setting).setVisible(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.setting:
                SettingButtonMethod();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
