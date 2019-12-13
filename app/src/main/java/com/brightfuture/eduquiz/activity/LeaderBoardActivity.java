package com.brightfuture.eduquiz.activity;

import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.CircleImageView;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.model.LeaderBoard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LeaderBoardActivity extends AppCompatActivity {


    public RecyclerView recyclerView;
    public static CircleImageView image, img1, img2, img3;
    public TextView name1, name2, name3, score1, score2, score3;
    public ProgressBar progressbar;

    public ArrayList<LeaderBoard> lstleaderboard;
    public LinearLayout lyt1, lyt2, lyt3;
    public static ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public TextView txtname, txtscore, txtno;
    public RelativeLayout lytownscore, mainLayout;
    public int myrank;
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        mainLayout = findViewById(R.id.mainLayout);
        Utils.transparentStatusAndNavigation(LeaderBoardActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.leaderboard));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        lstleaderboard = new ArrayList<>();
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        lytownscore = (RelativeLayout) findViewById(R.id.lytownscore);
        image = (CircleImageView) findViewById(R.id.imgprofile);

        txtno = (TextView) findViewById(R.id.txtno);
        txtscore = (TextView) findViewById(R.id.txtscore);
        txtname = (TextView) findViewById(R.id.txtname);

        lyt1 = (LinearLayout) findViewById(R.id.lyt1);
        lyt2 = (LinearLayout) findViewById(R.id.lyt2);
        lyt3 = (LinearLayout) findViewById(R.id.lyt3);

        img1 = (CircleImageView) findViewById(R.id.img1);
        img2 = (CircleImageView) findViewById(R.id.img2);
        img3 = (CircleImageView) findViewById(R.id.img3);
        img1.setDefaultImageResId(R.drawable.ic_account);
        img2.setDefaultImageResId(R.drawable.ic_account);
        img3.setDefaultImageResId(R.drawable.ic_account);
        name1 = (TextView) findViewById(R.id.txt1name);
        name2 = (TextView) findViewById(R.id.txt2name);
        name3 = (TextView) findViewById(R.id.txt3name);

        score1 = (TextView) findViewById(R.id.txt1score);
        score2 = (TextView) findViewById(R.id.txt2score);
        score3 = (TextView) findViewById(R.id.txt3score);


        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(LeaderBoardActivity.this));

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        LeaderBoardData(formattedDate);


    }


    public void LeaderBoardData(final String date) {
        if (Utils.isNetworkAvailable(LeaderBoardActivity.this)) {
            StringRequest strReq = new StringRequest(Request.Method.POST, Constant.QUIZ_URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    System.out.println("====leader board res " + response);
                    try {
                        lstleaderboard.clear();
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("error").equals("false")) {
                            JSONArray jsonArray = obj.getJSONArray("data");

                            lyt1.setVisibility(View.GONE);
                            lyt2.setVisibility(View.GONE);
                            lyt3.setVisibility(View.GONE);


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                myrank = Integer.parseInt(object.getString(Constant.RANK));
                                if (Session.getUserData(Session.USER_ID, getApplicationContext()).equalsIgnoreCase(object.getString("user_id"))) {
                                    if (myrank > 8) {
                                        lytownscore.setVisibility(View.VISIBLE);
                                        if (!object.getString(Constant.PROFILE).equalsIgnoreCase(""))
                                            image.setImageUrl(object.getString(Constant.PROFILE), imageLoader);
                                        txtscore.setText(Math.round(Float.parseFloat(object.getString(Constant.SCORE))) + "");
                                        txtname.setText(object.getString(Constant.name));
                                        txtno.setText("" + myrank);
                                    } else {
                                        lytownscore.setVisibility(View.GONE);
                                    }
                                }
                                if (myrank == 1) {
                                    lyt1.setVisibility(View.VISIBLE);

                                    if (!object.getString(Constant.PROFILE).equalsIgnoreCase(""))
                                        img1.setImageUrl(object.getString(Constant.PROFILE), imageLoader);


                                    name1.setText(object.getString(Constant.name));
                                    score1.setText(Math.round(Float.parseFloat(object.getString(Constant.SCORE))) + "");
                                } else if (myrank == 2) {
                                    lyt2.setVisibility(View.VISIBLE);

                                    if (!object.getString(Constant.PROFILE).equalsIgnoreCase(""))
                                        img2.setImageUrl(object.getString(Constant.PROFILE), imageLoader);

                                    name2.setText(object.getString(Constant.name));
                                    score2.setText(Math.round(Float.parseFloat(object.getString(Constant.SCORE))) + "");
                                } else if (myrank == 3) {
                                    lyt3.setVisibility(View.VISIBLE);

                                    if (!object.getString(Constant.PROFILE).equalsIgnoreCase(""))
                                        img3.setImageUrl(object.getString(Constant.PROFILE), imageLoader);

                                    name3.setText(object.getString(Constant.name));
                                    score3.setText(Math.round(Float.parseFloat(object.getString(Constant.SCORE))) + "");
                                } else {
                                    LeaderBoard leaderBoard = new LeaderBoard(object.getString(Constant.RANK),
                                            object.getString(Constant.name), object.getString(Constant.SCORE),
                                            object.getString("user_id"), object.getString(Constant.PROFILE));
                                    lstleaderboard.add(leaderBoard);
                                }

                            }
                            DisplayData();
                        } else {
                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(LeaderBoardActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressbar.setVisibility(View.GONE);
                }

            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Constant.accessKey, Constant.accessKeyValue);
                    params.put(Constant.getMontlyLeaderboard, "1");
                    params.put(Constant.DATE, date);
                    return params;

                }
            };

            AppController.getInstance().getRequestQueue().getCache().clear();
            AppController.getInstance().addToRequestQueue(strReq);
        }
    }

    public void DisplayData() {
        progressbar.setVisibility(View.GONE);
        try {
            LeaderBoardAdapter adapter = new LeaderBoardAdapter(LeaderBoardActivity.this, lstleaderboard);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ItemRowHolder> {

        private ArrayList<LeaderBoard> dataList;
        private Context mContext;

        public LeaderBoardAdapter(Context context, ArrayList<LeaderBoard> dataList) {
            this.dataList = dataList;
            this.mContext = context;
        }

        @Override
        public LeaderBoardAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_leaderboard, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(LeaderBoardAdapter.ItemRowHolder holder, final int position) {
            final LeaderBoard singleItem = dataList.get(position);
            holder.txtscore.setText(Math.round(Float.parseFloat(singleItem.getScore())) + "");
            holder.image.setDefaultImageResId(R.drawable.ic_account);
            holder.image.setImageUrl(singleItem.getProfile(), imageLoader);
            holder.txtname.setText(singleItem.getName());
            holder.txtno.setText((position + 4) + "");
        }


        @Override
        public int getItemCount() {
            return (null != dataList ? dataList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            public CircleImageView image;
            public TextView txtname, txtscore, txtno;

            public ItemRowHolder(View itemView) {
                super(itemView);
                image = (CircleImageView) itemView.findViewById(R.id.imgprofile);
                txtno = (TextView) itemView.findViewById(R.id.txtno);
                txtscore = (TextView) itemView.findViewById(R.id.txtscore);
                txtname = (TextView) itemView.findViewById(R.id.txtname);
            }
        }
    }

}
