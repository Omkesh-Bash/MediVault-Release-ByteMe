package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorHomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvEmpty;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    List<DoctorReportItem> reportList;
    DoctorReportsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);

        recyclerView = findViewById(R.id.recyclerReports);
        progressBar = findViewById(R.id.progressReports);
        tvEmpty = findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        reportList = new ArrayList<>();
        adapter = new DoctorReportsAdapter(reportList, this::openReport);
        recyclerView.setAdapter(adapter);

        loadSharedReports();
    }

    private void loadSharedReports() {

        String doctorUid = auth.getCurrentUser().getUid();
        progressBar.setVisibility(View.VISIBLE);

        firestore.collectionGroup("reports")
                .whereArrayContains("sharedWith", doctorUid)
                .get()
                .addOnSuccessListener(query -> {

                    progressBar.setVisibility(View.GONE);
                    reportList.clear();

                    if (query.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    tvEmpty.setVisibility(View.GONE);

                    for (QueryDocumentSnapshot doc : query) {
                        DoctorReportItem item = new DoctorReportItem(
                                doc.getId(),
                                doc.getString("title"),
                                doc.getString("fileUrl")
                        );
                        reportList.add(item);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this,
                            "Failed to load reports",
                            Toast.LENGTH_LONG).show();
                });
    }

    private void openReport(DoctorReportItem item) {

        Intent intent = new Intent(this, ViewReportActivity.class);
        intent.putExtra("fileUrl", item.fileUrl);
        intent.putExtra("reportId", item.reportId);
        startActivity(intent);
    }
}
