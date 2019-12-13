package com.brightfuture.eduquiz.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.model.CommunityAnswer;
import com.brightfuture.eduquiz.model.CommunityQuestion;

import java.util.ArrayList;

public class CommunityAnswerAdapter extends RecyclerView.Adapter<CommunityAnswerAdapter.ViewHolder> {
    public static String TAG="CommunityAnswerAdapter";
    public ArrayList<CommunityAnswer> answerList;
    private  Context activity;

    public CommunityAnswerAdapter(ArrayList<CommunityAnswer> answerList, Context context) {
        this.answerList = answerList;
        this.activity=context;
    }

    @NonNull
    @Override
    public CommunityAnswerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_answer_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityAnswerAdapter.ViewHolder holder, int position) {

        CommunityAnswer q = answerList.get(position);

        holder.tvQuestion.setText(q.getCa_answer());

        holder.tvname.setText(q.getU_name());
        if(q.getCa_created().length()<10)
        {
            holder.tvdate.setText(q.getCa_created());
        }
        else {
            holder.tvdate.setText(q.getCa_created().substring(0, 10));

        }





    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuestion,tvname,tvdate;


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



         }

    @Override
    public int getItemCount() {
        return answerList.size();
    }
}
