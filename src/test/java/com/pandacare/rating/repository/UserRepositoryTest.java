package com.pandacare.rating.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void testExistsById_UserExists() {
        // Arrange
        String userId = "DOK001";
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        boolean result = userRepository.existsById(userId);

        // Assert
        assertTrue(result);
        verify(userRepository).existsById(userId);
    }

    @Test
    void testExistsById_UserNotExists() {
        // Arrange
        String userId = "DOK999";
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act
        boolean result = userRepository.existsById(userId);

        // Assert
        assertFalse(result);
        verify(userRepository).existsById(userId);
    }
}