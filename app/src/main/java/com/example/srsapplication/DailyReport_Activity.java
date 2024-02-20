package com.example.srsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DailyReport_Activity extends AppCompatActivity {
    private DatabaseReference reportsRef;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private SimpleDateFormat dateFormat;
    private Spinner locationSpinner;
    private EditText editName, editPhoneNum, editLocation, editDate, editDescription;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        locationSpinner = findViewById(R.id.locationSpinner);
        editName = findViewById(R.id.editName);
        editPhoneNum = findViewById(R.id.editPhoneNumber);
        editLocation = findViewById(R.id.editLocation);
        editDate = findViewById(R.id.editDate);
        editDescription = findViewById(R.id.editDescription);
        btnSubmit = findViewById(R.id.btnSubmit);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        calendar = Calendar.getInstance();

        dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editDate.setText(dateFormat.format(calendar.getTime()));
        };

        editDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(DailyReport_Activity.this,
                    (view1, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        String selectedDate = selectedDayOfMonth + "-" + (selectedMonth + 1) + "-" + selectedYear;
                        editDate.setText(selectedDate);
                    }, year, month, dayOfMonth);

            datePickerDialog.show();
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(DailyReport_Activity.this,
                R.array.locations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        editLocation.setOnClickListener(v -> {
            editLocation.setVisibility(View.GONE);
            locationSpinner.setVisibility(View.VISIBLE);
        });

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = (String) parent.getItemAtPosition(position);
                editLocation.setText(selectedLocation);
                locationSpinner.setVisibility(View.GONE);
                editLocation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null && !intent.getExtras().isEmpty()) {
            String name, date, contact, location, shift;
            name = intent.getStringExtra("fullName");
            date = intent.getStringExtra("date");
            contact = intent.getStringExtra("contact");
            location = intent.getStringExtra("location");
            editName.setText(name);
            editPhoneNum.setText(contact);
            editDate.setText(date);
            editLocation.setText(location);
            initializeActivity();
        } else {
            showBuilderDialog();
        }
    }

    private void showBuilderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DailyReport_Activity.this);
        builder.setTitle("User verification");
        View viewInflated = LayoutInflater.from(DailyReport_Activity.this).inflate(R.layout.builder_dialog, null);
        final EditText inputName = viewInflated.findViewById(R.id.inputName);
        final EditText inputDate = viewInflated.findViewById(R.id.inputDate);
        final CheckBox remarkCheckbox = viewInflated.findViewById(R.id.remarkCheckbox);
        builder.setView(viewInflated);

        inputDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(DailyReport_Activity.this,
                    (view1, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        String selectedDate = selectedDayOfMonth + "-" + (selectedMonth + 1) + "-" + selectedYear;
                        inputDate.setText(selectedDate);
                    }, year, month, dayOfMonth);

            datePickerDialog.show();
        });

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String date = inputDate.getText().toString().trim();
            boolean isLeader = remarkCheckbox.isChecked();

            // Validate the input fields
            if (name.isEmpty() || date.isEmpty()) {
                Toast.makeText(DailyReport_Activity.this, "Please enter your name and date", Toast.LENGTH_SHORT).show();
                showBuilderDialog();
            } else {
                validateUser(name, date, isLeader);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> finish());

        builder.setCancelable(false);
        builder.show();
    }

    private void validateUser(String name, String date, boolean isLeader) {
        DatabaseReference assignRef = FirebaseDatabase.getInstance().getReference().child("person in charge");

        assignRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isValidUser = false;

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String dateRetrieve = dateSnapshot.getKey();
                    if (dateRetrieve != null) {
                        for (DataSnapshot userSnapshot : dateSnapshot.getChildren()) {
                            UserOnDuty userOnDuty = userSnapshot.getValue(UserOnDuty.class);
                            if (userOnDuty != null) {
                                String nameRetrieve = userOnDuty.getName();
                                String contact = userOnDuty.getContact();
                                if (date.equals(dateRetrieve) && name.equals(nameRetrieve)) {
                                    if (isLeader && Boolean.TRUE.equals(userOnDuty.getRemark())) {
                                        isValidUser = true;
                                        editName.setText(name);
                                        editPhoneNum.setText(contact);
                                        editDate.setText(date);
                                        editLocation.setText(userOnDuty.getLocation());
                                    } else
                                        isValidUser = false;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (isValidUser) {
                    initializeActivity();
                } else {
                    // User is not authorized, show toast message and return to home
                    Toast.makeText(DailyReport_Activity.this, "Only the assigned leader on the date can fill the report", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyReport_Activity.this, "Failed to validate user", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initializeActivity() {
        // Set click listener for the submit button
        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void submitReport() {
        // Get the input values
        String name = editName.getText().toString().trim();
        String phoneNumber = editPhoneNum.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (name.isEmpty() || phoneNumber.isEmpty() || location.isEmpty() || date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if a report already exists for the given date
        reportsRef.orderByChild("date").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(DailyReport_Activity.this, "A report already exists for the given date", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a new report object
                    Report report = new Report(name, phoneNumber, location, date, description);
                    String reportId = reportsRef.push().getKey();

                    reportsRef.child(reportId).setValue(report)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(DailyReport_Activity.this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                                editName.setText("");
                                editPhoneNum.setText("");
                                editLocation.setText("");
                                editDate.setText("");
                                editDescription.setText("");
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(DailyReport_Activity.this, "Failed to submit report", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });

    }
}