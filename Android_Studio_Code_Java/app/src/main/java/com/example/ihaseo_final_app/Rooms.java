package com.example.ihaseo_final_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Rooms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        TextView bedroom=findViewById(R.id.bedroom);
        bedroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open rooms page
                startActivity(new Intent(Rooms.this, Appliance_1.class));
            }
        });
        TextView livingroom=findViewById(R.id.livingroom);
        livingroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Rooms.this, Appliances.class));
            }
        });
        TextView studyroom=findViewById(R.id.studyroom);
        studyroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open rooms page
                startActivity(new Intent(Rooms.this, Appliance_1.class));
            }
        });
        TextView kitchen=findViewById(R.id.kitchen);
        kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open rooms page
                startActivity(new Intent(Rooms.this, Kit_Appliance.class));
            }
        });
        TextView bathroom=findViewById(R.id.bathroom);
        bathroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open rooms page
                startActivity(new Intent(Rooms.this, Bath_appliance.class));
            }
        });
        TextView familyroom=findViewById(R.id.familyroom);
        familyroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open rooms page
                startActivity(new Intent(Rooms.this, Appliance_1.class));
            }
        });
        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Dashboard activity (Refresh dashboard)
                startActivity(new Intent(Rooms.this, Dashboard.class));
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
                startActivity(new Intent(Rooms.this, Rooms.class));
            }
        });
    }

}