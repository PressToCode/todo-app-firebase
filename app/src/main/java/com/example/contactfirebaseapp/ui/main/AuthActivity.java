/*
 * This is the authentication page that also
 * work as a registration page. Shows simple
 * form for inserting input, then Firebase
 * operation is called through ContactRepository
 */

package com.example.contactfirebaseapp.ui.main;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.contactfirebaseapp.R;
import com.example.contactfirebaseapp.data.repository.AuthRepository;

public class AuthActivity extends AppCompatActivity {
    // Variables
    TextView authTitle, emailTitle, passwordTitle, phoneTitle;
    EditText emailField, passwordField, phoneField;
    Button authBtn, switchModeBtn, passwordlessBtn;
    ImageButton backBtn;
    Boolean registerMode = false, passwordlessMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialization
        authTitle = findViewById(R.id.authTitleText);
        emailTitle = findViewById(R.id.emailAuthText);
        passwordTitle = findViewById(R.id.passwordAuthText);
        phoneTitle = findViewById(R.id.phoneAuthText);
        emailField = findViewById(R.id.emailEditText);
        passwordField = findViewById(R.id.passwordEditText);
        phoneField = findViewById(R.id.phoneEditText);
        authBtn = findViewById(R.id.authButton);
        switchModeBtn = findViewById(R.id.switchAuthModeButton);
        passwordlessBtn = findViewById(R.id.passwordlessButton);
        backBtn = findViewById(R.id.authBackButton);

        // Check if trying to register or login
        if(getIntent().hasExtra("register")) {
            registerMode = true;
        }

        // Update UI based on Information
        updateUI();

        // Initialize Buttons Listener
        initializeButtons();
    }

    private void updateUI() {
        if(registerMode) {
            authTitle.setText("Register");
            authBtn.setText("Register Now!");
            switchModeBtn.setText("Login Instead?");
            passwordlessBtn.setVisibility(GONE);
            return;
        }

        authTitle.setText("Email Login");
        authBtn.setText("Login");
        switchModeBtn.setText("Register Instead?");
    }

    private void switchLoginRegister() {
        registerMode = !registerMode;

        authTitle.setText(registerMode ? "Register" : "Email Login");
        authBtn.setText(registerMode ? "Register Now!" : "Login");
        switchModeBtn.setText(registerMode ? "Login Instead?" : "Register Instead?");
        passwordlessBtn.setVisibility(registerMode ? GONE : VISIBLE);
    }

    private void initializeButtons() {
        // Back Button
        backBtn.setOnClickListener(v -> finish());

        // Auth Button
        authBtn.setOnClickListener(v -> {
            /*
             * Structured to Prevent Null Value
             */
            // Get Email
            String email = emailField.getText().toString();

            // Guard Clause
            if(email.isBlank()) {
                Toast.makeText(AuthActivity.this, "Email is Missing!", Toast.LENGTH_LONG).show();
                return;
            }

            // If passwordless-mode Login (Email link)
            if(passwordlessMode) {
                AuthRepository.loginWithEmailLink(AuthActivity.this, email);
                return;
            }

            // Get Password
            String password = passwordField.getText().toString();

            // Guard Clause
            if(password.isBlank()) {
                Toast.makeText(AuthActivity.this, "Password is Missing!", Toast.LENGTH_LONG).show();
                return;
            }

            // If Registering
            if (registerMode) {
                AuthRepository.registerWithEmailAndPassword(AuthActivity.this, email, password, success -> {
                    if (success) {
                        AuthRepository.loginWithEmailAndPassword(AuthActivity.this, email, password);
                    }
                });
                return;
            }

            AuthRepository.loginWithEmailAndPassword(AuthActivity.this, email, password);
        });

        // Switch Mode Button
        switchModeBtn.setOnClickListener(v -> switchLoginRegister());

        // Passwordless Button
        passwordlessBtn.setOnClickListener(v -> {
            passwordlessMode = !passwordlessMode;

            passwordTitle.setVisibility(passwordlessMode ? GONE : VISIBLE);
            passwordField.setVisibility(passwordlessMode ? GONE : VISIBLE);
            passwordlessBtn.setText(passwordlessMode ? "Email-Password?" : "Email Only?");
            switchModeBtn.setVisibility(passwordlessMode ? GONE : VISIBLE);
        });
    }
}