package com.example.medivault;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorUtils {

    public interface OnDoctorsLoadedListener {
        void onLoaded(List<Doctor> doctors);
        void onError(Exception e);
    }

    public static void fetchDoctors(OnDoctorsLoadedListener listener) {

        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("role", "doctor")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Doctor> doctors = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Doctor doctor = new Doctor(
                                doc.getId(),
                                doc.getString("name"),
                                doc.getString("email")
                        );
                        doctors.add(doctor);
                    }

                    listener.onLoaded(doctors);
                })
                .addOnFailureListener(listener::onError);
    }
}
