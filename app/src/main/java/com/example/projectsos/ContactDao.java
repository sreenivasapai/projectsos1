package com.example.projectsos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {



    // Insert a new contact
    @Insert
    void insert(Contact contact);

    // Update an existing contact
    @Update
    void update(Contact contact);

    // Delete a contact
    @Delete
    void delete(Contact contact);

    // Get a contact by phone number, return LiveData
    @Query("SELECT * FROM contacts WHERE phone_number = :phoneNumber LIMIT 1")
    LiveData<Contact> getContactByPhoneNumber(String phoneNumber);

    // Get all contacts, return LiveData of the list
    @Query("SELECT * FROM contacts")
    LiveData<List<Contact>> getAllContacts();

    @Query("SELECT * FROM contacts WHERE phone_number = :phoneNumber LIMIT 1")
    Contact getContactByPhoneNumberSync(String phoneNumber);

    // Get a contact by ID, return LiveData
    @Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
    LiveData<Contact> getContactById(int id); // Return LiveData

    @Query("SELECT * FROM contacts ORDER BY id ASC")
    LiveData<List<Contact>> getAllContactsOrderedById();
}
