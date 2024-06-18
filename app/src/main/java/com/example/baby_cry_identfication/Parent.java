package com.example.baby_cry_identfication;

import java.util.ArrayList;

public class Parent {
    protected String UserName, Email , Number ;
    protected boolean rememberMe_flage;
    protected ArrayList<String>sleep;


    public Parent(String userName, String email, String number, boolean rememberMe_flage,ArrayList<String> sleep) {
        UserName = userName;
        Email = email;
        Number = number;
        this.rememberMe_flage = rememberMe_flage;
        this.sleep = sleep;
        //this.imageUrl = imageUrl;
    }




    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }



    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public boolean isRememberMe_flage() {
        return rememberMe_flage;
    }

    public void setRememberMe_flage(boolean rememberMe_flage) {
        this.rememberMe_flage = rememberMe_flage;
    }
}
