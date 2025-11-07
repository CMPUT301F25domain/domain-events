package com.example.dev.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dev.R;
import com.example.dev.models.Event;
import com.example.dev.views.EventDetailsActivity;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.name.setText(event.getEventName());
        holder.location.setText("Location: " + event.getLocation());
        holder.date.setText("Date: " + event.getEventDate());


        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, EventDetailsActivity.class);
            i.putExtra("eventName", event.getEventName());
            i.putExtra("location", event.getLocation());
            i.putExtra("date", event.getEventDate());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() { return eventList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            location = itemView.findViewById(R.id.eventLocation);
            date = itemView.findViewById(R.id.eventDate);
        }
    }
}
