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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactfirebaseapp.R;
import com.example.contactfirebaseapp.data.firebase.FirebaseUtil;
import com.example.contactfirebaseapp.data.model.Contact;
import com.example.contactfirebaseapp.data.repository.AuthRepository;
import com.example.contactfirebaseapp.data.repository.ContactRepository;
import com.example.contactfirebaseapp.ui.adapter.ContactAdapter;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // Variables
    RecyclerView recycler;
    private NestedScrollView scrollContainer;
    private LinearLayout contentHolder, mainButtonContainer;
    private TextView titleMainTextAlt, greeting;
    ContactAdapter adapter;
    List<Contact> contactList;
    ImageButton addButton, deleteButton, profileButton, backButton;
    Boolean deleteMode;
    Context context = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_alt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_alt), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if user is signed in (non-null)
        if(!AuthRepository.checkLoginStatus()) {
            finish();
        }

        // Variables Initialization
        recycler = findViewById(R.id.recycler_view_alt);
        scrollContainer = findViewById(R.id.scroll_container);
        contentHolder = findViewById(R.id.scroll_container_content);
        mainButtonContainer = findViewById(R.id.mainButtonContainer);
        titleMainTextAlt = findViewById(R.id.titleMainTextAlt);
        greeting = findViewById(R.id.text_greeting);
        backButton = findViewById(R.id.btnMainBackAlt);
        addButton = findViewById(R.id.buttonAddAlt);
        profileButton = findViewById(R.id.buttonProfileAlt);
        deleteButton = findViewById(R.id.buttonDeleteModeAlt);

        // set Greeting
        setGreeting();

        // Initialize Recycler View Height Dynamically
        renderRecycler();

        // Initialize Adapter
        deleteMode = false;
        contactList = new ArrayList<>();
        adapter = new ContactAdapter(this, contactList);
        recycler.setAdapter(adapter);

        // Get All Contact Data
        ContactRepository.loadContacts(this, contactList, adapter);

        // Initialize Button Behavior
        initializeButtons();
    }

    private void setGreeting() {
        // Get User name and set the name for greeting
        String name;
        FirebaseUser user = FirebaseUtil.getAuth().getCurrentUser();
        if (user == null) {
            finish();
        }

        if (user.isAnonymous()) {
            name = "guest";
        } else if (user.getDisplayName() == null || user.getDisplayName().isEmpty()) {
            String email = user.getEmail();
            name = email.split("@")[0];
        } else {
            name = user.getDisplayName();
        }

        greeting.setText("Hey, " + name + "!");
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

    private void renderRecycler() {
        recycler.setLayoutManager(new LinearLayoutManager(this));

        scrollContainer.post(() -> {
            if (context == null) return;

            // Get Scroll Container Height
            int availableHeightInScrollContainer = scrollContainer.getHeight();

            // Get Content Holder Top Padding (to substract later)
            int contentHolderPaddingTop = contentHolder.getPaddingTop();

            // Get height of TextView status reservation w/ margins
            int titleMainTextAltHeight = titleMainTextAlt.getHeight();
            ViewGroup.MarginLayoutParams titleMainTextAltMargins = (ViewGroup.MarginLayoutParams) titleMainTextAlt.getLayoutParams();
            int titleMainTextAltTotalHeightWithMargins = titleMainTextAltHeight + titleMainTextAltMargins.topMargin + titleMainTextAltMargins.bottomMargin;

            // Get height of main Button Container w/ margins
            int mainButtonContainerHeight = mainButtonContainer.getHeight();
            ViewGroup.MarginLayoutParams mainButtonContainerMargins = (ViewGroup.MarginLayoutParams) mainButtonContainer.getLayoutParams();
            int mainButtonContainerTotalHeightWithMargins = mainButtonContainerHeight + mainButtonContainerMargins.topMargin + mainButtonContainerMargins.bottomMargin;

            // Calculate target height for RecyclerView
            int recyclerViewTargetHeight = availableHeightInScrollContainer
                    - contentHolderPaddingTop
                    - titleMainTextAltTotalHeightWithMargins
                    - mainButtonContainerTotalHeightWithMargins;


            if (recyclerViewTargetHeight < 0) {
                recyclerViewTargetHeight = 0;
            }

            ViewGroup.LayoutParams rvLayoutParams = recycler.getLayoutParams();
            if (rvLayoutParams.height != recyclerViewTargetHeight) {
                rvLayoutParams.height = recyclerViewTargetHeight;
                recycler.setLayoutParams(rvLayoutParams);
            }
        });
    }
}