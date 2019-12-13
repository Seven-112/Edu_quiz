package com.brightfuture.eduquiz.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.activity.TestMcqActivity;
import com.brightfuture.eduquiz.model.Question;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;

public class TestMcqAdapter1 extends RecyclerView.Adapter<TestMcqAdapter1.ViewHolder> {
    public static String TAG="TestMcqAdapter1";
    public ArrayList<Question> questionList;
    private  Context activity;

    public TestMcqAdapter1(ArrayList<Question> questionList, Context context) {
        this.questionList = questionList;
        this.activity=context;
    }

    @NonNull
    @Override
    public TestMcqAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_mcq_adapter_item1, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TestMcqAdapter1.ViewHolder holder, int position) {

        Question q = questionList.get(position);

        holder.tvQuestion.setText(position+1 +". "+q.getQuestion());
        ArrayList<String> options=q.getOptions();
        Collections.shuffle(options);
        holder.tvAns1.setText(options.get(0));
        holder.tvAns2.setText(options.get(1));
        holder.tvAns3.setText(options.get(2));
        holder.tvAns4.setText(options.get(3));

        if(position%10==0)
        {
            holder.banner_AdView.setVisibility(View.VISIBLE);
            holder.banner_AdView.loadAd(new AdRequest.Builder().build());
        }
        else
        {
            holder.banner_AdView.setVisibility(View.GONE);
        }

        holder.tvAns1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tvAns1.getText().toString().equalsIgnoreCase(q.getTrueAns())) {
                    TestMcqActivity.addScore(TestMcqActivity.isAlreadyAnswered(q),true);
                    TestMcqActivity.AddReview1(q, holder.tvAns1, true);

                }
                else
                {
                    TestMcqActivity.addScore(TestMcqActivity.isAlreadyAnswered(q),false);
                    TestMcqActivity.AddReview1(q, holder.tvAns1, false);
                }
            }
        });

        holder.tvAns2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tvAns2.getText().toString().equalsIgnoreCase(q.getTrueAns())) {
                    TestMcqActivity.addScore(TestMcqActivity.isAlreadyAnswered(q),true);
                    TestMcqActivity.AddReview1(q, holder.tvAns2, true);

                }
                else
                {
                    TestMcqActivity.addScore(TestMcqActivity.isAlreadyAnswered(q),false);
                    TestMcqActivity.AddReview1(q, holder.tvAns2, false);
                }
            }
        });

        holder.tvAns3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tvAns3.getText().toString().equalsIgnoreCase(q.getTrueAns())) {
                    TestMcqActivity.addScore(TestMcqActivity.isAlreadyAnswered(q),true);
                    TestMcqActivity.AddReview1(q, holder.tvAns3, true);

                }
                else
                {
                    TestMcqActivity.addScore(TestMcqActivity.isAlreadyAnswered(q),false);
                    TestMcqActivity.AddReview1(q, holder.tvAns3, false);
                }
            }
        });

        holder.tvAns4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tvAns4.getText().toString().equalsIgnoreCase(q.getTrueAns())) {
                    TestMcqActivity.addScore(TestMcqActivity.isAlreadyAnswered(q),true);
                    TestMcqActivity.AddReview1(q, holder.tvAns4, true);

                }
                else
                {
                    TestMcqActivity.addScore(TestMcqActivity.isAlreadyAnswered(q),false);
                    TestMcqActivity.AddReview1(q, holder.tvAns4, false);
                }
            }
        });
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuestion;
        RadioButton tvAns1,tvAns2,tvAns3,tvAns4;
        RadioGroup rg_options;
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
            tvQuestion = (TextView) v.findViewById(R.id.tvQuestion);
            tvAns1 = (RadioButton) v.findViewById(R.id.tvAns1);
            tvAns2 = (RadioButton) v.findViewById(R.id.tvAns2);
            tvAns3 = (RadioButton) v.findViewById(R.id.tvAns3);
            tvAns4 = (RadioButton) v.findViewById(R.id.tvAns4);
            rg_options=(RadioGroup) v.findViewById(R.id.rg_options);
            banner_AdView=(AdView) v.findViewById(R.id.banner_AdView);


        }
        public AdView getBanner_AdView() {
            return banner_AdView;
        }
        public TextView getTvQuestion() {
            return tvQuestion;
        }

        public RadioButton getTvAns1() {
            return tvAns1;
        }

        public void setTvAns1(RadioButton tvAns1) {
            this.tvAns1 = tvAns1;
        }

        public RadioButton getTvAns2() {
            return tvAns2;
        }

        public void setTvAns2(RadioButton tvAns2) {
            this.tvAns2 = tvAns2;
        }

        public RadioButton getTvAns3() {
            return tvAns3;
        }

        public void setTvAns3(RadioButton tvAns3) {
            this.tvAns3 = tvAns3;
        }

        public RadioButton getTvAns4() {
            return tvAns4;
        }

        public void setTvAns4(RadioButton tvAns4) {
            this.tvAns4 = tvAns4;
        }

        public RadioGroup getRg_options() {
            return rg_options;
        }

        public void setRg_options(RadioGroup rg_options) {
            this.rg_options = rg_options;
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
}
