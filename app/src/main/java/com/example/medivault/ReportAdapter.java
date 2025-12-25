package com.example.medivault;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    Context context;
    List<Report> reports;
    OnReportClickListener listener;

    public ReportAdapter(Context context, List<Report> reports, OnReportClickListener listener) {
        this.context = context;
        this.reports = reports;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);

        // Prevent null display
        holder.tvDate.setText(report.getDate() != null ? report.getDate() : "");
        holder.tvType.setText(report.getType() != null ? report.getType() : "");
        holder.tvNotes.setText(report.getFileName() != null ? report.getFileName() : "");

        // View Report
        holder.btnViewReport.setOnClickListener(v -> {
            if (listener != null) listener.onClick(report);
        });

        // Share Report (top-right icon)
        holder.btnShareSingle.setOnClickListener(v -> {
            try {
                File reportsDir = new File(context.getFilesDir(), "MyReports");
                File file = new File(reportsDir, report.getFileName());

                if (!file.exists()) {
                    Toast.makeText(context, "File not found ❌", Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri uri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".provider",
                        file
                );

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                if (report.getFileName().toLowerCase().endsWith(".pdf")) {
                    shareIntent.setType("application/pdf");
                } else if (report.getFileName().toLowerCase().endsWith(".jpg") ||
                        report.getFileName().toLowerCase().endsWith(".jpeg") ||
                        report.getFileName().toLowerCase().endsWith(".png")) {
                    shareIntent.setType("image/*");
                } else {
                    shareIntent.setType("*/*");
                }

                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                context.startActivity(Intent.createChooser(shareIntent, "Share this report via"));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Unable to share report ❌", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvType, tvNotes;
        Button btnViewReport;
        ImageButton btnShareSingle;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            btnViewReport = itemView.findViewById(R.id.btnViewReport);
            btnShareSingle = itemView.findViewById(R.id.btnShareSingle);
        }
    }

    public interface OnReportClickListener {
        void onClick(Report report);
    }
}
