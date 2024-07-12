package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;

public class Kids_activity_Tutorial extends AppCompatActivity {


    private ImageView imageView13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids_tutorial);

        // Initialize the views

        imageView13 = findViewById(R.id.imageView13);



    }
}
