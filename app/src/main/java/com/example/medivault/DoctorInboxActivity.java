package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DoctorInboxActivity extends AppCompatActivity
        implements ReportAdapter.OnReportClickListener {

    RecyclerView recyclerView;
    ReportAdapter adapter;
    List<Report> reportList = new ArrayList<>();
    List<String> reportDocIds = new ArrayList<>(); // 🔑 STORE DOC IDs

    FirebaseFirestore firestore;
    String doctorUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_inbox);

        firestore = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }

        doctorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recyclerDoctorInbox);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ReportAdapter(this, reportList, this);
        recyclerView.setAdapter(adapter);

        fetchReceivedReports();
    }

    // 🔹 Fetch shared reports (DO NOT mark read here)
    private void fetchReceivedReports() {

        firestore.collection("users")
                .document(doctorUid)
                .collection("received_reports")
                .get()
                .addOnSuccessListener(snapshot -> {

                    reportList.clear();
                    reportDocIds.clear();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        Report report = doc.toObject(Report.class);
                        if (report != null) {
                            reportList.add(report);
                            reportDocIds.add(doc.getId()); // 🔑 SAVE DOC ID
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (reportList.isEmpty()) {
                        Toast.makeText(
                                this,
                                "No reports shared yet",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load reports ❌",
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    // 🔹 View report → mark read INSIDE ViewReportActivity
    @Override
    public void onViewReportClick(Report report) {

        int position = reportList.indexOf(report);
        if (position == -1) return;

        String docId = reportDocIds.get(position);

        Intent intent = new Intent(this, ViewReportActivity.class);
        intent.putExtra("fileUrl", report.fileUrl);
        intent.putExtra("receivedReportDocId", docId); // 🔑 VERY IMPORTANT
        startActivity(intent);
    }
}
