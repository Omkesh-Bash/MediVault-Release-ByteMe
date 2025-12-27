package com.example.medivault;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UploadReportActivity extends AppCompatActivity {

    private EditText etTitle, etType, etDate;
    private TextView tvFileName;
    private ImageView ivFileIcon;
    private Button btnUpload;
    private ProgressBar progressUpload;

    private Uri fileUri;
    private static final int FILE_PICKER_REQUEST = 100;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        etTitle = findViewById(R.id.etTitle);
        etType = findViewById(R.id.etType);
        etDate = findViewById(R.id.etDate);
        tvFileName = findViewById(R.id.tvFileName);
        ivFileIcon = findViewById(R.id.ivFileIcon);
        btnUpload = findViewById(R.id.btnUpload);
        progressUpload = findViewById(R.id.progressUpload);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ivFileIcon.setOnClickListener(v -> chooseFile());
        tvFileName.setOnClickListener(v -> chooseFile());
        btnUpload.setOnClickListener(v -> uploadReport());

        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new android.app.DatePickerDialog(
                    this,
                    (view, y, m, d) -> etDate.setText(d + "/" + (m + 1) + "/" + y),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select Report"),
                FILE_PICKER_REQUEST
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            fileUri = data.getData();
            tvFileName.setText(fileUri.getLastPathSegment());
        }
    }

    private void uploadReport() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in ❌", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (title.isEmpty() || type.isEmpty() || date.isEmpty() || fileUri == null) {
            Toast.makeText(this, "Fill all fields & select file", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        // UI state
        btnUpload.setEnabled(false);
        btnUpload.setText("Uploading...");
        progressUpload.setVisibility(View.VISIBLE);
        progressUpload.setProgress(0);

        MediaManager.get()
                .upload(fileUri)
                .option("resource_type", "raw")           // for PDFs & docs
                .unsigned("medivault_reports")            // preset ID (must match exactly)
                .option("folder", "medivault/reports/" + uid)
                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {
                        progressUpload.setProgress(0);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        if (totalBytes > 0) {
                            int progress = (int) ((bytes * 100) / totalBytes);
                            progressUpload.setProgress(progress);
                        }
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {

                        String fileUrl = resultData.get("secure_url").toString();

                        Map<String, Object> report = new HashMap<>();
                        report.put("title", title);
                        report.put("type", type);
                        report.put("date", date);
                        report.put("fileUrl", fileUrl);
                        report.put("uploadedAt", System.currentTimeMillis());

                        // 🔴 FIX: handle BOTH success & failure
                        firestore.collection("users")
                                .document(uid)
                                .collection("reports")
                                .add(report)
                                .addOnSuccessListener(doc -> {

                                    resetUI();

                                    Toast.makeText(
                                            UploadReportActivity.this,
                                            "Report uploaded successfully ✅",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {

                                    resetUI();

                                    Toast.makeText(
                                            UploadReportActivity.this,
                                            "Firestore error ❌ : " + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                });
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        resetUI();
                        Toast.makeText(
                                UploadReportActivity.this,
                                "Upload failed ❌ : " + error.getDescription(),
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        resetUI();
                    }
                })
                .dispatch();   // 🔥 REQUIRED to start upload
    }

    private void resetUI() {
        btnUpload.setEnabled(true);
        btnUpload.setText("Upload");
        progressUpload.setVisibility(View.GONE);
    }
}
