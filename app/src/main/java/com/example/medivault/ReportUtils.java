package com.example.medivault;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportUtils {

    private static final String PREFS_NAME = "my_reports";
    private static final String KEY_REPORTS = "reports";

    // Save a new report
    public static void saveReport(Context context, String title, String type, String date, String fileName) {
        List<Report> reports = getReports(context);

        // Prevent duplicates (same fileName)
        for (Report r : reports) {
            if (r.getFileName().equals(fileName)) {
                return; // already exists
            }
        }

        reports.add(new Report(title, type, date, fileName));

        JSONArray array = new JSONArray();
        try {
            for (Report r : reports) {
                JSONObject obj = new JSONObject();
                obj.put("title", r.getTitle());
                obj.put("type", r.getType());
                obj.put("date", r.getDate());
                obj.put("fileName", r.getFileName());
                array.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_REPORTS, array.toString()).apply();
    }

    // Get all saved reports
    public static List<Report> getReports(Context context) {
        List<Report> reports = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_REPORTS, null);

        if (json == null || json.trim().isEmpty()) {
            // No saved reports yet
            return reports;
        }

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                reports.add(new Report(
                        obj.optString("title", ""),
                        obj.optString("type", ""),
                        obj.optString("date", ""),
                        obj.optString("fileName", "")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reports;
    }

    // Optional — clear all reports (e.g., when creating a new account)
    public static void clearReports(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_REPORTS).apply();
    }
}
