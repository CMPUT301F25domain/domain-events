package com.example.dev.firebaseobjects;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;

import java.util.List;
import java.util.Map;

public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.ViewHolder> {

    private List<Map<String, Object>> waitingList;

    public EntrantAdapter(List<Map<String, Object>> waitingList) {
        this.waitingList = waitingList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organizer_draw_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, Object> entrantMap = waitingList.get(position);

        holder.name.setText((String) entrantMap.get("name"));
        holder.gmail.setText((String) entrantMap.get("email"));

        String status = (String) entrantMap.get("status");
        if (status == null) status = "unknown";
        holder.status.setText("Status: " + status);

        switch (status) {
            case "invited":
                holder.status.setTextColor(Color.YELLOW);
                break;
            case "accepted":
                holder.status.setTextColor(Color.GREEN);
                break;
            case "declined":
            case "deleted":
                holder.status.setTextColor(Color.RED);
                break;
            case "waitListed":
            default:
                holder.status.setTextColor(Color.GRAY);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return waitingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, gmail, status;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.entrantName);
            gmail = itemView.findViewById(R.id.entrantGmail);
            status = itemView.findViewById(R.id.entrantStatus);
        }
    }
}
