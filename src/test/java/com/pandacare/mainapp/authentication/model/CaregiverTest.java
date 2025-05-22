package com.pandacare.mainapp.authentication.model;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CaregiverTest {

    private Caregiver caregiver;

    @BeforeEach
    public void setUp() {
        caregiver = new Caregiver("John Doe", "NIK123", "08123456789", "123 Street", "General");
    }

    @Test
    public void testDefaultConstructor() {
        Caregiver defaultCaregiver = new Caregiver();
        assertNull(defaultCaregiver.getWorkAddress());
        assertNull(defaultCaregiver.getSpeciality());
        assertNotNull(defaultCaregiver.getWorkingSchedules());
        assertTrue(defaultCaregiver.getWorkingSchedules().isEmpty());
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("John Doe", caregiver.getName());
        assertEquals("NIK123", caregiver.getNik());
        assertEquals("08123456789", caregiver.getPhoneNumber());
        assertEquals("123 Street", caregiver.getWorkAddress());
        assertEquals("General", caregiver.getSpeciality());
        assertNotNull(caregiver.getWorkingSchedules());
        assertTrue(caregiver.getWorkingSchedules().isEmpty());
    }

    @Test
    public void testSetters() {
        caregiver.setWorkAddress("456 Avenue");
        caregiver.setSpeciality("Pediatrics");
        assertEquals("456 Avenue", caregiver.getWorkAddress());
        assertEquals("Pediatrics", caregiver.getSpeciality());
    }

    @Test
    public void testAddWorkingSchedule() {
        CaregiverSchedule schedule = new CaregiverSchedule();
        caregiver.addWorkingSchedule(schedule);
        assertEquals(1, caregiver.getWorkingSchedules().size());
        assertSame(schedule, caregiver.getWorkingSchedules().get(0));
        assertEquals(caregiver.getId(), schedule.getIdCaregiver());
    }

    @Test
    public void testRemoveWorkingSchedule() {
        CaregiverSchedule schedule = new CaregiverSchedule();
        caregiver.addWorkingSchedule(schedule);
        caregiver.removeWorkingSchedule(schedule);
        assertTrue(caregiver.getWorkingSchedules().isEmpty());
        assertNull(schedule.getIdCaregiver());
    }
}
