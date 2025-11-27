/**
 * EntrantEventAdapter
 *
 * RecyclerView adapter that binds event data to the entrant event list UI.
 * Each event item displays:
 *  - Event name
 *  - Location
 *  - Date
 *
 * When an item is clicked, the user is taken to EventDetailsActivity.
 *
 * Responsibilities:
 *  - Inflate event list item layout
 *  - Bind model data to UI elements
 *  - Handle click navigation
 */

package com.example.dev.entrant.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;
import com.example.dev.entrant.models.EntrantEvent;
import com.example.dev.entrant.EventDetailsActivity;

import java.util.List;

public class EntrantEventAdapter extends RecyclerView.Adapter<EntrantEventAdapter.ViewHolder> {

    private Context context;
    private List<EntrantEvent> eventList;

    public EntrantEventAdapter(Context context, List<EntrantEvent> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.entrant_item_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EntrantEvent event = eventList.get(position);

        holder.name.setText(event.getEventName());
        holder.location.setText("Location: " + event.getLocation());
        holder.date.setText("Date: " + event.getEventDate());

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, EventDetailsActivity.class);
            i.putExtra("eventId", event.getEventId()); // better to send ID
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() { return eventList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, date, joinedTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            location = itemView.findViewById(R.id.eventLocation);
            date = itemView.findViewById(R.id.eventDate);
            joinedTextView = itemView.findViewById(R.id.joined_text_view);
        }
    }
}
