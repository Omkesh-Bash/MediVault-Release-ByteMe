package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    // Views
    TextView tvWelcome, tvNotificationCount;
    CardView cardProfile, cardUpload, cardMyReports, cardEmergency, cardAiChat;
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
}
