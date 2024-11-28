package com.example.projectsos;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SavedContactsActivity extends AppCompatActivity implements ContactAdapter.ContactDeleteListener {

    private RecyclerView savedContactsRecyclerView;
    private ContactDao contactDao;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_contacts);

        savedContactsRecyclerView = findViewById(R.id.savedContactsRecyclerView);
        savedContactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with delete listener
        contactAdapter = new ContactAdapter(this);
        savedContactsRecyclerView.setAdapter(contactAdapter);

        // Initialize the database and DAO
        AppDatabase db = AppDatabase.getInstance(this);
        contactDao = db.contactDao();

        // Fetch contacts and observe changes
        contactDao.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                if (contacts != null && !contacts.isEmpty()) {
                    contactAdapter.setContactList(contacts); // Update the list in the adapter
                } else {
                    Toast.makeText(SavedContactsActivity.this, "No contacts found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Implementation of the delete click listener
    @Override
    public void onDeleteClick(Contact contact) {
        // Delete the contact from the database
        new Thread(() -> {
            contactDao.delete(contact); // Perform the deletion in a background thread
            runOnUiThread(() -> Toast.makeText(SavedContactsActivity.this, "Contact Deleted", Toast.LENGTH_SHORT).show());
        }).start();
    }
}
