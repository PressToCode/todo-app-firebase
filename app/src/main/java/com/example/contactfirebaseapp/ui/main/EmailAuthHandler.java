/*
 * This class is an activity that gets called
 * when clicking email link. Does not show any UI
 * and will redirect to MainActivity upon success
 */

package com.example.contactfirebaseapp.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contactfirebaseapp.data.firebase.FirebaseUtil;

public class EmailAuthHandler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Link
        Uri deepLink = getIntent().getData();

        // Do Sign-In with Email Link
        if (deepLink != null && FirebaseUtil.getAuth().isSignInWithEmailLink(deepLink.toString())) {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            String email = prefs.getString("email", null);

            if (email != null) {
                FirebaseUtil.getAuth().signInWithEmailLink(email, deepLink.toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUtil.setup(getApplicationContext());
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                        return;
                    }

                    Toast.makeText(this, "Sign-in failed.", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}