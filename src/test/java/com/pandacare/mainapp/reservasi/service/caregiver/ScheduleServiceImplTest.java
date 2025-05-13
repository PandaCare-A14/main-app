package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @Mock
    private CaregiverScheduleRepository repository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @Test
    void testGetByIdFound() {
        String id = "schedule-1";
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(schedule));

        CaregiverSchedule result = scheduleService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(repository).findById(id);
    }

    @Test
    void testGetByIdNotFound() {
        String id = "non-existent";
        when(repository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            scheduleService.getById(id);
        });

        assertEquals("Schedule with ID " + id + " not found", exception.getMessage());
        verify(repository).findById(id);
    }

    @Test
    void testIsScheduleAvailableTrue() {
        String id = "available-schedule";
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(id);
        schedule.setStatus(ScheduleStatus.AVAILABLE);
        when(repository.findById(id)).thenReturn(Optional.of(schedule));

        boolean result = scheduleService.isScheduleAvailable(id);

        assertTrue(result);
        verify(repository).findById(id);
    }

    @Test
    void testIsScheduleAvailableFalse() {
        String id = "unavailable-schedule";
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(id);
        schedule.setStatus(ScheduleStatus.UNAVAILABLE);
        when(repository.findById(id)).thenReturn(Optional.of(schedule));

        boolean result = scheduleService.isScheduleAvailable(id);

        assertFalse(result);
        verify(repository).findById(id);
    }

    @Test
    void testUpdateScheduleStatusWithObject() {
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        scheduleService.updateScheduleStatus(schedule, ScheduleStatus.UNAVAILABLE);

        assertEquals(ScheduleStatus.UNAVAILABLE, schedule.getStatus());
        verify(repository).save(schedule);
    }

    @Test
    void testUpdateScheduleStatusWithNullObject() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            scheduleService.updateScheduleStatus((CaregiverSchedule) null, ScheduleStatus.AVAILABLE);
        });

        assertEquals("Schedule cannot be null", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdateScheduleStatusWithId() {
        String id = "schedule-1";
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(id);
        schedule.setStatus(ScheduleStatus.AVAILABLE);
        when(repository.findById(id)).thenReturn(Optional.of(schedule));

        scheduleService.updateScheduleStatus(id, ScheduleStatus.UNAVAILABLE);

        assertEquals(ScheduleStatus.UNAVAILABLE, schedule.getStatus());
        verify(repository).findById(id);
        verify(repository).save(schedule);
    }
}