package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvAge, tvGender, tvBlood, tvEmergency;
    Button btnLogout;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvAge = findViewById(R.id.tvAge);
        tvGender = findViewById(R.id.tvGender);
        tvBlood = findViewById(R.id.tvBloodGroup);
        tvEmergency = findViewById(R.id.tvEmergency);
        btnLogout = findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String uid = auth.getCurrentUser().getUid();

        firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        tvName.setText(documentSnapshot.getString("name"));
                        tvEmail.setText(documentSnapshot.getString("email"));
                        tvAge.setText(documentSnapshot.getString("age"));
                        tvGender.setText(documentSnapshot.getString("gender"));
                        tvBlood.setText(documentSnapshot.getString("bloodGroup"));
                        tvEmergency.setText(documentSnapshot.getString("emergency"));
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show()
                );

        btnLogout.setOnClickListener(v -> {
            // Clear text fields
            tvName.setText("");
            tvEmail.setText("");
            tvAge.setText("");
            tvGender.setText("");
            tvBlood.setText("");
            tvEmergency.setText("");

            // Firebase Sign out
            auth.signOut();

            // Navigate to LoginActivity
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close ProfileActivity
        });
    }
}
