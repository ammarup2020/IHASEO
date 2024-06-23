package com.example.ihaseo_final_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class Plan_Owner extends AppCompatActivity {

    private DatabaseReference rootDatabaseref, rootDatabaseref2;
    private TextView total_cost;
    private LinearLayout dynamicContentLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_owner);

        dynamicContentLayout = findViewById(R.id.dynamicContentLayout);

        rootDatabaseref = FirebaseDatabase.getInstance().getReference().child("plan");

        rootDatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Clear existing views to prevent duplication
                    dynamicContentLayout.removeAllViews();

                    // Iterate through each child (each day) under "plan"
                    for (DataSnapshot daySnapshot : snapshot.getChildren()) {
                        // Create a TextView to display the day
                        TextView dayTextView = new TextView(Plan_Owner.this);
                        String day = daySnapshot.getKey(); // Get the day (e.g., "Day1", "Day2", etc.)
                        dayTextView.setText(day);
                        dayTextView.setTextSize(20);
                        dayTextView.setTextColor(Color.BLACK);
                        dynamicContentLayout.addView(dayTextView);

                        // Iterate through each sub-child (each item within a day)
                        for (DataSnapshot itemSnapshot : daySnapshot.getChildren()) {
                            // Extract data from the item
                            String device = itemSnapshot.child("device").getValue(String.class);
                            int costPerDay = itemSnapshot.child("cost_per_day").getValue(Integer.class);
                            int duration = itemSnapshot.child("duration").getValue(Integer.class);
                            String startTime = itemSnapshot.child("start_time").getValue(String.class);

                            // Create a TextView to display the item details
                            TextView itemTextView = new TextView(Plan_Owner.this);
                            String itemText = "Device: " + device + ", Cost per day: " + costPerDay + ", Duration: " + duration + ", Start time: " + startTime;
                            itemTextView.setText(itemText);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 10, 0, 10); // Add margin for separation
                            itemTextView.setLayoutParams(layoutParams);
                            dynamicContentLayout.addView(itemTextView);

                            // Add a separating line
                            View lineView = new View(Plan_Owner.this);
                            LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2); // Adjust the height of the line as needed
                            lineLayoutParams.setMargins(0, 5, 0, 5); // Add margin for separation
                            lineView.setLayoutParams(lineLayoutParams);
                            lineView.setBackgroundColor(Color.LTGRAY);
                            dynamicContentLayout.addView(lineView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                android.util.Log.e("Plan_User", "DatabaseError: " + error.getMessage());
            }
        });

        rootDatabaseref2 = FirebaseDatabase.getInstance().getReference().child("total_cost_30_days");
        total_cost = findViewById(R.id.totalCostTextView);

        rootDatabaseref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalCost = snapshot.getValue(Double.class);
                    DecimalFormat decimalFormat = new DecimalFormat("0.00"); // Format to display only two decimal points
                    String formattedTotalCost = decimalFormat.format(totalCost);
                    total_cost.setText(formattedTotalCost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Plan_User", "DatabaseError: " + error.getMessage());
            }
        });





        // Initialize the button
        FloatingActionButton add_plan = findViewById(R.id.add_plan);
        // Set OnClickListener for the button
        add_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the next activity
                Intent intent = new Intent(Plan_Owner.this, Generate_Plan.class);
                startActivity(intent);
            }
        });

        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Dashboard activity (Refresh dashboard)
                startActivity(new Intent(Plan_Owner.this, Dashboard.class));
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
                startActivity(new Intent(Plan_Owner.this, Rooms.class));
            }
        });

    }
}
