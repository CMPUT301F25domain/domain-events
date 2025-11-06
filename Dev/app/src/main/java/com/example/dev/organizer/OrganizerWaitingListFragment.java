package com.example.dev.organizer;



import static com.example.dev.organizer.Keys.DEMO_EVENT_ID;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import java.text.DateFormat;
import java.util.*;
import com.example.dev.organizer.di.ServiceLocator;
import com.example.dev.organizer.model.Entrant;

public class OrganizerWaitingListFragment extends Fragment {
    private RecyclerView rv; private TextView count;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
                                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_organizer_waiting_list, container, false);
        rv = v.findViewById(R.id.recyclerWaitingList);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        count = v.findViewById(R.id.textCount);
        bind();
        v.findViewById(R.id.buttonFilter).setOnClickListener(view ->
                // TODO: show your filter sheet: date joined / order / location
        {});
        return v;
    }

    private void bind() {
        List<Entrant> list = ServiceLocator.waiting().list(DEMO_EVENT_ID);
        count.setText("Waiting: " + ServiceLocator.waiting().count(DEMO_EVENT_ID));
        rv.setAdapter(new WaitingAdapter(list));
    }

    static class WaitingAdapter extends RecyclerView.Adapter<VH> {
        private final List<Entrant> data;
        WaitingAdapter(List<Entrant> d){ data = d; }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_waiting_entrant, p, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Entrant e = data.get(pos);
            h.name.setText(e.name);
            h.email.setText(e.email);
            h.joined.setText("Joined: " + DateFormat.getDateTimeInstance().format(new Date(e.joinedAtMillis)));
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class VH extends RecyclerView.ViewHolder {
        TextView name, email, joined;
        VH(View v){ super(v);
            name=v.findViewById(R.id.textName);
            email=v.findViewById(R.id.textEmail);
            joined=v.findViewById(R.id.textJoined);
        }
    }
}
