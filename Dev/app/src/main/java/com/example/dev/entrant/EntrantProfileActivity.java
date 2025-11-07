package com.example.dev.entrant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dev.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Optional;

public class EntrantProfileActivity extends AppCompatActivity {

    private static final String PREFS_PROFILE = "entrant_profile";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";

    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private MaterialButton btnSave;
    private MaterialButton btnDelete;
    private boolean editMode = false;
    private MenuItem editMenuItem;

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

        btnSave.setOnClickListener(v -> saveProfile());
        btnDelete.setOnClickListener(v -> confirmDeletion());

        populateFields();
        setEditing(false);
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

    private void populateFields() {
        SharedPreferences prefs = getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);
        String name = prefs.getString(KEY_NAME, "");
        String email = prefs.getString(KEY_EMAIL, "");
        String phone = prefs.getString(KEY_PHONE, "");
        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);
    }

    private void toggleEditMode() {
        setEditing(!editMode);
    }
    private void setEditing(boolean editing) {
        editMode = editing;
        etName.setEnabled(editing);
        etEmail.setEnabled(editing);
        etPhone.setEnabled(editing);
        tilName.setEnabled(editing);
        tilEmail.setEnabled(editing);
        tilPhone.setEnabled(editing);
        btnSave.setVisibility(editing ? View.VISIBLE : View.GONE);
        if (editMenuItem != null) {
            editMenuItem.setTitle(editing ? R.string.profile_menu_done : R.string.profile_menu_edit);
            editMenuItem.setIcon(editing ? R.drawable.ic_check_24 : R.drawable.ic_edit_24);
        }
    }

    private void updateEditMenuTitle() {
        if (editMenuItem != null) {
            editMenuItem.setTitle(editMode ? R.string.profile_menu_done : R.string.profile_menu_edit);
            editMenuItem.setIcon(editMode ? R.drawable.ic_check_24 : R.drawable.ic_edit_24);
        }
    }

    private void saveProfile() {
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

        if (phone.isEmpty()) {
            phone = "";
        }

        if (!valid) {
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PHONE, phone)
                .apply();

        Snackbar.make(etName, R.string.profile_saved_message, Snackbar.LENGTH_SHORT).show();
        setEditing(false);
    }

    private void confirmDeletion() {
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
        SharedPreferences prefs = getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        Snackbar.make(etName, R.string.profile_deleted_message, Snackbar.LENGTH_SHORT).show();
        setEditing(true);
    }
}

