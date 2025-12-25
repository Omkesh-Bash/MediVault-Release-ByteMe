package com.example.medivault;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyReportsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ReportAdapter adapter;
    List<Report> reportList;
    File reportsDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        recyclerView = findViewById(R.id.reportsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Internal app storage folder -> /data/data/<package>/files/MyReports
        reportsDir = new File(getFilesDir(), "MyReports");
        if (!reportsDir.exists()) reportsDir.mkdir();

        // Load reports list
        reportList = ReportUtils.getReports(this);

        // Adapter setup
        adapter = new ReportAdapter(this, reportList, report -> openReport(report.getFileName()));
        recyclerView.setAdapter(adapter);

        // Setup share all button
        Button btnShareAll = findViewById(R.id.btnShareAll);
        if (btnShareAll != null) {
            btnShareAll.setOnClickListener(v -> shareAllReports());
        }
    }

    /** Opens individual report */
    private void openReport(String fileName) {
        try {
            File file = new File(reportsDir, fileName);
            if (!file.exists()) {
                Toast.makeText(this, "File not found ❌", Toast.LENGTH_SHORT).show();
                return;
            }

            // Use FileProvider for modern file sharing
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (fileName.toLowerCase().endsWith(".pdf")) {
                intent.setDataAndType(uri, "application/pdf");
            } else {
                intent.setDataAndType(uri, "image/*");
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No app found to open this file ⚠️", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot open file ❌", Toast.LENGTH_SHORT).show();
        }
    }

    /** Shares all reports via apps like Gmail, WhatsApp, Drive, etc. */
    private void shareAllReports() {
        if (reportList == null || reportList.isEmpty()) {
            Toast.makeText(this, "No reports to share ❌", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Uri> uris = new ArrayList<>();
        String mimeType = null; // To store type dynamically

        for (Report report : reportList) {
            File file = new File(reportsDir, report.getFileName());
            if (file.exists()) {
                Uri uri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider",
                        file
                );
                uris.add(uri);

                // Detect file type from name
                if (mimeType == null) {
                    if (report.getFileName().toLowerCase().endsWith(".pdf")) {
                        mimeType = "application/pdf";
                    } else if (report.getFileName().toLowerCase().endsWith(".jpg") ||
                            report.getFileName().toLowerCase().endsWith(".jpeg") ||
                            report.getFileName().toLowerCase().endsWith(".png")) {
                        mimeType = "image/*";
                    } else {
                        mimeType = "*/*";
                    }
                }
            }
        }

        if (uris.isEmpty()) {
            Toast.makeText(this, "No valid files found ❌", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType(mimeType); // use detected type
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share reports via"));
    }
}
