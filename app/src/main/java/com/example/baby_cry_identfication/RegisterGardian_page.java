package com.example.baby_cry_identfication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterGardian_page extends AppCompatActivity {

    private EditText userName, loginEmail, phoneNumber, password;
    private boolean flag = false;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CheckBox Verfied;
    private String emailsave, passwordsave,namesave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_guardian_page);


        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI Components
        userName = findViewById(R.id.UserName);
        loginEmail = findViewById(R.id.LoginEmail);
        phoneNumber = findViewById(R.id.ConfirmPassword);
        password = findViewById(R.id.Password);
        signUpButton = findViewById(R.id.Login_LoginPage);
        Verfied = findViewById(R.id.rememberMe);
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
        boolean verfied = this.Verfied.isChecked();
        String imageUrl = "no image";

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || number.isEmpty()) {
            Toast.makeText(RegisterGardian_page.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Guardian user = new Guardian(userName, email, password, number, false, verfied, imageUrl);
                        FirebaseFirestore.getInstance().collection("Parents").document(email).set(user);
                        saveData();
                        Intent intent = new Intent(RegisterGardian_page.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        /*
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("userName", userName);
                            user.put("email", email);
                            user.put("phoneNumber", number);
                            user.put("rememberMe_flage", false);
                            user.put("verified_flage", Verfied);

                            db.collection("users").document(userId) // Using userId as the document ID
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(Register_page.this, "User registered successfully.", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Register_page.this, "Error adding user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d("FirestoreError", e.getMessage()); // Log the error
                                    });
                        }*/
                    } else {
                        Toast.makeText(RegisterGardian_page.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveData() {

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Toast.makeText(this, "flag " + flag, Toast.LENGTH_SHORT).show();
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