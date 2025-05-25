package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {
    @Mock
    private CaregiverScheduleRepository repository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @Test
    void testGetByIdFound() {
        UUID id = UUID.randomUUID();
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
        UUID nonExistentId = UUID.randomUUID();
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            scheduleService.getById(nonExistentId);
        });
    }

    @Test
    void testIsScheduleAvailableTrue() {
        UUID id = UUID.randomUUID();
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
        UUID id = UUID.randomUUID();
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
        UUID id = UUID.randomUUID();
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