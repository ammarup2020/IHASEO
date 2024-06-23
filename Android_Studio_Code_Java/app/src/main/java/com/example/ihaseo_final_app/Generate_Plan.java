package com.example.ihaseo_final_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Generate_Plan extends AppCompatActivity {

    private LinearLayout container;
    private List<LinearLayout> rows;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_plan);

        container = findViewById(R.id.container);
        rows = new ArrayList<>();
        submitButton = findViewById(R.id.submitButton);

        // Set OnClickListener for submitButton
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayJSON();
            }
        });

        // Set OnClickListener for addFieldButton
        findViewById(R.id.addFieldButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFields();
            }
        });
    }

    private void addFields() {
        // Create a new row
        LinearLayout rowLayout = new LinearLayout(this);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowLayout.setLayoutParams(rowParams);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        container.addView(rowLayout);
        rows.add(rowLayout);

        // Add three EditText fields to the row
        for (int i = 0; i < 3; i++) {
            EditText editText = new EditText(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            layoutParams.setMargins(0, 16, 0, 0);
            editText.setLayoutParams(layoutParams);
            editText.setHint("Enter text");
            rowLayout.addView(editText);
        }
    }

    private void displayJSON() {
        List<List<String>> deviceData = new ArrayList<>();
        for (LinearLayout rowLayout : rows) {
            List<String> row = new ArrayList<>();
            for (int i = 0; i < rowLayout.getChildCount(); i++) {
                EditText editText = (EditText) rowLayout.getChildAt(i);
                row.add(editText.getText().toString());
            }
            deviceData.add(row);
        }

        // Get Total Cost
        EditText totalCostEditText = findViewById(R.id.totalCostEditText);
        String totalCost = totalCostEditText.getText().toString();

        // Create JSON object
        Gson gson = new Gson();
        String deviceDataJSON = gson.toJson(deviceData);

        // Combine deviceDataJSON with Total Cost as JSON
        String jsonResult = "{\"device_data\":" + deviceDataJSON + ",\"total_cost_constraint\":" + totalCost + "}";

        //Using Log to Show the JSON Output
        //Log.d("JSON", jsonResult);

        // Create RequestBody from JSON string
        RequestBody requestBody = RequestBody.create(jsonResult, MediaType.parse("application/json"));
        // Create OkHttpClient instance
        OkHttpClient okHttpClient = new OkHttpClient();
        // Build the request with the JSON body
        Request request = new Request.Builder().url("http://192.168.1.5:5000/plan").post(requestBody).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Generate_Plan.this, "Network Connected anf File is downloaded", Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Generate_Plan.this, "Network Connected and Reply is Send Check Log!!!", Toast.LENGTH_LONG).show();
                    }
                });
                Log.d("RESPONSE", response.body().string());
            }
        });
    }
}

