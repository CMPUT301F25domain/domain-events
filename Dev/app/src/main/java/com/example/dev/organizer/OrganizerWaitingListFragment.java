// LLM was used for outlining initial ideas for RecyclerView adapter/ViewHolder
// structure and refresh(); I reviewed, implemented, and tested the code myself.

package com.example.dev.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;
import com.example.dev.organizer.di.ServiceLocator;
import com.example.dev.repo.WaitingListRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class OrganizerWaitingListFragment extends Fragment {

    private RecyclerView rv;
    private TextView countTv;
    private ProgressBar loadingPb;
    private View filterPanel;
    private MaterialButtonToggleGroup sortToggleGroup;
    private MaterialButton newestBtn;
    private MaterialButton oldestBtn;
    private final List<Entrant> data = new ArrayList<>();
    private boolean newestFirst = true; // simple filter: toggle sort order
    private String eventId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            eventId = args.getString("extra_event_id");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_organizer_waiting_list, container, false);

        countTv = v.findViewById(R.id.textCount);
        rv = v.findViewById(R.id.recyclerWaitingList);
        loadingPb = v.findViewById(R.id.progressWaitingList);
        filterPanel = v.findViewById(R.id.filterPanel);
        sortToggleGroup = v.findViewById(R.id.toggleSortOrder);
        newestBtn = v.findViewById(R.id.buttonNewestFirst);
        oldestBtn = v.findViewById(R.id.buttonOldestFirst);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new WaitingAdapter(data));

        View filterBtn = v.findViewById(R.id.buttonFilter);
        if (filterBtn != null) {
            filterBtn.setOnClickListener(btn -> {
                if (filterPanel != null) {
                    int visibility = filterPanel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                    filterPanel.setVisibility(visibility);
                }
            });
        }

        if (sortToggleGroup != null) {
            sortToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (!isChecked) return;
                if (checkedId == R.id.buttonOldestFirst) {
                    newestFirst = false;
                    Toast.makeText(getContext(), "Sorting by oldest first", Toast.LENGTH_SHORT).show();
                } else {
                    newestFirst = true;
                    Toast.makeText(getContext(), "Sorting by newest first", Toast.LENGTH_SHORT).show();
                }
                refresh();
            });
            updateSortToggles();
        }

        refresh();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Keep the list fresh if returning to this screen
        refresh();
    }

    private void refresh() {
        if (eventId == null) {
            Toast.makeText(getContext(), "Missing event id", Toast.LENGTH_SHORT).show();
            return;
        }
        setLoading(true);
        WaitingListRepository repo = ServiceLocator.waiting();

        // Show cached data immediately if available
        applyData(repo.cached(eventId));

        repo.list(eventId, new WaitingListRepository.Callback() {
            @Override
            public void onSuccess(List<Entrant> entrants) {
                if (!isAdded()) return;
                setLoading(false);
                applyData(entrants);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                setLoading(false);
                Toast.makeText(getContext(), "Failed to load waiting list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyData(List<Entrant> latest) {
        data.clear();
        if (latest != null) {
            data.addAll(latest);
        }

        // Sort by join time (newest first by default)
        Comparator<Entrant> cmp = Comparator.comparingLong(e -> e.joinedAtMillis);
        if (newestFirst) {
            cmp = Collections.reverseOrder(cmp);
        }
        Collections.sort(data, cmp);

        // Update count from the latest snapshot (US 01.05.04)
        int total = data.size();
        countTv.setText("Waiting: " + total);

        updateSortToggles();

        // Notify adapter
        RecyclerView.Adapter<?> adapter = rv.getAdapter();
        if (adapter == null) {
            rv.setAdapter(new WaitingAdapter(data));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void updateSortToggles() {
        if (sortToggleGroup != null) {
            int targetId = newestFirst ? R.id.buttonNewestFirst : R.id.buttonOldestFirst;
            if (sortToggleGroup.getCheckedButtonId() != targetId) {
                sortToggleGroup.check(targetId);
            }
        }
        if (newestBtn != null) {
            newestBtn.setChecked(newestFirst);
        }
        if (oldestBtn != null) {
            oldestBtn.setChecked(!newestFirst);
        }
    }
    private void setLoading(boolean loading) {
        if (loadingPb != null) {
            loadingPb.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    // --- Adapter & ViewHolder ---

    static class WaitingAdapter extends RecyclerView.Adapter<VH> {
        private final List<Entrant> items;

        WaitingAdapter(List<Entrant> items) {
            this.items = items;
            setHasStableIds(false);
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_waiting_entrant, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Entrant e = items.get(pos);
            h.name.setText(e.name);
            h.email.setText(e.email);
            h.joined.setText(formatJoined(e.joinedAtMillis));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private static String formatJoined(long millis) {
            return "Joined: " + DateFormat.getDateTimeInstance().format(new Date(millis));
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, email, joined;

        VH(View v) {
            super(v);
            name = v.findViewById(R.id.textName);
            email = v.findViewById(R.id.textEmail);
            // Make sure your layout uses this ID; if your XML has textJoined instead,
            // change this findViewById to R.id.textJoined.
            joined = v.findViewById(R.id.textJoinedAt);
        }
    }
}
