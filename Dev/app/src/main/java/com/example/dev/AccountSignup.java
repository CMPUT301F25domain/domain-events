package com.example.dev;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.admin.AdminNavActivity;
import com.example.dev.entrant.EntrantMainActivity;
import com.example.dev.organizer.CreateEventActivity;
import com.example.dev.organizer.FirebaseEvent;
import com.example.dev.organizer.OrganizerDashboardActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class AccountSignup extends AppCompatActivity {
    private Spinner accountSpi;
    private EditText clearanceEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button signupBtn;
    private TextView loginRedirectText;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        accountSpi = findViewById(R.id.spi_account_type);
        clearanceEditText = findViewById(R.id.ET_clearance_password);
        usernameEditText = findViewById(R.id.ET_username);
        passwordEditText = findViewById(R.id.ET_password);
        confirmPasswordEditText = findViewById(R.id.ET_confirm_password);
        signupBtn = findViewById(R.id.btn_signup);
        loginRedirectText = findViewById(R.id.TV_login_redirect);
        database = FirebaseFirestore.getInstance();

        List<String> accountTypes = Arrays.asList("Entrant", "Admin", "Organizer");

        String adminClearance = "admin clearance";
        String organizerClearance = "organizer clearance";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.item_spinner_default, accountTypes
        );
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        accountSpi.setAdapter(adapter);

        accountSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (accountTypes.get(position).equals("Admin") || accountTypes.get(position).equals("Organizer")) {
                    clearanceEditText.setVisibility(View.VISIBLE);
                } else {
                    clearanceEditText.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nithing
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputUsername = usernameEditText.getText().toString().trim();
                String inputPassword = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(inputUsername)) {
                    usernameEditText.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(inputPassword)) {
                    passwordEditText.setError("Password is required");
                    return;
                }
                if (!inputPassword.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Passwords do not match");
                    return;
                }



                database.collection("accounts").whereEqualTo("username", inputUsername).get().addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Toast.makeText(AccountSignup.this, "Username already exists", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(AccountSignup.this, "Finna add account", Toast.LENGTH_LONG).show();
                        DocumentReference newAccountRef = database.collection("accounts").document();
                        String accountId = newAccountRef.getId();

                        FirebaseAccount newAccount = new FirebaseAccount(accountId,2, inputPassword, inputUsername);

                        newAccountRef.set(newAccount).addOnSuccessListener(aVoid -> {
                            Toast.makeText(AccountSignup.this, "Account '" + inputUsername + "' created successfully", Toast.LENGTH_LONG).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(AccountSignup.this, "Error registering account", Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountSignup.this, AccountLogin.class);
                startActivity(intent);
            }
        });
    }
}
