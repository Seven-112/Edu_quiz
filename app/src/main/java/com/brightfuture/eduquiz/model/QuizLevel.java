package com.brightfuture.eduquiz.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;

public class QuizLevel {
    private int levelNo;
    private int noOfQuestion;
    private List<Question> question;
    //DbAdapter questionsDao;
    ArrayList<Question> question1 = new ArrayList<Question>();

    public QuizLevel(int levelNo, int noOfQuestion, Context context) {
        super();
        this.levelNo = levelNo;
        this.noOfQuestion = noOfQuestion;
        //questionsDao = new DbAdapter(context.getPackageName());
    }

    public int getLevelNo() {
        return levelNo;
    }

    public int getNoOfQuestion() {
        return noOfQuestion;
    }

    public List<Question> getQuestion() {
        return question;
    }

    public void setJsonQuestion(List<Question> question) {
        this.question = question;
    }

    public void setQuestionGuj(Activity activity, List<Question> question) {

        this.question = question;
        Collections.shuffle(this.question);

		/*question = questionsDao.getQuestionGuj(getNoOfQuestion(),getLevelNo());
		Collections.shuffle(question);*/

    }

}
