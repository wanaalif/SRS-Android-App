package com.example.srsapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    TextView textName, textDesc, textDate;
    ProgressBar progressBar;
    FrameLayout progressOverlay;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        progressBar = findViewById(R.id.progressBar);
        progressOverlay = findViewById(R.id.progressOverlay);

        progressOverlay.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            // Hide the progress overlay after 2 seconds
            progressOverlay.setVisibility(View.GONE);
        }, 2000);

        // Find buttons by their IDs
        Button profileButton = findViewById(R.id.profileButton);
        Button scheduleButton = findViewById(R.id.scheduleButton);
        Button reportButton = findViewById(R.id.reportButton);
        Button emergencyButton = findViewById(R.id.emergencyButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        textName = findViewById(R.id.textName);
        textDesc = findViewById(R.id.textDesc);
        textDate = findViewById(R.id.textDate);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null && !intent.getExtras().isEmpty() && intent.hasExtra("email")) {
            String fullName, email, contact;
            fullName = intent.getStringExtra("fullName");
            email = intent.getStringExtra("email");
            contact = intent.getStringExtra("contact");
            String userID = user.getUid();
            databaseReference.child("users").child(userID).child("fullName").setValue(fullName);
            databaseReference.child("users").child(userID).child("email").setValue(email);
            databaseReference.child("users").child(userID).child("contact").setValue(contact);
        }

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String currentEmail = dataSnapshot.child("email").getValue(String.class);
                    if(currentEmail != null && user != null && currentEmail.equals(user.getEmail())) {
                        String userName = dataSnapshot.child("fullName").getValue(String.class);
                        textName.setText(userName);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final AtomicBoolean isOnDuty = new AtomicBoolean(false);
                final AtomicBoolean isDatePast = new AtomicBoolean(false);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String currentEmail = dataSnapshot.child("email").getValue(String.class);
                    if (currentEmail != null && user != null && currentEmail.equals(user.getEmail())) {
                        String userName = dataSnapshot.child("fullName").getValue(String.class);
                        databaseReference.child("person in charge").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String date="", location="", shift="";
                                String currentDate = getCurrentDate(); // Assuming you have a method to get the current date as a String

                                SimpleDateFormat dateFormat = new SimpleDateFormat("d-M-yyyy", Locale.getDefault());
                                Date currentDateObj;
                                try {
                                    currentDateObj = dateFormat.parse(currentDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return; // Handle the parsing exception according to your needs
                                }

                                List<UserOnDuty> userList = new ArrayList<>();
                                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                    String dateRet = dateSnapshot.getKey();
                                    if (dateRet != null) {
                                        for (DataSnapshot userSnapshot : dateSnapshot.getChildren()) {
                                            UserOnDuty userOnDuty = userSnapshot.getValue(UserOnDuty.class);
                                            if (userOnDuty != null) {
                                                String nameRet = userOnDuty.getName();
                                                String contactRet = userOnDuty.getContact();
                                                String locationRet = userOnDuty.getLocation();
                                                String shiftRet = userOnDuty.getShift();
                                                Boolean remarkRet = userOnDuty.getRemark();
                                                if(Objects.equals(userName, nameRet)) {
                                                    UserOnDuty user = new UserOnDuty(dateRet, nameRet, contactRet, locationRet, shiftRet, remarkRet);
                                                    userList.add(user);
                                                }
                                            }
                                        }
                                    }
                                }

                                if(userList.size() > 1){
                                    userList.sort(new Comparator<UserOnDuty>() {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("d-M-yyyy");

                                        @Override
                                        public int compare(UserOnDuty user1, UserOnDuty user2) {
                                            try {
                                                Date date1 = dateFormat.parse(user1.getDate());
                                                Date date2 = dateFormat.parse(user2.getDate());
                                                return date1.compareTo(date2);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            return 0;
                                        }
                                    });

                                    String latestDate = userList.get(userList.size() - 1).getDate();

                                    for (UserOnDuty user : userList) {
                                        if (user.getDate().equals(latestDate)) {
                                            location = user.getLocation();
                                            shift = user.getShift();
                                            date = user.getDate();
                                            Date retrievedDateObj;

                                            try {
                                                retrievedDateObj = dateFormat.parse(date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                                continue; // Handle the parsing exception according to your needs
                                            }

                                            if (getCurrentDate().equals(date))
                                                textDesc.setText("You are on duty today on " + location + " at " + shift);
                                            else if(retrievedDateObj.before(currentDateObj))
                                                textDesc.setText("Your duty date has passed. Your duty will be determined later");
                                            else
                                                textDesc.setText("The next day you are on duty on " + location + " at " + shift);

                                            try {
                                                Date formattedDate = dateFormat.parse(date);
                                                dateFormat.applyPattern("d/M"); // Change the pattern to "d/M"
                                                String formattedDateString = dateFormat.format(formattedDate);
                                                textDate.setText(formattedDateString);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else{
                                    for (UserOnDuty user : userList) {
                                        location = user.getLocation();
                                        shift = user.getShift();
                                        date = user.getDate();
                                        Date retrievedDateObj;

                                        try {
                                            retrievedDateObj = dateFormat.parse(date);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            continue; // Handle the parsing exception according to your needs
                                        }

                                        if (getCurrentDate().equals(date))
                                            textDesc.setText("You are on duty today on " + location + " at " + shift);
                                        else if(retrievedDateObj.before(currentDateObj))
                                            textDesc.setText("Your duty date has passed. Your duty will be determined later");
                                        else
                                            textDesc.setText("The next day you are on duty on " + location + " at " + shift);

                                        try {
                                            Date formattedDate = dateFormat.parse(date);
                                            dateFormat.applyPattern("d/M"); // Change the pattern to "d/M"
                                            String formattedDateString = dateFormat.format(formattedDate);
                                            textDate.setText(formattedDateString);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set click listeners for the buttons
        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Profile_Activity.class));
        });
        scheduleButton.setOnClickListener(v -> {
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String currentEmail = dataSnapshot.child("email").getValue(String.class);
                        if(currentEmail != null && user != null && currentEmail.equals(user.getEmail())) {
                            String userName = dataSnapshot.child("fullName").getValue(String.class);
                            Intent intent1 = new Intent(MainActivity.this, ScheduleForUser.class);
                            intent1.putExtra("fullName", userName);
                            startActivity(intent1);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        reportButton.setOnClickListener(v -> {
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final AtomicBoolean isLeaderOrOnDuty = new AtomicBoolean(false); // Declare as final AtomicBoolean
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String currentEmail = dataSnapshot.child("email").getValue(String.class);
                        if (currentEmail != null && user != null && currentEmail.equals(user.getEmail())) {
                            String userName = dataSnapshot.child("fullName").getValue(String.class);
                            databaseReference.child("person in charge").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                        String dateRetrieve = dateSnapshot.getKey();
                                        if (dateRetrieve != null) {
                                            for (DataSnapshot userSnapshot : dateSnapshot.getChildren()) {
                                                UserOnDuty userOnDuty = userSnapshot.getValue(UserOnDuty.class);
                                                if (userOnDuty != null) {
                                                    String nameRetrieve = userOnDuty.getName();
                                                    String contact = userOnDuty.getContact();
                                                    String location = userOnDuty.getLocation();
                                                    if (getCurrentDate().equals(dateRetrieve) && userName.equals(nameRetrieve)) {
                                                        if (userOnDuty.getRemark()) {
                                                            Intent intent1 = new Intent(MainActivity.this, DailyReport_Activity.class);
                                                            intent1.putExtra("fullName", nameRetrieve);
                                                            intent1.putExtra("date", dateRetrieve);
                                                            intent1.putExtra("contact", contact);
                                                            intent1.putExtra("location", location);
                                                            startActivity(intent1);
                                                            return; // Exit the method to avoid displaying the toast message
                                                        } else
                                                            isLeaderOrOnDuty.set(true); // Set the flag to true if the user is on duty but not a leader
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!isLeaderOrOnDuty.get()) {
                                        Toast.makeText(MainActivity.this, "Only volunteers on duty on " + getCurrentDate() + " can make a report", Toast.LENGTH_SHORT).show();
                                    } else
                                        startActivity(new Intent(MainActivity.this,DailyReport_Activity.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


        emergencyButton.setOnClickListener(v -> {
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final AtomicBoolean isLeaderOrOnDuty = new AtomicBoolean(false); // Declare as final AtomicBoolean
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String currentEmail = dataSnapshot.child("email").getValue(String.class);
                        if (currentEmail != null && user != null && currentEmail.equals(user.getEmail())) {
                            String userName = dataSnapshot.child("fullName").getValue(String.class);
                            databaseReference.child("person in charge").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                        String dateRetrieve = dateSnapshot.getKey();
                                        if (dateRetrieve != null) {
                                            for (DataSnapshot userSnapshot : dateSnapshot.getChildren()) {
                                                UserOnDuty userOnDuty = userSnapshot.getValue(UserOnDuty.class);
                                                if (userOnDuty != null) {
                                                    String nameRetrieve = userOnDuty.getName();
                                                    if (getCurrentDate().equals(dateRetrieve) && userName.equals(nameRetrieve)) {
                                                        if (userOnDuty.getRemark()) {
                                                            Intent intent1 = new Intent(MainActivity.this, EmergencyAlertActivity.class);
                                                            intent1.putExtra("verification","verified");
                                                            startActivity(intent1);
                                                            return; // Exit the method to avoid displaying the toast message
                                                        } else
                                                            isLeaderOrOnDuty.set(true); // Set the flag to true if the user is on duty but not a leader
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!isLeaderOrOnDuty.get()) {
                                        Toast.makeText(MainActivity.this, "Only volunteers on duty on " + getCurrentDate() + " can use the emergency alert", Toast.LENGTH_SHORT).show();
                                    } else
                                        startActivity(new Intent(MainActivity.this,EmergencyAlertActivity.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        logoutButton.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            FirebaseAuth.getInstance().signOut();
            Intent intent1 = new Intent(MainActivity.this, Login_activity.class);
            startActivity(intent1);
            finish();
        });
    }

    public String getCurrentDate() {
        // Get the current date
        Date currentDate = new Date();

        // Define the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-M-yyyy", Locale.getDefault());

        // Format the current date as a string
        String formattedDate = dateFormat.format(currentDate);

        // Return the formatted date string
        return formattedDate;
    }
}

