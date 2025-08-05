package org.example;

import java.security.NoSuchAlgorithmException;

public class AuthManager {
    private final UserRepository userRepository;

    public AuthManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }    public boolean login(String email, String password) throws NoSuchAlgorithmException {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password cannot be null");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }

        String hashedPassword = PasswordHashing.hashPassword(password);
        return hashedPassword.equals(user.getPassword());
    }

    public void register(String email, String password) throws NoSuchAlgorithmException {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password cannot be null");
        }

        if (userRepository.findByEmail(email) != null) {
            throw new IllegalStateException("Email already exists");
        }

        String hashedPassword = PasswordHashing.hashPassword(password);
        userRepository.saveUser(new User(email, hashedPassword));
    }
}
