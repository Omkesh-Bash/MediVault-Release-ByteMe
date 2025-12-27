package com.example.medivault;

public class Report {

    public String title;
    public String type;
    public String date;
    public String fileUrl;   // Firebase Storage download URL

    // 🔴 REQUIRED empty constructor for Firebase
    public Report() {
    }

    public Report(String title, String type, String date, String fileUrl) {
        this.title = title;
        this.type = type;
        this.date = date;
        this.fileUrl = fileUrl;
    }
}
