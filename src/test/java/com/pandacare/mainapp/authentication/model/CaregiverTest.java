package com.pandacare.mainapp.authentication.model;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CaregiverTest {

    private Caregiver caregiver;

    @BeforeEach
    void init() {
        caregiver = new Caregiver(
                "John Doe",
                "NIK123",
                "08123456789",
                "123 Street",
                "General"
        );
    }

    @Nested
    @DisplayName("Default Constructor")
    class DefaultConstructor {

        private Caregiver defaultCaregiver;

        @BeforeEach
        void setup() {
            defaultCaregiver = new Caregiver();
        }

        @Test
        @DisplayName("should initialize empty fields")
        void shouldInitializeEmptyFields() {
            assertAll("Default constructor",
                    () -> assertNull(defaultCaregiver.getWorkAddress()),
                    () -> assertNull(defaultCaregiver.getSpeciality()),
                    () -> assertNotNull(defaultCaregiver.getWorkingSchedules()),
                    () -> assertTrue(defaultCaregiver.getWorkingSchedules().isEmpty())
            );
        }
    }

    @Nested
    @DisplayName("Parameterized Constructor")
    class ParameterizedConstructor {

        @Test
        @DisplayName("should set all fields except schedules")
        void shouldSetAllFields() {
            assertAll("Constructor fields",
                    () -> assertEquals("John Doe", caregiver.getName()),
                    () -> assertEquals("NIK123", caregiver.getNik()),
                    () -> assertEquals("08123456789", caregiver.getPhoneNumber()),
                    () -> assertEquals("123 Street", caregiver.getWorkAddress()),
                    () -> assertEquals("General", caregiver.getSpeciality()),
                    () -> assertTrue(caregiver.getWorkingSchedules().isEmpty())
            );
        }
    }

    @Nested
    @DisplayName("Setters")
    class Setters {

        @Test
        @DisplayName("should update work address and speciality")
        void shouldUpdateFields() {
            caregiver.setWorkAddress("456 Avenue");
            caregiver.setSpeciality("Pediatrics");
            assertAll("Updated fields",
                    () -> assertEquals("456 Avenue", caregiver.getWorkAddress()),
                    () -> assertEquals("Pediatrics", caregiver.getSpeciality())
            );
        }
    }

    @Nested
    @DisplayName("Working Schedule")
    class WorkingScheduleTests {

        private CaregiverSchedule schedule;

        @BeforeEach
        void setup() {
            schedule = new CaregiverSchedule();
        }

        @Test
        @DisplayName("addWorkingSchedule should link schedule")
        void addLinkSchedule() {
            caregiver.addWorkingSchedule(schedule);
            assertAll("Add schedule",
                    () -> assertEquals(1, caregiver.getWorkingSchedules().size()),
                    () -> assertSame(schedule, caregiver.getWorkingSchedules().get(0)),
                    () -> assertEquals(caregiver.getId(), schedule.getIdCaregiver())
            );
        }

        @Test
        @DisplayName("removeWorkingSchedule should unlink schedule")
        void removeUnlinkSchedule() {
            caregiver.addWorkingSchedule(schedule);
            caregiver.removeWorkingSchedule(schedule);
            assertAll("Remove schedule",
                    () -> assertTrue(caregiver.getWorkingSchedules().isEmpty()),
                    () -> assertNull(schedule.getIdCaregiver())
            );
        }
    }
}
