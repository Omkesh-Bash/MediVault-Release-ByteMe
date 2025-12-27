package com.example.medivault;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportUtils {

    // 🔹 Save report metadata to Firestore
    public static void saveReportToCloud(Report report,
                                         OnReportSavedListener listener) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(uid)
                .collection("reports")
                .add(report)
                .addOnSuccessListener(docRef -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
    }

    // 🔹 Fetch all reports of current user
    public static void fetchReportsFromCloud(OnReportsFetchedListener listener) {

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
                        reports.add(report);
                    }

                    listener.onFetched(reports);
                })
                .addOnFailureListener(listener::onError);
    }

    // 🔹 Interfaces for callbacks
    public interface OnReportSavedListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnReportsFetchedListener {
        void onFetched(List<Report> reports);
        void onError(Exception e);
    }
}
