package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthManagerTest {

    @Mock
    private UserRepository userRepository;

    private AuthManager authManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        authManager = new AuthManager(userRepository);
    }  
    
    @Test
    public void testSuccessfulLogin() throws NoSuchAlgorithmException {
        String email = "test@example.com";
        String password = "password123";
        String hashedPassword = PasswordHashing.hashPassword(password);
        User user = new User(email, hashedPassword);
        
        when(userRepository.findByEmail(email)).thenReturn(user);

        boolean result = authManager.login(email, password);

        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    public void testFailedLogin() throws NoSuchAlgorithmException {
        String email = "test@example.com";
        String password = "wrongPassword";
        User user = new User(email, PasswordHashing.hashPassword("correctPassword"));
        
        when(userRepository.findByEmail(email)).thenReturn(user);

        boolean result = authManager.login(email, password);

        assertFalse(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    public void testLoginWithNonexistentUser() throws NoSuchAlgorithmException {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        boolean result = authManager.login("nonexistent@example.com", "password");

        assertFalse(result);
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    public void testLoginWithNullEmail() throws NoSuchAlgorithmException {
        assertThrows(IllegalArgumentException.class, () -> {
            authManager.login(null, "password");
        });
    }

    @Test
    public void testSuccessfulRegistration() throws NoSuchAlgorithmException {
        String email = "new@example.com";
        String password = "password123";
        when(userRepository.findByEmail(email)).thenReturn(null);

        authManager.register(email, password);

        verify(userRepository).findByEmail(email);
        verify(userRepository).saveUser(any(User.class));
    }

    @Test
    public void testRegisterExistingUser() throws NoSuchAlgorithmException {
        String email = "existing@example.com";
        String password = "password123";
        
        // Arrange - Mock that user already exists
        when(userRepository.findByEmail(email)).thenReturn(new User(email, "someHash"));
        
        // Act & Assert - Should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            authManager.register(email, password);
        });
        
        // Verify that findByEmail was called
        verify(userRepository).findByEmail(email);
    }
}
