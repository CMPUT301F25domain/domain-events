package com.example.dev;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AccountLogin extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginBtn;
    private TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.ET_username);
        passwordEditText = findViewById(R.id.ET_password);
        loginBtn = findViewById(R.id.btn_login);
        signupRedirectText = findViewById(R.id.TV_signup_redirect);


        loginBtn.setOnClickListener(view -> {
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

            // TODO: Authenticate account and delegate to org,admin or entrant screen
            Toast.makeText(AccountLogin.this, "Logged in as: " + username, Toast.LENGTH_SHORT).show();
        });

        signupRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(AccountLogin.this, AccountSignup.class);
            startActivity(intent);
        });
    }
}
