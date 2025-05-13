/*
 * This page shows UI for create or update
 * operation to all contact object
 * Usually called from clicking the plus
 * icon in MainActivity or by clicking
 * contact object that calls adapter,
 * then adapter redirect to here
 */
package com.example.contactfirebaseapp.ui.main;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
    Button editBtn, cancelBtn, submitBtn;
    EditText nameField, emailField, phoneField;
    TextView statusText;
    Boolean updateMode = false;
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
        editBtn = findViewById(R.id.btnEdit);
        cancelBtn = findViewById(R.id.btnCancel);
        submitBtn = findViewById(R.id.btnSubmit);
        nameField = findViewById(R.id.edtName);
        emailField = findViewById(R.id.edtEmail);
        phoneField = findViewById(R.id.edtPhone);
        statusText = findViewById(R.id.statusText);

        // Check if Intent has data to be passed (update mode)
        if(getIntent().hasExtra("id")) {
            updateMode = true;
            id = getIntent().getStringExtra("id");
        }

        // Update UI based on operation mode
        updateUI();

        // Button Listener Behavior
        initializeButtons();
    }

    private void updateUI() {
        // Guard Clause
        if(updateMode) {
            nameField.setText(getIntent().getStringExtra("name"));
            emailField.setText(getIntent().getStringExtra("email"));
            phoneField.setText(getIntent().getStringExtra("phone"));
            setEditMode(false);
            return;
        }

        submitBtn.setVisibility(VISIBLE);
    }

    private void initializeButtons() {
        // Only attach when update mode
        if(updateMode) {
            // Edit Button
            editBtn.setOnClickListener(v -> setEditMode(true));

            // Cancel Button
            cancelBtn.setOnClickListener(v -> {
                setEditMode(false);
                statusText.setText("Unsaved Progress..");
            });
        }

        submitBtn.setOnClickListener(v -> {
            String name, email, phone;
            name = nameField.getText().toString();
            email = emailField.getText().toString();
            phone = phoneField.getText().toString();

            // Guard Clause
            if(Validator.stringHasBlank(name, email, phone)) {
                statusText.setText("Failed! Check Field Again!");
                return;
            }

            // If not update mode
            if(id == null) {
                id = ContactRepository.newEntry();
            }

            statusText.setText("");
            Contact contact = new Contact(id, name, email, phone);
            ContactRepository.updateDatabase(contact);
            finish();
        });

        backBtn.setOnClickListener(v -> finish());
    }

    // Purely Visual
    private void setEditMode(boolean enabled) {
        nameField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        phoneField.setEnabled(enabled);

        editBtn.setVisibility(enabled ? View.GONE : View.VISIBLE);
        cancelBtn.setVisibility(enabled ? View.VISIBLE : View.GONE);
        submitBtn.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
}