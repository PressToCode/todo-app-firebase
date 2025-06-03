/*
 * This is an adapter for Contact List Recycler View
 * Handles Redirect upon click and data passing
 * Handles Deletion with Firebase
 */
package com.example.contactfirebaseapp.ui.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactfirebaseapp.R;
import com.example.contactfirebaseapp.data.firebase.FirebaseUtil;
import com.example.contactfirebaseapp.data.model.Contact;
import com.example.contactfirebaseapp.ui.main.ContactOperationActivity;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final List<Contact> contactList;
    private final Context context;
    private final redirector redirect;
    private final DatabaseReference reference;
    private boolean deleteMode = false;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.reference = FirebaseUtil.getUserDatabase();
        this.redirect = new redirector();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView profile;

        public ContactViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            description = itemView.findViewById(R.id.textDescription);
            profile = itemView.findViewById(R.id.imagePlaceholder);
        }
    }

    public class redirector {
        void onContactOpen(Contact contact) {
            Intent intent = new Intent(context, ContactOperationActivity.class);
            intent.putExtra("id", contact.getId());
            intent.putExtra("title", contact.getTitle());
            intent.putExtra("description", contact.getDescription());

            context.startActivity(intent);
        }

        void onContactDelete(Contact contact, int pos) {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete \"" + contact.getTitle() + "\" Note?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        contactList.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, contactList.size());
                        reference.child(contact.getId()).removeValue();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void toggleDeleteMode() {
        deleteMode = !deleteMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        holder.title.setText(contact.getTitle());
        holder.description.setText(contact.getShortDescription());

        holder.itemView.setOnClickListener(v -> {
            if(redirect == null) { return; }

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Contact clickedUser = contactList.get(pos);

            if(deleteMode) {
                redirect.onContactDelete(clickedUser, pos);
                return;
            }

            redirect.onContactOpen(clickedUser);
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
