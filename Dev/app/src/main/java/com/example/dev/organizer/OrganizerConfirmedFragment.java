package com.example.dev.organizer;


import static com.example.dev.Keys.DEMO_EVENT_ID;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import java.io.*;
import java.util.*;

import com.example.dev.R;
import com.example.dev.organizer.di.ServiceLocator;
import com.example.dev.organizer.Enrollment;
public class OrganizerConfirmedFragment extends Fragment {
    private RecyclerView rv; private List<Enrollment> data = new ArrayList<>();

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
                                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_organizer_confirmed, container, false);
        rv = v.findViewById(R.id.recyclerConfirmed);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        v.findViewById(R.id.buttonExportCsv).setOnClickListener(view -> exportCsv());
        bind();
        return v;
    }

    private void bind() {
        data = ServiceLocator.enroll().list(DEMO_EVENT_ID);
        rv.setAdapter(new ConfirmedAdapter(data));
    }

    // US 02.06.05
    private void exportCsv() {
        try {
            File dir = new File(requireContext().getFilesDir(), "exports");
            if (!dir.exists()) dir.mkdirs();
            File f = new File(dir, "confirmed_" + System.currentTimeMillis() + ".csv");
            try (BufferedWriter w = new BufferedWriter(new FileWriter(f))) {
                w.write("eventId,entrantId,enrolledAt\n");
                for (Enrollment e : data) {
                    w.write(e.eventId + "," + e.entrantId + "," + e.enrolledAtMillis + "\n");
                }
            }
            // this takes part of the share part
            Uri uri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".provider", f);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/csv");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, "Share CSV"));
        } catch (Exception ex) {
            Toast.makeText(getContext(), "Export failed: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    static class ConfirmedAdapter extends RecyclerView.Adapter<VH> {
        private final List<Enrollment> data;
        ConfirmedAdapter(List<Enrollment> d){ data=d; }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_confirmed_entrant, p, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Enrollment e = data.get(pos);
            h.name.setText(e.entrantId);
            h.email.setText(e.entrantId + "@example");
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class VH extends RecyclerView.ViewHolder {
        android.widget.TextView name, email;
        VH(View v){ super(v);
            name=v.findViewById(R.id.textName);
            email=v.findViewById(R.id.textEmail);
        }
    }
}
