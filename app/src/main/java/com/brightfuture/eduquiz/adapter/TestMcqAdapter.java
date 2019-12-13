package com.brightfuture.eduquiz.adapter;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.model.Question;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;

public class TestMcqAdapter extends RecyclerView.Adapter<TestMcqAdapter.ViewHolder> {
    public static String TAG="TestMcqAdapter";
    public ArrayList<Question> questionList;
    private  Context activity;

    public TestMcqAdapter(ArrayList<Question> questionList, Context context) {
        this.questionList = questionList;
        this.activity=context;
    }

    @NonNull
    @Override
    public TestMcqAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_mcq_adapter_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TestMcqAdapter.ViewHolder holder, int position) {

        Question q = questionList.get(position);

        holder.tvQuestion.setText(position+1 +". "+q.getQuestion());
        ArrayList<String> options=q.getOptions();
        Collections.shuffle(options);
        holder.tvAns1.setText(options.get(0));
        holder.tvAns2.setText(options.get(1));
        holder.tvAns3.setText(options.get(2));
        holder.tvAns4.setText(options.get(3));

        holder.tvtruAns.setText("Right Answer : "+q.getTrueAns());

        if(position%10==0)
        if(position%10==0)
        {
            holder.banner_AdView.setVisibility(View.VISIBLE);
            holder.banner_AdView.loadAd(new AdRequest.Builder().build());
        }
        else
        {
            holder.banner_AdView.setVisibility(View.GONE);
        }


    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuestion,tvAns1,tvAns2,tvAns3,tvAns4,tvtruAns;
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

        }
        public AdView getBanner_AdView() {
            return banner_AdView;
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
}
