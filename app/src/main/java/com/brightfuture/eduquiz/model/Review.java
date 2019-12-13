package com.brightfuture.eduquiz.model;

import java.util.ArrayList;

public class Review {

    private String question, imgUrl, rightAns, wrongAns, extraNote;
    public ArrayList<String> optionList;
    private int queId;
    boolean isRight;

    public Review() {
    }
    public Review(int queId, String question, String imgUrl, String rightAns, String wrongAns, ArrayList<String> optionList, String extraNote) {
        this.queId = queId;
        this.question = question;
        this.imgUrl = imgUrl;
        this.rightAns = rightAns;
        this.wrongAns = wrongAns;
        this.optionList = optionList;
        this.extraNote = extraNote;

    }

    public Review(int queId, String question, String imgUrl, String rightAns, String wrongAns, ArrayList<String> optionList, String extraNote, boolean isRight) {
        this.queId = queId;
        this.question = question;
        this.imgUrl = imgUrl;
        this.rightAns = rightAns;
        this.wrongAns = wrongAns;
        this.optionList = optionList;
        this.extraNote = extraNote;
        this.isRight=isRight;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public int getQueId() {
        return queId;
    }

    public void setQueId(int queId) {
        this.queId = queId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRightAns() {
        return rightAns;
    }

    public void setRightAns(String rightAns) {
        this.rightAns = rightAns;
    }

    public String getWrongAns() {
        return wrongAns;
    }

    public void setWrongAns(String wrongAns) {
        this.wrongAns = wrongAns;
    }

    public ArrayList<String> getOptionList() {
        return optionList;
    }

    public void setOptionList(ArrayList<String> optionList) {
        this.optionList = optionList;
    }

    public String getExtraNote() {
        return extraNote;
    }

    public void setExtraNote(String extraNote) {
        this.extraNote = extraNote;
    }
}
