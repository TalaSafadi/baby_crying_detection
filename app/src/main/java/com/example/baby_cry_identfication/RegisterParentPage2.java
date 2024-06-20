package com.example.baby_cry_identfication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterParentPage2 extends AppCompatActivity {

    private EditText childName, childAge, emergencyContactName, emergencyContactNumber;
    private Button signUpButton;
    private ImageButton informationButton;
    private FirebaseFirestore db;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerparent_page2);

        db = FirebaseFirestore.getInstance();

        childName = findViewById(R.id.ChildName);
        childAge = findViewById(R.id.ChidAge);
        emergencyContactName = findViewById(R.id.EmergencyContactName);
        emergencyContactNumber = findViewById(R.id.emergencyContactNumber);
        signUpButton = findViewById(R.id.SignUpButton);
        informationButton = findViewById(R.id.informationButton);

        // Get email from intent
        email = getIntent().getStringExtra("email");

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeSignUp();
            }
        });

        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to SignUpActivity
                Intent intent = new Intent(RegisterParentPage2.this, popUp.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void completeSignUp() {
        String childName = this.childName.getText().toString().trim();
        String childAge = this.childAge.getText().toString().trim();
        String emergencyContactName = this.emergencyContactName.getText().toString().trim();
        String emergencyContactNumber = this.emergencyContactNumber.getText().toString().trim();

        if (childName.isEmpty() || childAge.isEmpty() || emergencyContactName.isEmpty() || emergencyContactNumber.isEmpty()) {
            Toast.makeText(RegisterParentPage2.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedUser = new HashMap<>();
        updatedUser.put("ChildNmae", childName);
        updatedUser.put("child_age", Integer.parseInt(childAge));
        updatedUser.put("Emergency_contactName", emergencyContactName);
        updatedUser.put("Emergency_contactNumber", emergencyContactNumber);

        db.collection("Parents").document(email)
                .update(updatedUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RegisterParentPage2", "DocumentSnapshot successfully updated!");

                    Intent intent = new Intent(RegisterParentPage2.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w("RegisterParentPage2", "Error updating document", e);
                    Toast.makeText(RegisterParentPage2.this, "Failed to update information. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
