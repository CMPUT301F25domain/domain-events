package com.example.dev.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dev.R;

public class AdminImageFragment extends Fragment {
    public AdminImageFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout image1 = view.findViewById(R.id.image1);
        LinearLayout image2 = view.findViewById(R.id.image2);
        LinearLayout image3 = view.findViewById(R.id.image3);
        LinearLayout image4 = view.findViewById(R.id.image4);
        LinearLayout image5 = view.findViewById(R.id.image5);

        TextView remove1 = view.findViewById(R.id.removeImage1);
        TextView remove2 = view.findViewById(R.id.removeImage2);
        TextView remove3 = view.findViewById(R.id.removeImage3);
        TextView remove4 = view.findViewById(R.id.removeImage4);
        TextView remove5 = view.findViewById(R.id.removeImage5);

        remove1.setOnClickListener(v -> image1.setVisibility(View.GONE));
        remove2.setOnClickListener(v -> image2.setVisibility(View.GONE));
        remove3.setOnClickListener(v -> image3.setVisibility(View.GONE));
        remove4.setOnClickListener(v -> image4.setVisibility(View.GONE));
        remove5.setOnClickListener(v -> image5.setVisibility(View.GONE));
    }
}
