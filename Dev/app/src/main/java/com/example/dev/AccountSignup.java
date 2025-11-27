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

import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.admin.AdminNavActivity;
import com.example.dev.entrant.EntrantMainActivity;
import com.example.dev.firebaseobjects.FirebaseAccount;
import com.example.dev.organizer.OrganizerDashboardActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class AccountSignup extends AppCompatActivity {
    private Spinner accountSpi;
    private EditText clearanceEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button signupBtn;
    private TextView loginRedirectText;
    private FirebaseFirestore database;
    private List<String> accountTypes = Arrays.asList("Entrant", "Admin", "Organizer");     //account type 2, 3, 1 respectively
    private String adminClearance = "iamadmin";             //account type 3
    private String organizerClearance = "iamorganizer";     //account type 1
    private int selectedAccountType = 2;
    private static final int ROLE_ENTRANT = 2;
    private static final int ROLE_ADMIN = 3;
    private static final int ROLE_ORGANIZER = 1;

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.item_spinner_default, accountTypes
        );
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        accountSpi.setAdapter(adapter);

        accountSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (accountTypes.get(position)) {
                    case "Admin":
                        selectedAccountType = ROLE_ADMIN;
                        clearanceEditText.setVisibility(View.VISIBLE);
                        break;
                    case "Organizer":
                        selectedAccountType = ROLE_ORGANIZER;
                        clearanceEditText.setVisibility(View.VISIBLE);
                        break;
                    case "Entrant":
                        selectedAccountType = ROLE_ENTRANT;
                        clearanceEditText.setVisibility(View.GONE);
                        break;
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
                String clearancePassword = clearanceEditText.getText().toString().trim();
                String inputUsername = usernameEditText.getText().toString().trim().toLowerCase();  //username is case insensitive
                String inputPassword = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                DocumentReference newAccountRef = database.collection("accounts").document(inputUsername);

                if (selectedAccountType == ROLE_ADMIN && !clearancePassword.equals(adminClearance)) {
                    clearanceEditText.setError("You don't have Admin Clearance");
                    return;
                }
                if (selectedAccountType == ROLE_ORGANIZER && !clearancePassword.equals(organizerClearance)) {
                    clearanceEditText.setError("You don't have Organizer Clearance");
                    return;
                }
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

                newAccountRef.get().addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Toast.makeText(AccountSignup.this, "Username already exists", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(AccountSignup.this, "Finna add account", Toast.LENGTH_SHORT).show();

                        FirebaseAccount newAccount = new FirebaseAccount(inputUsername, selectedAccountType, inputPassword, inputUsername);

                        newAccountRef.set(newAccount)
                                .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AccountSignup.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                            if (selectedAccountType == ROLE_ORGANIZER) {
                                startActivity(new Intent(AccountSignup.this, OrganizerDashboardActivity.class));
                            } else if (selectedAccountType == ROLE_ENTRANT) {
                                startActivity(new Intent(AccountSignup.this, EntrantMainActivity.class));
                            } else if (selectedAccountType == ROLE_ADMIN) {
                                startActivity(new Intent(AccountSignup.this, AdminNavActivity.class));
                            }
                        })
                                .addOnFailureListener(e ->
                                        Toast.makeText(AccountSignup.this, "Error registering account", Toast.LENGTH_LONG).show());
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
