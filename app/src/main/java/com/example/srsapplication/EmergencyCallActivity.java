package com.example.srsapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

public class EmergencyCallActivity extends AppCompatActivity {
Button buttonNDCC, buttonPerhilitan, buttonJPAM, buttonMERS;
String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_call);

        buttonNDCC = findViewById(R.id.buttonNDCC);
        buttonPerhilitan = findViewById(R.id.buttonPerhilitan);
        buttonJPAM = findViewById(R.id.buttonJPAM);
        buttonMERS = findViewById(R.id.buttonMERS);

        buttonNDCC.setOnClickListener(v -> {
            phoneNumber = "03-8064 2400";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phoneNumber));
            startActivity(intent);
        });

        buttonPerhilitan.setOnClickListener(v -> {
            phoneNumber = "1-800-885-151";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phoneNumber));
            startActivity(intent);
        });

        buttonJPAM.setOnClickListener(v -> {
            phoneNumber = "07-234 7360";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phoneNumber));
            startActivity(intent);
        });

        buttonMERS.setOnClickListener(v -> {
            phoneNumber = "999";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phoneNumber));
            startActivity(intent);
        });
    }
}