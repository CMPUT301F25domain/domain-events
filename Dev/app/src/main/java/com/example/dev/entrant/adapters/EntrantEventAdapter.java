/**
 * EntrantEventAdapter
 *
 * RecyclerView adapter used to display a scrollable list of events
 * in the Entrant Home screen.
 *
 * Responsibilities:
 *  - Inflate the event card layout (entrant_item_event.xml)
 *  - Bind EntrantEvent model data to UI fields (name, date, location, image)
 *  - Handle click events that open EventDetailsActivity
 *  - Load event poster images from URL using Glide
 *
 * Data Flow:
 *  - Receives a list of EntrantEvent objects
 *  - Populates each row in the RecyclerView
 */

package com.example.dev.entrant.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

        // Load image with Glide
        String posterUrl = event.getPosterUrl();
        if (posterUrl != null && !posterUrl.isEmpty() && !posterUrl.equals("null")) {
            Glide.with(context)
                    .load(posterUrl)
                    .placeholder(R.drawable.images)
                    .error(R.drawable.images)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.images);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, EventDetailsActivity.class);
            i.putExtra("eventId", event.getEventId());
            i.putExtra("eventName", event.getEventName());
            i.putExtra("location", event.getLocation());
            i.putExtra("eventDate", event.getEventDate());
            i.putExtra("posterUrl", event.getPosterUrl());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() { return eventList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, location, date;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            location = itemView.findViewById(R.id.eventLocation);
            date = itemView.findViewById(R.id.eventDate);
            image = itemView.findViewById(R.id.eventImage);
        }
    }
}