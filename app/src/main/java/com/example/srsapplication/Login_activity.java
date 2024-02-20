package com.example.srsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_activity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    TextView tvSignUp, tvForgotPassword;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        editTextEmail = findViewById(R.id.edEmailLogIn);
        editTextPassword = findViewById(R.id.edPasswordLogIn);
        btnLogin = findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);

        tvSignUp.setOnClickListener(v -> startActivity(new Intent(Login_activity.this, Signup_activity.class)));
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(Login_activity.this, ForgotPassword_activity.class)));

        btnLogin.setOnClickListener(v -> {
            String email, password;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Login_activity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            } else if (!Signup_activity.isValidEmail(email)) {
                Toast.makeText(Login_activity.this, "Enter a valid Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Login_activity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            } else{
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login_activity.this, task -> {
                                // Sign in success, update UI with the signed-in user's information
                                if (task.isSuccessful()) {
                                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.apply();

                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                                String currentEmail = dataSnapshot.child("email").getValue(String.class);
                                                if(currentEmail.equals(email)) {
                                                    name = dataSnapshot.child("fullName").getValue(String.class);
                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    if(email.equals("user@admin.com") && password.equals("Adminuser")){
                                        Intent intent = new Intent(Login_activity.this,Admin_activity.class);
                                        startActivity(intent);
                                        finish();
                                    } else{
                                        Toast.makeText(Login_activity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login_activity.this, MainActivity.class);
                                        intent.putExtra("fullName",name);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login_activity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
            }
        });

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // User is already logged in, navigate to the appropriate activity
            if (mAuth.getCurrentUser() != null) {
                if (mAuth.getCurrentUser().getEmail().equals("user@admin.com")) {
                    Intent intent = new Intent(Login_activity.this, Admin_activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Login_activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }
}