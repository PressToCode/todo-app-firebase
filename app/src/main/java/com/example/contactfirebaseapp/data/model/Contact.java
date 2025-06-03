/*
 * This is the main model used as a blueprint
 * for contact including id, name, email, and phone
 */
package com.example.contactfirebaseapp.data.model;

public class Contact {
    private String id;
    private String title;
    private String description;

    public Contact() {
        // ...
    }

    public Contact(String id, String title, String description) {
        setId(id);
        setTitle(title);
        setDescription(description);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getShortDescription() {
        if(this.description.length() <= 40) {
            return this.description;
        }
        return this.description.substring(0, 40) + "...";
    }
}
