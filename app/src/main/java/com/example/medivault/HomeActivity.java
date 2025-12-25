package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    CardView cardProfile, cardUpload, cardMyReports, cardEmergency;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase
        FirebaseApp.initializeApp(this); // This fixes the crash

        // Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // If user not logged in, redirect to login
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize cards
        cardProfile = findViewById(R.id.card_profile);
        cardUpload = findViewById(R.id.card_upload);
        cardMyReports = findViewById(R.id.card_my_reports);
        cardEmergency = findViewById(R.id.card_emergency);

        // Card click listeners
        cardProfile.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Opening Profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        cardUpload.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Upload Reports", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, UploadReportActivity.class));
        });

        cardMyReports.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Viewing My Reports", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, MyReportsActivity.class));
        });

        cardEmergency.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Emergency Info", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, EmergencyActivity.class));
        });
    }

    // Optional: Logout function
    private void logout() {
        auth.signOut();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();
    }
}
