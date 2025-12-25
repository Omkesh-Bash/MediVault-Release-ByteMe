package com.example.medivault;

public class Report {
    private String title;
    private String type;
    private String date;
    private String fileName;

    public Report(String title, String type, String date, String fileName) {
        this.title = title;
        this.type = type;
        this.date = date;
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getFileName() {
        return fileName;
    }
}
