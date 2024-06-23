package com.example.ihaseo_final_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class update_user_profile extends AppCompatActivity {

    EditText editTextNewUsername, editTextNewMobile;
    Button buttonSave;
    SessionManager sessionManager;
    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReferenceFromUrl("https://ihaseo-b20e6-default-rtdb.firebaseio.com/");;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        editTextNewUsername = findViewById(R.id.editTextNEWUsername);
        editTextNewMobile = findViewById(R.id.editTextNEWMobile);
        buttonSave = findViewById(R.id.buttonSave);

        sessionManager = new SessionManager(this);
        String userEmail = sessionManager.getUserEmail(); // Retrieve user email from session manager

        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(encodeEmail(userEmail));

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editTextNewUsername.getText().toString().trim();
                String newMobile = editTextNewMobile.getText().toString().trim();

                // Validate username
                if (!isValidUsername(newUsername)) {
                    editTextNewUsername.setError("Please enter a valid username");
                    return;
                }

                // Validate mobile number
                if (!isValidMobile(newMobile)) {
                    editTextNewMobile.setError("Please enter a valid mobile number");
                    return;
                }

                // Update username and mobile in Firebase
                updateProfile(newUsername, newMobile);
            }
        });
    }

    // Method to validate username
    private boolean isValidUsername(String username) {
        // Username should contain only alphabets
        return username.matches("[a-zA-Z]+");
    }

    // Method to validate mobile number
    private boolean isValidMobile(String mobile) {
        // Mobile number should contain exactly 11 digits
        return Patterns.PHONE.matcher(mobile).matches() && mobile.length() == 11;
    }

    // Method to update username and mobile in Firebase
    private void updateProfile(String newUsername, String newMobile) {
        // Update username and mobile in Firebase Realtime Database
        databaseReference.child("editTextUsername").setValue(newUsername);
        databaseReference.child("editTextMobile").setValue(newMobile);

        Toast.makeText(update_user_profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(update_user_profile.this, user_profile.class);
        startActivity(intent);
        // Finish the activity or take any other action
        finish();
    }

    // Method to encode email for use as a Firebase database key
    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }

}