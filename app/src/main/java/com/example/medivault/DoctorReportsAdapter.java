package com.example.medivault;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorReportsAdapter
        extends RecyclerView.Adapter<DoctorReportsAdapter.ViewHolder> {

    public interface OnReportClick {
        void onClick(DoctorReportItem item);
    }

    List<DoctorReportItem> reports;
    OnReportClick listener;

    public DoctorReportsAdapter(List<DoctorReportItem> reports, OnReportClick listener) {
        this.reports = reports;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoctorReportItem item = reports.get(position);
        holder.text.setText(item.title);

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(android.R.id.text1);
        }
    }
}
