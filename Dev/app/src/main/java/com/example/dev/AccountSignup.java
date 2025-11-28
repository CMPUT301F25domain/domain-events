package com.example.dev;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.dev.entrant.EntrantBottomNavActivity;
import com.example.dev.organizer.OrganizerDashboardActivity;
import com.example.dev.firebaseobjects.FirebaseEntrant;
import com.example.dev.firebaseobjects.FirebaseOrganizer;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountSignup extends AppCompatActivity {
    private Spinner accountSpi;
    private EditText clearanceEditText, nameEditText, gmailEditText, phoneNumberEditText;
    private Button signupBtn;
    private TextView loginRedirectText;
    private FirebaseFirestore database;
    private List<String> accountTypes = Arrays.asList("Entrant", "Organizer");     //account type 2, 1 respectively
    private String organizerClearance = "iamorganizer";     //account type 1
    private int selectedAccountType = 2;
    private static final int ROLE_ENTRANT = 2;
    private static final int ROLE_ADMIN = 3;
    private static final int ROLE_ORGANIZER = 1;
    private String androidId;
    private static final Set<String> ADMIN_IDS = new HashSet<>(Arrays.asList(
            "a70410e72b8758f5", "admin_id_2", "admin_id_3", "admin_id_4", "admin_id_5", "admin_id_6"        //ADD YOURSELVES TO THIS
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("AndroidID", "Device ID: " + androidId);
        database = FirebaseFirestore.getInstance();

        if (checkIfAdmin()) {
            return;
        }

        accountSpi = findViewById(R.id.spi_account_type);
        clearanceEditText = findViewById(R.id.ET_clearance_password);
        nameEditText = findViewById(R.id.ET_name);
        gmailEditText = findViewById(R.id.ET_gmail);
        phoneNumberEditText = findViewById(R.id.ET_phone_number);
        signupBtn = findViewById(R.id.btn_signup);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.item_spinner_default, accountTypes
        );
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        accountSpi.setAdapter(adapter);

        accountSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (accountTypes.get(position)) {
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
                String inputName = nameEditText.getText().toString().trim();
                String inputGmail = gmailEditText.getText().toString().trim();
                String inputPhone = phoneNumberEditText.getText().toString().trim();

                if (selectedAccountType == ROLE_ORGANIZER && !clearancePassword.equals(organizerClearance)) {
                    clearanceEditText.setError("You don't have Organizer Clearance");
                    return;
                }
                if (TextUtils.isEmpty(inputName)) {
                    nameEditText.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(inputGmail)) {
                    gmailEditText.setError("Gmail is required");
                    return;
                }
                if (!inputGmail.toLowerCase().endsWith("@gmail.com")) {
                    gmailEditText.setError("Please enter a valid Gmail address");
                    return;
                }
                if (TextUtils.isEmpty(inputPhone)) {
                    phoneNumberEditText.setError("Phone number is required");
                    return;
                }

                database.collection("organizers")
                        .whereEqualTo("email", inputGmail)
                        .get(Source.SERVER)
                        .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Toast.makeText(AccountSignup.this, "An account with this email already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        database.collection("entrants")
                                .whereEqualTo("email", inputGmail)
                                .get(Source.SERVER)
                                .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful() && !task2.getResult().isEmpty()) {
                                Toast.makeText(AccountSignup.this, "An account with this email already exists.", Toast.LENGTH_SHORT).show();
                            } else {
                                if (selectedAccountType == ROLE_ORGANIZER) {
                                    DocumentReference newOrganizerRef = database.collection("organizers").document(androidId);
                                    FirebaseOrganizer newOrganizer = new FirebaseOrganizer(inputGmail, inputName, inputPhone);
                                    newOrganizerRef.set(newOrganizer).addOnSuccessListener(bVoid -> {
                                        Toast.makeText(AccountSignup.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AccountSignup.this, OrganizerDashboardActivity.class);
                                        intent.putExtra("organizerID", androidId);
                                        startActivity(intent);
                                        finish();
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(AccountSignup.this, "Error creating organizer profile.", Toast.LENGTH_SHORT).show();
                                    });
                                } else if (selectedAccountType == ROLE_ENTRANT) {
                                    DocumentReference newEntrantRef = database.collection("entrants").document(androidId);
                                    FirebaseEntrant newEntrant = new FirebaseEntrant(inputName, inputGmail, inputPhone);
                                    newEntrantRef.set(newEntrant).addOnSuccessListener(bVoid -> {
                                        Toast.makeText(AccountSignup.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AccountSignup.this, EntrantBottomNavActivity.class);
                                        intent.putExtra("entrantID", androidId);
                                        startActivity(intent);
                                        finish();
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(AccountSignup.this, "Error creating entrant profile.", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
        checkIfExistingEntrantOrOrganizer();
    }

    private boolean checkIfAdmin() {
        if (ADMIN_IDS.contains(androidId)) {
            Intent intent = new Intent(AccountSignup.this, AdminNavActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }

    private void checkIfExistingEntrantOrOrganizer() {
        database.collection("entrants").document(androidId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Intent intent = new Intent(AccountSignup.this, EntrantBottomNavActivity.class);
                intent.putExtra("entrantID", androidId);
                startActivity(intent);
                finish();
            } else {
                database.collection("organizers").document(androidId).get().addOnSuccessListener(documentSnapshot2 -> {
                    if (documentSnapshot2.exists()) {
                        Intent intent = new Intent(AccountSignup.this, OrganizerDashboardActivity.class);
                        intent.putExtra("organizerID", androidId);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}
