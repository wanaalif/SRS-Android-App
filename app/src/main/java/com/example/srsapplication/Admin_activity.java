package com.example.srsapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Admin_activity extends AppCompatActivity {
    Button btnUserList, btnReportList, btnSchedule, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        btnUserList = findViewById(R.id.btnUserList);
        btnReportList = findViewById(R.id.btnReportList);
        btnSchedule = findViewById(R.id.btnSchedule);
        btnLogout = findViewById(R.id.btnLogout);

        btnUserList.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_activity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_activity.this, ScheduleOnDuty.class);
            startActivity(intent);
        });

        btnReportList.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_activity.this, ReportList_Activity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            FirebaseAuth.getInstance().signOut();
            Intent intent1 = new Intent(Admin_activity.this, Login_activity.class);
            startActivity(intent1);
            finish();
        });
    }
}