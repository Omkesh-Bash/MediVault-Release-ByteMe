package com.example.medivault;

public class Report {

    public String id;              // ✅ Firestore document ID
    public String title;
    public String type;
    public String date;
    public String fileUrl;

    public String patientName;
    public String patientEmail;

    // Required empty constructor
    public Report() {
    }

    public Report(String title, String type, String date, String fileUrl) {
        this.title = title;
        this.type = type;
        this.date = date;
        this.fileUrl = fileUrl;
    }

    public Report(String title, String type, String date, String fileUrl,
                  String patientName, String patientEmail) {

        this.title = title;
        this.type = type;
        this.date = date;
        this.fileUrl = fileUrl;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
    }
}
