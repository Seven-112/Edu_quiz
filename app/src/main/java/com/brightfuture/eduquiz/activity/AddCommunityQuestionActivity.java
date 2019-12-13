package com.brightfuture.eduquiz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.brightfuture.eduquiz.helper.AppController.StopSound;
import static com.brightfuture.eduquiz.helper.AppController.getAppContext;

public class AddCommunityQuestionActivity extends AppCompatActivity {
    private Context mContext;
    private Dialog mCustomDialog;

    private TextView ok_btn,question,location;
    private String strQues,strLoc,user_id,name;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.activity_add_community_question);

        mContext = AddCommunityQuestionActivity.this;
        AppController.currentActivity = this;
        initViews();


    }


    private void initViews() {
       question=findViewById(R.id.etQuestion);
       location=findViewById(R.id.etLocation);


user_id=Session.getUserData(Session.USER_ID,AddCommunityQuestionActivity.this);
name=Session.getUserData(Constant.USER_NAME,AddCommunityQuestionActivity.this);


        ok_btn = (TextView) findViewById(R.id.ok);
        ok_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(validateText(question) && validateText(location))
                {
                    strQues=question.getText().toString();
                    strLoc=location.getText().toString();
                    addQuestion();
                }


            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:

                setTelephoneListener();

                break;
        }
    }

    private void setTelephoneListener() {
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    StopSound();
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    StopSound();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        TelephonyManager telephoneManager = (TelephonyManager) getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephoneManager != null) {
            telephoneManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        overridePendingTransition(R.anim.close_next, R.anim.open_next);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {

        if (mContext != null) {
            if (mCustomDialog != null) {
                mCustomDialog.dismiss();
                mCustomDialog = null;
            }

            mContext = null;
            super.onDestroy();
        }
    }

    private boolean validateText(TextView question) {



        String str_pass = question.getText().toString();
        if (TextUtils.isEmpty(str_pass)) {

            question.setError(getString(R.string.this_is_required));
            question.requestFocus();
            return false;
        } else {
            question.setError(null);
        }
        return true;
    }//validate

    public void addQuestion() {
      //  progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.GONE);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);

                            if (!error) {
                                Log.d("ADDCommunityACTIVITY","Added question");
                                Toast.makeText(AddCommunityQuestionActivity.this,"Question Added Succesfully",Toast.LENGTH_SHORT).show();
                                /*JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                Intent intent2 = new Intent(AddQuestionActivity.this, CommunityActivity.class);

                                startActivity(intent2);*/

                               /* for (int i = 0; i < jsonArray.length(); i++) {
                                    SubCategory subCate = new SubCategory();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    subCate.setId(object.getString(Constant.ID));
                                    subCate.setCategoryId(object.getString(Constant.MAIN_CATE_ID));
                                    subCate.setCategory_name(object.getString(Constant.CATEGORY_NAME));
                                    subCate.setName(object.getString(Constant.SUB_CATE_NAME));
                                    subCate.setImage(object.getString(Constant.IMAGE));
                                    subCate.setStatus(object.getString(Constant.STATUS));
                                    subCate.setMaxLevel(object.getString(Constant.MAX_LEVEL));

                                    subCate.setQuestion_count(object.getString(Constant.QUES_COUNT));
                                    int count= Integer.parseInt(object.getString(Constant.QUES_COUNT));
                                    if(count>0) {

                                    }
                                }*/
                                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                                finish();

                            }else{

                                Toast.makeText(AddCommunityQuestionActivity.this,"Something went wrong. Please try again.",Toast.LENGTH_SHORT).show();
                                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddCommunityQuestionActivity.this,"Something went wrong. Please try again.",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                      //  progressBar.setVisibility(View.GONE);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.addCommunityQuestion, "1");
                params.put(Constant.QUESTION, strQues);
                params.put(Constant.CATEGORY, "");
                params.put(Constant.SUBCATEGORY, "");
                params.put(Constant.USER_ID, user_id);
                params.put(Constant.USER_NAME, name);
                params.put(Constant.STATUS, "Active");
                params.put(Constant.TYPE, "normal");
                params.put(Constant.LOCATION, strLoc);




                System.out.println("params  " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void addAnswer() {
        //  progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.GONE);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);

                            if (!error) {
                                Log.d("ADDCommunityACTIVITY","Added Answer");
                                Toast.makeText(AddCommunityQuestionActivity.this,"Answer Added Succesfully",Toast.LENGTH_SHORT).show();
                                /*JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                Intent intent2 = new Intent(AddQuestionActivity.this, CommunityActivity.class);

                                startActivity(intent2);*/

                               /* for (int i = 0; i < jsonArray.length(); i++) {
                                    SubCategory subCate = new SubCategory();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    subCate.setId(object.getString(Constant.ID));
                                    subCate.setCategoryId(object.getString(Constant.MAIN_CATE_ID));
                                    subCate.setCategory_name(object.getString(Constant.CATEGORY_NAME));
                                    subCate.setName(object.getString(Constant.SUB_CATE_NAME));
                                    subCate.setImage(object.getString(Constant.IMAGE));
                                    subCate.setStatus(object.getString(Constant.STATUS));
                                    subCate.setMaxLevel(object.getString(Constant.MAX_LEVEL));

                                    subCate.setQuestion_count(object.getString(Constant.QUES_COUNT));
                                    int count= Integer.parseInt(object.getString(Constant.QUES_COUNT));
                                    if(count>0) {

                                    }
                                }*/
                                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                                finish();

                            }else{

                                Toast.makeText(AddCommunityQuestionActivity.this,"Something went wrong. Please try again.",Toast.LENGTH_SHORT).show();
                                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddCommunityQuestionActivity.this,"Something went wrong. Please try again.",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //  progressBar.setVisibility(View.GONE);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.addCommunityAnswer, "1");
                params.put(Constant.QUESTIONID, "7");
                params.put(Constant.ANSWER, strQues);
                params.put(Constant.USER_ID, user_id);
                params.put(Constant.USER_NAME, name);
                params.put(Constant.STATUS, "Active");
                params.put(Constant.TYPE, "normal");
                params.put(Constant.LOCATION, strLoc);




                System.out.println("params  " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
