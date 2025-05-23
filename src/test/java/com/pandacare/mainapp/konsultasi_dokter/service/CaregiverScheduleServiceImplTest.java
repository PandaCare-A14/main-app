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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CaregiverScheduleServiceImplTest {
    @Mock
    private CaregiverScheduleRepository repository;

    @InjectMocks
    private CaregiverScheduleServiceImpl service;

    @Test
    void testCreateScheduleSuccess() {
        UUID caregiverId = UUID.randomUUID();
        DayOfWeek day = DayOfWeek.MONDAY;
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        CaregiverSchedule schedule = new CaregiverSchedule();

        when(repository.existsOverlappingSchedule(caregiverId, day, startTime, endTime)).thenReturn(false);
        when(repository.save(any(CaregiverSchedule.class))).thenReturn(schedule);

        CaregiverSchedule result = service.createSchedule(caregiverId, day, startTime, endTime);

        assertNotNull(result);
        verify(repository).existsOverlappingSchedule(caregiverId, day, startTime, endTime);
        verify(repository).save(any(CaregiverSchedule.class));
    }

    @Test
    void testCreateScheduleThrowsWhenOverlapping() {
        UUID caregiverId = UUID.randomUUID();
        DayOfWeek day = DayOfWeek.MONDAY;
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        when(repository.existsOverlappingSchedule(caregiverId, day, startTime, endTime)).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () ->
                service.createSchedule(caregiverId, day, startTime, endTime)
        );

        assertEquals("Schedule already exists.", exception.getMessage());
        verify(repository).existsOverlappingSchedule(caregiverId, day, startTime, endTime);
        verify(repository, never()).save(any());
    }

    @Test
    void testCreateMultipleSchedulesSuccess() {
        UUID caregiverId = UUID.randomUUID();
        DayOfWeek day = DayOfWeek.MONDAY;
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        List<CaregiverSchedule> nonOverlappingSlots = new ArrayList<>();
        nonOverlappingSlots.add(new CaregiverSchedule());

        when(repository.existsOverlappingSchedule(any(), any(), any(), any())).thenReturn(false);

        when(repository.saveAll(any())).thenReturn(nonOverlappingSlots);

        List<CaregiverSchedule> result = service.createMultipleSchedules(caregiverId, day, startTime, endTime);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository, atLeastOnce()).existsOverlappingSchedule(any(), any(), any(), any());
        verify(repository).saveAll(any());
    }

    @Test
    void testCreateMultipleSchedulesThrowsWhenAllOverlap() {
        UUID caregiverId = UUID.randomUUID();
        DayOfWeek day = DayOfWeek.MONDAY;
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        when(repository.existsOverlappingSchedule(any(), any(), any(), any())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () ->
                service.createMultipleSchedules(caregiverId, day, startTime, endTime)
        );

        assertEquals("All requested slots overlap with existing schedules.", exception.getMessage());
        verify(repository, atLeastOnce()).existsOverlappingSchedule(any(), any(), any(), any());
        verify(repository, never()).saveAll(any());
    }

    @Test
    void testGetSchedulesByCaregiver() {
        UUID caregiverId = UUID.randomUUID();
        List<CaregiverSchedule> schedules = new ArrayList<>();
        schedules.add(new CaregiverSchedule());

        when(repository.findByIdCaregiver(caregiverId)).thenReturn(schedules);

        List<CaregiverSchedule> result = service.getSchedulesByCaregiver(caregiverId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findByIdCaregiver(caregiverId);
    }

    @Test
    void testGetSchedulesByCaregiverAndDay() {
        UUID caregiverId = UUID.randomUUID();
        DayOfWeek day = DayOfWeek.MONDAY;
        List<CaregiverSchedule> schedules = new ArrayList<>();
        schedules.add(new CaregiverSchedule());

        when(repository.findByIdCaregiverAndDay(caregiverId, day)).thenReturn(schedules);

        List<CaregiverSchedule> result = service.getSchedulesByCaregiverAndDay(caregiverId, day);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findByIdCaregiverAndDay(caregiverId, day);
    }

    @Test
    void testGetSchedulesByCaregiverAndStatus() {
        UUID caregiverId = UUID.randomUUID();
        ScheduleStatus status = ScheduleStatus.AVAILABLE;
        List<CaregiverSchedule> schedules = new ArrayList<>();
        schedules.add(new CaregiverSchedule());

        when(repository.findByIdCaregiverAndStatus(caregiverId, status)).thenReturn(schedules);

        List<CaregiverSchedule> result = service.getSchedulesByCaregiverAndStatus(caregiverId, status);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findByIdCaregiverAndStatus(caregiverId, status);
    }

    @Test
    void testGetSchedulesByCaregiverAndIdScheduleFound() {
        UUID caregiverId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        CaregiverSchedule schedule = new CaregiverSchedule();

        when(repository.findByIdCaregiverAndIdSchedule(caregiverId, scheduleId)).thenReturn(Optional.of(schedule));

        CaregiverSchedule result = service.getSchedulesByCaregiverAndIdSchedule(caregiverId, scheduleId);

        assertNotNull(result);
        verify(repository).findByIdCaregiverAndIdSchedule(caregiverId, scheduleId);
    }

    @Test
    void testGetSchedulesByCaregiverAndIdScheduleNotFound() {
        UUID caregiverId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();

        when(repository.findByIdCaregiverAndIdSchedule(caregiverId, scheduleId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                service.getSchedulesByCaregiverAndIdSchedule(caregiverId, scheduleId)
        );

        assertEquals("Schedule not found with id: " + scheduleId + " and caregiver: " + caregiverId, exception.getMessage());
        verify(repository).findByIdCaregiverAndIdSchedule(caregiverId, scheduleId);
    }

    @Test
    void testDeleteScheduleSuccess(){
        UUID caregiverId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setIdCaregiver(caregiverId);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        when(repository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        when(repository.save(any(CaregiverSchedule.class))).thenAnswer(invocation -> {
            CaregiverSchedule savedSchedule = invocation.getArgument(0);
            return savedSchedule;
        });

        CaregiverSchedule result = service.deleteSchedule(scheduleId);

        assertEquals(scheduleId, result.getId());
        assertEquals(ScheduleStatus.INACTIVE, result.getStatus());
        verify(repository).findById(scheduleId);
        verify(repository).save(any(CaregiverSchedule.class));
    }
}