package com.example.projectsos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText phoneEditText;
    private Button addContactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact); // Assuming you have a layout file named activity_add_contact.xml

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addContactButton = findViewById(R.id.addContactButton);

        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });
    }

    private void addContact() {
        String name = nameEditText.getText().toString();
        String phoneNumber = phoneEditText.getText().toString();

        // Perform validation if needed

        // Store contact details (e.g., in SharedPreferences)
        // ... (Implementation for storing contact details) ...

        Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
        finish(); // Optionally finish the activity
    }
}