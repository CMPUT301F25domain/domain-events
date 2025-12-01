// LLM was used sparingly to validate RecyclerView/adapter structure and method naming.
// I wrote, adapted, and verified the final implementation; any errors are my own.

package com.example.dev.organizer;

import static com.example.dev.Keys.DEMO_EVENT_ID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dev.R;
import com.example.dev.organizer.di.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class OrganizerDrawStatusFragment extends Fragment {

    private RecyclerView rv;
    private TextView declinedTv;
    private final List<Invitation> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_organizer_draw_status, container, false);
        rv = v.findViewById(R.id.recyclerInvited);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        declinedTv = v.findViewById(R.id.textDeclinedCount);

        v.findViewById(R.id.buttonNotifySelected).setOnClickListener(view -> notifySelected());
        v.findViewById(R.id.buttonNotifyCancelled).setOnClickListener(view -> notifyCancelled());

        bind();
        return v;
    }

    private void bind() {
        // Pull all invitations for this event and refresh UI
        data.clear();
        data.addAll(ServiceLocator.invites().listByEvent(DEMO_EVENT_ID));
        rv.setAdapter(new InvitedAdapter(data, this::onReplaceClick, this::onCancelClick));

        int declined = 0;
        for (Invitation i : data) {
            if (i.status == InvitationStatus.DECLINED) declined++;
        }
        declinedTv.setText("Declined: " + declined);
    }

    private void onReplaceClick(Invitation inv) {
        if (inv.status != InvitationStatus.DECLINED && inv.status != InvitationStatus.CANCELLED) {
            Toast.makeText(getContext(), "Replace only on declined/cancelled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get next eligible entrant from waiting queue
        String nextEntrant = ServiceLocator.lottery().nextEligible(DEMO_EVENT_ID);
        if (nextEntrant == null) {
            Toast.makeText(getContext(), "No one left in queue", Toast.LENGTH_SHORT).show();
            return;
        }

        // Invite the replacement
        ServiceLocator.invites().invite(DEMO_EVENT_ID, nextEntrant);

        // TODO: when NotificationRepoMem provides a method, call it here.
        // Example (adjust to your actual signature):
        // ServiceLocator.notifications().log(nextEntrant, DEMO_EVENT_ID, "You have been selected!", "SELECTED");

        Toast.makeText(getContext(), "Invited replacement", Toast.LENGTH_SHORT).show();
        bind();
    }

    // US 02.06.04: Organizer cancels an invite
    private void onCancelClick(Invitation inv) {
        ServiceLocator.invites().setStatus(inv.id, InvitationStatus.CANCELLED);

        // TODO: when NotificationRepoMem exposes an API, notify the entrant here.
        // Example:
        // ServiceLocator.notifications().log(inv.entrantId, inv.eventId, "Your invitation was cancelled", "CANCELLED");

        Toast.makeText(getContext(), "Invitation cancelled", Toast.LENGTH_SHORT).show();
        bind();
    }

    // US 02.07.02: notify all currently INVITED entrants
    private void notifySelected() {
        int count = 0;
        for (Invitation i : data) {
            if (i.status == InvitationStatus.INVITED) {
                // TODO: hook into notifications repo when available
                // ServiceLocator.notifications().log(i.entrantId, i.eventId, "Reminder: you're selected!", "SELECTED_BROADCAST");
                count++;
            }
        }
        Toast.makeText(getContext(), "Would notify " + count + " selected entrants", Toast.LENGTH_SHORT).show();
    }

    // US 02.07.03: notify all currently CANCELLED entrants
    private void notifyCancelled() {
        int count = 0;
        for (Invitation i : data) {
            if (i.status == InvitationStatus.CANCELLED) {
                // TODO: hook into notifications repo when available
                // ServiceLocator.notifications().log(i.entrantId, i.eventId, "Update: your invite was cancelled", "CANCELLED_BROADCAST");
                count++;
            }
        }
        Toast.makeText(getContext(), "Would notify " + count + " cancelled entrants", Toast.LENGTH_SHORT).show();
    }

    /* ===== RecyclerView adapter ===== */

    static class InvitedAdapter extends RecyclerView.Adapter<VH> {
        interface OnInvAction { void run(Invitation inv); }

        private final List<Invitation> data;
        private final OnInvAction onReplace;
        private final OnInvAction onCancel;

        InvitedAdapter(List<Invitation> data, OnInvAction onReplace, OnInvAction onCancel) {
            this.data = data;
            this.onReplace = onReplace;
            this.onCancel = onCancel;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_invited_entrant, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Invitation inv = data.get(position);
            h.name.setText(inv.entrantId);
            h.email.setText(inv.entrantId + "@example");
            h.status.setText(inv.status.name());
            h.replace.setOnClickListener(v -> onReplace.run(inv));
            h.cancel.setOnClickListener(v -> onCancel.run(inv));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, email, status;
        View replace, cancel;

        VH(View v) {
            super(v);
            name = v.findViewById(R.id.textName);
            email = v.findViewById(R.id.textEmail);
            status = v.findViewById(R.id.textStatus);
            replace = v.findViewById(R.id.buttonReplace);
            cancel = v.findViewById(R.id.buttonCancel);
        }
    }
}
