package com.pandacare.mainapp.authentication.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PacillianTest {

    private Pacillian pacillian;

    @BeforeEach
    public void setUp() {
        pacillian = new Pacillian("Jane Doe", "NIK456", "08987654321", "456 Street", "No allergies");
    }

    @Test
    public void testDefaultConstructor() {
        Pacillian defaultPacillian = new Pacillian();
        assertNull(defaultPacillian.getAddress());
        assertNull(defaultPacillian.getMedicalHistory());
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("Jane Doe", pacillian.getName());
        assertEquals("NIK456", pacillian.getNik());
        assertEquals("08987654321", pacillian.getPhoneNumber());
        assertEquals("456 Street", pacillian.getAddress());
        assertEquals("No allergies", pacillian.getMedicalHistory());
    }

    @Test
    public void testSetters() {
        pacillian.setAddress("789 Avenue");
        pacillian.setMedicalHistory("Asthma");
        assertEquals("789 Avenue", pacillian.getAddress());
        assertEquals("Asthma", pacillian.getMedicalHistory());
    }
}
