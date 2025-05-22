package com.pandacare.mainapp.authentication.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PacillianTest {

    @Nested
    @DisplayName("Default Constructor")
    class DefaultConstructor {

        private Pacillian pacilian;

        @BeforeEach
        void setUp() {
            pacilian = new Pacillian();
        }

        @Test
        @DisplayName("should initialize all fields to null")
        void shouldHaveAllFieldsNull() {
            assertAll("All fields should be null after no-arg construction",
                    () -> assertNull(pacilian.getId(),           "id should be null"),
                    () -> assertNull(pacilian.getEmail(),        "email should be null"),
                    () -> assertNull(pacilian.getName(),         "name should be null"),
                    () -> assertNull(pacilian.getNik(),          "nik should be null"),
                    () -> assertNull(pacilian.getPhoneNumber(),  "phoneNumber should be null"),
                    () -> assertNull(pacilian.getAddress(),      "address should be null"),
                    () -> assertNull(pacilian.getMedicalHistory(),"medicalHistory should be null")
            );
        }
    }

    @Nested
    @DisplayName("Parameterized Constructor")
    class ParameterizedConstructor {

        private Pacillian pacilian;
        private final String name    = "Jane Doe";
        private final String nik     = "9876543210";
        private final String phone   = "081298765432";
        private final String address = "Surabaya";
        private final String history = "Asthma";

        @BeforeEach
        void setUp() {
            pacilian = new Pacillian(name, nik, phone, address, history);
        }

        @Test
        @DisplayName("should set all fields correctly (except id and email)")
        void shouldSetFieldsCorrectly() {
            assertAll("Fields should match constructor arguments",
                    () -> assertNull(pacilian.getId(),           "id should be null (assigned by DB)"),
                    () -> assertNull(pacilian.getEmail(),        "email should be null (not in ctor)"),
                    () -> assertEquals(name, pacilian.getName(), "name should match"),
                    () -> assertEquals(nik, pacilian.getNik(),   "nik should match"),
                    () -> assertEquals(phone, pacilian.getPhoneNumber(), "phoneNumber should match"),
                    () -> assertEquals(address, pacilian.getAddress(),   "address should match"),
                    () -> assertEquals(history, pacilian.getMedicalHistory(), "medicalHistory should match")
            );
        }
    }

    @Nested
    @DisplayName("Setter Methods")
    class SetterTests {

        private Pacillian pacilian;

        @BeforeEach
        void setUp() {
            pacilian = new Pacillian();
        }

        @Test
        @DisplayName("should update address")
        void shouldUpdateAddress() {
            String newAddress = "Bandung";
            pacilian.setAddress(newAddress);
            assertEquals(newAddress, pacilian.getAddress(), "address should be updated");
        }

        @Test
        @DisplayName("should update medicalHistory")
        void shouldUpdateMedicalHistory() {
            String newHistory = "Hypertension";
            pacilian.setMedicalHistory(newHistory);
            assertEquals(newHistory, pacilian.getMedicalHistory(), "medicalHistory should be updated");
        }
    }
}
