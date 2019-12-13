package com.brightfuture.eduquiz.model;



public class LeaderBoard {
  private String rank,name,score,userId,profile;

    public LeaderBoard(String rank, String name, String score, String userId, String profile) {
        this.rank = rank;
        this.name = name;
        this.score = score;
        this.userId = userId;
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }

    public String getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public String getUserId() {
        return userId;
    }
}
