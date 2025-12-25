package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword, etAge, etEmergency;
    Spinner spinnerBloodGroup, spinnerGender;
    Button btnSignup;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etAge = findViewById(R.id.etAge);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        etEmergency = findViewById(R.id.etEmergency);
        btnSignup = findViewById(R.id.btnSignup);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.genders,
                android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_groups,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnSignup.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();
            String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();
            String emergency = etEmergency.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || age.isEmpty() || emergency.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!emergency.matches("\\d{10}")) {
                Toast.makeText(this, "Please enter a valid 10-digit emergency number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (spinnerGender.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select a valid Gender", Toast.LENGTH_SHORT).show();
                return;
            }
            if (spinnerBloodGroup.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select a valid Blood Group", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Create user in Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // ✅ CLEAR any old reports for new account
                            ReportUtils.clearReports(this);
                            EmergencyActivity.clearEmergencyInfo(this);

                            // Save additional details in Firestore
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("age", age);
                            userMap.put("gender", gender);
                            userMap.put("bloodGroup", bloodGroup);
                            userMap.put("emergency", emergency);

                            String uid = auth.getCurrentUser().getUid();
                            firestore.collection("users").document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(this, "Signup Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
