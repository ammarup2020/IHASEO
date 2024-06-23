package com.example.ihaseo_final_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Dashboard extends AppCompatActivity {

    SessionManager sessionManager;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    public static final String LIGHT_STATE_PREF_KEY = "light_state";
    public static final String FAN_STATE_PREF_KEY = "fan_state";
    public static final String AC_STATE_PREF_KEY = "ac_state";
    public static final String TV_STATE_PREF_KEY = "tv_state";
    public static final String FRIDGE_STATE_PREF_KEY = "fridge_state";

    private SharedPreferences lightStatePref, lightStatePreflr, lightStatePrefbath, lightStatePrefkit;
    private SharedPreferences fanStatePref, fanStatePreflr, fanStatePrefbath, fanStatePrefkit;
    private SharedPreferences acStatePref, acStatePreflr, acStatePrefbath, acStatePrefkit;
    private SharedPreferences fridgeStatePref, fridgeStatePreflr, fridgeStatePrefbath, fridgeStatePrefkit;
    private SharedPreferences tvStatePref, tvStatePreflr, tvStatePrefbath, tvStatePrefkit;
    TextView welcomeTextView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ihaseo-b20e6-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        lightStatePref = getSharedPreferences("light_state_pref", MODE_PRIVATE);
        lightStatePreflr = getSharedPreferences("light_state_pref_lr", MODE_PRIVATE);
        lightStatePrefbath = getSharedPreferences("light_state_pref_bath", MODE_PRIVATE);
        lightStatePrefkit = getSharedPreferences("light_state_pref_kit", MODE_PRIVATE);
        fanStatePref = getSharedPreferences("fan_state_pref", MODE_PRIVATE);
        fanStatePreflr = getSharedPreferences("fan_state_pref_lr", MODE_PRIVATE);
        fanStatePrefbath = getSharedPreferences("fan_state_pref_bath", MODE_PRIVATE);
        fanStatePrefkit = getSharedPreferences("fan_state_pref_kit", MODE_PRIVATE);
        acStatePref = getSharedPreferences("ac_state_pref", MODE_PRIVATE);
        acStatePreflr = getSharedPreferences("ac_state_pref_lr", MODE_PRIVATE);
        acStatePrefbath = getSharedPreferences("ac_state_pref_bath", MODE_PRIVATE);
        acStatePrefkit = getSharedPreferences("ac_state_pref_kit", MODE_PRIVATE);
        tvStatePref = getSharedPreferences("tv_state_pref", MODE_PRIVATE);
        tvStatePreflr = getSharedPreferences("tv_state_pref_lr", MODE_PRIVATE);
        tvStatePrefbath = getSharedPreferences("tv_state_pref_bath", MODE_PRIVATE);
        tvStatePrefkit = getSharedPreferences("tv_state_pref_kit", MODE_PRIVATE);
        fridgeStatePref = getSharedPreferences("fridge_state_pref", MODE_PRIVATE);
        fridgeStatePreflr = getSharedPreferences("fridge_state_pref_lr", MODE_PRIVATE);
        fridgeStatePrefbath = getSharedPreferences("fridge_state_pref_bath", MODE_PRIVATE);
        fridgeStatePrefkit = getSharedPreferences("fridge_state_pref_kit", MODE_PRIVATE);



        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        sessionManager = new SessionManager(this); // Initialize SessionManager

        // Check if user is already logged in
        sessionManager.checkLogin();
        // Get user details if logged in
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        String email = userDetails.get(SessionManager.KEY_EMAIL);

        // Fetch username from Firebase database based on the logged-in user's email
        fetchUsername(email);

        // Log user access to the dashboard
        Log.d("Dashboard", "User accessed dashboard. Email: " + email);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        TextView profileTextView = findViewById(R.id.ProfileTextView);
        profileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open profile page
                startActivity(new Intent(Dashboard.this, Profile.class));
            }
        });
        TextView roomsTextView = findViewById(R.id.roomsTextView);
        roomsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open rooms page
                startActivity(new Intent(Dashboard.this, Rooms.class));
            }
        });
        /**TextView PlanTextView = findViewById(R.id.PlanTextView);
        roomsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open rooms page
                startActivity(new Intent(Dashboard.this, Plan.class));
            }
        });**/

        TextView settingsTextView = findViewById(R.id.settingsTextView);
        settingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open settings page
                startActivity(new Intent(Dashboard.this, Setting.class));
            }
        });
        TextView feedbackTextView = findViewById(R.id.feedbackTextView);
        feedbackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open feedback page
                startActivity(new Intent(Dashboard.this, Feedback.class));
            }
        });
        TextView logoutTextView = findViewById(R.id.logoutTextView);
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call logout method from session manager
                sessionManager.logoutUser();
                startActivity(new Intent(Dashboard.this,Signin.class));
                finish();
            }
        });
        TextView planTextView = findViewById(R.id.planTextView);
        planTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Dashboard activity (Refresh dashboard)
                startActivity(new Intent(Dashboard.this, Plan_Owner.class));
                finish(); // Close the current dashboard activity
            }
        });
        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Dashboard activity (Refresh dashboard)
                startActivity(new Intent(Dashboard.this, Dashboard.class));
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
                startActivity(new Intent(Dashboard.this, Rooms.class));
            }
        });
    }
    private void fetchUsername(String email) {
        databaseReference.child("users").child(encodeEmail(email)).child("editTextUsername").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                if (username != null) {
                    // Trigger notification with personalized welcome message
                    sendNotification("Welcome " + username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void sendNotification(String message) {
        String notificationTitle = "IHASEO";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(notificationTitle)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set notification priority
                .setAutoCancel(true); // Automatically remove the notification when tapped

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel if the Android version is Oreo or higher
            NotificationChannel channel = new NotificationChannel("1", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Show notification
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    // Method to start voice recognition when the button is clicked
    public void startVoiceRecognition(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Speech recognition not supported on your device", Toast.LENGTH_SHORT).show();
        }
    }

    // Process the speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = result.get(0);

                // Process the spoken text and potentially interact with TestActivity
                processVoiceCommand(spokenText);
            }
        }
    }

    // Process voice commands and potentially interact with TestActivity
    private void processVoiceCommand(String spokenText) {
        // Convert spoken text to lowercase and trim leading/trailing spaces
        spokenText = spokenText.toLowerCase().trim();

        if (spokenText.contains("turn on the lights of bedroom"))
        {
            lightStatePref.edit().putBoolean(LIGHT_STATE_PREF_KEY, true).apply();
            sendBroadcast(new Intent("light_on_action"));

            Toast.makeText(this, "Light turned On", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn on the lights of living room"))
        {
            lightStatePreflr.edit().putBoolean(LIGHT_STATE_PREF_KEY, true).apply();
            sendBroadcast(new Intent("light_on_action"));

            Toast.makeText(this, "Light turned On", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn on the lights of bath"))
        {
            lightStatePrefbath.edit().putBoolean(LIGHT_STATE_PREF_KEY, true).apply();
            sendBroadcast(new Intent("light_on_action"));

            Toast.makeText(this, "Light turned On", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn on the lights of kit"))
        {
            lightStatePrefkit.edit().putBoolean(LIGHT_STATE_PREF_KEY, true).apply();
            sendBroadcast(new Intent("light_on_action"));

            Toast.makeText(this, "Light turned On", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn off the lights of bedroom"))
        {
            lightStatePref.edit().putBoolean(LIGHT_STATE_PREF_KEY, false).apply();
            sendBroadcast(new Intent("light_off_action"));

            Toast.makeText(this, "light Turned Off", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn off the lights of living room"))
        {
            lightStatePreflr.edit().putBoolean(LIGHT_STATE_PREF_KEY, false).apply();
            sendBroadcast(new Intent("light_off_action"));

            Toast.makeText(this, "light Turned Off", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn off the lights of bath"))
        {
            lightStatePrefbath.edit().putBoolean(LIGHT_STATE_PREF_KEY, false).apply();
            sendBroadcast(new Intent("light_off_action"));

            Toast.makeText(this, "light Turned Off", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn off the lights of kitchen"))
        {
            lightStatePrefkit.edit().putBoolean(LIGHT_STATE_PREF_KEY, false).apply();
            sendBroadcast(new Intent("light_off_action"));

            Toast.makeText(this, "light Turned Off", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn on the fans of bedroom"))
        {
            fanStatePref.edit().putBoolean(FAN_STATE_PREF_KEY, true).apply();
            sendBroadcast(new Intent("fan_on_action"));

            Toast.makeText(this, "Fan turned On", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn on the fans of living room"))
        {
            fanStatePreflr.edit().putBoolean(FAN_STATE_PREF_KEY, true).apply();
            sendBroadcast(new Intent("fan_on_action"));

            Toast.makeText(this, "Fan turned On", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn on the fans of bathroom"))
        {
            fanStatePrefbath.edit().putBoolean(FAN_STATE_PREF_KEY, true).apply();
            sendBroadcast(new Intent("fan_on_action"));

            Toast.makeText(this, "Fan turned On", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn on the fans of kitchen"))
        {
            fanStatePrefkit.edit().putBoolean(FAN_STATE_PREF_KEY, true).apply();
            sendBroadcast(new Intent("fan_on_action"));

            Toast.makeText(this, "Fan turned On", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn off the fans of bedroom"))
        {
            fanStatePref.edit().putBoolean(FAN_STATE_PREF_KEY, false).apply();
            sendBroadcast(new Intent("fan_off_action"));

            Toast.makeText(this, "Fan Turned Off", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn off the fans of living room"))
        {
            fanStatePreflr.edit().putBoolean(FAN_STATE_PREF_KEY, false).apply();
            sendBroadcast(new Intent("fan_off_action"));

            Toast.makeText(this, "Fan Turned Off", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn off the fans of bath"))
        {
            fanStatePrefbath.edit().putBoolean(FAN_STATE_PREF_KEY, false).apply();
            sendBroadcast(new Intent("fan_off_action"));

            Toast.makeText(this, "Fan Turned Off", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn off the fans of kitchen"))
        {
            fanStatePrefkit.edit().putBoolean(FAN_STATE_PREF_KEY, false).apply();
            sendBroadcast(new Intent("fan_off_action"));

            Toast.makeText(this, "light Turned Off", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn on the fridge in kitchen"))
        {
            fridgeStatePrefkit.edit().putBoolean(FRIDGE_STATE_PREF_KEY, true).apply();
            sendBroadcast(new Intent("fridge_on_action"));

            Toast.makeText(this, "Fridge turned On", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("turn off the fridge in kitchen"))
        {
            fridgeStatePrefkit.edit().putBoolean(FRIDGE_STATE_PREF_KEY, false).apply();
            sendBroadcast(new Intent("fridge_off_action"));

            Toast.makeText(this, "Fridge Turned Off", Toast.LENGTH_SHORT).show();
        }
        else if (spokenText.contains("open feedback"))
        {
            Intent intent = new Intent(this, Feedback.class);
            startActivity(intent);
        }
        else if (spokenText.contains("open setting"))
        {
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
        }
        else if (spokenText.contains("open plan"))
        {
            Intent intent = new Intent(this, Plan_Owner.class);
            startActivity(intent);
        }
        else if (spokenText.contains("open room"))
        {
            Intent intent = new Intent(this, Rooms.class);
            startActivity(intent);
        }
        //else if (spokenText.contains("open profile"))
        //{
        //    Intent intent = new Intent(this, ContactsContract.Profile.class);
        //    startActivity(intent);
        //}
        else if (spokenText.contains("Logout"))
        {
            // Call logout method from session manager
            sessionManager.logoutUser();
            startActivity(new Intent(Dashboard.this,Signin.class));
            finish();
        }
        else
        {
            Toast.makeText(this, "No Device Detected", Toast.LENGTH_SHORT).show();
        }
    }



    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}