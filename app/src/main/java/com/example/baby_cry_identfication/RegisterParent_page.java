package com.example.baby_cry_identfication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterParent_page extends AppCompatActivity {

    private EditText userName, loginEmail, phoneNumber, password;
    private boolean flag = false;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String emailsave, passwordsave, namesave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerparent_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userName = findViewById(R.id.UserName);
        loginEmail = findViewById(R.id.EmailProfile);
        password = findViewById(R.id.ParentNumberProfile);
        phoneNumber = findViewById(R.id.ParentNumber);
        signUpButton = findViewById(R.id.NextPageSignUp);
        // loadData();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
    }

    private void signUpUser() {
        String userName = this.userName.getText().toString().trim();
        String email = this.loginEmail.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        String number = this.phoneNumber.getText().toString().trim();

        // Validate inputs
        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || number.isEmpty()) {
            Toast.makeText(RegisterParent_page.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(RegisterParent_page.this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!number.startsWith("+97")) {
            Toast.makeText(RegisterParent_page.this, "Emergency contact number must start with +97.", Toast.LENGTH_SHORT).show();
            return;}


            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Create a map to store user data
                        Map<String, Object> user = new HashMap<>();
                        user.put("UserName", userName);
                        user.put("Email", email);
                        user.put("Number", number);
                        user.put("rememberMe_flage", false);
                        user.put("sleep", Arrays.asList("00:00"));

                        // Save to Firestore
                        FirebaseFirestore.getInstance().collection("Parents").document(email).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("RegisterParent_page", "DocumentSnapshot successfully written!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("RegisterParent_page", "Error writing document", e);
                                });

                        // Navigate to RegisterParentPage2
                        Intent intent = new Intent(RegisterParent_page.this, RegisterParentPage2.class);
                        intent.putExtra("email", email); // Pass email to next activity
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterParent_page.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("RegisterParent_page", "signUpUser: " + task.getException().getMessage());
                    }
                });
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("remeberMe", flag);
        editor.putString("email", loginEmail.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.putString("nameUser", userName.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        flag = sharedPreferences.getBoolean("remeberMe", false);
        emailsave = sharedPreferences.getString("email", null);
        namesave = sharedPreferences.getString("nameUser", null);
        passwordsave = sharedPreferences.getString("password", null);

        if (emailsave != null && !emailsave.isEmpty()) {
            loginEmail.setText(emailsave);
        }
        if (passwordsave != null && !passwordsave.isEmpty()) {
            password.setText(passwordsave);
        }
        if (namesave != null && !namesave.isEmpty()) {
            userName.setText(namesave);
        }
    }
}
