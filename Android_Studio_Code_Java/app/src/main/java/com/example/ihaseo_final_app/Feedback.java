package com.example.ihaseo_final_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Feedback extends AppCompatActivity {

    EditText editTextFeedback;
    Button buttonSubmitFeedback;
    SessionManager sessionManager;
    public String adminEmail = "shahnilanaheed@gmail.com"; // Change this to your admin's email address

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback2);

        sessionManager = new SessionManager(this);

        editTextFeedback = findViewById(R.id.editTextFeedback);
        buttonSubmitFeedback = findViewById(R.id.buttonSubmitFeedback);

        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });
        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Dashboard activity (Refresh dashboard)
                startActivity(new Intent(Feedback.this, Dashboard.class));
                finish(); // Close the current dashboard activity
            }
        });
        /**ImageView voiceImageView = findViewById(R.id.voiceImageView);
         homeImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        // Navigate to Dashboard activity (Refresh dashboard)
        startActivity(new Intent(Dashboard.this, voice.class));
        finish(); // Close the current dashboard activity
        }
        });**/
        ImageView roomImageView = findViewById(R.id.roomImageView);
        roomImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RoomActivity
                startActivity(new Intent(Feedback.this, Rooms.class));
            }
        });
    }

    public void sendFeedback() {
        String feedback = editTextFeedback.getText().toString().trim();

        // Retrieve user's email from session
        String userEmail = sessionManager.getUserEmail();

        // Check if user email is available
        if (userEmail != null && !userEmail.isEmpty()) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{adminEmail});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from " + userEmail);
            emailIntent.putExtra(Intent.EXTRA_TEXT, feedback);

            // Suggest to the user to change the sender's email address in the email client
           // emailIntent.putExtra(Intent.EXTRA_TEXT, "Please change the sender's email address to: " + userEmail);

            try {
                startActivity(Intent.createChooser(emailIntent, "Send feedback via..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User email not found. Please log in again.", Toast.LENGTH_SHORT).show();
            // Optionally, you can navigate the user to the login screen here
        }
    }
}