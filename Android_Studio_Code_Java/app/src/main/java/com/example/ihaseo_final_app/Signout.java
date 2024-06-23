package com.example.ihaseo_final_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Signout extends AppCompatActivity {
    private SessionManager sessionManager;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Find the logout button in the layout
        logoutButton = findViewById(R.id.logoutButton);

        // Set OnClickListener for the logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call logout method from SessionManager
                sessionManager.logoutUser();

                // Redirect user to MainActivity
                Intent intent = new Intent(Signout.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
                startActivity(intent);

                finish(); // Finish current activity
            }
        });
    }
}