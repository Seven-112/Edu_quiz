package com.brightfuture.eduquiz.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.activity.CommunityAnswersActivity;
import com.brightfuture.eduquiz.activity.CompleteActivity;
import com.brightfuture.eduquiz.activity.LearnMcqActivity;
import com.brightfuture.eduquiz.model.CommunityQuestion;
import com.brightfuture.eduquiz.model.Question;

import java.util.ArrayList;
import java.util.Collections;

import static com.itextpdf.text.factories.GreekAlphabetFactory.getString;

public class CommunityQuestionAdapter extends RecyclerView.Adapter<CommunityQuestionAdapter.ViewHolder> {
    public static String TAG="CommunityQuestionAdapter";
    public ArrayList<CommunityQuestion> questionList;
    private  Context activity;

    public CommunityQuestionAdapter(ArrayList<CommunityQuestion> questionList, Context context) {
        this.questionList = questionList;
        this.activity=context;
    }

    @NonNull
    @Override
    public CommunityQuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_question_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityQuestionAdapter.ViewHolder holder, int position) {

        CommunityQuestion q = questionList.get(position);

        holder.tvQuestion.setText(q.getCq_question());

        holder.tvname.setText(q.getU_name());
        holder.tvdate.setText(q.getCq_created().substring(0,10));
        holder.tvlocation.setText("Location: "+q.getCq_location());


        holder.btnanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, CommunityAnswersActivity.class);
                intent.putExtra(Constant.QUESTIONID,q.getCq_id());
                intent.putExtra(Constant.CQUSERNAME, q.getU_name());
                intent.putExtra(Constant.CQCREATED, q.getCq_created());
                intent.putExtra(Constant.QUESTION, q.getCq_question());
                intent.putExtra(Constant.CQLOCATION, q.getCq_location());


                activity.startActivity(intent);


            }
        });
        holder.btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String sharetext = q.getCq_question() +" - ask questions on "  + activity.getString(R.string.app_name);
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
                share.putExtra(Intent.EXTRA_TEXT, "" + sharetext + " " + Constant.PLAY_STORE_URL + activity.getPackageName());
                activity.startActivity(Intent.createChooser(share, "Share " + activity.getString(R.string.app_name) + "!"));

            }
        });





    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuestion,tvname,tvdate,tvlocation;
        private final TextView btnanswer,btnshare;


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
            tvname = (TextView) v.findViewById(R.id.tvName);
            tvdate = (TextView) v.findViewById(R.id.tvdate);
            tvlocation = (TextView) v.findViewById(R.id.tvlocation);
            btnanswer = (TextView) v.findViewById(R.id.btnanswer);
            btnshare = (TextView) v.findViewById(R.id.btnshare);


        }

        public TextView getTvQuestion() {
            return tvQuestion;
        }

        public TextView getTvname() {
            return tvname;
        }

        public TextView getTvdate() {
            return tvdate;
        }

        public TextView getTvlocation() {
            return tvlocation;
        }

        public TextView getBtnanswer() {
            return btnanswer;
        }

        public TextView getBtnshare() {
            return btnshare;
        }

         }

    @Override
    public int getItemCount() {
        return questionList.size();
    }


}
