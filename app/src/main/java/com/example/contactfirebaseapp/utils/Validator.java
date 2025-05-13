/*
 * This is an utility class
 * Mainly used to validate multiple Strings
 */
package com.example.contactfirebaseapp.utils;

public class Validator {
    // Check for Multiple Form Field that is Blank
    public static boolean stringHasBlank(String... fields) {
        for (String field : fields) {
            if (field == null || field.isBlank()) {
                return true;
            }
        }

        return false;
    }
}
