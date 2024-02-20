package com.example.srsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signup_activity extends AppCompatActivity {
    EditText editTextFullName,  editTextEmail, editTextPassword, editTextContact;
    AppCompatButton btnSignup;
    FirebaseAuth mAuth;
    TextView textViewLogin;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        editTextFullName = findViewById(R.id.edFullNameSignUp);
        editTextEmail = findViewById(R.id.edEmailSignup);
        editTextPassword = findViewById(R.id.edPasswordSignUp);
        editTextContact = findViewById(R.id.edContactSignIn);
        btnSignup = findViewById(R.id.btnSignup);
        mAuth = FirebaseAuth.getInstance();
        textViewLogin = findViewById(R.id.tvLogin);


        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Signup_activity.this, Login_activity.class);
            startActivity(intent);
        });

        btnSignup.setOnClickListener(v -> {
            String fullName, email, password, contact;
            fullName = String.valueOf(editTextFullName.getText());
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());
            contact = String.valueOf(editTextContact.getText());

            if (TextUtils.isEmpty(fullName)) {
                Toast.makeText(Signup_activity.this, "Enter Full Name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (containsDigit(fullName)) {
                Toast.makeText(Signup_activity.this, "Name cannot contain any digit", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Signup_activity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            } else if (!isValidEmail(email)) {
                Toast.makeText(Signup_activity.this, "Enter a valid Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Signup_activity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(contact)) {
                Toast.makeText(Signup_activity.this, "Enter Contact", Toast.LENGTH_SHORT).show();
                return;
            } else if (!isValidPhoneNumber(contact)) {
                Toast.makeText(Signup_activity.this, "Phone Number must contain 10-11 digit", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            String storedName = dataSnapshot.child("fullName").getValue(String.class);
                            String storedEmail = dataSnapshot.child("email").getValue(String.class);
                            String storedContact = dataSnapshot.child("contact").getValue(String.class);
                            if(storedName!= null && storedEmail!= null && storedContact!=null && storedName.equals(fullName) && storedEmail.equals(email) && storedContact.equals(contact)){
                                Toast.makeText(Signup_activity.this, "the user has been registered", Toast.LENGTH_SHORT).show();
                            }
                        }
                        // Register the user with email and password
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Signup_activity.this, "Registration successfully", Toast.LENGTH_SHORT).show();
                                        // Registration successful, log in the user
                                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(authTask -> {
                                                    if (authTask.isSuccessful()) {

                                                        Intent intent = new Intent(Signup_activity.this, MainActivity.class);
                                                        intent.putExtra("fullName", fullName);
                                                        intent.putExtra("email", email);
                                                        intent.putExtra("password", password);
                                                        intent.putExtra("contact", contact);
                                                        startActivity(intent);
                                                        finish();
                                                    } else{
                                                        Toast.makeText(Signup_activity.this, "Failed to get user information", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(Signup_activity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public static boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean containsDigit(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^[0-9]{10,11}$";
        return phoneNumber.matches(regex);
    }
}