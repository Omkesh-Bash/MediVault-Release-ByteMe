package com.example.medivault;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportUtils {

    // 🔹 Fetch all reports of current user (CRASH-PROOF)
    public static void fetchReportsFromCloud(OnReportsFetchedListener listener) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            if (listener != null) {
                listener.onError(new Exception("User not logged in"));
            }
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(uid)
                .collection("reports")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<Report> reports = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {

                        Report report = doc.toObject(Report.class);
                        if (report == null) continue;

                        // 🔑 ALWAYS set document ID
                        report.id = doc.getId();

                        // 🔐 NULL-SAFETY (VERY IMPORTANT)
                        if (report.title == null) report.title = "";
                        if (report.type == null) report.type = "";
                        if (report.date == null) report.date = "";
                        if (report.fileUrl == null) report.fileUrl = "";

                        reports.add(report);
                    }

                    if (listener != null) {
                        listener.onFetched(reports);
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onError(e);
                    }
                });
    }

    // 🔹 Interfaces
    public interface OnReportsFetchedListener {
        void onFetched(List<Report> reports);
        void onError(Exception e);
    }
}
