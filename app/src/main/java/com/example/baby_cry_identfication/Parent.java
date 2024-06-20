package com.example.baby_cry_identfication;

import java.util.ArrayList;

public class Parent {
    protected String UserName, Email , Number , ChildNmae , Emergency_contactName ;
    //protected boolean rememberMe_flage;
    protected ArrayList<String>sleep;
    int Emergency_contactNumber, child_age;


  /*  public Parent(String userName, String email, String number, boolean rememberMe_flage,ArrayList<String> sleep,) {
        UserName = userName;
        Email = email;
        Number = number;
        //this.rememberMe_flage = rememberMe_flage;
        this.sleep = sleep;
        //this.imageUrl = imageUrl;
    }*/

    public Parent(String userName, String email, String number, ArrayList<String> sleep, String childNmae,
                  String emergency_contactName, int emergency_contactNumber, int child_age) {
        UserName = userName;
        Email = email;
        Number = number;
        //this.rememberMe_flage = rememberMe_flage;
        this.sleep = sleep;
        ChildNmae = childNmae;
        Emergency_contactName = emergency_contactName;
        Emergency_contactNumber = emergency_contactNumber;
        this.child_age = child_age;
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

    public String getChildNmae() {
        return ChildNmae;
    }

    public void setChildNmae(String childNmae) {
        ChildNmae = childNmae;
    }

    public String getEmergency_contactName() {
        return Emergency_contactName;
    }

    public void setEmergency_contactName(String emergency_contactName) {
        Emergency_contactName = emergency_contactName;
    }

    public ArrayList<String> getSleep() {
        return sleep;
    }

    public void setSleep(ArrayList<String> sleep) {
        this.sleep = sleep;
    }

    public int getEmergency_contactNumber() {
        return Emergency_contactNumber;
    }

    public void setEmergency_contactNumber(int emergency_contactNumber) {
        Emergency_contactNumber = emergency_contactNumber;
    }

    public int getChild_age() {
        return child_age;
    }

    public void setChild_age(int child_age) {
        this.child_age = child_age;
    }
}
