package com.example.srsapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_assign_user, container, false);

        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView emailTextView = view.findViewById(R.id.emailTextView);
        TextView contactTextView = view.findViewById(R.id.contactTextView);

        Bundle args = getArguments();
        if (args != null) {
            nameTextView.setText(args.getString("name"));
            emailTextView.setText(args.getString("email"));
            contactTextView.setText(args.getString("contact"));
        }

        return view;
    }

    public static MyBottomSheetDialogFragment newInstance(User user) {
        MyBottomSheetDialogFragment fragment = new MyBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putString("name", user.getFullName());
        args.putString("email", user.getEmail());
        args.putString("contact", user.getContact());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView emailTextView = view.findViewById(R.id.emailTextView);
        TextView contactTextView = view.findViewById(R.id.contactTextView);
        String nameText = (String) nameTextView.getText(), emailText = (String) emailTextView.getText(), contactText = (String) contactTextView.getText();
        EditText dateEditText = view.findViewById(R.id.dateEditText);
        EditText shiftStartTimeEditText = view.findViewById(R.id.shiftStartTimeEditText);
        EditText shiftEndTimeEditText = view.findViewById(R.id.shiftEndTimeEditText);
        EditText locationEditText = view.findViewById(R.id.locationEditText);
        Spinner locationSpinner = view.findViewById(R.id.locationSpinner);
        CheckBox leaderCheckBox = view.findViewById(R.id.leaderCheckBox);
        Button assignDutyBtn = view.findViewById(R.id.assignButton);

        Bundle args = getArguments();
        if (args != null) {
            nameText = args.getString("name");
            emailText = args.getString("email");
            contactText = args.getString("contact");
            nameTextView.setText("Name: "+nameText);
            emailTextView.setText("Email: "+emailText);
            contactTextView.setText("Contact: "+contactText);
        }

        // Set up the location spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.locations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        dateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        String selectedDate = selectedDayOfMonth + "-" + (selectedMonth + 1) + "-" + selectedYear;
                        dateEditText.setText(selectedDate);
                    }, year, month, dayOfMonth);

            datePickerDialog.show();
        });

        shiftStartTimeEditText.setOnClickListener(v -> {
            CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(getContext(),
                    (view1, hourOfDay, minute) -> {
                        String selectedTime = hourOfDay + ":" + minute;
                        shiftStartTimeEditText.setText(selectedTime);
                    }, 0, 0, true);

            timePickerDialog.show();
        });

        shiftEndTimeEditText.setOnClickListener(v -> {
            CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(getContext(),
                    (view1, hourOfDay, minute) -> {
                        String selectedTime = hourOfDay + ":" + minute;
                        shiftEndTimeEditText.setText(selectedTime);
                    }, 0, 0, true);

            timePickerDialog.show();
        });

        locationEditText.setOnClickListener(v -> {
            locationSpinner.setVisibility(View.VISIBLE);
            locationEditText.setVisibility(View.GONE);
            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedLocation = (String) parent.getItemAtPosition(position);
                    locationEditText.setText(selectedLocation);
                    locationSpinner.setVisibility(View.GONE);
                    locationEditText.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        });

        String finalNameText = nameText;
        String finalContactText = contactText;
        assignDutyBtn.setOnClickListener(v -> {
            String selectedDate = dateEditText.getText().toString();
            String shift = shiftStartTimeEditText.getText().toString() + " - " + shiftEndTimeEditText.getText().toString();
            String selectedLocation = locationEditText.getText().toString();
            boolean leader = leaderCheckBox.isChecked();

            DatabaseReference assignRef = FirebaseDatabase.getInstance().getReference()
                    .child("person in charge")
                    .child(selectedDate);

            UserOnDuty userOnDuty = new UserOnDuty(selectedDate,finalNameText,finalContactText,selectedLocation,shift,leader);
            String personId = assignRef.push().getKey();

            assignRef.child(personId).setValue(userOnDuty)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "The user has been assigned to a duty", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                    });
        });
    }
}
