package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private Button logout;
    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences= getSharedPreferences("sharedPreferences",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();


                editor.putBoolean("remeberMe",false);
                editor.apply();
                Intent intent=new Intent(MainActivity.this,LoginPage.class);
                startActivity(intent);
                finish();
            }

        });

    }
}