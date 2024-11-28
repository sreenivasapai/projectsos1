package com.example.projectsos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText;
    private Button addContactButton, viewSavedContactsButton;
    private ContactDao contactDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addContactButton = findViewById(R.id.addContactButton);
        viewSavedContactsButton = findViewById(R.id.viewSavedContactsButton);

        // Initialize the database and DAO
        AppDatabase db = AppDatabase.getInstance(this);
        contactDao = db.contactDao();

        // Add Contact Button functionality
        addContactButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String phoneNumber = phoneEditText.getText().toString().trim();

            if (!name.isEmpty() && !phoneNumber.isEmpty()) {
                // Create a new Contact object
                Contact contact = new Contact(name, phoneNumber);

                new Thread(() -> {
                    // Check if the contact already exists in the database before adding it
                    Contact existingContact = contactDao.getContactByPhoneNumberSync(phoneNumber);
                    if (existingContact == null) {
                        // Insert the new contact
                        contactDao.insert(contact);

                        // Notify UI on the main thread
                        runOnUiThread(() -> {
                            Toast.makeText(AddContactActivity.this, "Contact Added", Toast.LENGTH_SHORT).show();
                            nameEditText.setText("");
                            phoneEditText.setText("");
                        });
                    } else {
                        // Notify user that the contact already exists
                        runOnUiThread(() -> Toast.makeText(AddContactActivity.this, "Contact already exists", Toast.LENGTH_SHORT).show());
                    }
                }).start();

            } else {
                // Notify user if input fields are empty
                Toast.makeText(AddContactActivity.this, "Please enter both name and phone number", Toast.LENGTH_SHORT).show();
            }
        });

        // View Saved Contacts Button functionality
        viewSavedContactsButton.setOnClickListener(v -> {
            // Navigate to SavedContactsActivity to view saved contacts
            Intent intent = new Intent(AddContactActivity.this, SavedContactsActivity.class);
            startActivity(intent);
        });
    }
}
