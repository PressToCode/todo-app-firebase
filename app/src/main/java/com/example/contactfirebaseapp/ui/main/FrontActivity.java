/*
 * This Activity is what User see upon first
 * opening the app. Provides title of app, choice
 * to login or register using different methods
 */

package com.example.contactfirebaseapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.contactfirebaseapp.R;
import com.example.contactfirebaseapp.data.firebase.FirebaseUtil;
import com.example.contactfirebaseapp.data.repository.AuthRepository;

public class FrontActivity extends AppCompatActivity {
    // Variables
    Button loginBtn, registerBtn;
    ImageButton googleLoginBtn, anonymousLoginBtn;
    final Context context = FrontActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install SplashScreen
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        // Always Setup upon Start
        FirebaseUtil.setup(getApplicationContext());

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_front);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if user is signed in (non-null)
        if(AuthRepository.checkLoginStatus()) {
            startActivity(goToMainPage());
        }

        // Initialization
        loginBtn = findViewById(R.id.btnLogin);
        registerBtn = findViewById(R.id.btnRegister);
        googleLoginBtn = findViewById(R.id.btnGoogleLogin);
        anonymousLoginBtn = findViewById(R.id.btnAnonymousLogin);

        // Button Listener Initialization
        initializeButtons();
    }

    /*
     * Redirect is handled in here, while
     * Firebase Operation is handled in
     * ContactRepository class (including Auth)
     */
    private void initializeButtons() {
        // Login
        loginBtn.setOnClickListener(v -> startActivity(new Intent(context, AuthActivity.class)));

        // Register
        registerBtn.setOnClickListener(v -> startActivity(new Intent(context, AuthActivity.class).putExtra("register", "")));

        // Google Login
        googleLoginBtn.setOnClickListener(v -> AuthRepository.loginWithGoogle(context, goToMainPage()));

        // Anonymous Login
        anonymousLoginBtn.setOnClickListener(v -> AuthRepository.loginAnonymous(context, goToMainPage()));
    }

    // Redirector
    private Intent goToMainPage() {
        return new Intent(context, MainActivity.class);
    }
}