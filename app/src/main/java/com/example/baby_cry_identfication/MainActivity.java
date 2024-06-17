package com.example.baby_cry_identfication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private Button logout, button5;
    private ImageView imageView8, imageView9, imageView11;
    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
      //  logout = findViewById(R.id.logout);
        button5 = findViewById(R.id.button5);

        imageView11 = findViewById(R.id.imageView11);

        // Set up logout button
       /* logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("rememberMe", false);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });*/

        // Add other button functionalities here
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle button click
            }
        });
    }
}
