package com.pandacare.mainapp.authentication.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private TestUser user;

    // Concrete subclass to test the abstract User
    private static class TestUser extends User {
        public TestUser() {
            super();
        }

        public TestUser(String name, String nik, String phoneNumber) {
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
        assertNull(defaultUser.getId());
        assertNull(defaultUser.getName());
        assertNull(defaultUser.getNik());
        assertNull(defaultUser.getPhoneNumber());
        assertNull(defaultUser.getEmail());
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("Alice", user.getName());
        assertEquals("NIK789", user.getNik());
        assertEquals("0811223344", user.getPhoneNumber());
        assertEquals("alice@example.com", user.getEmail());
    }

    @Test
    void testSetters() {
        UUID uuid = UUID.randomUUID();
        user.setId(uuid);
        user.setName("Bob");
        user.setNik("NIK000");
        user.setPhoneNumber("0899001122");
        user.setEmail("bob@example.com");

        assertEquals(uuid, user.getId());
        assertEquals("Bob", user.getName());
        assertEquals("NIK000", user.getNik());
        assertEquals("0899001122", user.getPhoneNumber());
        assertEquals("bob@example.com", user.getEmail());
    }
}
