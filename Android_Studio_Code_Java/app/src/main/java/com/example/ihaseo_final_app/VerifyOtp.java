package com.example.ihaseo_final_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class VerifyOtp extends AppCompatActivity {

    EditText otpEditText;
    Button verifyButton;
    String phoneNumber;
    String generatedOTP;
    public static final String EXTRA_OTP = "extra_otp";
    SessionManager sessionManager;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ihaseo-b20e6-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        otpEditText = findViewById(R.id.otpEditText);
        verifyButton = findViewById(R.id.verifyButton);
        sessionManager=new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        String email = userDetails.get(SessionManager.KEY_EMAIL);

        // Retrieve data from intent
        Intent intent = getIntent();
        if (intent != null) {
            phoneNumber = intent.getStringExtra(SendOtp.EXTRA_PHONE_NUMBER);
            generatedOTP = intent.getStringExtra(EXTRA_OTP);
        }

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOTP = otpEditText.getText().toString().trim();
                if (enteredOTP.equals(generatedOTP)) {
                    checkUserRoleAndRedirect(email);
                } else {
                    Toast.makeText(VerifyOtp.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUserRoleAndRedirect(String email) {
        databaseReference.child("users").child(encodeEmail(email)).child("spinnerRole").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.getValue(String.class);
                    if (role != null) {
                        if (role.equals("User")) {
                            startActivity(new Intent(VerifyOtp.this, User_Dashboard.class));
                        } else if (role.equals("Owner")) {
                            startActivity(new Intent(VerifyOtp.this, Dashboard.class));
                        } else {
                            Toast.makeText(VerifyOtp.this, "Invalid role", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    } else {
                        Toast.makeText(VerifyOtp.this, "Role not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerifyOtp.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}