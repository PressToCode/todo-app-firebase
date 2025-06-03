/*
 * This class is for handling Authentication
 * using Firebase.
 */

package com.example.contactfirebaseapp.data.repository;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.contactfirebaseapp.data.firebase.FirebaseUtil;
import com.example.contactfirebaseapp.ui.main.MainActivity;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AuthRepository {
    private static final FirebaseAuth auth = FirebaseUtil.getAuth();
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public static Boolean checkLoginStatus() {
        // Guard Clause
        if (auth.getCurrentUser() == null) {
            return false;
        }

        // If Logged-in, Set Database
        FirebaseUtil.setUserDatabase();
        return (auth.getCurrentUser() != null);
    }

    // Logout ------------------
    public static void logout(Context context, result callback) {
        new AlertDialog.Builder(context)
                .setTitle("Log-Out")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> FirebaseUtil.getCredentialManager()
                    .clearCredentialStateAsync(
                            FirebaseUtil.getClearCredentialStateRequest(),
                            null,
                            executor,
                            new CredentialManagerCallback<>() {
                                @Override
                                public void onResult(Void unused) {
                                    FirebaseUtil.reset();
                                    auth.signOut();
                                    callback.onResult(true);
                                }

                                @Override
                                public void onError(@NonNull ClearCredentialException e) {
                                    callback.onResult(false);
                                }
                            }
                    ))
                .setNegativeButton("No", null)
                .show();
    }

    // End of Logout -----------

    public static void registerWithEmailAndPassword(Context context, String email, String password, result callback) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(context, "Register Complete", Toast.LENGTH_SHORT).show();
                callback.onResult(true);
                return;
            }

            Toast.makeText(context, "Register Failed!", Toast.LENGTH_LONG).show();
        });
    }

    public static void loginWithEmailAndPassword(Context context, String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                return;
            }

            Toast.makeText(context, "Login Failed!", Toast.LENGTH_LONG).show();
        });
    }

    public static void loginWithEmailLink(Context context, String email) {
        auth.sendSignInLinkToEmail(email, FirebaseUtil.getActionCodeSettings()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SharedPreferences prefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
                prefs.edit().putString("email", email).apply();
                Toast.makeText(context, "Check your email for login link", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(context, "Failed to send sign-in link", Toast.LENGTH_SHORT).show();
        });
    }

    // GOOGLE ----------------------------
    public static void loginWithGoogle(Context context, Intent intent) {
        FirebaseUtil.getCredentialManager()
                .getCredentialAsync(context, FirebaseUtil.getGoogleRequest(), null, executor, new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        handleSignIn(getCredentialResponse.getCredential(), context, intent);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        executor.execute(() -> {
                            mainThreadHandler.post(() -> Toast.makeText(context, "Google Log-in Failed", Toast.LENGTH_LONG).show());
                        });
                    }
                });
    }

    private static void handleSignIn(Credential credential, Context context, Intent intent) {
        if (credential instanceof CustomCredential && credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            // Readable Version
            // Bundle credentialData = credential.getData();
            // GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);
            // AuthCredential authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.getIdToken(), null);

            // Shorter Version
            AuthCredential authCredential = GoogleAuthProvider.getCredential(GoogleIdTokenCredential.createFrom(credential.getData()).getIdToken(), null);
            auth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    context.startActivity(intent);
                    return;
                }

                Toast.makeText(context, "Google Credential Failed", Toast.LENGTH_LONG).show();
            });
        } else {
            Toast.makeText(context, "Invalid Credential Type", Toast.LENGTH_LONG).show();
        }
    }

    // END of GOOGLE OAUTH ---------------

    public static void loginAnonymous(Context context, Intent intent) {
        auth.signInAnonymously().addOnCompleteListener(task ->  {
            if(task.isSuccessful()) {
                context.startActivity(intent);
                return;
            }

            Toast.makeText(context, "Login Failed!", Toast.LENGTH_LONG).show();
        });
    }

    public static void bindAccount(Context context, String email, String password, result callback) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        Objects.requireNonNull(auth.getCurrentUser()).linkWithCredential(credential).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(context, "Bind Successful!", Toast.LENGTH_LONG).show();
                callback.onResult(true);
                return;
            }

            Toast.makeText(context, "Bind Failed!", Toast.LENGTH_LONG).show();
        });
    }

    public interface result {
        void onResult(boolean Success);
    }
}
