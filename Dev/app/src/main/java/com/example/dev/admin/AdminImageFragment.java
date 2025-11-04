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

        // Array of image layout and remove button pairs
        int[][] imagePairs = {
                {R.id.image1, R.id.removeImage1},
                {R.id.image2, R.id.removeImage2},
                {R.id.image3, R.id.removeImage3},
                {R.id.image4, R.id.removeImage4},
                {R.id.image5, R.id.removeImage5}
        };

        // Loop through all image pairs to attach remove listeners
        for (int[] pair : imagePairs) {
            LinearLayout imageLayout = view.findViewById(pair[0]);
            TextView removeButton = view.findViewById(pair[1]);
            removeButton.setOnClickListener(v -> imageLayout.setVisibility(View.GONE));
        }
    }
}
