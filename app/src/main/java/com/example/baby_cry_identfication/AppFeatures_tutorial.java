package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AppFeatures_tutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_features_tutorial);
        TextView textViewFeatures = findViewById(R.id.textViewFeatures);
        textViewFeatures.setText(getText(R.string.app_features));
    }
}