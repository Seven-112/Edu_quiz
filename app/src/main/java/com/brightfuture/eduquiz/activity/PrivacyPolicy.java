package com.brightfuture.eduquiz.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PrivacyPolicy extends AppCompatActivity {


    public ProgressBar prgLoading;
    public WebView mWebView;
    public String type;
    public Toolbar toolbar;


    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        RelativeLayout mainLayout = findViewById(R.id.mainLayout);
        Utils.transparentStatusAndNavigation(PrivacyPolicy.this);
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
        type = getIntent().getStringExtra("type");
        prgLoading = findViewById(R.id.prgLoading);
        mWebView = findViewById(R.id.webView1);

        try {
            if (Utils.isNetworkAvailable(this)) {
                mWebView.setClickable(true);
                mWebView.setFocusableInTouchMode(true);
                mWebView.getSettings().setJavaScriptEnabled(true);
                if (type.equals("privacy")) {
                    getSupportActionBar().setTitle(getString(R.string.privacy_policy));
                    GetPrivacyAndTerms(Constant.getPrivacy);
                } else if (type.equals("terms")) {
                    getSupportActionBar().setTitle(getString(R.string.terms));
                    GetPrivacyAndTerms(Constant.getTerms);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetPrivacyAndTerms(final String api) {
        if (!prgLoading.isShown()) {
            prgLoading.setVisibility(View.VISIBLE);
        }
        StringRequest strReq = new StringRequest(Request.Method.POST, Constant.QUIZ_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("error").equals("false")) {
                        String privacyStr = obj.getString("data");
                        mWebView.setVerticalScrollBarEnabled(true);
                        mWebView.loadDataWithBaseURL("", privacyStr, "text/html", "UTF-8", "");
                        mWebView.setBackgroundColor(getResources().getColor(R.color.white));

                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                    prgLoading.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prgLoading.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(api, "1");
                return params;
            }
        };

        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(strReq);

    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        finish();
        super.onBackPressed();

    }
}
