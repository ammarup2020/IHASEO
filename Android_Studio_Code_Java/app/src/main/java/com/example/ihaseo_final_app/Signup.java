package com.example.ihaseo_final_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signup extends AppCompatActivity {
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://ihaseo-b20e6-default-rtdb.firebaseio.com/");

    EditText editTextUsername, editTextMobile, editTextEmail, editTextPassword;
    Button buttonSignUp;
    CheckBox checkBoxShowPassword;
    Spinner spinnerRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxShowPassword = findViewById(R.id.checkBoxShowPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        spinnerRole = findViewById(R.id.spinnerRole); // Initialize spinnerRole
        String[] roles = {"Select your role","User", "Owner"}; // Define roles
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        checkBoxShowPassword.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            showHidePassword(isChecked);
        });
    }

    private void signUp() {
        String username = editTextUsername.getText().toString().trim();
        String mobile = editTextMobile.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();
        if (TextUtils.isEmpty(username)) {
            showToast("Username is required");
            return;
        } else if (!username.matches("[a-zA-Z ]+")) {
            showToast("Enter a valid username");
            return;
        }

        if (TextUtils.isEmpty(mobile)) {
            showToast("Number is required");
            return;
        } else if (!mobile.matches("[0-9]+") || mobile.length() != 11) {
            showToast("Enter valid phone number");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            showToast("Email address is required");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter a valid email address");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("Password is required");
            return;
        } else if (!password.matches("^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?])(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
            showToast("Password must contain at least one uppercase letter, one lowercase letter, one digit, one special character, and be at least 6 characters");
            return;
        }

        else {
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(encodeEmail(email))){
                        Toast.makeText(Signup.this,"You are already registered",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        databaseReference.child("users").child(encodeEmail(email)).child("editTextUsername").setValue(username);
                        databaseReference.child("users").child(encodeEmail(email)).child("spinnerRole").setValue(role);
                        databaseReference.child("users").child(encodeEmail(email)).child("editTextMobile").setValue(mobile);
                        databaseReference.child("users").child(encodeEmail(email)).child("editTextPassword").setValue(password);
                        Toast.makeText(Signup.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Signup.this, Signin.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
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
