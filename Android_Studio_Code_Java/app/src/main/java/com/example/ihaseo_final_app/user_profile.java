package com.example.ihaseo_final_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class user_profile extends AppCompatActivity {
    EditText editTextUsername, editTextMobile, editTextEmail;
    Button buttonProfile;
    SessionManager sessionManager;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ihaseo-b20e6-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        sessionManager = new SessionManager(this); // Initialize SessionManager

        // Check if user is already logged in
        sessionManager.checkLogin();
        // Get user details if logged in
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        String email = userDetails.get(SessionManager.KEY_EMAIL);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonProfile = findViewById(R.id.buttonProfile);

        // Disable editing of email field
        editTextEmail.setEnabled(false);

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_profile.this, Update_Profile.class);
                startActivity(intent);
            }
        });

        // Fetch user data from Firebase and populate EditText fields
        getUserData(email);
        editTextEmail.setText(email);

    }

    private void getUserData(String email) {
        databaseReference.child("users").child(encodeEmail(email)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("editTextUsername").getValue(String.class);
                    String mobile = snapshot.child("editTextMobile").getValue(String.class);

                    // Set fetched data to EditText fields
                    editTextUsername.setText(username);
                    editTextMobile.setText(mobile);

                } else {
                    Log.e("Profile", "User data not found for email: " + email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Profile", "Error retrieving user data: " + error.getMessage());
            }
        });


        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Dashboard activity (Refresh dashboard)
                startActivity(new Intent(user_profile.this, User_Dashboard.class));
                finish(); // Close the current dashboard activity
            }
        });
        /**ImageView voiceImageView = findViewById(R.id.voiceImageView);
         homeImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        // Navigate to Dashboard activity (Refresh dashboard)
        startActivity(new Intent(Rooms.this, voice.class));
        finish(); // Close the current dashboard activity
        }
        });**/
        ImageView roomImageView = findViewById(R.id.roomImageView);
        roomImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RoomActivity
                startActivity(new Intent(user_profile.this, User_Rooms.class));
            }
        });
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}