package com.pandacare.mainapp.konsultasi_dokter.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaregiverScheduleTest {
    private CaregiverSchedule schedule;
    private UUID caregiverId;
    private UUID scheduleId;

    @BeforeEach
    void setUp() {
        caregiverId = UUID.randomUUID();
        scheduleId = UUID.randomUUID();

        schedule = new CaregiverSchedule();
        schedule.setId(caregiverId);
        schedule.setIdCaregiver(scheduleId);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.parse("10:00"));
        schedule.setEndTime(LocalTime.parse("11:00"));
    }

    @Test
    void testDefaultState() {
        assertEquals(ScheduleStatus.AVAILABLE, schedule.getStatus());
    }

    @Test
    void testGetterSetter() {
        assertEquals(caregiverId, schedule.getId());
        assertEquals(scheduleId, schedule.getIdCaregiver());
        assertEquals(DayOfWeek.MONDAY, schedule.getDay());
        assertEquals(LocalTime.parse("10:00"), schedule.getStartTime());
        assertEquals(LocalTime.parse("11:00"), schedule.getEndTime());
        assertEquals(ScheduleStatus.AVAILABLE, schedule.getStatus());

        schedule.setDay(DayOfWeek.TUESDAY);
        schedule.setStartTime(LocalTime.parse("14:00"));
        schedule.setEndTime(LocalTime.parse("15:00"));
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        assertEquals(DayOfWeek.TUESDAY, schedule.getDay());
        assertEquals(LocalTime.parse("14:00"), schedule.getStartTime());
        assertEquals(LocalTime.parse("15:00"), schedule.getEndTime());
        assertEquals(ScheduleStatus.AVAILABLE, schedule.getStatus());
    }

    @Test
    void testConstructor() {
        CaregiverSchedule newSchedule = new CaregiverSchedule();
        assertNotNull(newSchedule.getId());
        assertEquals(ScheduleStatus.AVAILABLE, newSchedule.getStatus());
    }
}