package com.example.ihaseo_final_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class user_appliances_1 extends AppCompatActivity {

    private ToggleButton lightToggle, fanToggle, acToggle, tvToggle;

    private BroadcastReceiver lightOnReceiver, lightOffReceiver;
    private BroadcastReceiver fanOnReceiver, fanOffReceiver;
    private SharedPreferences lightStatePref;
    private SharedPreferences fanStatePref;
    private ColorStateList colorStateList; // ColorStateList reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_appliances1);

        lightToggle = findViewById(R.id.light_toggle);
        fanToggle = findViewById(R.id.fan_toggle);
        acToggle = findViewById(R.id.ac_toggle);
        tvToggle = findViewById(R.id.tv_toggle);

        // Simulate initial device states
        lightToggle.setChecked(false);
        lightToggle.setBackgroundColor(Color.RED);
        fanToggle.setChecked(false);
        fanToggle.setBackgroundColor(Color.RED);
        acToggle.setChecked(false);
        acToggle.setBackgroundColor(Color.RED);
        tvToggle.setChecked(false);
        tvToggle.setBackgroundColor(Color.RED);

        lightStatePref = getSharedPreferences("light_state_pref", MODE_PRIVATE);
        boolean isLightOn = lightStatePref.getBoolean(User_Dashboard.LIGHT_STATE_PREF_KEY, true); // Access using MainActivity.class
        lightToggle.setChecked(isLightOn);
        // Update the background color based on the new state
        int backgroundColor;
        if (isLightOn) {
            backgroundColor = Color.GREEN;
        } else {
            backgroundColor = Color.RED;
        }
        lightToggle.setBackgroundColor(backgroundColor);


        // Register broadcast receiver for light command
        lightOnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("light_on_action")) {
                    lightToggle.setChecked(true);
                    // Optional: Implement additional logic for turning on light (backend interaction)
                }
            }
        };

        registerReceiver(lightOnReceiver, new IntentFilter("light_on_action"));

        // Register broadcast receiver for light command
        lightOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("light_off_action")) {
                    lightToggle.setChecked(false);
                    // Optional: Implement additional logic for turning off light (backend interaction)
                }
            }
        };

        registerReceiver(lightOffReceiver, new IntentFilter("light_off_action"));



        fanStatePref = getSharedPreferences("fan_state_pref", MODE_PRIVATE);
        boolean isFanOn = fanStatePref.getBoolean(User_Dashboard.FAN_STATE_PREF_KEY, true); // Access using MainActivity.class
        fanToggle.setChecked(isFanOn);
        // Update the background color based on the new state
        int fanbackgroundColor;
        if (isFanOn) {
            fanbackgroundColor = Color.GREEN;
        } else {
            fanbackgroundColor = Color.RED;
        }
        fanToggle.setBackgroundColor(fanbackgroundColor);


        // Register broadcast receiver for fan command
        fanOnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("fan_on_action")) {
                    fanToggle.setChecked(true);
                    // Optional: Implement additional logic for turning on fan (backend interaction)
                }
            }
        };

        registerReceiver(fanOnReceiver, new IntentFilter("fan_on_action"));

        // Register broadcast receiver for fan command
        fanOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("fan_off_action")) {
                    fanToggle.setChecked(false);
                    // Optional: Implement additional logic for turning fan light (backend interaction)
                }
            }
        };

        registerReceiver(fanOffReceiver, new IntentFilter("fan_off_action"));

        // Add listeners for toggle buttons
        lightToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the background color based on the new state
                int backgroundColor;
                if (isChecked) {
                    backgroundColor = Color.GREEN;
                } else {
                    backgroundColor = Color.RED;
                }
                lightToggle.setBackgroundColor(backgroundColor);


                // Simulate light state change
                if (isChecked) {
                    System.out.println("Light turned on");
                    Toast.makeText(user_appliances_1.this, "Light Turned On", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Light turned off");
                    Toast.makeText(user_appliances_1.this, "Light Turned Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add listeners for toggle buttons
        fanToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the background color based on the new state
                int backgroundColor;
                if (isChecked) {
                    backgroundColor = Color.GREEN;
                } else {
                    backgroundColor = Color.RED;
                }
                fanToggle.setBackgroundColor(backgroundColor);


                // Simulate light state change
                if (isChecked) {
                    System.out.println("Fan turned on");
                    Toast.makeText(user_appliances_1.this, "Fan Turned On", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Fan turned off");
                    Toast.makeText(user_appliances_1.this, "Fan Turned Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add listeners for toggle buttons
        acToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the background color based on the new state
                int backgroundColor;
                if (isChecked) {
                    backgroundColor = Color.GREEN;
                } else {
                    backgroundColor = Color.RED;
                }
                acToggle.setBackgroundColor(backgroundColor);


                // Simulate light state change
                if (isChecked) {
                    System.out.println("AC turned on");
                    Toast.makeText(user_appliances_1.this, "AC Turned On", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("AC turned off");
                    Toast.makeText(user_appliances_1.this, "AC Turned Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add listeners for toggle buttons
        tvToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the background color based on the new state
                int backgroundColor;
                if (isChecked) {
                    backgroundColor = Color.GREEN;
                } else {
                    backgroundColor = Color.RED;
                }
                tvToggle.setBackgroundColor(backgroundColor);


                // Simulate light state change
                if (isChecked) {
                    System.out.println("TV turned on");
                    Toast.makeText(user_appliances_1.this, "TV Turned On", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("TV turned off");
                    Toast.makeText(user_appliances_1.this, "TV Turned Off", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Dashboard activity (Refresh dashboard)
                startActivity(new Intent(user_appliances_1.this, User_Dashboard.class));
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
                startActivity(new Intent(user_appliances_1.this, User_Rooms.class));
            }
        });
    }
}