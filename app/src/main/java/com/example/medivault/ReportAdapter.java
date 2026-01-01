package com.example.medivault;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder>
        implements Filterable {

    public interface OnReportClickListener {
        void onViewReportClick(Report report);
    }

    Context context;
    List<Report> reports;
    public List<Report> reportsFull;
    OnReportClickListener listener;

    public ReportAdapter(Context context, List<Report> reports, OnReportClickListener listener) {
        this.context = context;
        this.reports = reports;
        this.listener = listener;

        reportsFull = new ArrayList<>();
        reportsFull.addAll(reports);
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {

        Report report = reports.get(position);

        holder.tvDate.setText(report.date != null ? report.date : "");
        holder.tvType.setText(report.type != null ? report.type : "");
        holder.tvNotes.setText(report.title != null ? report.title : "");

        // VIEW REPORT
        holder.btnViewReport.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewReportClick(report);
            }
        });

        // DOCTOR VIEW CHECK (SAFE)
        boolean isDoctorView =
                report.patientName != null && report.patientEmail != null;

        if (isDoctorView) {
            holder.layoutPatientInfo.setVisibility(View.VISIBLE);
            holder.tvPatientName.setText("Patient: " + report.patientName);
            holder.tvPatientEmail.setText(report.patientEmail);
            holder.btnShareSingle.setVisibility(View.GONE);
        } else {
            holder.layoutPatientInfo.setVisibility(View.GONE);
            holder.btnShareSingle.setVisibility(View.VISIBLE);

            holder.btnShareSingle.setOnClickListener(v -> confirmAndShare(report));
        }
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    // 🔍 SEARCH SUPPORT
    @Override
    public Filter getFilter() {
        return reportFilter;
    }

    private final Filter reportFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Report> filtered = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(reportsFull);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();

                for (Report r : reportsFull) {
                    if ((r.title != null && r.title.toLowerCase().contains(pattern)) ||
                            (r.type != null && r.type.toLowerCase().contains(pattern)) ||
                            (r.date != null && r.date.toLowerCase().contains(pattern)) ||
                            (r.patientName != null && r.patientName.toLowerCase().contains(pattern))) {
                        filtered.add(r);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtered;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            reports.clear();
            reports.addAll((List<Report>) results.values);
            notifyDataSetChanged();
        }
    };

    // ✅ CONFIRM & SHARE (FIXED — NO LAMBDA)
    private void confirmAndShare(Report report) {

        new AlertDialog.Builder(context)
                .setTitle("Share Report")
                .setMessage("Share this report with a doctor?")
                .setPositiveButton("Share", (dialog, which) -> {

                    new DoctorSelectDialog(
                            context,
                            doctorUid -> {

                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(doctorUid)
                                        .collection("received_reports")
                                        .add(report)
                                        .addOnSuccessListener(doc -> {
                                            Toast.makeText(
                                                    context,
                                                    "Report shared with doctor ✅",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(
                                                    context,
                                                    "Failed to share report ❌",
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        });
                            }
                    ).show();

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvType, tvNotes;
        TextView tvPatientName, tvPatientEmail;
        View layoutPatientInfo;
        Button btnViewReport;
        ImageButton btnShareSingle;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvNotes = itemView.findViewById(R.id.tvNotes);

            layoutPatientInfo = itemView.findViewById(R.id.layoutPatientInfo);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvPatientEmail = itemView.findViewById(R.id.tvPatientEmail);

            btnViewReport = itemView.findViewById(R.id.btnViewReport);
            btnShareSingle = itemView.findViewById(R.id.btnShareSingle);
        }
    }
}
