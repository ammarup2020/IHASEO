package com.example.ihaseo_final_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class SendOtp extends AppCompatActivity {
    EditText phoneNumberEditText;
    Button sendOTPButton;

    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    public static final String EXTRA_PHONE_NUMBER = "extra_phone_number";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        sendOTPButton = findViewById(R.id.sendOTPButton);

        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEditText.getText().toString().trim();

                // Check if the entered phone number matches the desired format
                if (!isValidPhoneNumber(phoneNumber)) {
                    Toast.makeText(SendOtp.this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Generate OTP
                String otp = generateOTP();

                // Send OTP via SMS
                sendSMS(phoneNumber, "Your OTP is: " + otp);

                // Start OTP Verification Activity
                Intent intent = new Intent(SendOtp.this, VerifyOtp.class);
                intent.putExtra(EXTRA_PHONE_NUMBER, phoneNumber);
                intent.putExtra(VerifyOtp.EXTRA_OTP, otp);
                startActivity(intent);
            }
        });

        // Request SMS permission if not granted already
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\+?\\d{10,12}$");
    }

    private String generateOTP() {
        // Generate a 6-digit OTP
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SendOtp.this, VerifyOtp.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now send SMS
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}