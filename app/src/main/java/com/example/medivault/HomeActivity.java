package com.example.medivault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    // Views
    TextView tvWelcome, tvNotificationCount;
    CardView cardProfile, cardUpload, cardMyReports, cardEmergency, cardAiChat, cardSOS;
    FrameLayout layoutNotification;
    ImageView ivDoctorNotifications;

    // Firebase
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Redirect to login if not authenticated
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        uid = auth.getCurrentUser().getUid();

        // Initialize Views
        initViews();

        // Set up listeners
        setupClickListeners();

        // Fetch user data and configure UI based on role
        fetchUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh notification badge when returning to the activity
        updateNotificationBadge();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvNotificationCount = findViewById(R.id.tvNotificationCount);

        cardProfile = findViewById(R.id.card_profile);
        cardUpload = findViewById(R.id.card_upload);
        cardMyReports = findViewById(R.id.card_my_reports);
        cardEmergency = findViewById(R.id.card_emergency);
        cardAiChat = findViewById(R.id.card_ai_chat);
        cardSOS = findViewById(R.id.card_sos);

        layoutNotification = findViewById(R.id.layoutNotification);
        ivDoctorNotifications = findViewById(R.id.ivDoctorNotifications);

        // Initially hide notification bell
        layoutNotification.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        cardProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        cardUpload.setOnClickListener(v -> startActivity(new Intent(this, UploadReportActivity.class)));
        cardMyReports.setOnClickListener(v -> startActivity(new Intent(this, MyReportsActivity.class)));
        cardEmergency.setOnClickListener(v -> startActivity(new Intent(this, EmergencyActivity.class)));
        cardAiChat.setOnClickListener(v -> startActivity(new Intent(this, AiChatActivity.class)));
        cardSOS.setOnClickListener(v -> triggerSOS());

        layoutNotification.setOnClickListener(v -> startActivity(new Intent(this, DoctorInboxActivity.class)));
    }

    private void fetchUserData() {
        firestore.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String name = doc.getString("name");
                String role = doc.getString("role");

                // Set personalized welcome message
                if (name != null && !name.isEmpty()) {
                    tvWelcome.setText("Hi, " + name + "!");
                }

                // Show notifications for doctors
                if ("doctor".equals(role)) {
                    layoutNotification.setVisibility(View.VISIBLE);
                    updateNotificationBadge();
                }
            }
        });
    }

    private void updateNotificationBadge() {
        if (uid == null) return;

        firestore.collection("users").document(uid).collection("received_reports")
                .whereEqualTo("read", false)
                .get()
                .addOnSuccessListener(snapshot -> {
                    int count = snapshot.size();
                    if (count > 0) {
                        tvNotificationCount.setText(String.valueOf(count));
                        tvNotificationCount.setVisibility(View.VISIBLE);
                    } else {
                        tvNotificationCount.setVisibility(View.GONE);
                    }
                });
    }

    private void triggerSOS() {

        SharedPreferences prefs =
                EmergencyActivity.getPrefs(this);

        String emergencyContact =
                prefs.getString("emergencyContact", "");


        if (emergencyContact.isEmpty()) {
            Toast.makeText(this, "Emergency contact not set!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔐 Permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.SEND_SMS
                    },
                    101
            );
            return;
        }

        // 📞 Call
        Intent callIntent = new Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:" + emergencyContact)
        );
        startActivity(callIntent);

        // 📩 SMS
        String msg = "🚨 EMERGENCY!\nI need immediate help.\n"
                + "This message was sent via MediVault+.";

        SmsManager.getDefault().sendTextMessage(
                emergencyContact,
                null,
                msg,
                null,
                null
        );

        Toast.makeText(this, "SOS Sent 🚨", Toast.LENGTH_SHORT).show();
    }

}
