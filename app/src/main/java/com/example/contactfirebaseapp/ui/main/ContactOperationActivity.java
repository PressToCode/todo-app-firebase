/*
 * This page shows UI for create or update
 * operation to all contact object
 * Usually called from clicking the plus
 * icon in MainActivity or by clicking
 * contact object that calls adapter,
 * then adapter redirect to here
 */
package com.example.contactfirebaseapp.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.contactfirebaseapp.R;
import com.example.contactfirebaseapp.data.model.Contact;
import com.example.contactfirebaseapp.data.repository.AuthRepository;
import com.example.contactfirebaseapp.data.repository.ContactRepository;
import com.example.contactfirebaseapp.utils.Validator;

public class ContactOperationActivity extends AppCompatActivity {
    // Variables
    ImageButton backBtn;
    Button saveBtn;
    EditText noteTitle, noteDescription;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_operation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if user is signed in (non-null)
        if(!AuthRepository.checkLoginStatus()) {
            finish();
        }

        // Initialization
        backBtn = findViewById(R.id.btnBack);
        saveBtn = findViewById(R.id.saveButton);
        noteTitle = findViewById(R.id.noteTitle);
        noteDescription = findViewById(R.id.noteDescription);

        // Check if Intent has data to be passed (update mode)
        if(getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");
            noteTitle.setText(getIntent().getStringExtra("title"));
            noteDescription.setText(getIntent().getStringExtra("description"));
        }

        // Button Listener Behavior
        initializeButtons();
    }

    @SuppressLint("SetTextI18n")
    private void initializeButtons() {
        saveBtn.setOnClickListener(v -> {
            String title, description;
            title = noteTitle.getText().toString();
            description = noteDescription.getText().toString();

            // Guard Clause
            if(Validator.stringHasBlank(title)) {
                Toast.makeText(this, "Title cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            // If not update mode
            if(id == null) {
                id = ContactRepository.newEntry();
            }

            Contact contact = new Contact(id, title, description);
            ContactRepository.updateDatabase(contact);
            finish();
        });

        backBtn.setOnClickListener(v -> finish());
    }
}