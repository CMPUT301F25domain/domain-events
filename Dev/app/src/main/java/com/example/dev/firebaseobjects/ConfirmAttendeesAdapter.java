package com.example.dev.firebaseobjects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;

import java.util.List;
import java.util.Map;

public class ConfirmAttendeesAdapter extends RecyclerView.Adapter<ConfirmAttendeesAdapter.ViewHolder> {

    private List<Map<String, Object>> attendees;

    public ConfirmAttendeesAdapter(List<Map<String, Object>> attendees) {
        this.attendees = attendees;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_confirm_attendee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> entrant = attendees.get(position);
        holder.nameTextView.setText((String) entrant.get("name"));
        holder.emailTextView.setText((String) entrant.get("email"));
    }

    @Override
    public int getItemCount() {
        return attendees.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.entrant_name_text_view);
            emailTextView = itemView.findViewById(R.id.entrant_email_text_view);
        }
    }
}
