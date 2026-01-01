package com.example.medivault;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EmergencyLockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔓 Show over lock screen
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        setContentView(R.layout.activity_emergency_lock);

        TextView tvInfo = findViewById(R.id.tvEmergencyInfo);

        String info =
                EmergencyActivity
                        .getPrefs(this)
                        .getString("emergencyContact", "Not Set");

        tvInfo.setText("Emergency Contact:\n" + info);
    }
}
