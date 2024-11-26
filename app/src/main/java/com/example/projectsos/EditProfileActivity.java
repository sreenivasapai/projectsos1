package com.example.projectsos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText phoneEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile); // Corrected layout file name

        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
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