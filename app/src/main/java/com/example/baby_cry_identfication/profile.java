package com.example.baby_cry_identfication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class profile extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private FirebaseFirestore db;

    private EditText emailEditText;
    private EditText childNameEditText;
    private EditText childAgeEditText;
    private EditText emergencyContactNameEditText;
    private EditText emergencyContactNumberEditText;
    private EditText parentNumberEditText;
    private TextView parentname;
    private Button logoutButton;

    private SharedPreferences sharedPreferences;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize EditTexts
        emailEditText = findViewById(R.id.EmailProfileedit);
        childNameEditText = findViewById(R.id.childName);
        childAgeEditText = findViewById(R.id.ChildAgeProfile);
        emergencyContactNameEditText = findViewById(R.id.EmergencycontactNameProfile);
        emergencyContactNumberEditText = findViewById(R.id.EmergencyContactNumber);
        parentNumberEditText = findViewById(R.id.ParentNumberProfile);
        parentname = findViewById(R.id.ParentName);
        logoutButton = findViewById(R.id.logoutButton);

        // Retrieve the email from SharedPreferences
        sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);

        if (email != null) {
            fetchUserProfile();
        } else {
            Toast.makeText(this, "No email found in shared preferences", Toast.LENGTH_SHORT).show();
        }

        // Set up logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        // Clear SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", null);
        editor.putBoolean("rememberMe", false);
        editor.apply();

        // Redirect to login page
        Intent intent = new Intent(profile.this, LoginPage.class);
        startActivity(intent);
        finish();
    }

    private void fetchUserProfile() {
        Log.d(TAG, "Email used for fetching profile: " + email); // Log the email

        if (email != null && !email.isEmpty()) {
            db.collection("Parents").document(email).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Populate the EditTexts with the data
                                    populateProfileFields(document.getData());
                                } else {
                                    Log.d(TAG, "No such document");
                                    Toast.makeText(profile.this, "No profile found for this user", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                                Toast.makeText(profile.this, "Failed to retrieve profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "No email found in shared preferences", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateProfileFields(Map<String, Object> data) {
        emailEditText.setText(email);
        childNameEditText.setText((String) data.get("ChildNmae"));
        childAgeEditText.setText(String.valueOf(data.get("child_age")));
        emergencyContactNameEditText.setText((String) data.get("Emergency_contactName"));
        emergencyContactNumberEditText.setText((String) data.get("Emergency_contactNumber"));
        parentNumberEditText.setText((String) data.get("Number"));
        parentname.setText((String) data.get("UserName"));
    }
}
