package com.example.ihaseo_final_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

public class SessionManager {
    public static final String KEY_USERNAME = "username";
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public Context _context;
    public static final int PRIVATE_MODE = 0;
    public static final String PREF_NAME = "MyAppPref";
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_MOBILE="mobile";
    public static final String KEY_PASSWORD="password";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Login session start
    public void createLoginSession(String email){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);// Add username
        editor.apply();
        Log.d("SessionManager", "User logged in with email: " + email);
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            // User is not logged in, redirect to SignIn activity
            Intent intent = new Intent(_context, Signin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
        }
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        return user;
    }

    public void logoutUser(){
        if (isLoggedIn()) {
            editor.clear();
            editor.apply();
            Log.d("SessionManager", "User logged out");
            // Redirect user to SignIn activity after logout
            Intent intent = new Intent(_context, Signin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
        } else {
            // User is not logged in, do nothing or handle appropriately
            Log.d("SessionManager", "User attempted to log out without being logged in");
        }
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
    public void saveUserEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }
    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }
}