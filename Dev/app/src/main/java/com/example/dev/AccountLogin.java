package com.example.dev;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dev.admin.AdminNavActivity;
import com.example.dev.entrant.EntrantMainActivity;
import com.example.dev.organizer.OrganizerDashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class AccountLogin extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginBtn;
    private TextView signupRedirectText;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.ET_username);
        passwordEditText = findViewById(R.id.ET_password);
        loginBtn = findViewById(R.id.btn_login);
        signupRedirectText = findViewById(R.id.TV_signup_redirect);
        database = FirebaseFirestore.getInstance();


        loginBtn.setOnClickListener(view -> {
            String inputUsername = usernameEditText.getText().toString().trim();
            String inputPassword = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(inputUsername)) {
                usernameEditText.setError("Username is required");
                return;
            }

            if (TextUtils.isEmpty(inputPassword)) {
                passwordEditText.setError("Password is required");
                return;
            }

            database.collection("accounts").whereEqualTo("username", inputUsername).get().addOnSuccessListener(querySnapshot -> {
                if (querySnapshot.isEmpty()) {
                    Toast.makeText(AccountLogin.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                String storedPassword = doc.getString("password");
                int accountType = doc.getLong("account type").intValue();

                if (storedPassword != null && storedPassword.equals(inputPassword)) {
                    if (accountType == 1) {
                        startActivity(new Intent(AccountLogin.this, OrganizerDashboardActivity.class));
                    }
                    else if (accountType == 2) {
                        startActivity(new Intent(AccountLogin.this, EntrantMainActivity.class));
                    }
                    else if (accountType == 3) {
                        startActivity(new Intent(AccountLogin.this, AdminNavActivity.class));
                    }
                }
                else {
                    Toast.makeText(AccountLogin.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        });

        signupRedirectText.setOnClickListener(view -> {
            startActivity(new Intent(AccountLogin.this, AccountSignup.class));
        });
    }
}
