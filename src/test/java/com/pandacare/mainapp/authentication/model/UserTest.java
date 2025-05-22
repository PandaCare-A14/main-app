package com.pandacare.mainapp.authentication.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private TestUser user;

    /**
     * Concrete subclass to test the abstract User model.
     */
    private static class TestUser extends User {
        TestUser() { super(); }
        TestUser(String name, String nik, String phoneNumber) {
            super(name, nik, phoneNumber);
        }
    }

    @BeforeEach
    void setUp() {
        user = new TestUser("Alice", "NIK789", "0811223344");
        user.setEmail("alice@example.com");
    }

    @Test
    void testDefaultConstructor() {
        TestUser defaultUser = new TestUser();
        assertAll("Default user should have null fields",
                () -> assertNull(defaultUser.getId(), "ID should be null"),
                () -> assertNull(defaultUser.getName(), "Name should be null"),
                () -> assertNull(defaultUser.getNik(), "NIK should be null"),
                () -> assertNull(defaultUser.getPhoneNumber(), "Phone number should be null"),
                () -> assertNull(defaultUser.getEmail(), "Email should be null")
        );
    }

    @Test
    void testConstructorAndGetters() {
        assertAll("Constructor should set provided values",
                () -> assertNull(user.getId(), "ID is null until explicitly set"),
                () -> assertEquals("Alice", user.getName(), "Name getter"),
                () -> assertEquals("NIK789", user.getNik(), "NIK getter"),
                () -> assertEquals("0811223344", user.getPhoneNumber(), "Phone number getter"),
                () -> assertEquals("alice@example.com", user.getEmail(), "Email getter")
        );
    }

    @Test
    void testSetters() {
        UUID uuid = UUID.randomUUID();
        user.setId(uuid);
        user.setName("Bob");
        user.setNik("NIK000");
        user.setPhoneNumber("0899001122");
        user.setEmail("bob@example.com");

        assertAll("Setters should update fields correctly",
                () -> assertEquals(uuid, user.getId(), "ID setter/getter"),
                () -> assertEquals("Bob", user.getName(), "Name setter/getter"),
                () -> assertEquals("NIK000", user.getNik(), "NIK setter/getter"),
                () -> assertEquals("0899001122", user.getPhoneNumber(), "Phone number setter/getter"),
                () -> assertEquals("bob@example.com", user.getEmail(), "Email setter/getter")
        );
    }
}
