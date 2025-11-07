package com.example.dev.entrant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dev.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;


import java.util.HashMap;
import java.util.Map;


public class EntrantProfileActivity extends AppCompatActivity {

    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private MaterialButton btnSave;
    private MaterialButton btnDelete;
    private LinearProgressIndicator progressIndicator;

    private MenuItem editMenuItem;
    private boolean editMode = false;
    private boolean isLoading = false;
    private boolean hasProfile = false;

    private FirebaseFirestore firestore;
    private DocumentReference profileRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_profile);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(view.getPaddingLeft(), insets.top, view.getPaddingRight(), view.getPaddingBottom());
            return windowInsets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_root), (v, insets) -> {
            v.setPadding(
                    v.getPaddingLeft(),
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );
            return insets;
        });

        tilName = findViewById(R.id.til_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        progressIndicator = findViewById(R.id.profile_progress);
        progressIndicator.hide();

        firestore = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> saveProfile());
        btnDelete.setOnClickListener(v -> confirmDeletion());

        initializeProfile();
    }

    private void initializeProfile() {
        String profileId = EntrantProfileStore.getProfileId(this);
        if (profileId != null) {
            profileRef = EntrantProfileStore.profilesCollection().document(profileId);
            loadProfile();
        } else {
            hasProfile = false;
            setEditing(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        editMenuItem = menu.findItem(R.id.action_edit_profile);
        updateEditMenuTitle();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_profile) {
            toggleEditMode();
            return true;
        } else if (id == R.id.action_view_history) {
            startActivity(HistoryActivity.intent(this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleEditMode() {
        if (isLoading) {
            return;
        }
        if (editMode) {
            saveProfile();
        } else {
            setEditing(true);
        }
    }
    private void setEditing(boolean editing) {
        editMode = editing;
        btnSave.setVisibility(editMode ? View.VISIBLE : View.GONE);
        refreshInputStates();
        updateEditMenuTitle();
    }

    private void refreshInputStates() {
        boolean enableInputs = editMode && !isLoading;
        etName.setEnabled(enableInputs);
        etEmail.setEnabled(enableInputs);
        etPhone.setEnabled(enableInputs);
        tilName.setEnabled(enableInputs);
        tilEmail.setEnabled(enableInputs);
        tilPhone.setEnabled(enableInputs);
        btnSave.setEnabled(enableInputs);
        btnDelete.setEnabled(hasProfile && !isLoading);
        if (editMenuItem != null) {
            editMenuItem.setEnabled(!isLoading);
        }
    }

    private void updateEditMenuTitle() {
        if (editMenuItem != null) {
            editMenuItem.setTitle(editMode ? R.string.profile_menu_done : R.string.profile_menu_edit);
            editMenuItem.setIcon(editMode ? R.drawable.ic_check_24 : R.drawable.ic_edit_24);
        }
    }    private void setLoading(boolean loading) {
        isLoading = loading;
        if (loading) {
            progressIndicator.show();
        } else {
            progressIndicator.hide();
        }
        refreshInputStates();
    }

    private void loadProfile() {
        if (profileRef == null) {
            return;
        }
        setLoading(true);
        profileRef.get()
                .addOnSuccessListener(this::applySnapshot)
                .addOnFailureListener(e -> showMessage(R.string.profile_load_error))
                .addOnCompleteListener(task -> setLoading(false));
    }

    private void applySnapshot(@NonNull DocumentSnapshot snapshot) {
        if (snapshot.exists()) {
            hasProfile = true;
            etName.setText(snapshot.getString("name"));
            etEmail.setText(snapshot.getString("email"));
            String phone = snapshot.getString("phone");
            etPhone.setText(phone != null ? phone : "");
            clearErrors();
            setEditing(false);
        } else {
            hasProfile = false;
            etName.setText("");
            etEmail.setText("");
            etPhone.setText("");
            clearErrors();
            setEditing(true);
        }
    }

    private void saveProfile() {
        if (!editMode || isLoading) {
            return;
        }

        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";

        boolean valid = true;
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.profile_error_name));
            valid = false;
        } else {
            tilName.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.profile_error_email));
            valid = false;
        } else {
            tilEmail.setError(null);
        }

        if (!valid) {
            return;
        }

        boolean creating = profileRef == null || !hasProfile;
        if (profileRef == null) {
            String profileId = EntrantProfileStore.ensureProfileId(this);
            profileRef = EntrantProfileStore.profilesCollection().document(profileId);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        if (phone.isEmpty()) {
            data.put("phone", FieldValue.delete());
        } else {
            data.put("phone", phone);
        }
        data.put("updatedAt", FieldValue.serverTimestamp());
        if (creating) {
            data.put("createdAt", FieldValue.serverTimestamp());
        }

        setLoading(true);
        profileRef.set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    hasProfile = true;
                    showMessage(R.string.profile_saved_message);
                    setEditing(false);
                })
                .addOnFailureListener(e -> showMessage(R.string.profile_save_error))
                .addOnCompleteListener(task -> setLoading(false));
    }

    private void confirmDeletion() {
        if (!hasProfile || profileRef == null) {
            showMessage(R.string.profile_delete_missing);
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.profile_delete_title)
                .setMessage(R.string.profile_delete_message)
                .setNegativeButton(R.string.profile_delete_history, (dialog, which) -> {
                    startActivity(HistoryActivity.intent(this));
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.profile_delete_confirm, (dialog, which) -> {
                    deleteProfile();
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteProfile() {
        if (profileRef == null) {
            showMessage(R.string.profile_delete_error);
            return;
        }

        setLoading(true);
        profileRef.collection(EntrantProfileStore.COLLECTION_HISTORY)
                .get()
                .addOnSuccessListener(snapshot -> {
                    WriteBatch batch = firestore.batch();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }
                    batch.delete(profileRef);
                    batch.commit()
                            .addOnSuccessListener(unused -> onProfileDeleted())
                            .addOnFailureListener(e -> showMessage(R.string.profile_delete_error))
                            .addOnCompleteListener(task -> setLoading(false));
                })
                .addOnFailureListener(e -> {
                    showMessage(R.string.profile_delete_error);
                    setLoading(false);
                });
    }

    private void onProfileDeleted() {
        EntrantProfileStore.clearProfileId(this);
        profileRef = null;
        hasProfile = false;
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        clearErrors();
        showMessage(R.string.profile_deleted_message);
        setEditing(true);
    }

    private void clearErrors() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
    }

    private void showMessage(@StringRes int messageRes) {
        Snackbar.make(etName, messageRes, Snackbar.LENGTH_LONG).show();
    }
}

