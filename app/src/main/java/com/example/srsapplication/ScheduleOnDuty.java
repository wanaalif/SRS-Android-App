package com.example.srsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ScheduleOnDuty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_on_duty);

        retrieveUserData();
    }

    public void retrieveUserData() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("person in charge");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserOnDuty> userList = new ArrayList<>();
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    if (date != null) {
                        for (DataSnapshot userSnapshot : dateSnapshot.getChildren()) {
                            UserOnDuty userOnDuty = userSnapshot.getValue(UserOnDuty.class);
                            if (userOnDuty != null) {
                                String name = userOnDuty.getName();
                                String contact = userOnDuty.getContact();
                                String location = userOnDuty.getLocation();
                                String shift = userOnDuty.getShift();
                                Boolean remark = userOnDuty.getRemark();
                                UserOnDuty user = new UserOnDuty(date, name, contact, location, shift, remark);
                                userList.add(user);
                            }
                        }
                    }
                }

                // Sort the user list based on the date
                Collections.sort(userList, new Comparator<UserOnDuty>() {
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

                // Create and set up the RecyclerView adapter
                OnDutyAdapter adapter = new OnDutyAdapter(userList, user -> {});
                RecyclerView recyclerView = findViewById(R.id.recycler_view_schedule1);
                recyclerView.setLayoutManager(new LinearLayoutManager(ScheduleOnDuty.this));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}