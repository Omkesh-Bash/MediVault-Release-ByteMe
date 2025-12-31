package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyReportsActivity extends AppCompatActivity
        implements ReportAdapter.OnReportClickListener {

    RecyclerView recyclerView;
    ReportAdapter adapter;
    List<Report> reportList;

    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        recyclerView = findViewById(R.id.reportsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reportList = new ArrayList<>();

        adapter = new ReportAdapter(this, reportList, this);
        recyclerView.setAdapter(adapter);

        // 🔹 Load reports
        fetchReportsFromFirebase();

        Button btnShareAll = findViewById(R.id.btnShareAll);
        if (btnShareAll != null) {
            btnShareAll.setOnClickListener(v -> shareAllReports());
        }

        etSearch = findViewById(R.id.etSearch);

        // ✅ ENABLE SEARCH (FIXED)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }
        });
    }

    // 🔹 Fetch reports from Firestore
    private void fetchReportsFromFirebase() {

        ReportUtils.fetchReportsFromCloud(new ReportUtils.OnReportsFetchedListener() {

            @Override
            public void onFetched(List<Report> reports) {

                runOnUiThread(() -> {
                    reportList.clear();
                    reportList.addAll(reports);

                    // 🔴 VERY IMPORTANT FOR SEARCH
                    adapter.reportsFull.clear();
                    adapter.reportsFull.addAll(reports);

                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(
                        MyReportsActivity.this,
                        "Failed to load reports",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // ✅ VIEW REPORT
    @Override
    public void onViewReportClick(Report report) {

        Intent intent = new Intent(
                MyReportsActivity.this,
                ViewReportActivity.class
        );

        intent.putExtra("fileUrl", report.fileUrl);
        startActivity(intent);
    }

    // 🔹 Share all reports as links
    private void shareAllReports() {

        if (reportList.isEmpty()) {
            Toast.makeText(
                    this,
                    "No reports to share ❌",
                    Toast.LENGTH_SHORT
            ).show();
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

        startActivity(
                Intent.createChooser(
                        shareIntent,
                        "Share reports via"
                )
        );
    }
}
