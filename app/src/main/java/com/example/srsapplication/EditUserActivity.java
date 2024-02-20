package com.example.srsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditUserActivity extends AppCompatActivity {
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText contactEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        contactEditText = findViewById(R.id.contactEditText);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(view -> {
            String updatedFullName = fullNameEditText.getText().toString();
            String updatedEmail = emailEditText.getText().toString();
            String updatedPassword = passwordEditText.getText().toString();
            String updatedContact = contactEditText.getText().toString();

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        String fullName = extras.getString("fullName");

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String currentFullName = dataSnapshot.child("fullName").getValue(String.class);
                            if (currentFullName != null && currentFullName.equals(fullName)) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user != null) {
                                    String currentEmail = user.getEmail();
                                    if (currentEmail != null && currentEmail.equals(updatedEmail) ) {
                                        user.updatePassword(updatedPassword)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        // Password updated successfully
                                                        Toast.makeText(EditUserActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                        updateUser(dataSnapshot,updatedFullName,updatedEmail,updatedContact);
                                                    } else {
                                                        // Failed to update password
                                                        Toast.makeText(EditUserActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        Toast.makeText(EditUserActivity.this, "Data user updated successfully", Toast.LENGTH_SHORT).show();
                                    } else if(currentEmail != null) {
                                        user.updateEmail(updatedEmail)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        user.updatePassword(updatedPassword)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            // Password updated successfully
                                                                            Toast.makeText(EditUserActivity.this, "Email and password updated successfully", Toast.LENGTH_SHORT).show();
                                                                            updateUser(dataSnapshot,updatedFullName,updatedEmail,updatedContact);
                                                                        } else {
                                                                            // Failed to update password
                                                                            Toast.makeText(EditUserActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        // Failed to update email
                                                        Toast.makeText(EditUserActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(EditUserActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors
                    Toast.makeText(EditUserActivity.this, "Failed to update user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateUser(DataSnapshot snapshot, String name, String email, String contact) {
        snapshot.child("fullName").getRef().setValue(name);
        snapshot.child("email").getRef().setValue(email);
        snapshot.child("contact").getRef().setValue(contact);
        fullNameEditText.setText("");
        passwordEditText.setText("");
        emailEditText.setText("");
        contactEditText.setText("");
        finish();
    }
}