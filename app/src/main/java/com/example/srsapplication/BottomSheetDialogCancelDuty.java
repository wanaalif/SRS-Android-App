package com.example.srsapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BottomSheetDialogCancelDuty extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_cancel_duty, container, false);

        TextView dateTextView = view.findViewById(R.id.dateTextView);
        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView contactTextView = view.findViewById(R.id.contactTextView);
        TextView locationTextView = view.findViewById(R.id.locationTextView);
        TextView shiftTextView = view.findViewById(R.id.shiftTextView);
        TextView remark = view.findViewById(R.id.remark);


        Bundle args = getArguments();
        if (args != null) {
            dateTextView.setText(args.getString("date"));
            nameTextView.setText(args.getString("name"));
            contactTextView.setText(args.getString("contact"));
            locationTextView.setText(args.getString("location"));
            shiftTextView.setText(args.getString("shift"));
            remark.setText(args.getString("remark"));
        }

        return view;
    }

    public static BottomSheetDialogCancelDuty newInstance(UserOnDuty user) {
        BottomSheetDialogCancelDuty fragment = new BottomSheetDialogCancelDuty();
        Bundle args = new Bundle();
        args.putString("date",user.getDate());
        args.putString("name", user.getName());
        args.putString("contact", user.getContact());
        args.putString("location", user.getLocation());
        args.putString("shift", user.getShift());
        if(user.getRemark()){
            args.putString("remark", "leader");
        } else
            args.putString("remark", "");

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView contactTextView = view.findViewById(R.id.contactTextView);
        TextView locationTextView = view.findViewById(R.id.locationTextView);
        TextView shiftTextView = view.findViewById(R.id.shiftTextView);
        TextView remark = view.findViewById(R.id.remark);
        String nameText = (String) nameTextView.getText(), contactText = (String) contactTextView.getText(),
                locationText = (String) locationTextView.getText(), shiftText = (String) shiftTextView.getText(),
                remarkText = (String) remark.getText(), dateText = (String) dateTextView.getText();
        Button cancelDutyBtn = view.findViewById(R.id.cancelButton);

        Bundle args = getArguments();
        if (args != null) {
            nameText = args.getString("name");
            contactText = args.getString("contact");
            locationText = args.getString("location");
            shiftText = args.getString("shift");
            remarkText = args.getString("remark");

            dateTextView.setText("Date: "+dateText);
            nameTextView.setText("Name: "+nameText);
            contactTextView.setText("Contact: "+contactText);
            shiftTextView.setText("Shift: "+shiftText);
            locationTextView.setText("Location: " + locationText);
            remark.setText("remark: "+remarkText);
        }

        String finalNameText = nameText;
        String finalContactText = contactText;
        String finalDate = dateText;

        cancelDutyBtn.setOnClickListener(v -> {

            DatabaseReference assignRef = FirebaseDatabase.getInstance().getReference()
                    .child("person in charge");

            assignRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        if(finalDate.equals(date)){
                            for(DataSnapshot userSnapshot : dateSnapshot.getChildren()){
                                UserOnDuty userOnDuty = userSnapshot.getValue(UserOnDuty.class);
                                if (userOnDuty != null) {
                                    String name = userOnDuty.getName();
                                    String contact = userOnDuty.getContact();

                                    if (finalNameText.equals(name) && finalContactText.equals(contact)) {
                                        Toast.makeText(getContext(), "The user on duty has been cancelled successfully", Toast.LENGTH_SHORT).show();
                                        userSnapshot.getRef().removeValue();  // Remove the specific value
                                        break;
                                    }
                                }
                            }

                        }
                    }
                    dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }
}
