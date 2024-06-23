package com.example.ihaseo_final_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class User_Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        final TextView privacyPolicyText = findViewById(R.id.privacy_policy_text);
        Button privacyPolicyBtn = findViewById(R.id.privacy_policy_btn);
        privacyPolicyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (privacyPolicyText.getVisibility() == View.VISIBLE) {
                    privacyPolicyText.setVisibility(View.GONE);
                } else {
                    privacyPolicyText.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextView dataSecurityText = findViewById(R.id.data_security_text);
        Button dataSecurityBtn = findViewById(R.id.data_security_btn);
        dataSecurityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataSecurityText.getVisibility() == View.VISIBLE) {
                    dataSecurityText.setVisibility(View.GONE);
                } else {
                    dataSecurityText.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextView faqsText = findViewById(R.id.faqs_text);
        Button faqsBtn = findViewById(R.id.faqs_btn);
        faqsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (faqsText.getVisibility() == View.VISIBLE) {
                    faqsText.setVisibility(View.GONE);
                } else {
                    faqsText.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextView helpSupportText = findViewById(R.id.help_support_text);
        Button helpSupportBtn = findViewById(R.id.help_support_btn);
        helpSupportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpSupportText.getVisibility() == View.VISIBLE) {
                    helpSupportText.setVisibility(View.GONE);
                } else {
                    helpSupportText.setVisibility(View.VISIBLE);
                }
            }
        });

        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Dashboard activity (Refresh dashboard)
                startActivity(new Intent(User_Setting.this, User_Dashboard.class));
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
                startActivity(new Intent(User_Setting.this, User_Rooms.class));
            }
        });
    }
}