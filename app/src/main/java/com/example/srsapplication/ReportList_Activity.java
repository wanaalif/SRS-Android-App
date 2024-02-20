package com.example.srsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportList_Activity extends AppCompatActivity {
    private DatabaseReference reportRef;
    private BottomSheetDialog bottomSheetDialog;
    private View view;
    private TextView textFullName, textPhoneNum, textDate, textLocation, textDescription;
    private Button btnDeleteReport;
    private String selectedDate, selectedName, selectedPhone, selectedLocation, selectedDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        reportRef = FirebaseDatabase.getInstance().getReference("reports");
        bottomSheetDialog = new BottomSheetDialog(ReportList_Activity.this);
        view = LayoutInflater.from(ReportList_Activity.this).inflate(R.layout.bottom_sheet_report_list, null);
        bottomSheetDialog.setContentView(view);
        textFullName = view.findViewById(R.id.textName);
        textPhoneNum = view.findViewById(R.id.textPhoneNum);
        textDate = view.findViewById(R.id.textDate);
        textLocation = view.findViewById(R.id.textLocation);
        textDescription = view.findViewById(R.id.textDescription);
        btnDeleteReport = view.findViewById(R.id.deleteReportButton);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
            reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isReportFound = false; // Flag to track if a report is found

                    for (DataSnapshot reportSnapshot : snapshot.getChildren()) {
                        Report report = reportSnapshot.getValue(Report.class);
                        if (report != null && report.getDate().equals(selectedDate)) {
                            String name = report.getName();
                            String phoneNumber = report.getPhoneNumber();
                            String location = report.getLocation();
                            String date = report.getDate();
                            String description = report.getDescription();

                            selectedName = name;
                            selectedPhone = phoneNumber;
                            selectedLocation = location;
                            selectedDesc = description;

                            textFullName.setText("Name: " + name);
                            textPhoneNum.setText("Phone Number: " + phoneNumber);
                            textDate.setText("Date: " + date);
                            textLocation.setText("Location: " + location);
                            textDescription.setText("Description: " + description);
                            bottomSheetDialog.show();

                            isReportFound = true; // Set flag to true as a report is found
                            break;
                        }
                    }

                    if (!isReportFound) {
                        // No report found on the selected date, display toast message
                        Toast.makeText(ReportList_Activity.this, "There is no report on this date", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors
                }
            });
        });

        btnDeleteReport.setOnClickListener(v -> {
            if (selectedDate != null) {

                // Show an alert dialog for confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(ReportList_Activity.this);
                builder.setTitle("Delete Report");
                builder.setMessage("Are you sure you want to delete this report?");

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    // Delete the report from the database
                    deleteReport(selectedName, selectedPhone, selectedLocation, selectedDate, selectedDesc);
                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog
                    dialog.dismiss();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void deleteReport(String name, String phoneNumber, String location, String date, String description){
        reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot reportSnapshot : snapshot.getChildren()) {
                    Report report = reportSnapshot.getValue(Report.class);
                    String reportName = report.getName();
                    String reportPhoneNumber = report.getPhoneNumber();
                    String reportLocation = report.getLocation();
                    String reportDate = report.getDate();
                    String reportDescription = report.getDescription();

                    if (reportName != null && reportName.equals(name) && reportPhoneNumber != null && reportPhoneNumber.equals(phoneNumber)
                                    && reportLocation != null && reportLocation.equals(location) && reportDate != null && reportDate.equals(date)
                                    && reportDescription != null && reportDescription.equals(description)) {
                        reportSnapshot.getRef().removeValue();
                        refresh();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void refresh() {
        bottomSheetDialog.dismiss();
        finish();
    }
}