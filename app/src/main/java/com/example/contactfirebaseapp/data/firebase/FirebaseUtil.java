/*
 * This class is for Firebase Configuration
 * and Singleton Instances
 */

package com.example.contactfirebaseapp.data.firebase;

import android.content.Context;

import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;

import com.example.contactfirebaseapp.R;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class FirebaseUtil {
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static DatabaseReference dbRef;
    private static DatabaseReference userDbRef;
    private static ActionCodeSettings actionCodeSettings;
    private static CredentialManager credentialManager;
    private static GetCredentialRequest request;

    public static void setup(Context context) {
        // Build Database Reference
        dbRef = FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_database_ref));

        // Build actionCodeSettings
        actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl(context.getString(R.string.firebase_url))
                .setHandleCodeInApp(true)
                .setAndroidPackageName(context.getPackageName(), true, "12")
                .build();

        // Build Google Request Object
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(context.getString(R.string.firebase_web_client_id))
                .build();

        GetSignInWithGoogleOption signInWithGoogleOption = new GetSignInWithGoogleOption
                .Builder(context.getString(R.string.firebase_web_client_id))
                .build();

        // Build Credential Manager Request
        request = new GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build();

        // Create a credentialManager
        credentialManager = CredentialManager.create(context);
    }

    public static CredentialManager getCredentialManager() {
        return credentialManager;
    }

    public static GetCredentialRequest getGoogleRequest() {
        return request;
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static void setUserDatabase() {
        userDbRef = dbRef.child(Objects.requireNonNull(auth.getCurrentUser()).getUid());
    }

    public static DatabaseReference getUserDatabase() {
        return userDbRef;
    }

    public static void reset() {
        userDbRef = null;
    }

    public static ActionCodeSettings getActionCodeSettings() {
        return actionCodeSettings;
    }
}
