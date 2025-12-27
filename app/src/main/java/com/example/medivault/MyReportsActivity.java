package com.example.medivault;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyReportsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ReportAdapter adapter;
    List<Report> reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        recyclerView = findViewById(R.id.reportsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reportList = new ArrayList<>();

        // Adapter → open report using URL
        adapter = new ReportAdapter(this, reportList, report ->
                openReport(report.fileUrl)
        );
        recyclerView.setAdapter(adapter);

        // Load reports from Firebase
        fetchReportsFromFirebase();

        // Optional: Share all reports (URLs)
        Button btnShareAll = findViewById(R.id.btnShareAll);
        if (btnShareAll != null) {
            btnShareAll.setOnClickListener(v -> shareAllReports());
        }
    }

    // 🔹 Fetch reports from Firestore
    private void fetchReportsFromFirebase() {
        ReportUtils.fetchReportsFromCloud(new ReportUtils.OnReportsFetchedListener() {
            @Override
            public void onFetched(List<Report> reports) {
                reportList.clear();
                reportList.addAll(reports);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MyReportsActivity.this,
                        "Failed to load reports ❌",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔹 Open report using Firebase Storage URL
    private void openReport(String fileUrl) {
        try {
            Uri uri = Uri.parse(fileUrl);
            Intent intent = new Intent(this, ViewReportActivity.class);
            intent.putExtra("fileUrl", fileUrl);
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(this,
                    "No app found to open this report ❌",
                    Toast.LENGTH_LONG).show();

            // 🔴 FINAL fallback → open in browser
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(fileUrl)
            );
            startActivity(browserIntent);
        }
    }


    // 🔹 Share all reports as LINKS
    private void shareAllReports() {
        if (reportList == null || reportList.isEmpty()) {
            Toast.makeText(this,
                    "No reports to share ❌",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder shareText = new StringBuilder("My Medical Reports:\n\n");

        for (Report report : reportList) {
            shareText.append(report.title)
                    .append(" (").append(report.type).append(")\n")
                    .append(report.fileUrl)
                    .append("\n\n");
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());

        startActivity(Intent.createChooser(shareIntent, "Share reports via"));
    }
}
