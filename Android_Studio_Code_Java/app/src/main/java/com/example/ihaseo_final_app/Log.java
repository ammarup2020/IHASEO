package com.example.ihaseo_final_app;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Log {
    private static final String LOG_FILE_NAME = "user_log.txt";

    public static void logEvent(Context context, String tag, String message) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String logMessage = timeStamp + " [" + tag + "] " + message + "\n";
        writeToFile(context, logMessage);
    }

    private static void writeToFile(Context context, String logMessage) {
        File logFile = new File(Environment.getExternalStorageDirectory(),LOG_FILE_NAME);
        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.append(logMessage);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}