package com.example.ihaseo_final_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Forget extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ihaseo-b20e6-default-rtdb.firebaseio.com/");
    private EditText editTextEmail;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        editTextEmail = findViewById(R.id.editTextEmaill);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();

                if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // Check if the user exists in the database
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(encodeEmail(email))) {
                                String password = dataSnapshot.child(encodeEmail(email)).child("editTextPassword").getValue(String.class);
                                showToast("Password: " + password);
                                startActivity(new Intent(Forget.this, Signout.class));
                                finish();
                            } else {
                                showToast("Email address not found");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            showToast("Error: " + databaseError.getMessage());
                        }
                    });
                } else {
                    showToast("Enter a valid email address");
                }
            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}