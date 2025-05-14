/*
 * Main Activity is the main page of this app
 * Showing RecyclerView of all available contacts
 * Menu includes deletion, insertion, and user profile
 */

package com.example.contactfirebaseapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactfirebaseapp.R;
import com.example.contactfirebaseapp.data.model.Contact;
import com.example.contactfirebaseapp.data.repository.AuthRepository;
import com.example.contactfirebaseapp.data.repository.ContactRepository;
import com.example.contactfirebaseapp.ui.adapter.ContactAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Variables
    RecyclerView recycler;
    ContactAdapter adapter;
    List<Contact> contactList;
    ImageButton addButton, deleteButton, profileButton, backButton;
    Boolean deleteMode;
    Context context = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if user is signed in (non-null)
        if(!AuthRepository.checkLoginStatus()) {
            finish();
        }

        // Variables Initialization
        recycler = findViewById(R.id.recyclerViewContacts);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();
        backButton = findViewById(R.id.btnMainBack);
        addButton = findViewById(R.id.buttonAdd);
        profileButton = findViewById(R.id.buttonProfile);
        deleteButton = findViewById(R.id.buttonDeleteMode);
        deleteMode = false;

        // Initialize Adapter
        adapter = new ContactAdapter(this, contactList);
        recycler.setAdapter(adapter);

        // Get All Contact Data
        ContactRepository.loadContacts(this, contactList, adapter);

        // Initialize Button Behavior
        initializeButtons();
    }

    private void initializeButtons() {
        // Back Button - Log Out
        backButton.setOnClickListener(v -> AuthRepository.logout(context, success -> {
            if (success) {
                finish();
                return;
            }

            Toast.makeText(context, "Logout Failed", Toast.LENGTH_LONG).show();
        }));

        // Delete Mode Button
        deleteButton.setOnClickListener(v -> {
            deleteMode = !deleteMode;
            adapter.toggleDeleteMode();

            Drawable icon = deleteButton.getDrawable();
            if (deleteMode) {
                icon.setTintList(ContextCompat.getColorStateList(this, android.R.color.holo_red_dark));
            } else {
                icon.setTintList(ContextCompat.getColorStateList(this, R.color.blue));
            }
        });

        // Add Button
        addButton.setOnClickListener(v -> startActivity(new Intent(context, ContactOperationActivity.class)));

        // Profile Button
        profileButton.setOnClickListener(v -> startActivity(new Intent(context, ProfileActivity.class)));
    }
}