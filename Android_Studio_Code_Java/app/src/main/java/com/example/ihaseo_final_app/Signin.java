package com.example.ihaseo_final_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signin extends AppCompatActivity {
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://ihaseo-b20e6-default-rtdb.firebaseio.com/");

    EditText editTextEmail, editTextPassword;
    Button buttonSignIn;
    CheckBox checkBoxShowPassword;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxShowPassword = findViewById(R.id.checkBoxShowPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);

        sessionManager = new SessionManager(this);


        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    String email = editTextEmail.getText().toString().trim();
                    sessionManager.createLoginSession(email);
                    // Log the login action
                    Log.d("Signin", "User logged in: " + email);
                }
            }
        });

        checkBoxShowPassword.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            showHidePassword(isChecked);
        });

        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signin.this, Forget.class));
            }
        });
    }

    private boolean validateForm() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showToast("Email address is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter a valid email address");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("Password required");
            return false;
        } else if (!password.matches("^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?])(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
            showToast("Invalid password");
            return false;
        } else {
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(encodeEmail(email))) {
                        String getpassword = snapshot.child(encodeEmail(email)).child("editTextPassword").getValue(String.class);
                        if (getpassword.equals(password)) {
                            Toast.makeText(Signin.this, "Login success", Toast.LENGTH_SHORT).show();
                            // Pass email to SendOtp class
                            Intent intent = new Intent(Signin.this, SendOtp.class);
                            intent.putExtra("email", email); // Pass email as an extra
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Signin.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Signin.this, "You are not registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showHidePassword(boolean showPassword) {
        if (showPassword) {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        editTextPassword.setSelection(editTextPassword.length());
    }
    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }
}