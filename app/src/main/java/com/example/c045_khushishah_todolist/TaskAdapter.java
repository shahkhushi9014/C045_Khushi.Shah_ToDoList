package com.example.c045_khushishah_todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task t = taskList.get(position);
        holder.tvTitle.setText(t.getTitle());
        holder.tvDescription.setText(t.getDescription());
        holder.tvPriority.setText(priorityText(t.getPriority()));

        // color indicator
        int color = priorityColor(holder.itemView.getContext(), t.getPriority());
        holder.viewPriority.setBackgroundColor(color);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(t);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public Task getTaskAt(int pos) {
        return taskList.get(pos);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPriority;
        View viewPriority;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            viewPriority = itemView.findViewById(R.id.viewPriority);
        }
    }

    private String priorityText(int p) {
        switch (p) {
            case 1: return "High";
            case 2: return "Medium";
            default: return "Low";
        }
    }

    private int priorityColor(Context ctx, int p) {
        // replace with your color resources if you added them
        if (p == 1) return ContextCompat.getColor(ctx, android.R.color.holo_red_dark);
        if (p == 2) return ContextCompat.getColor(ctx, android.R.color.holo_orange_dark);
        return ContextCompat.getColor(ctx, android.R.color.holo_green_dark);
    }
}
