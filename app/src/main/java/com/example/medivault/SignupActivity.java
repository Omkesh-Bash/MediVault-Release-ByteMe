package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText etName, etEmail, etPassword, etAge, etEmergency;
    AutoCompleteTextView spinnerRole, spinnerGender, spinnerBloodGroup;
    Button btnSignup;
    TextView tvLogin;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // UI references
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etAge = findViewById(R.id.etAge);
        spinnerRole = findViewById(R.id.spinnerRole);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        etEmergency = findViewById(R.id.etEmergency);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);

        // Role spinner
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.roles,
                android.R.layout.simple_spinner_item
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        // Gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.genders,
                android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // Blood group spinner
        ArrayAdapter<CharSequence> bloodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_groups,
                android.R.layout.simple_spinner_item
        );
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(bloodAdapter);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnSignup.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String roleSelected = spinnerRole.getText().toString();
            String gender = spinnerGender.getText().toString();
            String bloodGroup = spinnerBloodGroup.getText().toString();
            String emergency = etEmergency.getText().toString().trim();

            // Validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()
                    || age.isEmpty() || emergency.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (roleSelected.isEmpty() || roleSelected.equals("Select Role")) {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            if (gender.isEmpty() || gender.equals("Select Gender") || bloodGroup.isEmpty() || bloodGroup.equals("Select Blood Group")) {
                Toast.makeText(this, "Please select valid options", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!emergency.matches("\\d{10}")) {
                Toast.makeText(this, "Enter valid 10-digit emergency number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            String uid = auth.getCurrentUser().getUid();

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("age", age);
                            userMap.put("gender", gender);
                            userMap.put("bloodGroup", bloodGroup);
                            userMap.put("emergency", emergency);

                            // 🔴 ROLE LOGIC (SAFE)
                            if (roleSelected.equalsIgnoreCase("Doctor")) {
                                userMap.put("role", "user");              // default
                                userMap.put("requestedRole", "doctor");   // approval needed
                            } else {
                                userMap.put("role", "user");
                            }

                            firestore.collection("users")
                                    .document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this,
                                                "Signup Successful ✅",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, HomeActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this,
                                                    "Firestore Error: " + e.getMessage(),
                                                    Toast.LENGTH_LONG).show()
                                    );
                        } else {
                            Toast.makeText(this,
                                    "Signup Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}
