package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.service.CaregiverScheduleFacade;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @Mock
    private CaregiverScheduleFacade caregiverFacade;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @Test
    void testGetByIdFound() {
        UUID id = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(id);
        when(caregiverFacade.findById(id)).thenReturn(schedule);

        CaregiverSchedule result = scheduleService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(caregiverFacade).findById(id);
    }

    @Test
    void testGetByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(caregiverFacade.findById(nonExistentId))
                .thenThrow(new EntityNotFoundException("Schedule not found"));

        assertThrows(EntityNotFoundException.class, () -> {
            scheduleService.getById(nonExistentId);
        });

        verify(caregiverFacade).findById(nonExistentId);
    }

    @Test
    void testIsScheduleAvailableTrue() {
        UUID id = UUID.randomUUID();
        when(caregiverFacade.isAvailable(id)).thenReturn(true);

        boolean result = scheduleService.isScheduleAvailable(id);

        assertTrue(result);
        verify(caregiverFacade).isAvailable(id);
    }

    @Test
    void testIsScheduleAvailableFalse() {
        UUID id = UUID.randomUUID();
        when(caregiverFacade.isAvailable(id)).thenReturn(false);

        boolean result = scheduleService.isScheduleAvailable(id);

        assertFalse(result);
        verify(caregiverFacade).isAvailable(id);
    }

    @Test
    void testUpdateScheduleStatusWithObject() {
        UUID scheduleId = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        scheduleService.updateScheduleStatus(schedule, ScheduleStatus.UNAVAILABLE);

        verify(caregiverFacade).updateStatus(scheduleId, ScheduleStatus.UNAVAILABLE);
    }

    @Test
    void testUpdateScheduleStatusWithNullObject() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            scheduleService.updateScheduleStatus((CaregiverSchedule) null, ScheduleStatus.AVAILABLE);
        });

        verify(caregiverFacade, never()).updateStatus(any(UUID.class), any(ScheduleStatus.class));
    }

    @Test
    void testUpdateScheduleStatusWithId() {
        UUID id = UUID.randomUUID();

        scheduleService.updateScheduleStatus(id, ScheduleStatus.UNAVAILABLE);

        verify(caregiverFacade).updateStatus(id, ScheduleStatus.UNAVAILABLE);
    }
}