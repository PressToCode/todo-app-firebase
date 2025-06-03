/*
 * This page shows the current authenticated user
 * information, including whether they're using
 * an authenticated email (via email link) or
 * an anonymous account
 */
package com.example.contactfirebaseapp.ui.main;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.contactfirebaseapp.R;
import com.example.contactfirebaseapp.data.firebase.FirebaseUtil;
import com.example.contactfirebaseapp.data.repository.AuthRepository;
import com.example.contactfirebaseapp.utils.Validator;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    // Variables
    ImageButton backBtn;
    TextView userIdText, nameText, emailText, statusText, bindEmailTitle, bindPasswordTitle;
    EditText bindEmailField, bindPasswordField;
    Button bindButton;
    LinearLayout bindLayout;
    Boolean bindMode = false;
    Context context = ProfileActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialization
        backBtn = findViewById(R.id.btnProfileBack);
        userIdText = findViewById(R.id.currentUID);
        nameText = findViewById(R.id.currentName);
        emailText = findViewById(R.id.currentEmail);
        statusText = findViewById(R.id.currentStatus);
        bindEmailTitle = findViewById(R.id.profileEmailTitle);
        bindPasswordTitle = findViewById(R.id.profilePasswordTitle);
        bindEmailField = findViewById(R.id.profileEmailField);
        bindPasswordField = findViewById(R.id.profilePasswordField);
        bindButton = findViewById(R.id.profileBindButton);
        bindLayout = findViewById(R.id.bindLayout);

        // Update UI according to Account Type
        updateUI();

        // Button Listener Behavior
        initializeButtons();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        FirebaseUser user = FirebaseUtil.getAuth().getCurrentUser();

        if (user == null) {
            finish();
        }

        userIdText.setText(user.getUid());
        if (user.isAnonymous()) {
            nameText.setText("Guest");
        } else if (user.getDisplayName() == null || user.getDisplayName().isBlank()) {
            nameText.setText(user.getEmail().split("@")[0] + " (Email)");
        } else {
            nameText.setText(user.getDisplayName());
        }

        if (user.isAnonymous()) {
            emailText.setText("Anonymous");
        } else {
            emailText.setText(user.getEmail());
        }
        statusText.setText(user.isEmailVerified() ? "Verified" : "Not Verified");

        if(user.isAnonymous()) {
            bindLayout.setVisibility(VISIBLE);
            statusText.setText("Anonymous Account");
        }
    }

    @SuppressLint("SetTextI18n")
    private void initializeButtons() {
        // Back Button
        backBtn.setOnClickListener(v -> finish());

        // Bind Button
        bindButton.setOnClickListener(v -> {
            // Guard Clause
            if(bindMode) {
                String email = bindEmailField.getText().toString();
                String password = bindPasswordField.getText().toString();

                if(Validator.stringHasBlank(email, password)) {
                    Toast.makeText(context, "Input Invalid!", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthRepository.bindAccount(context, email, password, success -> {
                    if (success) finish();
                });

                return;
            }

            bindMode = true;
            bindButton.setText("Confirm Bind");

            bindEmailTitle.setVisibility(VISIBLE);
            bindEmailField.setVisibility(VISIBLE);
            bindPasswordField.setVisibility(VISIBLE);
            bindPasswordTitle.setVisibility(VISIBLE);
        });
    }
}