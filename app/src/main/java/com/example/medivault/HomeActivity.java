package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    CardView cardProfile, cardUpload, cardMyReports, cardEmergency, cardAiChat;
    ImageView ivDoctorNotifications;
    TextView tvNotificationCount;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        uid = auth.getCurrentUser().getUid();

        // Cards
        cardProfile = findViewById(R.id.card_profile);
        cardUpload = findViewById(R.id.card_upload);
        cardMyReports = findViewById(R.id.card_my_reports);
        cardEmergency = findViewById(R.id.card_emergency);
        cardAiChat = findViewById(R.id.card_ai_chat);

        // Notification views
        ivDoctorNotifications = findViewById(R.id.ivDoctorNotifications);
        tvNotificationCount = findViewById(R.id.tvNotificationCount);

        if (ivDoctorNotifications != null) ivDoctorNotifications.setVisibility(View.GONE);
        if (tvNotificationCount != null) tvNotificationCount.setVisibility(View.GONE);

        // 🔹 Check role
        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String role = doc.getString("role");
                    if (!"doctor".equals(role)) return;

                    if (ivDoctorNotifications != null) {
                        ivDoctorNotifications.setVisibility(View.VISIBLE);
                    }

                    updateNotificationBadge(); // 🔔 INITIAL LOAD
                });

        // Navigation (same for all users)
        cardProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        cardUpload.setOnClickListener(v ->
                startActivity(new Intent(this, UploadReportActivity.class)));

        cardMyReports.setOnClickListener(v ->
                startActivity(new Intent(this, MyReportsActivity.class)));

        cardEmergency.setOnClickListener(v ->
                startActivity(new Intent(this, EmergencyActivity.class)));

        cardAiChat.setOnClickListener(v ->
                startActivity(new Intent(this, AiChatActivity.class)));

        if (ivDoctorNotifications != null) {
            ivDoctorNotifications.setOnClickListener(v ->
                    startActivity(new Intent(this, DoctorInboxActivity.class)));
        }
    }

    // 🔄 VERY IMPORTANT — refresh badge after returning
    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationBadge();
    }

    // 🔔 UNREAD COUNT LOGIC (REUSABLE)
    private void updateNotificationBadge() {

        if (tvNotificationCount == null || uid == null) return;

        firestore.collection("users")
                .document(uid)
                .collection("received_reports")
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
