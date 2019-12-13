package com.brightfuture.eduquiz.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.CircularProgressIndicator2;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.helper.Utils;

import com.brightfuture.eduquiz.model.Question;
import com.brightfuture.eduquiz.model.Review;
import com.google.android.gms.ads.AdListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LearningCompletedActivity extends AppCompatActivity implements android.view.View.OnClickListener {

    public Toolbar toolbar;

    public Button btnPlayAgain, btnShare, btnRateUs, btnQuite, btnReview, btnPdf;
    public TextView txt_result_title, txt_score, txtLevelTotalScore, txt_right, txt_wrong, point, coin_count;

    public CircularProgressIndicator2 result_prog;
    int levelNo = 1,
            lastLevelScore = 0,
            coin = 0,
            totalScore = 0;

    public PlayActivity playActivity;
    public LevelActivity levelActivity;
    boolean isLevelCompleted;
    public RelativeLayout mainLayout;
    public String fromQue ,sub_cat_name,cat_name;
    public int sub_cat_id,qstn_cnt;
    public Context context;
    AlertDialog alertDialog;
    Button btnView, btnShare1, btnCancel;
    ProgressBar progressBar;
    public static final String[] WRITE_EXTERNAL_STORAGE_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_completed);
        mainLayout = findViewById(R.id.mainLayout);
        Utils.transparentStatusAndNavigation(LearningCompletedActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sub_cat_id = getIntent().getIntExtra("sub_cat_id",0);
        sub_cat_name = getIntent().getStringExtra("sub_cat_name");
        cat_name = getIntent().getStringExtra("cat_name");
        qstn_cnt = getIntent().getIntExtra("qstn_cnt", 0);
        context = LearningCompletedActivity.this;
        final int[] CLICKABLE = new int[]{R.id.btn_playagain, R.id.btn_share, R.id.btn_quite};
        for (int i : CLICKABLE) {
            findViewById(i).setOnClickListener(this);
        }
        playActivity = new PlayActivity();
        levelActivity = new LevelActivity();


        Utils.loadAd(LearningCompletedActivity.this);



        btnPdf = findViewById(R.id.btnPdf);
        coin = Session.getPoint(LearningCompletedActivity.this);
        totalScore = Session.getScore(LearningCompletedActivity.this);

        lastLevelScore = Session.getLastLevelScore(LearningCompletedActivity.this);


        btnPlayAgain = findViewById(R.id.btn_playagain);
        btnRateUs = findViewById(R.id.btn_rate);
        btnQuite = findViewById(R.id.btn_quite);
        btnReview = findViewById(R.id.btn_review);
        btnPlayAgain.setOnClickListener(this);
        btnRateUs.setOnClickListener(this);
        btnQuite.setOnClickListener(this);
        btnReview.setOnClickListener(this);
        btnPdf.setOnClickListener(this);
        if (PlayActivity.reviews.size() == 0) {
            btnReview.setVisibility(View.GONE);
        } else {
            btnReview.setVisibility(View.VISIBLE);
        }
        btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        //isLevelCompleted = Session.isLevelCompleted(LearningCompletedActivity.this);
      //  levelNo = MainActivity.dbHelper.GetLevelById(Constant.CATE_ID, Constant.SUB_CAT_ID);



        Utils.interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Utils.loadAd(LearningCompletedActivity.this);
            }
        });
        if (Session.isLogin(LearningCompletedActivity.this))
            getUserScore();

    }

    public static float getPercentageCorrect(int questions, int correct) {
        float proportionCorrect = ((float) correct) / ((float) questions);
        return proportionCorrect * 100;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_playagain:
                PlayActivity.reviews.clear();

                Intent intent = new Intent(LearningCompletedActivity.this, LearnMcqActivity.class);
                intent.putExtra("sub_cat_id", sub_cat_id);
                intent.putExtra("sub_cat_name", sub_cat_name);
                intent.putExtra("cat_name", cat_name);
                intent.putExtra("qstn_cnt", qstn_cnt);
                startActivity(intent);
                ((LearningCompletedActivity) context).finish();

                break;
            case R.id.btn_share:
                Utils.displayInterstitial();
                shareClicked();
                break;

            case R.id.btnPdf:
                Utils.displayInterstitial();
                PdfClicked();
                break;
            case R.id.btn_rate:
                Utils.displayInterstitial();
                rateClicked();
                break;
            case R.id.btn_quite:
                //finish();
                Intent intent1 = new Intent(LearningCompletedActivity.this, MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("type", "default");
                startActivity(intent1);
                break;
            case R.id.btn_review:

                Intent intentReview = new Intent(LearningCompletedActivity.this, ReviewActivity.class);
                startActivity(intentReview);

                break;
            default:
                break;
        }
    }


    public void shareClicked() {
        final String sharetext = "I have finished Learning : " + sub_cat_name+" on " + getString(R.string.app_name);
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, "" + sharetext + " " + Constant.PLAY_STORE_URL + getPackageName());
        startActivity(Intent.createChooser(share, "Share " + getString(R.string.app_name) + "!"));
    }

    private void rateClicked() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Constant.PLAY_STORE_URL + getPackageName())));
        }
    }


    public void getUserScore() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject object = obj.getJSONObject(Constant.DATA);

                                txt_score.setText(object.getString(Constant.SCORE));
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
                params.put(Constant.getUserScore, "1");
                params.put(Constant.userId, Session.getUserData(Session.USER_ID, LearningCompletedActivity.this));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date());
                params.put(Constant.DATE, date);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void PdfClicked() {
        if (ContextCompat.checkSelfPermission(LearningCompletedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(LearningCompletedActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LearningCompletedActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, 0);
        } else {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(LearningCompletedActivity.this);
            LayoutInflater inflater1 = (LayoutInflater) (LearningCompletedActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            assert inflater1 != null;
            @SuppressLint("InflateParams") final View dialogView = inflater1.inflate(R.layout.progress_dialog, null);
            dialog.setView(dialogView);
            progressBar = (ProgressBar) dialogView.findViewById(R.id.progressBar);
            btnView = (Button) dialogView.findViewById(R.id.btnView);
            btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
            btnShare1 = (Button) dialogView.findViewById(R.id.btnShare);
            btnShare1.setVisibility(View.INVISIBLE);
            btnView.setVisibility(View.INVISIBLE);

            progressBar.setMax(100);
            alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name) + "/report.pdf");
                    Uri uri = FileProvider.getUriForFile(LearningCompletedActivity.this, getPackageName() + ".provider", file);
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(uri, "application/pdf");
                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    try {
                        startActivity(Intent.createChooser(target, "Open File"));
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        // Instruct the user to install a PDF reader here, or something
                    }
                    alertDialog.dismiss();
                }
            });
            btnShare1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name) + "/report.pdf");
                    // Uri uri = Uri.fromFile(file);
                    Uri uri = FileProvider.getUriForFile(LearningCompletedActivity.this, getPackageName() + ".provider", file);
                    String sharetext = "I have finished Level No : " + Utils.RequestlevelNo + " with " + lastLevelScore + " Score in " + getString(R.string.app_name);
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.setType("application/pdf");
                    share.putExtra(Intent.EXTRA_TEXT, "" + sharetext + "  " + Constant.PLAY_STORE_URL + getPackageName());
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share"));
                    alertDialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            if (getApplicationContext() != null)
                new GeneratePdf().execute();
        }
    }

    /*
     * Generate pdf for current level question
     */
    public void GeneratePdf(String dest) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (new File(dest).exists()) {
            new File(dest).delete();
        }

        try {
            /*
             * Creating Document
             */
            final Document document = new Document();

            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(dest));

            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("WRTEAM");
            document.addCreator("WRTEAM");

            /*
             * Variables for further use....
             */
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 15.0f;
            final float mValueFontSize = 10.0f;

            /*
             * How to USE FONT....
             */
            BaseFont bf = null;
            try {
                bf = BaseFont.createFont("assets/FreeSans.ttf", BaseFont.IDENTITY_H, true);
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final Font unicode = new Font(bf, 20.0f, Font.NORMAL, BaseColor.BLACK);
            //final BaseFont urName = BaseFont.createFont("assets/fonts/centarell.ttf", "UTF-8", BaseFont.EMBEDDED);

            // LINE SEPARATOR
            final LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Title Order Details...
            // Adding Title....
            //Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunk = new Chunk("Online Quiz", unicode);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph);


            //here use loop for add questionList in page
            for (int i = 0; i < LearnMcqActivity.questionList.size(); i++) {
                final Question review = LearnMcqActivity.questionList.get(i);
                Font mOrderAcNameFont1 = new Font(bf, mHeadingFontSize, Font.NORMAL, mColorAccent);
                Chunk mOrderAcNameChunk1 = new Chunk(("" + (i + 1) + ". " + Html.fromHtml(review.getQuestion())), mOrderAcNameFont1);
                Paragraph mOrderAcNameParagraph1 = new Paragraph(mOrderAcNameChunk1);
                document.add(mOrderAcNameParagraph1);
                try {
                    if (!review.getImage().isEmpty() || review.getImage() != null) {
                        String imageUrl = review.getImage();

                        Image image2 = Image.getInstance(new URL(imageUrl));
                        image2.scaleAbsolute(100f, 100f);
                        document.add(image2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                QuestionOption(document, bf, mValueFontSize, review, lineSeparator);
            }

            document.close();


            //FileUtils.openFile(getActivity(), new File(dest));

        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error", "" + ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(LearningCompletedActivity.this, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }


    public void QuestionOption(Document document, BaseFont urName, float mValueFontSize, Question review, LineSeparator lineSeparator) {
        try {
            Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk = new Chunk("(a) " + Html.fromHtml(review.getOptions().get(0)), mOrderDateValueFont);
            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
            document.add(mOrderDateValueParagraph);

            Font mOrderDateValueFont1 = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk1 = new Chunk("(b) " + Html.fromHtml(review.getOptions().get(1)), mOrderDateValueFont1);
            Paragraph mOrderDateValueParagraph1 = new Paragraph(mOrderDateValueChunk1);
            document.add(mOrderDateValueParagraph1);

            Font mOrderDateValueFont2 = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk2 = new Chunk("(c) " + Html.fromHtml(review.getOptions().get(2)), mOrderDateValueFont2);
            Paragraph mOrderDateValueParagraph2 = new Paragraph(mOrderDateValueChunk2);
            document.add(mOrderDateValueParagraph2);

            Font mOrderDateValueFont3 = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk3 = new Chunk("(d) " + Html.fromHtml(review.getOptions().get(3)), mOrderDateValueFont3);
            Paragraph mOrderDateValueParagraph3 = new Paragraph(mOrderDateValueChunk3);
            document.add(mOrderDateValueParagraph3);

            Font mOrderDateValueFont4 = new Font(urName, mValueFontSize, Font.NORMAL, new BaseColor(139, 0, 0));
            Chunk mOrderDateValueChunk4 = new Chunk("True Answer  : " + Html.fromHtml(review.getTrueAns()), mOrderDateValueFont4);
            mOrderDateValueChunk4.setUnderline(0.1f, -2f);
            Paragraph mOrderDateValueParagraph4 = new Paragraph(mOrderDateValueChunk4);
            document.add(mOrderDateValueParagraph4);

            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));
        } catch (DocumentException ie) {
            Log.e("createPdf: Error", "" + ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(LearningCompletedActivity.this, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GeneratePdf extends AsyncTask<String, Void, String> {
        @Override
        public void onPreExecute() {
            progressBar.setIndeterminate(true);
        }

        @Override
        protected String doInBackground(String... params) {
            GeneratePdf(getAppPath(Objects.requireNonNull(LearningCompletedActivity.this)) + "report.pdf");
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            btnShare1.setVisibility(View.VISIBLE);
            btnView.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(false);
            progressBar.setProgress(100);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    /**
     * Get Path of App which contains Files
     *
     * @return path of root dir
     */
    public static String getAppPath(Context context) {

        File dir = new File(android.os.Environment.getExternalStorageDirectory()
                + File.separator
                + context.getResources().getString(R.string.app_name)
                + File.separator);
        // Uri uri= FileProvider.getUriForFile(context, context.getPackageName()+".provider",dir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getPath() + File.separator;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.loadAd(LearningCompletedActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}