package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class Forget_password_Tutorial extends AppCompatActivity {
    private ImageView image1,image2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_tutorial);
        image1= findViewById(R.id.imageforgetpass);
        image2= findViewById(R.id.imageforgetpasslink);


    }
}