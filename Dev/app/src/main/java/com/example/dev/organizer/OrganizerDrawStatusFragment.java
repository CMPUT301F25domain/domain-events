
//LLM was used sparingly to validate RecyclerView and the adapter structure
// and method naming. I wrote and adapted also verified the final implementation any errors are my own.

package com.example.dev.organizer;

import static com.example.dev.Keys.DEMO_EVENT_ID;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import java.util.*;

import com.example.dev.organizer.di.ServiceLocator;
import com.example.dev.organizer.Invitation;
import com.example.dev.organizer.InvitationStatus;
import com.example.dev.R;


public class OrganizerDrawStatusFragment extends Fragment {
    private RecyclerView rv; private TextView declinedTv;
    private List<Invitation> data = new ArrayList<>();

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
                                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        data = ServiceLocator.invites().listByEvent(DEMO_EVENT_ID);
        rv.setAdapter(new InvitedAdapter(data, this::onReplaceClick, this::onCancelClick));
        int declined = 0;
        for (Invitation i : data) if (i.status == InvitationStatus.DECLINED) declined++;
        declinedTv.setText("Declined: " + declined);
    }

    private void onReplaceClick(Invitation inv) {
        if (inv.status != InvitationStatus.DECLINED && inv.status != InvitationStatus.CANCELLED) {
            Toast.makeText(getContext(),"Replace only on declined/cancelled", Toast.LENGTH_SHORT).show();
            return;
        }
        String nextEntrant = ServiceLocator.lottery().replaceWithNext(DEMO_EVENT_ID);
        if (nextEntrant == null) {
            Toast.makeText(getContext(),"No one left in queue", Toast.LENGTH_SHORT).show();
            return;
        }
        Invitation newInv = ServiceLocator.invites().invite(DEMO_EVENT_ID, nextEntrant);
        ServiceLocator.notif().log(nextEntrant, DEMO_EVENT_ID, "You have been selected!", "SELECTED");
        Toast.makeText(getContext(), "Invited replacement", Toast.LENGTH_SHORT).show();
        bind();
    }

    // US 02.06.04 (cancel)
    private void onCancelClick(Invitation inv) {
        ServiceLocator.invites().setStatus(inv.id, InvitationStatus.CANCELLED);
        ServiceLocator.notif().log(inv.entrantId, DEMO_EVENT_ID, "Your invitation was cancelled", "CANCELLED");
        bind();
    }

    // US 02.07.02
    private void notifySelected() {
        for (Invitation i : data) {
            if (i.status == InvitationStatus.INVITED) {
                ServiceLocator.notif().log(i.entrantId, DEMO_EVENT_ID, "Reminder: you're selected!", "SELECTED_BROADCAST");
            }
        }
        Toast.makeText(getContext(),"Notified all selected", Toast.LENGTH_SHORT).show();
    }

    private void notifyCancelled() {
        for (Invitation i : data) {
            if (i.status == InvitationStatus.CANCELLED) {
                ServiceLocator.notif().log(i.entrantId, DEMO_EVENT_ID, "Update: your invite was cancelled", "CANCELLED_BROADCAST");
            }
        }
        Toast.makeText(getContext(),"Notified all cancelled", Toast.LENGTH_SHORT).show();
    }


    // helpers to simulate entrant actions accept and decline
    private void acceptInvite(Invitation inv) {
        ServiceLocator.invites().setStatus(inv.id, InvitationStatus.ACCEPTED);
        ServiceLocator.enroll().enroll(inv.eventId, inv.entrantId);
        bind();
    }

    private void declineInvite(Invitation inv) {
        ServiceLocator.invites().setStatus(inv.id, InvitationStatus.DECLINED);
        bind();
    }

    // Start of Adapter
    static class InvitedAdapter extends RecyclerView.Adapter<VH> {
        interface OnInvAction { void run(Invitation inv); }
        private final List<Invitation> data;
        private final OnInvAction onReplace, onCancel;
        InvitedAdapter(List<Invitation> d, OnInvAction repl, OnInvAction cancel) {
            data=d; onReplace=repl; onCancel=cancel;
        }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_invited_entrant, p, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Invitation inv = data.get(pos);
            h.name.setText(inv.entrantId);
            h.email.setText(inv.entrantId + "@example");
            h.status.setText(inv.status.name());
            h.replace.setOnClickListener(v -> onReplace.run(inv));
            h.cancel.setOnClickListener(v -> onCancel.run(inv));
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class VH extends RecyclerView.ViewHolder {
        TextView name, email, status; View replace, cancel;
        VH(View v){ super(v);
            name=v.findViewById(R.id.textName);
            email=v.findViewById(R.id.textEmail);
            status=v.findViewById(R.id.textStatus);
            replace=v.findViewById(R.id.buttonReplace);
            cancel=v.findViewById(R.id.buttonCancel);
        }
    }
}
