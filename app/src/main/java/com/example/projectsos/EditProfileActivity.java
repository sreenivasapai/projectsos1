package com.example.projectsos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText; // Added for name
    private EditText emailEditText;
    private EditText phoneEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI elements
        nameEditText = findViewById(R.id.name_edit_text); // Initialized nameEditText
        emailEditText = findViewById(R.id.email_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
        String name = nameEditText.getText().toString(); // Get name
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        // Perform validation if needed

        // Store profile details (e.g., in SharedPreferences)
        // ... (Implementation for storing profile details) ...

        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
        // You might want to finish the activity or navigate back here
        // finish();
    }
}