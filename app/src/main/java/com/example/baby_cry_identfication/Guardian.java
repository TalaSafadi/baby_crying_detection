package com.example.baby_cry_identfication;

public class Guardian {
    protected String UserName, Email, Password , Number , imageUrl;
    protected boolean rememberMe_flage,verified_flage;


    public Guardian(String userName, String email, String password, String number, boolean rememberMe_flage, boolean verified_flage, String imageUrl) {
        UserName = userName;
        Email = email;
        Password = password;
        Number = number;
        this.rememberMe_flage = rememberMe_flage;
        this.verified_flage = verified_flage;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isVerified_flage() {
        return verified_flage;
    }

    public void setVerified_flage(boolean verified_flage) {
        this.verified_flage = verified_flage;
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

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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


