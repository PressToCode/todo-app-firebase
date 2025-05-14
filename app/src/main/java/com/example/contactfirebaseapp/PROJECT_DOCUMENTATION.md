# ContactFirebaseApp Project Documentation

## Overview

This is an Android contact management application that integrates Firebase Authentication and Firebase Realtime Database. The app supports multiple authentication methods including email-password, email-link, Google, and anonymous authentication.

## Project Structure

### UI Layer (ui/main)

1. **AuthActivity.java**

    - Handles all authentication flows
    - Manages email-password, email-link, Google, and anonymous authentication
    - Acts as the entry point for user authentication

2. **ContactOperationActivity.java**

    - Manages contact creation and modification operations
    - Handles form validation and data submission to Firebase
    - Provides UI for adding/editing contacts

3. **EmailAuthHandler.java**

    - Specialized handler for email-based authentication
    - Manages both email-password and email-link authentication flows
    - Handles email verification and password reset operations

4. **FrontActivity.java**

    - Landing page activity
    - Shows welcome screen and directs users to appropriate activities
    - Handles initial app state management

5. **MainActivity.java**

    - Main dashboard activity
    - Displays list of contacts
    - Provides navigation to other activities
    - Handles contact list operations

6. **ProfileActivity.java**
    - Manages user profile operations
    - Allows users to view and edit their profile information
    - Handles profile data synchronization with Firebase

### Data Layer (data)

1. **model/**

    - Contains data models for the application
    - Includes Contact model for contact information
    - Defines data structures used throughout the app

2. **repository/**

    - Implements data access layer
    - Manages Firebase database operations
    - Handles data persistence and retrieval
    - Includes FirebaseRepository for database operations

3. **firebase/**
    - Contains Firebase-specific implementations
    - Manages Firebase Authentication
    - Handles Firebase Realtime Database operations

### Utilities (utils)

1. **Validator.java**
    - Provides input validation utilities
    - Validates email addresses, passwords, and contact information
    - Contains reusable validation methods

## Key Features

1. **Authentication System**

    - Multiple authentication methods (email-password, email-link, Google, anonymous)
    - Secure user authentication with Firebase
    - Email verification support
    - Password reset functionality

2. **Contact Management**

    - Create, read, update, and delete contacts
    - Real-time synchronization with Firebase
    - Form validation for contact data
    - Search and filter capabilities

3. **User Profile**
    - User profile management
    - Profile data persistence
    - Profile picture support
    - Profile information synchronization

## Technical Stack

-   Android Studio
-   Java with Kotlin DSL
-   Firebase Authentication
-   Firebase Realtime Database
-   Material Design components
-   MVVM architecture pattern

## Authentication Methods

1. Email-Password Authentication
2. Email-Link Authentication
3. Google Sign-In
4. Anonymous Authentication

## Data Flow

1. User Authentication:

    - FrontActivity → AuthActivity → MainActivity
    - Multiple authentication methods supported

2. Contact Operations:

    - MainActivity → ContactOperationActivity
    - Firebase data synchronization

3. Profile Management:
    - MainActivity → ProfileActivity
    - Firebase profile data handling

This documentation provides a high-level overview of the project structure and functionality. Each class is designed to work together to provide a seamless contact management experience with Firebase integration.
