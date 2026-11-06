package com.example.Student_Course_Registration_System.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * One place for everything about passwords.
 *
 * Passwords are never stored as plain text. Before saving, the services call
 * hashIfNeeded(); at login they call matches().
 */
@Component
public class PasswordHasher {

    // What a BCrypt hash looks like: $2a$10$ followed by 53 characters
    private static final Pattern BCRYPT = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /** Turns a plain password into a BCrypt hash. */
    public String hash(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    /**
     * Hashes the value unless it is already a BCrypt hash.
     * Needed because the edit forms keep the existing (already hashed)
     * password when the user leaves the password box empty.
     */
    public String hashIfNeeded(String value) {
        if (value == null || value.isEmpty() || isHashed(value)) {
            return value;
        }
        return hash(value);
    }

    /** Checks a password typed at login against the stored hash. */
    public boolean matches(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null || !isHashed(storedHash)) {
            return false;
        }
        return encoder.matches(plainPassword, storedHash);
    }

    public boolean isHashed(String value) {
        return value != null && BCRYPT.matcher(value).matches();
    }
}
