package com.example.srsapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword_activity extends AppCompatActivity {

    EditText editTextEmail;
    Button btnReset;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextEmail = findViewById(R.id.edEmail);
        btnReset = findViewById(R.id.btnReset);
        mAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(editTextEmail.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPassword_activity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPassword_activity.this, "email has been sent successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else{
                            try{
                                throw task.getException();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                        }
                    });
                }
            }
        });
    }
}