package com.brightfuture.eduquiz.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.model.Category;
import com.brightfuture.eduquiz.model.SubCategory;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.itextpdf.text.factories.GreekAlphabetFactory.getString;

public class AddQuestionActivity extends AppCompatActivity {
    public RelativeLayout mainLayout;
    Toolbar toolbar;
    Spinner cat,subcat,options;
    TextView question,ans1,ans2,ans3,ans4;
    TextInputLayout tiQues,tiAns1,tiAns2,tiAns3,tiAns4;
    ProgressBar progressBar;
    ArrayList<Category> categoryList;
    ArrayList<SubCategory> subcatList;
    String catid,subcatid,quest,answer1,answer2,answer3,answer4,rightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_question);
        mainLayout = findViewById(R.id.mainLayout);
        Utils.transparentStatusAndNavigation(AddQuestionActivity.this);
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
        getSupportActionBar().setTitle("Add Question");

        init();
    }

    private void init() {

        cat=findViewById(R.id.spCat);
        subcat=findViewById(R.id.spSubCat);
        options=findViewById(R.id.spOptions);
        question=findViewById(R.id.etQuestion);
        ans1=findViewById(R.id.etAns1);
        ans2=findViewById(R.id.etAns2);
        ans3=findViewById(R.id.etAns3);
        ans4=findViewById(R.id.etAns4);
        tiQues=findViewById(R.id.tiQuestion);
        tiAns1=findViewById(R.id.tiAns1);
        tiAns2=findViewById(R.id.tiAns2);
        tiAns3=findViewById(R.id.tiAns3);
        tiAns4=findViewById(R.id.tiAns4);
        progressBar=findViewById(R.id.progressBar);
        categoryList= new ArrayList<>();
        subcatList= new ArrayList<>();

        getMainCategoryFromJson();

        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(categoryList!=null && categoryList.size()>0) {
                    catid = categoryList.get(i).getId();
                    getSubCategoryFromJson(catid);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void addQuestion(View view) {

      if(validateText(question,tiQues) && validateText(ans1,tiAns1) && validateText(ans2,tiAns2)
              && validateText(ans3,tiAns3)&& validateText(ans4,tiAns4))
      {
          int catpos=cat.getSelectedItemPosition();
          catid=categoryList.get(catpos).getId();

          int subcatpos=subcat.getSelectedItemPosition();
          subcatid= subcatList.get(subcatpos).getId();

          quest= question.getText().toString();
          answer1= ans1.getText().toString();
          answer2=ans2.getText().toString();
          answer3=ans3.getText().toString();
          answer4=ans4.getText().toString();

          rightAnswer=options.getSelectedItem().toString();
          addQuestion();
      }

    }

    private boolean validateText(TextView question, TextInputLayout tiQues) {



        String str_pass = question.getText().toString();
        if (TextUtils.isEmpty(str_pass)) {

            question.setError(getString(R.string.this_is_required));
            question.requestFocus();
            return false;
        } else {
                tiQues.setErrorEnabled(false);

        }
        return true;
    }//validate


    public void addQuestion() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);

                            if (!error) {
                                Log.d("ADDQUESTIONACTIVITY","Added question");
                               Toast.makeText(AddQuestionActivity.this,"Question Added Succesfully",Toast.LENGTH_SHORT).show();
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                Intent intent2 = new Intent(AddQuestionActivity.this, CommunityActivity.class);

                                startActivity(intent2);

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


                            }else{

                                Toast.makeText(AddQuestionActivity.this,"Something went wrong. Please try again.",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddQuestionActivity.this,"Something went wrong. Please try again.",Toast.LENGTH_SHORT).show();
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
                params.put(Constant.addQuestion, "1");
                params.put(Constant.QUESTION, quest);
                params.put(Constant.CATEGORY, catid);
                params.put(Constant.SUBCATEGORY, subcatid);
                params.put(Constant.A, answer1);
                params.put(Constant.B, answer2);
                params.put(Constant.C, answer3);
                params.put(Constant.D, answer4);
                params.put(Constant.LEVEL, "1");
                params.put(Constant.NOTE, "new");
                params.put(Constant.ANSWER, rightAnswer);

                System.out.println("params  " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void getMainCategoryFromJson() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("**cateRes" + response);
                            categoryList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);

                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Category category = new Category();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    category.setId(object.getString(Constant.ID));
                                    category.setName(object.getString(Constant.CATEGORY_NAME));
                                    category.setImage(object.getString(Constant.IMAGE));
                                    category.setMaxLevel(object.getString(Constant.MAX_LEVEL));
                                    category.setNoOfCate(object.getString(Constant.NO_OF_CATE));
                                    categoryList.add(category);

                                }
                                ArrayAdapter<Category> adapter =
                                        new ArrayAdapter<Category>(AddQuestionActivity.this,  android.R.layout.simple_dropdown_item_1line, categoryList);
                                adapter.setDropDownViewResource( android.R.layout.simple_dropdown_item_1line);

                                cat.setAdapter(adapter);
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
                params.put(Constant.getCategories, "1");
                return params;
            }
        };

        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getSubCategoryFromJson(String catid) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);
                            subcatList.clear();

                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    SubCategory subCate = new SubCategory();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    subCate.setId(object.getString(Constant.ID));
                                    subCate.setCategoryId(object.getString(Constant.MAIN_CATE_ID));
                                    subCate.setName(object.getString(Constant.SUB_CATE_NAME));
                                    subCate.setImage(object.getString(Constant.IMAGE));
                                    subCate.setStatus(object.getString(Constant.STATUS));
                                    subCate.setMaxLevel(object.getString(Constant.MAX_LEVEL));
                                    subcatList.add(subCate);
                                }

                                ArrayAdapter<SubCategory> adapter =
                                        new ArrayAdapter<SubCategory>(AddQuestionActivity.this,  android.R.layout.simple_spinner_dropdown_item, subcatList);
                                adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                                subcat.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);
                            }else{


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
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
                params.put(Constant.getSubCategory, "1");
                params.put(Constant.categoryId,catid);
                System.out.println("params  " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
