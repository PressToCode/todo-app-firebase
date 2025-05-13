/*
 * This class is for task interacting
 * with Firebase Contacts (CRUD)
 * All Firebase Related Operation is stored here
 */

package com.example.contactfirebaseapp.data.repository;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.contactfirebaseapp.data.firebase.FirebaseUtil;
import com.example.contactfirebaseapp.data.model.Contact;
import com.example.contactfirebaseapp.ui.adapter.ContactAdapter;
import com.example.contactfirebaseapp.ui.main.MainActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ContactRepository {
    public static void loadContacts(Context context, List<Contact> contactList, ContactAdapter adapter) {
        FirebaseUtil.getUserDatabase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // No data exists for this user
                    Toast.makeText(context, "No notes found", Toast.LENGTH_SHORT).show();
                    return;
                }

                contactList.clear(); // Prevent duplicates
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Contact note = snap.getValue(Contact.class);
                    contactList.add(note);
                }

                adapter.notifyDataSetChanged(); // Refresh UI
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to load notes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String newEntry() {
        return FirebaseUtil.getUserDatabase().push().getKey();
    }

    public static void updateDatabase(Contact contact) {
        FirebaseUtil.getUserDatabase().child(contact.getId()).setValue(contact);
    }
}
