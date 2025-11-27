package com.example.dev.organizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dev.R;

import java.util.List;

/**
 * Adapter for the RecyclerView in the Organizer Dashboard -> Displays a list of Event objects
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private final EventClickListener clickListener;

    public interface EventClickListener{
        void onEventClick(String eventId);
    }

    public EventAdapter(List<Event> eventList, EventClickListener clickListener){
        this.eventList = eventList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event,parent,false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position){
        Event event = eventList.get(position);

        holder.title.setText(event.getTitle());
        String locationCategory = event.getLocation() + " | " + event.getCategory();
        holder.locationCategory.setText(locationCategory);

        holder.eventEnd.setText(event.getClosingTime());
        holder.capacity.setText(String.valueOf("Capacity" + event.getCapacity()));
        Glide.with(holder.poster.getContext())
                .load(event.getPosterUrl())
                .placeholder(R.drawable.bg_event_poster_placeholder)
                .error(R.drawable.bg_event_poster_placeholder)
                .into(holder.poster);

        holder.cardLayout.setOnClickListener(v -> {
            clickListener.onEventClick(event.getEventId());
        });
    }

    @Override
    public int getItemCount(){
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView locationCategory;
        public TextView eventEnd;
        public TextView capacity;
        public CardView cardLayout;
        public ImageView poster;

        public EventViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.TV_event_title);
            locationCategory = itemView.findViewById(R.id.TV_event_loc_category);
            eventEnd = itemView.findViewById(R.id.TV_registration_closes);
            capacity = itemView.findViewById(R.id.TV_capacity);
            cardLayout = itemView.findViewById(R.id.card_view1);
            poster = itemView.findViewById(R.id.activity_image_view);
        }
    }


}
