package com.example.medivault;

public class DoctorReportItem {

    public String reportId;
    public String title;
    public String fileUrl;

    public DoctorReportItem(String reportId, String title, String fileUrl) {
        this.reportId = reportId;
        this.title = title;
        this.fileUrl = fileUrl;
    }
}
