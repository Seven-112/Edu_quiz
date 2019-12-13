package com.brightfuture.eduquiz.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brightfuture.eduquiz.helper.Session;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.brightfuture.eduquiz.helper.CircleImageView;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.model.SubCategory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.brightfuture.eduquiz.Constant.ISPREMIUM;
import static com.itextpdf.text.factories.GreekAlphabetFactory.getString;

public class TestMcqListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    public ProgressBar progressBar;
    public ArrayList<SubCategory> subCateList;
    public AdView mAdView;
    public TextView txtBlankList;
    public RelativeLayout layout;

    public SwipeRefreshLayout swipeRefreshLayout;
    public Snackbar snackbar;
    public Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mcq_list);
        layout = findViewById(R.id.layout);
        Utils.transparentStatusAndNavigation(TestMcqListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Topics To Discuss");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mAdView = findViewById(R.id.banner_AdView);
        boolean isPremium= Session.getBooleanData(ISPREMIUM,TestMcqListActivity.this);
        mAdView = findViewById(R.id.banner_AdView);
        if(isPremium)
        {

        }
        else {
            mAdView.loadAd(new AdRequest.Builder().build());
        }


        txtBlankList = findViewById(R.id.txtblanklist);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(TestMcqListActivity.this));
        txtBlankList.setText("Topics not available");

        subCateList = new ArrayList<>();
        getData();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                subCateList.clear();
                getData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(TestMcqListActivity.this)) {
            getSubCategoryFromJson();

        } else {
            setSnackBar();
            progressBar.setVisibility(View.GONE);
        }
    }

    public void getSubCategoryFromJson() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);

                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                                for (int i = 0; i < jsonArray.length(); i++) {
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
                                        subCateList.add(subCate);
                                    }
                                }
                                if (subCateList.size() == 0) {
                                    txtBlankList.setVisibility(View.VISIBLE);
                                    txtBlankList.setText("Topic Not Available");
                                }
                                SubCategoryAdapter1 adapter = new SubCategoryAdapter1(TestMcqListActivity.this, subCateList);
                                recyclerView.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);
                            }/*else{
                                if(Constant.TotalLevel!=0){
                                    Intent intent = new Intent(SubcategoryActivity.this, LevelActivity.class);
                                    intent.putExtra("fromQue", "subCate");
                                    startActivity(intent);
                                    ((SubcategoryActivity)getApplicationContext()).finish();
                                }
                            }*/
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
                params.put(Constant.getAllSubCategory, "1");

                System.out.println("params  " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void setSnackBar() {
        snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getData();
                    }
                });

        snackbar.show();
    }

    public class SubCategoryAdapter1 extends RecyclerView.Adapter<SubCategoryAdapter1.ItemRowHolder> {
        private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        private ArrayList<SubCategory> dataList;
        private Context mContext;

        public SubCategoryAdapter1(Context context, ArrayList<SubCategory> dataList) {
            this.dataList = dataList;
            this.mContext = context;
        }

        @Override
        public SubCategoryAdapter1.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SubCategoryAdapter1.ItemRowHolder holder, final int position) {
            txtBlankList.setVisibility(View.GONE);
            final SubCategory subCate = dataList.get(position);
            holder.text.setText((position+1)+":"+subCate.getCategory_name()+":"+subCate.getName());
            holder.image.setDefaultImageResId(R.drawable.ic_launcher);
            holder.image.setImageUrl(subCate.getImage(), imageLoader);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.SUB_CAT_ID = Integer.parseInt(subCate.getId());

                    Intent intent = new Intent(mContext, TestMcqActivity.class);
                    intent.putExtra("sub_cat_id", Constant.SUB_CAT_ID);
                    intent.putExtra("sub_cat_name", subCate.getName());
                    intent.putExtra("cat_name", subCate.getCategory_name());
                    intent.putExtra("qstn_cnt", subCate.getQuestion_count());


                    final AlertDialog.Builder dialog = new AlertDialog.Builder(TestMcqListActivity.this);

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View dialogView = inflater.inflate(R.layout.no_of_qstn_dialog, null);
                    dialog.setView(dialogView);

                    TextView ok = (TextView) dialogView.findViewById(R.id.ok);
                    TextView qstn_cnt = (TextView) dialogView.findViewById(R.id.qstn_cnt_et);
                    final AlertDialog alertDialog = dialog.create();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialog.show();

                    alertDialog.setCancelable(false);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            String cnt=qstn_cnt.getText().toString();
                            if (cnt!=null && cnt.length()>0) {
                                int count = Integer.parseInt(cnt);
                                int actualCount =Integer.parseInt(subCate.getQuestion_count());
                                if(count>actualCount) {
                                    Toast.makeText(TestMcqListActivity.this,"only "+actualCount+" no of questions avilable in this subcategory",Toast.LENGTH_SHORT);
                                    qstn_cnt.setText("");
                                    qstn_cnt.requestFocus();
                                }
                                else
                                {
                                    alertDialog.dismiss();
                                    intent.putExtra("qstn_cnt", count);
                                    startActivity(intent);
                                }
                            }
                            else{
                                Toast.makeText(TestMcqListActivity.this,"Enter valid no of questions",Toast.LENGTH_SHORT);
                                qstn_cnt.setText("");
                                qstn_cnt.requestFocus();
                            }



                        }
                    });










                }
            });
        }

        @Override
        public int getItemCount() {
            return (null != dataList ? dataList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            public CircleImageView image;
            public TextView text;
            RelativeLayout relativeLayout;

            public ItemRowHolder(View itemView) {
                super(itemView);
                image = (CircleImageView) itemView.findViewById(R.id.imgcategory);
                text = (TextView) itemView.findViewById(R.id.item_title);
                relativeLayout = (RelativeLayout) itemView.findViewById(R.id.cat_layout);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
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
                Utils.CheckVibrateOrSound(TestMcqListActivity.this);
                Intent playQuiz = new Intent(TestMcqListActivity.this, SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}