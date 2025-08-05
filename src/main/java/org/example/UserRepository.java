package org.example;

public interface UserRepository {
    User findByEmail(String email);
    void saveUser(User user);
}
