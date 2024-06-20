package com.example.baby_cry_identfication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

public class LoginPage extends AppCompatActivity {
    private static final String TAG = "LoginPage";

    private EditText UserEmail;
    private EditText userPassword;
    private Button LOGIN;
    private ArrayList<Parent> Userlist;
    private SharedPreferences sharedPreferences;

    private HashMap<String, String> loginInfo;
    private CheckBox rememberMe;
    private boolean flag = false;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Button fingerprint;
    private Button signUpButton;
    private Button forgetPassword;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String emailsave, passwordsave;
    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        setContentView(R.layout.activity_login_page_kid);
        UserEmail = findViewById(R.id.LoginEmailAdress);
        userPassword = findViewById(R.id.LoginPassword);
        LOGIN = findViewById(R.id.loginButton);
        rememberMe = findViewById(R.id.rememberMe);
        forgetPassword = findViewById(R.id.forgetPassword);

        LOGIN.setOnClickListener(view -> login());
        fingerprint = findViewById(R.id.fingerprintlogin);
        signUpButton = findViewById(R.id.signUpLogin);
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, RegisterParent_page.class);
            startActivity(intent);
        });

        // Initialize biometric prompt and prompt info
        createBiometricPrompt();
        createPromptInfo();

        forgetPassword.setOnClickListener(v -> {
            String email = UserEmail.getText().toString().trim();
            if (email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                mAuth.sendPasswordResetEmail(email);
                Toast.makeText(this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Email is not valid.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();

        if (flag) {
            Intent intent = new Intent(LoginPage.this, Alert.class);
            startActivity(intent);
        }

        if (emailsave != null) {
            fingerprint.setVisibility(View.VISIBLE); // Show fingerprint button
            fingerprint.setOnClickListener(view -> {
                Log.d(TAG, "Fingerprint button clicked"+emailsave+passwordsave);
                showBiometricPrompt();
            });
        } else {
            fingerprint.setVisibility(View.GONE); // Hide fingerprint button
        }
    }

    private void createBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "Authentication succeeded");

                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                finish();
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d(TAG, "Authentication failed");
                Toast.makeText(LoginPage.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPromptInfo() {
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Place your finger on the sensor")
                .setNegativeButtonText("Cancel")
                .build();
    }

    private void showBiometricPrompt() {
        Log.d(TAG, "Showing biometric prompt");
        biometricPrompt.authenticate(promptInfo);
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        flag = sharedPreferences.getBoolean("rememberMe", false);
        emailsave = sharedPreferences.getString("email", null);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("rememberMe", flag);
        editor.putString("email", UserEmail.getText().toString());
        editor.putString("password", userPassword.getText().toString());
        editor.apply();
    }

    public void login() {
        String email = UserEmail.getText().toString().trim();
        String password = userPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
                flag = rememberMe.isChecked();
                saveData();
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
