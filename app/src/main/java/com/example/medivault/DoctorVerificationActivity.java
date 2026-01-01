package com.example.medivault;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DoctorVerificationActivity extends AppCompatActivity {

    private static final int PICK_FILE = 101;

    Uri fileUri;
    TextView tvFileName;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_verification);

        Button btnChoose = findViewById(R.id.btnChooseFile);
        Button btnUpload = findViewById(R.id.btnUpload);
        tvFileName = findViewById(R.id.tvFileName);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnChoose.setOnClickListener(v -> chooseFile());
        btnUpload.setOnClickListener(v -> uploadCertificate());
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            tvFileName.setText("Selected");
        }
    }

    private void uploadCertificate() {

        if (fileUri == null) {
            Toast.makeText(this, "Select certificate first", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        MediaManager.get()
                .upload(fileUri)
                .option("resource_type", "raw")
                .option("folder", "doctor_certificates/" + uid)
                .callback(new UploadCallback() {

                    @Override
                    public void onSuccess(String requestId, Map resultData) {

                        String url = resultData.get("secure_url").toString();

                        Map<String, Object> update = new HashMap<>();
                        update.put("certificateUrl", url);
                        update.put("verificationStatus", "pending");

                        firestore.collection("users")
                                .document(uid)
                                .update(update)
                                .addOnSuccessListener(v -> {
                                    Toast.makeText(
                                            DoctorVerificationActivity.this,
                                            "Certificate submitted. Await approval ✅",
                                            Toast.LENGTH_LONG
                                    ).show();
                                    finish();
                                });
                    }

                    @Override
                    public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        Toast.makeText(
                                DoctorVerificationActivity.this,
                                "Upload failed ❌",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) {}
                })
                .dispatch();
    }
}
