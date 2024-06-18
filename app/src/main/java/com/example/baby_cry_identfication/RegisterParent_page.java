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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RegisterParent_page extends AppCompatActivity {

    private EditText userName, loginEmail, phoneNumber, password;
    private boolean flag = false;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String emailsave, passwordsave,namesave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerparent_page);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        userName = findViewById(R.id.UserName);
        loginEmail = findViewById(R.id.LoginEmail);
        phoneNumber = findViewById(R.id.EmergenyContact);
        password = findViewById(R.id.Password);
        signUpButton = findViewById(R.id.Login_LoginPage);
        loadData();


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

        // Initialize and populate sleep array
        ArrayList<String> sleep = new ArrayList<>();
        sleep.add("06-10, 12:00 to 2:30");
        sleep.add("06-20, 3:00 to 4:00");
        sleep.add("06-27, 1:00 to 1:30");
        sleep.add("06-28, 11:00 to 1:00");

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || number.isEmpty()) {
            Toast.makeText(RegisterParent_page.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

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

                        // Save data to SharedPreferences
                        saveData();

                        // Navigate to main activity
                        Intent intent = new Intent(RegisterParent_page.this, MainActivity.class);
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
       // Toast.makeText(this, "flag " + flag, Toast.LENGTH_SHORT).show();
        editor.putBoolean("remeberMe", flag);
        editor.putString("email", loginEmail.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.putString("nameUser",userName.getText().toString());
        editor.apply();
    }

    /*   protected void onStart() {
           super.onStart();
           ArrayList<User> UserList = new ArrayList<>();

           signUp.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(isEmpty(userName.getText())||isEmpty(UserEmail.getText())||isEmpty(userPassword.getText())){
                       Toast.makeText(Register_page.this, "Please enter all data required", Toast.LENGTH_LONG).show();
                   }else{
                       User user=new User(userName.getText().toString(),
                               UserEmail.getText().toString(),userPassword.getText().toString(),UserNumber.getText().toString(),false);
                       UserList.add(user);
                       SharedPreferences sharedPreferences= getSharedPreferences("sharedPreferences",MODE_PRIVATE);
                       SharedPreferences.Editor editor=sharedPreferences.edit();
                       Gson gson = new Gson();
                       String json =gson.toJson(UserList);
                       editor.putString("UserList",json);
                       editor.apply();
                       Intent intent=new Intent(Register_page.this,LoginPage.class);
                       startActivity(intent);



                   }
               }
           });

       }*/
    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }
    private void loadData(){
        SharedPreferences sharedPreferences= getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        //SharedPreferences.Editor editor=sharedPreferences.edit();
        // String json=sharedPreferences.getString("UserList",null);
        flag=sharedPreferences.getBoolean("remeberMe",false);
        emailsave=sharedPreferences.getString("email",null);
        namesave=sharedPreferences.getString("nameUser",null);
        passwordsave=sharedPreferences.getString("password",null);


        if( emailsave != null&&!emailsave.isEmpty()){
            loginEmail.setText(emailsave);
        }
        if(passwordsave != null&&!passwordsave.isEmpty()  ){
            password.setText(passwordsave);
        }
        if( namesave != null &&!namesave.isEmpty() ){
            userName.setText(namesave);
        }



        //flag=false;-+


//        if(json!=null){
//
//            // Toast.makeText(this, "email"+users.get(position).UserName+" "+users.get(position).UserName, Toast.LENGTH_SHORT).show();
//
//        }else{
//            Toast.makeText(this, "no user found no data ", Toast.LENGTH_SHORT).show();
//
//        }



    }
}