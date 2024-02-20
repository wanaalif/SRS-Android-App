package com.example.srsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile_Activity extends AppCompatActivity {
    TextView nameText, emailText, contactText;
    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        contactText = findViewById(R.id.contactText);
        editButton = findViewById(R.id.editButton);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            Toast.makeText(Profile_Activity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Profile_Activity.this, Login_activity.class));
            finish();
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String uid = dataSnapshot.getKey();
                    if(uid != null && uid.equals(user.getUid())){
                        String fullName = dataSnapshot.child("fullName").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String contact = dataSnapshot.child("contact").getValue(String.class);

                        nameText.setText("Name: " + fullName);
                        emailText.setText("Email: " + email);
                        contactText.setText("Contact: " + contact);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile_Activity.this,EditUserActivity.class);
            intent.putExtra("fullName",String.valueOf(nameText.getText()));
            startActivity(intent);
        });
    }
}