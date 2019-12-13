package com.brightfuture.eduquiz.activity;

import android.content.Context;
import android.content.Intent;
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
import com.brightfuture.eduquiz.model.Category;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.brightfuture.eduquiz.Constant.ISPREMIUM;


public class CategoryActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ProgressBar progressBar;
    public AdView mAdView;
    public TextView empty_msg;
    public RelativeLayout layout;
    public static ArrayList<Category> categoryList;

    public SwipeRefreshLayout swipeRefreshLayout;
    public Snackbar snackbar;
    public Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        layout = findViewById(R.id.layout);
        Utils.transparentStatusAndNavigation(CategoryActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.select_category));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        boolean isPremium= Session.getBooleanData(ISPREMIUM,CategoryActivity.this);
        mAdView = findViewById(R.id.banner_AdView);
        if(isPremium)
        {

        }
       else {
            mAdView.loadAd(new AdRequest.Builder().build());
        }

        empty_msg = findViewById(R.id.txtblanklist);
        progressBar =findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));

        getData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(CategoryActivity.this)) {
            getMainCategoryFromJson();
        } else {
            setSnackBar();
            progressBar.setVisibility(View.GONE);
        }

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

    /*
     * Get Quiz Category from Json
     */
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
                                CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this, categoryList);
                                recyclerView.setAdapter(adapter);
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


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ItemRowHolder> {
        private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        private ArrayList<Category> dataList;
        private Context mContext;

        public CategoryAdapter(Context context, ArrayList<Category> dataList) {
            this.dataList = dataList;
            this.mContext = context;
        }

        @Override
        public CategoryAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.ItemRowHolder holder, final int position) {
            empty_msg.setVisibility(View.GONE);
            final Category category = dataList.get(position);
            holder.text.setText(category.getName());
            holder.image.setDefaultImageResId(R.drawable.ic_launcher);
            holder.image.setImageUrl(category.getImage(), imageLoader);

            holder.relativeLayout
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Constant.CATE_ID = Integer.parseInt(category.getId());
                            if (!category.getNoOfCate().equals("0")) {
                                Intent intent = new Intent(CategoryActivity.this, SubcategoryActivity.class);
                                startActivity(intent);
                            } else {

                                if (category.getMaxLevel() == null) {
                                    Constant.TotalLevel = 0;
                                } else if (category.getMaxLevel().equals("null")) {
                                    Constant.TotalLevel = 0;
                                } else {
                                    Constant.TotalLevel = Integer.parseInt(category.getMaxLevel());
                                }
                                Intent intent = new Intent(CategoryActivity.this, LevelActivity.class);
                                intent.putExtra("fromQue", "cate");
                                startActivity(intent);
                            }

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
                Utils.CheckVibrateOrSound(CategoryActivity.this);
                Intent playQuiz = new Intent(CategoryActivity.this, SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
