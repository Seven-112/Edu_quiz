package com.brightfuture.eduquiz.model;

import java.util.ArrayList;

public class Bookmark {
    private int id,que_id;
    private String question,answer,solution,imageUrl;
    private ArrayList<String> options = new ArrayList<String>();
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQue_id() {
        return que_id;
    }

    public void setQue_id(int que_id) {
        this.que_id = que_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean addOption(String option) {
        return this.options.add(option);
    }

    public ArrayList<String> getOptions() {
        return options;
    }
}
