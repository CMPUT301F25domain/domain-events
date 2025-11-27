package com.example.dev.firebaseobjects;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;

import java.util.List;

public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.ViewHolder> {

    private List<FirebaseEntrant> entrantList;
    private List<String> statusList; // <--- NEW

    public EntrantAdapter(List<FirebaseEntrant> entrantList, List<String> statusList) {
        this.entrantList = entrantList;
        this.statusList = statusList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organizer_draw_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FirebaseEntrant entrant = entrantList.get(position);

        holder.name.setText(entrant.getName());
        holder.gmail.setText(entrant.getEmail());
        holder.status.setText("Status: " + statusList.get(position)); // <--- Use your own status array
    }

    @Override
    public int getItemCount() {
        return entrantList.size();
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
