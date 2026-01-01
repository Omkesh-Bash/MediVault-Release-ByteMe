package com.example.medivault;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmergencyActivity extends AppCompatActivity {

    Spinner spinnerBloodGroup;
    EditText etAllergies, etEmergencyContact, etDoctorContact;
    Button btnSaveEmergency;

    // Base pref name; actual name may include user uid
    private static final String PREF_NAME_BASE = "EmergencyPrefs";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        etAllergies = findViewById(R.id.etAllergies);
        etEmergencyContact = findViewById(R.id.etEmergencyContact);
        etDoctorContact = findViewById(R.id.etDoctorContact);
        btnSaveEmergency = findViewById(R.id.btnSaveEmergency);

        // Use user-specific prefs when possible, otherwise fallback to generic prefs.
        sharedPreferences = getPrefs(this);

        // Load saved data when activity opens
        loadEmergencyInfo();

        // Save on button click
        btnSaveEmergency.setOnClickListener(v -> saveEmergencyInfo());

    }

    private void saveEmergencyInfo() {
        String allergy = etAllergies.getText().toString().trim();
        String emergencyContact = etEmergencyContact.getText().toString().trim();
        String doctorContact = etDoctorContact.getText().toString().trim();

        if (emergencyContact.isEmpty()) {
            Toast.makeText(this, "Emergency Contact is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("allergies", allergy);
        editor.putString("emergencyContact", emergencyContact);
        editor.putString("doctorContact", doctorContact);
        editor.apply();

        Toast.makeText(this, "Emergency Info Saved ✅", Toast.LENGTH_SHORT).show();
    }

    private void loadEmergencyInfo() {

        String allergy = sharedPreferences.getString("allergies", "");
        String emergencyContact = sharedPreferences.getString("emergencyContact", "");
        String doctorContact = sharedPreferences.getString("doctorContact", "");

        etAllergies.setText(allergy);
        etEmergencyContact.setText(emergencyContact);
        etDoctorContact.setText(doctorContact);
    }

    public static SharedPreferences getPrefs(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String prefName = PREF_NAME_BASE;
        if (user != null && user.getUid() != null) {
            prefName = PREF_NAME_BASE + "_" + user.getUid();
        }
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public static void clearEmergencyInfo(Context context) {
        // Clear user-specific prefs if available
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getUid() != null) {
            String userPref = PREF_NAME_BASE + "_" + user.getUid();
            context.getSharedPreferences(userPref, Context.MODE_PRIVATE)
                    .edit().clear().apply();
        }

        // Also clear generic prefs (fallback) to be safe
        context.getSharedPreferences(PREF_NAME_BASE, Context.MODE_PRIVATE)
                .edit().clear().apply();
    }
}
