package com.example.medivault;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorSelectDialog extends Dialog {

    // ✅ ONLY INTERFACE
    public interface OnDoctorSelectedListener {
        void onDoctorSelected(String doctorUid);
    }

    private final OnDoctorSelectedListener listener;
    private FirebaseFirestore firestore;

    List<String> doctorNames = new ArrayList<>();
    List<String> doctorUids = new ArrayList<>();

    // ✅ ONLY ONE CONSTRUCTOR — THIS IS CRITICAL
    public DoctorSelectDialog(
            @NonNull Context context,
            @NonNull OnDoctorSelectedListener listener
    ) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_doctor_select);

        firestore = FirebaseFirestore.getInstance();

        ListView listView = findViewById(R.id.listDoctors);
        ProgressBar progressBar = findViewById(R.id.progressDoctors);

        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("users")
                .whereEqualTo("role", "doctor")
                .get()
                .addOnSuccessListener(query -> {

                    progressBar.setVisibility(View.GONE);

                    if (query.isEmpty()) {
                        Toast.makeText(
                                getContext(),
                                "No doctors available",
                                Toast.LENGTH_SHORT
                        ).show();
                        dismiss();
                        return;
                    }

                    doctorNames.clear();
                    doctorUids.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        doctorNames.add(doc.getString("name"));
                        doctorUids.add(doc.getId());
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    getContext(),
                                    android.R.layout.simple_list_item_1,
                                    doctorNames
                            );

                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        listener.onDoctorSelected(doctorUids.get(position));
                        dismiss();
                    });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(
                            getContext(),
                            "Failed to load doctors",
                            Toast.LENGTH_LONG
                    ).show();
                    dismiss();
                });
    }
}
