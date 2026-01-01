package com.example.medivault;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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

    private final List<String> doctorNames = new ArrayList<>();
    private final List<String> doctorUids = new ArrayList<>();
    private final List<String> allDoctorNames = new ArrayList<>();
    private final List<String> allDoctorUids = new ArrayList<>();
    private ArrayAdapter<String> adapter;


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
        SearchView searchView = findViewById(R.id.searchView);

        adapter = new ArrayAdapter<>(getContext(), R.layout.item_doctor, doctorNames);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("users")
                .whereEqualTo("role", "doctor")
                .get()
                .addOnSuccessListener(query -> {
                    progressBar.setVisibility(View.GONE);

                    if (query.isEmpty()) {
                        Toast.makeText(getContext(), "No doctors available", Toast.LENGTH_SHORT).show();
                        dismiss();
                        return;
                    }

                    allDoctorNames.clear();
                    allDoctorUids.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        allDoctorNames.add(doc.getString("name"));
                        allDoctorUids.add(doc.getId());
                    }

                    doctorNames.clear();
                    doctorNames.addAll(allDoctorNames);
                    doctorUids.clear();
                    doctorUids.addAll(allDoctorUids);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load doctors", Toast.LENGTH_LONG).show();
                    dismiss();
                });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            listener.onDoctorSelected(doctorUids.get(position));
            dismiss();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doctorNames.clear();
                doctorUids.clear();
                if (newText.isEmpty()) {
                    doctorNames.addAll(allDoctorNames);
                    doctorUids.addAll(allDoctorUids);
                } else {
                    String searchText = newText.toLowerCase();
                    for (int i = 0; i < allDoctorNames.size(); i++) {
                        String name = allDoctorNames.get(i);
                        if (name.toLowerCase().contains(searchText)) {
                            doctorNames.add(name);
                            doctorUids.add(allDoctorUids.get(i));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }
}
