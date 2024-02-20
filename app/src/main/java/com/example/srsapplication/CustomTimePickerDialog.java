package com.example.srsapplication;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

public class CustomTimePickerDialog extends TimePickerDialog {
    private boolean mIgnoreEvent = false;

    public CustomTimePickerDialog(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnShowListener(dialog -> {
            TimePicker timePicker = findViewById(getContext().getResources().getIdentifier("timePicker", "id", "android"));
            timePicker.setOnTouchListener((v, event) -> {
                if (!mIgnoreEvent) {
                    mIgnoreEvent = true;
                    return true;
                }
                return false;
            });
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIgnoreEvent = false;
    }
}
