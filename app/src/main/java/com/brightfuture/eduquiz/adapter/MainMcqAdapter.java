package com.brightfuture.eduquiz.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.activity.CategoryActivity;
import com.brightfuture.eduquiz.activity.LearnMcqActivity;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.model.Question;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;

import static com.brightfuture.eduquiz.Constant.ISPREMIUM;

public class MainMcqAdapter extends RecyclerView.Adapter<MainMcqAdapter.ViewHolder> {
    public static String TAG="TestMcqAdapter";
    public ArrayList<Question> questionList;
    private  Context activity;

    public MainMcqAdapter(ArrayList<Question> questionList, Context context) {
        this.questionList = questionList;
        this.activity=context;
    }

    @NonNull
    @Override
    public MainMcqAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_mcq_adapter_item_small, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainMcqAdapter.ViewHolder holder, int position) {
        boolean isPremium= Session.getBooleanData(ISPREMIUM, activity);

        Question q = questionList.get(position);

        if(isPremium)
        {
            holder.banner_AdView.setVisibility(View.GONE);
        }
else {
            if (position % 10 == 0) {
                holder.banner_AdView.setVisibility(View.VISIBLE);
                holder.banner_AdView.loadAd(new AdRequest.Builder().build());
            } else {
                holder.banner_AdView.setVisibility(View.GONE);
            }
        }
        holder.tvQuestion.setText(position+1 +". "+q.getQuestion());
        ArrayList<String> options=q.getOptions();
        Collections.shuffle(options);
        holder.tvAns1.setText(options.get(0));
        holder.tvAns2.setText(options.get(1));
        holder.tvAns3.setText(options.get(2));
        holder.tvAns4.setText(options.get(3));
        holder.subcat.setText("Learn MCQ from "+q.getCategory_name() +" and "+ q.getSubcategory_name());
        holder.tvtruAns.setText("Right Answer : "+q.getTrueAns());

        holder.tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareClicked(q.getCategory_name() +" and "+ q.getSubcategory_name());

            }
        });

        holder.tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Constant.SUB_CAT_ID = Integer.parseInt(subCate.getId());

                Intent intent = new Intent(activity, LearnMcqActivity.class);
                intent.putExtra("sub_cat_id", q.getSubcat_id());
                intent.putExtra("sub_cat_name", q.getSubcategory_name());
                intent.putExtra("cat_name", q.getCategory_name());


                activity.startActivity(intent);
            }
        });




    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuestion,tvAns1,tvAns2,tvAns3,tvAns4,tvtruAns,subcat,tvViewMore, tvShare;
        private final RelativeLayout play_layout;
        private final AdView banner_AdView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            banner_AdView=(AdView) v.findViewById(R.id.banner_AdView);
            tvQuestion = (TextView) v.findViewById(R.id.tvQuestion);
            tvAns1 = (TextView) v.findViewById(R.id.tvAns1);
            tvAns2 = (TextView) v.findViewById(R.id.tvAns2);
            tvAns3 = (TextView) v.findViewById(R.id.tvAns3);
            tvAns4 = (TextView) v.findViewById(R.id.tvAns4);
            tvtruAns = (TextView) v.findViewById(R.id.tv_trueAns);
            play_layout=(RelativeLayout)v.findViewById(R.id.play_layout);
            subcat= (TextView) v.findViewById(R.id.subcat);
            tvViewMore=(TextView)v.findViewById(R.id.viewMore);
            tvShare=(TextView)v.findViewById(R.id.shareMore);

        }

        public AdView getBanner_AdView() {
            return banner_AdView;
        }

        public TextView getTvViewMore() {
            return tvViewMore;
        }

        public TextView getTvShare() {
            return tvShare;
        }

        public TextView getSubcat() {
            return subcat;
        }

        public RelativeLayout getPlay_layout() {
            return play_layout;
        }

        public TextView getTvQuestion() {
            return tvQuestion;
        }

        public TextView getTvAns1() {
            return tvAns1;
        }

        public TextView getTvAns2() {
            return tvAns2;
        }

        public TextView getTvAns3() {
            return tvAns3;
        }

        public TextView getTvAns4() {
            return tvAns4;
        }

        public TextView getTvtruAns() {
            return tvtruAns;
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public void shareClicked(String type) {
        final String sharetext;

            sharetext = "I am learning MCQs on "+type+" on " + activity.getString(R.string.app_name);

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, "" + sharetext + " " + Constant.PLAY_STORE_URL + activity.getPackageName());
        activity.startActivity(Intent.createChooser(share, "Share " + activity.getString(R.string.app_name) + "!"));
    }
}
