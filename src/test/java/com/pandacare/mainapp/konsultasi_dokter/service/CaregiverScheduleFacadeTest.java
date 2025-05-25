package com.pandacare.mainapp.konsultasi_dokter.service;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CaregiverScheduleFacadeTest {

    @Mock
    private CaregiverScheduleRepository repository;

    @InjectMocks
    private CaregiverScheduleFacade facade;

    @Test
    void testFindByIdSuccess() {
        UUID scheduleId = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        when(repository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        CaregiverSchedule result = facade.findById(scheduleId);

        assertNotNull(result);
        assertEquals(scheduleId, result.getId());
        assertEquals(ScheduleStatus.AVAILABLE, result.getStatus());
        verify(repository).findById(scheduleId);
    }

    @Test
    void testFindByIdNotFound() {
        UUID scheduleId = UUID.randomUUID();
        when(repository.findById(scheduleId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                facade.findById(scheduleId)
        );

        assertEquals("Schedule not found with id: " + scheduleId, exception.getMessage());
        verify(repository).findById(scheduleId);
    }

    @Test
    void testUpdateStatusSuccess() {
        UUID scheduleId = UUID.randomUUID();
        ScheduleStatus newStatus = ScheduleStatus.UNAVAILABLE;

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        when(repository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(repository.save(any(CaregiverSchedule.class))).thenReturn(schedule);

        facade.updateStatus(scheduleId, newStatus);

        assertEquals(newStatus, schedule.getStatus());
        verify(repository).findById(scheduleId);
        verify(repository).save(schedule);
    }

    @Test
    void testUpdateStatusScheduleNotFound() {
        UUID scheduleId = UUID.randomUUID();
        ScheduleStatus newStatus = ScheduleStatus.UNAVAILABLE;

        when(repository.findById(scheduleId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                facade.updateStatus(scheduleId, newStatus)
        );

        assertEquals("Schedule not found with id: " + scheduleId, exception.getMessage());
        verify(repository).findById(scheduleId);
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdateStatusFromAvailableToBooked() {
        UUID scheduleId = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        when(repository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(repository.save(any(CaregiverSchedule.class))).thenReturn(schedule);

        facade.updateStatus(scheduleId, ScheduleStatus.UNAVAILABLE);

        assertEquals(ScheduleStatus.UNAVAILABLE, schedule.getStatus());
        verify(repository).findById(scheduleId);
        verify(repository).save(schedule);
    }

    @Test
    void testIsAvailableWhenScheduleIsAvailable() {
        UUID scheduleId = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        when(repository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        boolean result = facade.isAvailable(scheduleId);

        assertTrue(result);
        verify(repository).findById(scheduleId);
    }

    @Test
    void testIsAvailableWhenScheduleIsInactive() {
        UUID scheduleId = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setStatus(ScheduleStatus.INACTIVE);

        when(repository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        boolean result = facade.isAvailable(scheduleId);

        assertFalse(result);
        verify(repository).findById(scheduleId);
    }

    @Test
    void testIsAvailableWhenScheduleIsUnavailable() {
        UUID scheduleId = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setStatus(ScheduleStatus.UNAVAILABLE);

        when(repository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        boolean result = facade.isAvailable(scheduleId);

        assertFalse(result);
        verify(repository).findById(scheduleId);
    }

    @Test
    void testIsAvailableScheduleNotFound() {
        UUID scheduleId = UUID.randomUUID();
        when(repository.findById(scheduleId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                facade.isAvailable(scheduleId)
        );

        assertEquals("Schedule not found with id: " + scheduleId, exception.getMessage());
        verify(repository).findById(scheduleId);
    }

    @Test
    void testUpdateStatusWithNullStatus() {
        UUID scheduleId = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        when(repository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(repository.save(any(CaregiverSchedule.class))).thenReturn(schedule);

        facade.updateStatus(scheduleId, null);

        assertNull(schedule.getStatus());
        verify(repository).findById(scheduleId);
        verify(repository).save(schedule);
    }

    @Test
    void testUpdateStatusToAllPossibleStatuses() {
        UUID scheduleId = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);

        when(repository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(repository.save(any(CaregiverSchedule.class))).thenReturn(schedule);

        for (ScheduleStatus status : ScheduleStatus.values()) {
            facade.updateStatus(scheduleId, status);
            assertEquals(status, schedule.getStatus());
        }

        verify(repository, times(ScheduleStatus.values().length)).findById(scheduleId);
        verify(repository, times(ScheduleStatus.values().length)).save(schedule);
    }

    @Test
    void testIsAvailableForAllStatuses() {
        UUID scheduleId = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);

        when(repository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        schedule.setStatus(ScheduleStatus.AVAILABLE);
        assertTrue(facade.isAvailable(scheduleId));

        schedule.setStatus(ScheduleStatus.INACTIVE);
        assertFalse(facade.isAvailable(scheduleId));

        schedule.setStatus(ScheduleStatus.UNAVAILABLE);
        assertFalse(facade.isAvailable(scheduleId));

        verify(repository, times(3)).findById(scheduleId);
    }
}