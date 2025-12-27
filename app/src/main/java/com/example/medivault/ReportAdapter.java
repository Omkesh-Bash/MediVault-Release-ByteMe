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
import androidx.recyclerview.widget.RecyclerView;

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

        // Display safely
        holder.tvDate.setText(report.date != null ? report.date : "");
        holder.tvType.setText(report.type != null ? report.type : "");
        holder.tvNotes.setText(report.title != null ? report.title : "");

        // 🔹 View report (open URL)
        holder.btnViewReport.setOnClickListener(v -> {
            if (listener != null) listener.onClick(report);
        });

        // 🔹 Share report (share link)
        holder.btnShareSingle.setOnClickListener(v -> {
            if (report.fileUrl == null || report.fileUrl.isEmpty()) {
                Toast.makeText(context, "Invalid report link ❌", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Medical Report:\n" +
                            report.title + " (" + report.type + ")\n\n" +
                            report.fileUrl
            );

            context.startActivity(
                    Intent.createChooser(shareIntent, "Share report via")
            );
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    // 🔹 ViewHolder
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

    // 🔹 Click callback
    public interface OnReportClickListener {
        void onClick(Report report);
    }
}
