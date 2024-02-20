package com.example.srsapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class EmergencyAlertActivity extends AppCompatActivity {
    private ImageView photoImageView;
    private EditText messageEditText;
    private CheckBox ketuaKg, ketuaSRS;
    private ActivityResultLauncher<String> galleryLauncher;
    private Button sendButton, callButton;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_alert);

        ketuaKg = findViewById(R.id.contactKetuaKg);
        ketuaSRS = findViewById(R.id.contactKetuaSRS);
        photoImageView = findViewById(R.id.photoImageView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        callButton = findViewById(R.id.callButton);

        photoImageView.setOnClickListener(v -> openGallery());

        sendButton.setOnClickListener(v -> {
            sendMessage();
            finish();
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                            photoImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        callButton.setOnClickListener(v -> {
            startActivity(new Intent(EmergencyAlertActivity.this,EmergencyCallActivity.class));
            finish();
        });

        Intent intent = getIntent();
        if(intent == null )
            showBuilderDialog();
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void sendMessage() {
        message = messageEditText.getText().toString();
        String phoneNumbers;

        if (message.isEmpty()) {
            Toast.makeText(EmergencyAlertActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if WhatsApp is installed on the device
        if (isWhatsAppInstalled()) {
            Uri photoUri = null;
            if (photoImageView.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
                photoUri = getImageUri(bitmap);
            }
            if(ketuaKg.isChecked()){
                phoneNumbers = "601117587457";
                sendWhatsAppMessage(phoneNumbers, message, photoUri);
            }
            if(ketuaSRS.isChecked()){
                phoneNumbers = "60125574445";
                sendWhatsAppMessage(phoneNumbers, message, photoUri);
            }

        } else {
            Toast.makeText(EmergencyAlertActivity.this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isWhatsAppInstalled() {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void sendWhatsAppMessage(String phoneNumber, String message, Uri photoUri) {
        Intent intent;
        intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        if (photoUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, photoUri);
            intent.setType("image/*");
        }

        intent.putExtra("jid", phoneNumber + "@s.whatsapp.net");
        intent.setPackage("com.whatsapp");
        startActivity(intent);
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image", null);
        return Uri.parse(path);
    }

    private void showBuilderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyAlertActivity.this);
        builder.setTitle("User verification");
        View viewInflated = LayoutInflater.from(EmergencyAlertActivity.this).inflate(R.layout.builder_dialog, null);
        final EditText inputName = viewInflated.findViewById(R.id.inputName);
        final EditText inputDate = viewInflated.findViewById(R.id.inputDate);
        final CheckBox remarkCheckbox = viewInflated.findViewById(R.id.remarkCheckbox);
        builder.setView(viewInflated);

        inputDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EmergencyAlertActivity.this,
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
                Toast.makeText(EmergencyAlertActivity.this, "Please enter your name and date", Toast.LENGTH_SHORT).show();
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
                    if(dateRetrieve != null){
                        for(DataSnapshot userSnapshot: dateSnapshot.getChildren()){
                            UserOnDuty userOnDuty = userSnapshot.getValue(UserOnDuty.class);
                            if (userOnDuty != null) {
                                String nameRetrieve = userOnDuty.getName();
                                if (date.equals(dateRetrieve) && name.equals(nameRetrieve)) {
                                    if (isLeader && Boolean.TRUE.equals(userOnDuty.getRemark())) {
                                        isValidUser = true;
                                    } else
                                        isValidUser = false;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!isValidUser) {
                    Toast.makeText(EmergencyAlertActivity.this, "Only the assigned leader on the date can make an emergency alert", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmergencyAlertActivity.this, "Failed to verify user", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
