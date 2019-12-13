package com.brightfuture.eduquiz.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.model.Review;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.brightfuture.eduquiz.activity.MainActivity.bookmarkDBHelper;


public class ReviewTestActivity extends AppCompatActivity {

    public TextView txtQuestion, txtQuestion1, btnOpt1, btnOpt2, btnOpt3, btnOpt4, tvQuestionNo;
    public ImageView prev, next;
    NetworkImageView imgQuestion;
    public RelativeLayout layout_A, layout_B, layout_C, layout_D;
    private int questionIndex = 0;

    private int NO_OF_QUESTION;
    public Button btnSolution;
    public TextView tvSolution;
    public CardView cardView;
    public ArrayList<Review> reviews ;
    AlertDialog alertDialog;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ProgressBar imgProgress;
    RelativeLayout fabLayout;
    public Toolbar toolbar;
    public Menu menu;
    ArrayList<String> options;
    public ScrollView mainScroll, queScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_test);
        RelativeLayout mainLayout = findViewById(R.id.mainLayout);
        Utils.transparentStatusAndNavigation(ReviewTestActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.review_answer));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnOpt1 = findViewById(R.id.btnOpt1);
        btnOpt2 = findViewById(R.id.btnOpt2);
        btnOpt3 = findViewById(R.id.btnOpt3);
        btnOpt4 = findViewById(R.id.btnOpt4);
        txtQuestion = findViewById(R.id.question);
        txtQuestion1 = findViewById(R.id.question1);

        tvSolution = findViewById(R.id.tvSolution);
        tvQuestionNo = findViewById(R.id.questionNo);

        imgQuestion = findViewById(R.id.imgQuestion);
        imgProgress = findViewById(R.id.imgProgress);
        mainScroll = findViewById(R.id.mainScroll);
        queScroll = findViewById(R.id.queScroll);

        fabLayout = findViewById(R.id.fabLayout);
        layout_A = findViewById(R.id.a_layout);
        layout_B = findViewById(R.id.b_layout);
        layout_C = findViewById(R.id.c_layout);
        layout_D = findViewById(R.id.d_layout);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);

        btnSolution = findViewById(R.id.btnSolution);
        cardView = findViewById(R.id.cardView1);

        reviews = TestMcqActivity.reviews;
        Utils.displayInterstitial();
        ReviewQuestion();
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionIndex > 0) {
                    questionIndex--;
                    ReviewQuestion();
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionIndex < (reviews.size() - 1)) {
                    questionIndex++;
                    ReviewQuestion();
                }
            }
        });

        fabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.btnClick(view, ReviewTestActivity.this);
                Intent intent = new Intent(ReviewTestActivity.this, BookmarkList.class);
                startActivity(intent);
            }
        });

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
    }

    public void ReportDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ReviewTestActivity.this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.report_dialog, null);
        dialog.setView(dialogView);
        TextView tvReport = dialogView.findViewById(R.id.tvReport);
        TextView cancel = dialogView.findViewById(R.id.cancel);
        final EditText edtReport = dialogView.findViewById(R.id.edtReport);
        final TextInputLayout txtInputReport = dialogView.findViewById(R.id.txtInputReport);
        TextView tvQuestion = dialogView.findViewById(R.id.tvQuestion);
        tvQuestion.setText("Que : " + Html.fromHtml(reviews.get(questionIndex).getQuestion()));
        alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtReport.getText().toString().isEmpty()) {
                    ReportQuestion(edtReport.getText().toString());
                    txtInputReport.setError(null);
                } else {
                    txtInputReport.setError("Please fill all the data and submit!");
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    public void ReportQuestion(final String message) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("----report res  " + response);
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");
                            if (!error) {
                                alertDialog.dismiss();
                                Toast.makeText(ReviewTestActivity.this, message, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ReviewTestActivity.this, message, Toast.LENGTH_LONG).show();
                                System.out.println(" empty msg " + message);
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
                params.put(Constant.reportQuestion, "1");
                params.put(Constant.questionId, "" + reviews.get(questionIndex).getQueId());
                params.put(Constant.messageReport, message);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void ReviewQuestion() {
        if (questionIndex < reviews.size()) {
            txtQuestion.setText(Html.fromHtml(reviews.get(questionIndex).getQuestion()));
            txtQuestion1.setText(Html.fromHtml(reviews.get(questionIndex).getQuestion()));
            options = new ArrayList<String>();
            options.addAll(reviews.get(questionIndex).getOptionList());
            Collections.shuffle(options);
            btnOpt1.setText(Html.fromHtml(options.get(0).trim()));
            btnOpt2.setText(Html.fromHtml(options.get(1).trim()));
            btnOpt3.setText(Html.fromHtml(options.get(2).trim()));
            btnOpt4.setText(Html.fromHtml(options.get(3).trim()));
            layout_A.setBackgroundResource(R.drawable.answer_bg);
            layout_B.setBackgroundResource(R.drawable.answer_bg);
            layout_C.setBackgroundResource(R.drawable.answer_bg);
            layout_D.setBackgroundResource(R.drawable.answer_bg);
            tvQuestionNo.setText(" " + (questionIndex + 1) + "/" + reviews.size());

            if (reviews.get(questionIndex).getExtraNote().isEmpty()) {
                btnSolution.setVisibility(View.GONE);
            } else {
                btnSolution.setVisibility(View.VISIBLE);
            }
            invalidateOptionsMenu();
            tvSolution.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
            btnSolution.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String solution = reviews.get(questionIndex).getExtraNote();
                    cardView.setVisibility(View.VISIBLE);
                    tvSolution.setVisibility(View.VISIBLE);
                    tvSolution.setText(solution);

                }
            });


            if (!reviews.get(questionIndex).getImgUrl().isEmpty()) {
                txtQuestion1.setVisibility(View.GONE);
                txtQuestion.setVisibility(View.VISIBLE);
                imgQuestion.setImageUrl(reviews.get(questionIndex).getImgUrl(), imageLoader);
                imgQuestion.setVisibility(View.VISIBLE);
                imgProgress.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(reviews.get(questionIndex).getImgUrl())
                        .into(imgQuestion, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        imgProgress.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        imgProgress.setVisibility(View.GONE);
                                    }
                                }
                        );
            } else {
                imgQuestion.setVisibility(View.GONE);
                txtQuestion1.setVisibility(View.VISIBLE);
                txtQuestion.setVisibility(View.GONE);
            }

            String wrongAns = reviews.get(questionIndex).getWrongAns().trim();
            String rightAns = reviews.get(questionIndex).getRightAns().trim();


            if (btnOpt1.getText().toString().equalsIgnoreCase(rightAns)) {

                layout_A.setBackgroundResource(R.drawable.right_gradient);

                if (btnOpt2.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_B.setBackgroundResource(R.drawable.wrong_gradient);
                } else if (btnOpt3.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_C.setBackgroundResource(R.drawable.wrong_gradient);
                } else if (btnOpt4.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_D.setBackgroundResource(R.drawable.wrong_gradient);
                }

            } else if (btnOpt2.getText().toString().equalsIgnoreCase(rightAns)) {

                layout_B.setBackgroundResource(R.drawable.right_gradient);

                if (btnOpt1.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_A.setBackgroundResource(R.drawable.wrong_gradient);
                } else if (btnOpt3.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_C.setBackgroundResource(R.drawable.wrong_gradient);
                } else if (btnOpt4.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_D.setBackgroundResource(R.drawable.wrong_gradient);
                }

            } else if (btnOpt3.getText().toString().equalsIgnoreCase(rightAns)) {

                layout_C.setBackgroundResource(R.drawable.right_gradient);

                if (btnOpt1.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_A.setBackgroundResource(R.drawable.wrong_gradient);
                } else if (btnOpt2.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_B.setBackgroundResource(R.drawable.wrong_gradient);
                } else if (btnOpt4.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_D.setBackgroundResource(R.drawable.wrong_gradient);
                }

            } else if (btnOpt4.getText().toString().equalsIgnoreCase(rightAns)) {
                layout_D.setBackgroundResource(R.drawable.right_gradient);

                if (btnOpt1.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_A.setBackgroundResource(R.drawable.wrong_gradient);
                } else if (btnOpt2.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_B.setBackgroundResource(R.drawable.wrong_gradient);
                } else if (btnOpt3.getText().toString().equalsIgnoreCase(wrongAns)) {
                    layout_C.setBackgroundResource(R.drawable.wrong_gradient);


                }
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.setting).setVisible(false);
        final MenuItem menuItem = menu.findItem(R.id.bookmark);
        menuItem.setTitle("unmark");
        int isfav = 0;// bookmarkDBHelper.getBookmarks(reviews.get(questionIndex).getQueId());

      /*  if (isfav == reviews.get(questionIndex).getQueId()) {
            menuItem.setIcon(R.drawable.ic_mark);
            menuItem.setTitle("mark");
        } else {
            menuItem.setIcon(R.drawable.ic_unmark);
            menuItem.setTitle("unmark");
        }*/
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.bookmark:
                Review review = reviews.get(questionIndex);
                if (menuItem.getTitle().equals("unmark")) {
                    String solution = reviews.get(questionIndex).getExtraNote();
                    MainActivity.bookmarkDBHelper.insertIntoDB(review.getQueId(),
                            review.getQuestion(),
                            review.getRightAns(),
                            solution,
                            review.getImgUrl(),
                            options.get(0).trim(),
                            options.get(1).trim(),
                            options.get(2).trim(),
                            options.get(3).trim());
                    menuItem.setIcon(R.drawable.ic_mark);
                    menuItem.setTitle("mark");
                } else {
                    MainActivity.bookmarkDBHelper.delete_id(reviews.get(questionIndex).getQueId());
                    menuItem.setIcon(R.drawable.ic_unmark);
                    menuItem.setTitle("unmark");
                }
                return true;

            case R.id.report:
                ReportDialog();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
