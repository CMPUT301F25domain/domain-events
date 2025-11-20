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

import java.util.Arrays;
import java.util.List;

public class AccountSignup extends AppCompatActivity {
    private Spinner accountSpi;
    private EditText clearanceEditText, usernameEditText, passwordEditText;
    private Button signupBtn;
    private TextView loginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        accountSpi = findViewById(R.id.spi_account_type);
        clearanceEditText = findViewById(R.id.ET_clearance_password);
        usernameEditText = findViewById(R.id.ET_username);
        passwordEditText = findViewById(R.id.ET_password);
        signupBtn = findViewById(R.id.btn_signup);
        loginRedirectText = findViewById(R.id.TV_login_redirect);

        List<String> accountTypes = Arrays.asList("Entrant", "Admin", "Organizer");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, accountTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    usernameEditText.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required");
                    return;
                }

                // TODO: Check username duplicates
                Toast.makeText(AccountSignup.this, "Logged in as: " + username, Toast.LENGTH_SHORT).show();
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
