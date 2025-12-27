package com.example.medivault;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> implements Filterable {

    Context context;
    List<Report> reports;
    List<Report> reportsFull;
    OnReportClickListener listener;
    private SparseBooleanArray selectedItems;
    private List<Report> selectedReports;


    public ReportAdapter(Context context, List<Report> reports, OnReportClickListener listener) {
        this.context = context;
        this.reports = reports;
        this.listener = listener;
        this.reportsFull = new ArrayList<>(reports);
        this.selectedItems = new SparseBooleanArray();
        this.selectedReports = new ArrayList<>();
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

        // Change background color if selected
        holder.itemView.setSelected(selectedItems.get(position, false));
        if (selectedItems.get(position, false)) {
            holder.cardView.setCardBackgroundColor(Color.LTGRAY);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }


        // Toggle selection on item click
        holder.itemView.setOnClickListener(v -> {
            toggleSelection(position);
            listener.onItemClick(report);
        });

        // 🔹 View report (open URL)
        holder.btnViewReport.setOnClickListener(v -> {
            if (listener != null) listener.onViewReportClick(report);
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

    private void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
            selectedReports.remove(reports.get(position));
        } else {
            selectedItems.put(position, true);
            selectedReports.add(reports.get(position));
        }
        notifyItemChanged(position);
    }

    public List<Report> getSelectedReports() {
        return selectedReports;
    }


    @Override
    public int getItemCount() {
        return reports.size();
    }

    @Override
    public Filter getFilter() {
        return reportFilter;
    }

    private Filter reportFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Report> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(reportsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Report item : reportsFull) {
                    if (item.title.toLowerCase().contains(filterPattern) || item.type.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            reports.clear();
            reports.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // 🔹 ViewHolder
    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvType, tvNotes;
        Button btnViewReport;
        ImageButton btnShareSingle;
        CardView cardView;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            btnViewReport = itemView.findViewById(R.id.btnViewReport);
            btnShareSingle = itemView.findViewById(R.id.btnShareSingle);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    // 🔹 Click callback
    public interface OnReportClickListener {
        void onViewReportClick(Report report);
        void onItemClick(Report report);
    }
}
