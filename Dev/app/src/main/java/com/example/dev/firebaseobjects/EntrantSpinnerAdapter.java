package com.example.dev.firebaseobjects;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dev.R;

import java.util.List;
import java.util.Map;

public class EntrantSpinnerAdapter extends ArrayAdapter<Map<String, Object>> {

    private final List<Map<String, Object>> entrants;

    public EntrantSpinnerAdapter(@NonNull Context context, List<Map<String, Object>> entrants) {
        super(context, 0, entrants);
        this.entrants = entrants;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_entrant_status, parent, false);
        }

        view.setBackgroundColor(Color.parseColor("#2C2C2C")); //grey

        Map<String, Object> entrant = entrants.get(position);

        TextView nameEmail = view.findViewById(R.id.entrant_name_email);
        TextView statusView = view.findViewById(R.id.entrant_status);

        String name = (String) entrant.get("name");
        String email = (String) entrant.get("email");
        nameEmail.setText(name + " (" + email + ")");

        nameEmail.setTextColor(Color.WHITE);

        String status = (String) entrant.get("status");
        statusView.setText("Status: " + status);

        switch (status) {
            case "invited":
                statusView.setTextColor(Color.YELLOW);
                break;
            case "accepted":
                statusView.setTextColor(Color.GREEN);
                break;
            case "declined":
            case "deleted":
                statusView.setTextColor(Color.RED);
                break;
            case "waitListed":
            default:
                statusView.setTextColor(Color.GRAY);
                break;
        }
        return view;
    }
}