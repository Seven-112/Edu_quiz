package com.brightfuture.eduquiz.model;

public class User {

    public String status, email, name, current_Coins, profile_Pic, win_Coins, fcm_id, user_id;

    public boolean online_status;


    public User(String first_name,
                String email,
                String current_Coins,
                boolean online_status,
                String profile_Pic,
                String win_Coins,
                String fcm_id,
                String user_id) {
        this.name = first_name;
        this.email = email;
        this.current_Coins = current_Coins;
        this.online_status = online_status;
        this.profile_Pic = profile_Pic;
        this.win_Coins = win_Coins;
        this.fcm_id = fcm_id;
        this.user_id = user_id;
    }
}
