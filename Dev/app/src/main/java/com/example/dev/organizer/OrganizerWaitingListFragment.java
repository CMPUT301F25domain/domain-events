// LLM was used for outlining initial ideas for recyclerView adapter/viewHolder
// structure and refresh() while I did the reviewing and testing all code myself

package com.example.dev.organizer;

import static com.example.dev.Keys.DEMO_EVENT_ID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;
import com.example.dev.organizer.di.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class OrganizerWaitingListFragment extends Fragment {

    private RecyclerView rv;
    private TextView countTv;
    private final List<Entrant> data = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_organizer_waiting_list, container, false);
        countTv = v.findViewById(R.id.textCount);
        v.findViewById(R.id.buttonFilter).setOnClickListener(btn -> refresh());
        rv = v.findViewById(R.id.recyclerWaitingList);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new WaitingAdapter(data));
        refresh();
        return v;
    }

    private void refresh() {
        data.clear();
        data.addAll(ServiceLocator.waiting().list(DEMO_EVENT_ID));
        rv.getAdapter().notifyDataSetChanged();
        countTv.setText("Waiting: " + data.size());
    }


    static class WaitingAdapter extends RecyclerView.Adapter<VH> {
        private final List<Entrant> items;
        WaitingAdapter(List<Entrant> items) { this.items = items; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_waiting_entrant, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Entrant e = items.get(pos);
            h.name.setText(e.name);
            h.email.setText(e.email);
            h.joined.setText(String.valueOf(e.joinedAtMillis));
        }

        @Override public int getItemCount() { return items.size(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, email, joined;
        VH(View v) {
            super(v);
            name = v.findViewById(R.id.textName);
            email = v.findViewById(R.id.textEmail);
            joined = v.findViewById(R.id.textJoinedAt);
        }
    }
}





























