package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import com.pandacare.mainapp.konsultasi_dokter.model.state.AvailableState;
import com.pandacare.mainapp.konsultasi_dokter.model.state.RequestedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaregiverScheduleServiceImplTest {
    @Mock private CaregiverScheduleRepository repository;

    private CaregiverScheduleService service;

    private final String idCaregiver = "DOC-12345";
    private final String scheduleId = "SCHED-001";
    private final LocalDate date = LocalDate.parse("2025-05-06");
    private final LocalTime start = LocalTime.parse("10:00");
    private final LocalTime end = LocalTime.parse("11:00");

    private CaregiverSchedule schedule;

    @BeforeEach
    void setUp() {
        service = new CaregiverScheduleServiceImpl(repository);
        schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDate(date);
        schedule.setStartTime(start);
        schedule.setEndTime(end);
    }

    @Test
    void createScheduleSuccess() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CaregiverSchedule result = service.createSchedule(idCaregiver, date, start, end);

        assertNotNull(result);
        assertEquals(idCaregiver, result.getIdCaregiver());
        assertEquals(date, result.getDate());
        assertEquals(start, result.getStartTime());
        assertEquals(end, result.getEndTime());
        assertEquals("AVAILABLE", result.getStatusCaregiver());

        verify(repository).save(result);
    }

    @Test
    void changeScheduleSuccess() {
        schedule.setState(new RequestedState());
        LocalDate newDate = LocalDate.parse("2025-05-10");
        LocalTime newStart = LocalTime.parse("14:00");
        LocalTime newEnd = LocalTime.parse("15:00");
        String message = "Schedule changed";

        when(repository.findById(scheduleId)).thenReturn(schedule);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        boolean result = service.changeSchedule(scheduleId, newDate, newStart, newEnd, message);

        assertTrue(result);
        assertEquals(newDate, schedule.getDate());
        assertEquals(newStart, schedule.getStartTime());
        assertEquals(newEnd, schedule.getEndTime());
        assertEquals(message, schedule.getMessage());
        assertTrue(schedule.isChangeSchedule());
    }

    @Test
    void changeScheduleNotFound() {
        LocalDate date = LocalDate.parse("2025-05-10");
        LocalTime startTime = LocalTime.parse("14:00");
        LocalTime endTime = LocalTime.parse("15:00");

        when(repository.findById(scheduleId)).thenReturn(null);
        assertFalse(service.changeSchedule(scheduleId, date, startTime, endTime, ""));
        verify(repository, never()).save(any());
    }

    @Test
    void approveScheduleSuccess() {
        when(repository.findById(scheduleId)).thenReturn(schedule);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        schedule.setState(new RequestedState());
        assertTrue(service.approveSchedule(scheduleId));
    }

    @Test
    void approveScheduleNotFound() {
        when(repository.findById(scheduleId)).thenReturn(null);
        assertFalse(service.approveSchedule(scheduleId));
    }

    @Test
    void rejectScheduleSuccess() {
        schedule.setState(new RequestedState());
        when(repository.findById(scheduleId)).thenReturn(schedule);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        schedule.setState(new RequestedState());
        assertTrue(service.rejectSchedule(scheduleId));
        assertEquals("Jadwal tidak sesuai", schedule.getMessage());
    }

    @Test
    void rejectScheduleNotFound() {
        when(repository.findById(scheduleId)).thenReturn(null);
        assertFalse(service.rejectSchedule(scheduleId));
    }

    @Test
    void findByIdCaregiverReturnsList() {
        when(repository.findByIdCaregiver(idCaregiver)).thenReturn(Arrays.asList(schedule, schedule));
        List<CaregiverSchedule> result = service.findByIdCaregiver(idCaregiver);
        assertEquals(2, result.size());
    }

    @Test
    void findByIdCaregiverEmpty() {
        when(repository.findByIdCaregiver(idCaregiver)).thenReturn(Collections.emptyList());
        assertTrue(service.findByIdCaregiver(idCaregiver).isEmpty());
    }

    @Test
    void findByIdCaregiverAndStatusMatch() {
        schedule.setState(new AvailableState());
        CaregiverSchedule other = new CaregiverSchedule();
        other.setIdCaregiver(idCaregiver);
        other.setState(new RequestedState());

        when(repository.findByIdCaregiver(idCaregiver)).thenReturn(Arrays.asList(schedule, other));

        List<CaregiverSchedule> result = service.findByIdCaregiverAndStatus(idCaregiver, "AVAILABLE");
        assertEquals(1, result.size());
        assertEquals("AVAILABLE", result.getFirst().getStatusCaregiver());
    }

    @Test
    void findByIdCaregiverAndStatusNoMatch() {
        schedule.setState(new AvailableState());
        when(repository.findByIdCaregiver(idCaregiver)).thenReturn(Collections.singletonList(schedule));
        assertTrue(service.findByIdCaregiverAndStatus(idCaregiver, "REJECTED").isEmpty());
    }

    @Test
    void findByIdFound() {
        when(repository.findById(scheduleId)).thenReturn(schedule);
        assertEquals(schedule, service.findById(scheduleId));
    }

    @Test
    void findByIdNotFound() {
        when(repository.findById(scheduleId)).thenReturn(null);
        assertNull(service.findById(scheduleId));
    }

    @Test
    void createScheduleIntervalSuccess() {
        LocalDate testDate = LocalDate.parse("2025-05-06");
        LocalTime testStart = LocalTime.parse("10:00");
        LocalTime testEnd = LocalTime.parse("12:00");
        int testDuration = 30;

        when(repository.save(any(CaregiverSchedule.class)))
                .thenAnswer(invocation -> {
                    CaregiverSchedule schedule = invocation.getArgument(0);
                    if (schedule.getId() == null) {
                        schedule.setId(UUID.randomUUID().toString());
                    }
                    return schedule;
                });

        List<CaregiverSchedule> result = service.createScheduleInterval(idCaregiver, testDate, testStart, testEnd);

        assertEquals(4, result.size());

        assertEquals(idCaregiver, result.getFirst().getIdCaregiver());
        assertEquals(testDate, result.getFirst().getDate());
        assertEquals(testStart, result.getFirst().getStartTime());
        assertEquals(testStart.plusMinutes(testDuration), result.get(0).getEndTime());
        assertEquals("AVAILABLE", result.get(0).getStatusCaregiver());

        assertEquals(idCaregiver, result.get(3).getIdCaregiver());
        assertEquals(testDate, result.get(3).getDate());
        assertEquals(testStart.plusMinutes(testDuration * 3), result.get(3).getStartTime());
        assertEquals(testEnd, result.get(3).getEndTime());
        assertEquals("AVAILABLE", result.get(3).getStatusCaregiver());

        verify(repository, times(4)).save(any(CaregiverSchedule.class));
    }


    @Test
    void createScheduleIntervalWithInvalidTime() {
        LocalDate testDate = LocalDate.parse("2025-05-06");
        LocalTime testStart = LocalTime.parse("12:00");
        LocalTime testEnd = LocalTime.parse("10:00");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createScheduleInterval(idCaregiver, testDate, testStart, testEnd);
        });

        assertTrue(exception.getMessage().contains("Start time can't be set after end time.") ||
                exception.getMessage().contains("End time can't be set after start time."));

        verify(repository, never()).save(any(CaregiverSchedule.class));
    }

    @Test
    void createScheduleIntervalWithOverlap() {
        LocalDate testDate = LocalDate.parse("2025-05-06");
        LocalTime testStart = LocalTime.parse("10:00");
        LocalTime testEnd = LocalTime.parse("12:00");

        CaregiverSchedule existingSchedule = new CaregiverSchedule();
        existingSchedule.setId("existing-schedule");
        existingSchedule.setIdCaregiver(idCaregiver);
        existingSchedule.setDate(testDate);
        existingSchedule.setStartTime(LocalTime.parse("11:00"));
        existingSchedule.setEndTime(LocalTime.parse("11:30"));

        when(repository.findOverlappingSchedule(eq(idCaregiver), eq(testDate), eq(testStart), eq(testEnd)))
                .thenReturn(Collections.singletonList(existingSchedule));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createScheduleInterval(idCaregiver, testDate, testStart, testEnd);
        });

        assertTrue(exception.getMessage().contains("A schedule at the same time already exists."));

        verify(repository, never()).save(any(CaregiverSchedule.class));
    }
}