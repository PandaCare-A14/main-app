package com.pandacare.mainapp.konsultasi_dokter.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.model.state.RequestedState;
import com.pandacare.mainapp.konsultasi_dokter.model.state.ChangeScheduleState;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaregiverScheduleServiceImplTest {
    @Mock
    private CaregiverScheduleRepository repository;

    @InjectMocks
    private CaregiverScheduleServiceImpl service;

    private final String DOCTOR_ID = "DOC12345";
    private final String SCHEDULE_ID = "SCHED12345";
    private final DayOfWeek TEST_DAY = DayOfWeek.MONDAY;
    private final LocalTime START_TIME = LocalTime.of(9, 0);
    private final LocalTime END_TIME = LocalTime.of(10, 0);
    private CaregiverSchedule testSchedule;

    @BeforeEach
    void setUp() {
        testSchedule = new CaregiverSchedule();
        testSchedule.setId(SCHEDULE_ID);
        testSchedule.setIdCaregiver(DOCTOR_ID);
        testSchedule.setDay(TEST_DAY);
        testSchedule.setStartTime(START_TIME);
        testSchedule.setEndTime(END_TIME);
    }

    @Test
    void testCreateScheduleSuccess() {
        when(repository.findOverlappingSchedule(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME))
                .thenReturn(Collections.emptyList());
        when(repository.save(any(CaregiverSchedule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CaregiverSchedule result = service.createSchedule(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME);

        assertNotNull(result);
        assertEquals(DOCTOR_ID, result.getIdCaregiver());
        assertEquals(TEST_DAY, result.getDay());
        assertEquals(START_TIME, result.getStartTime());
        assertEquals(END_TIME, result.getEndTime());
        assertEquals("AVAILABLE", result.getStatusCaregiver());

        verify(repository).findOverlappingSchedule(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME);
        verify(repository).save(any(CaregiverSchedule.class));
    }

    @Test
    void testCreateScheduleWithOverlap() {
        when(repository.findOverlappingSchedule(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME))
                .thenReturn(Collections.singletonList(testSchedule));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                service.createSchedule(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME));

        assertEquals("A schedule at the same time already exists.", exception.getMessage());

        verify(repository).findOverlappingSchedule(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME);
        verify(repository, never()).save(any(CaregiverSchedule.class));
    }

    @Test
    void testCreateScheduleIntervalSuccess() {
        when(repository.findOverlappingSchedule(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME))
                .thenReturn(Collections.emptyList());
        when(repository.save(any(CaregiverSchedule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalTime intervalEndTime = LocalTime.of(10, 0);

        List<CaregiverSchedule> results = service.createScheduleInterval(DOCTOR_ID, TEST_DAY, START_TIME, intervalEndTime);

        assertNotNull(results);
        assertEquals(2, results.size());

        assertEquals(DOCTOR_ID, results.get(0).getIdCaregiver());
        assertEquals(TEST_DAY, results.get(0).getDay());
        assertEquals(START_TIME, results.get(0).getStartTime());
        assertEquals(LocalTime.of(9, 30), results.get(0).getEndTime());

        assertEquals(DOCTOR_ID, results.get(1).getIdCaregiver());
        assertEquals(TEST_DAY, results.get(1).getDay());
        assertEquals(LocalTime.of(9, 30), results.get(1).getStartTime());
        assertEquals(intervalEndTime, results.get(1).getEndTime());

        verify(repository).findOverlappingSchedule(DOCTOR_ID, TEST_DAY, START_TIME, intervalEndTime);
        verify(repository, times(2)).save(any(CaregiverSchedule.class));
    }

    @Test
    void testCreateScheduleIntervalInvalidTimeDuration() {
        LocalTime invalidEndTime = LocalTime.of(9, 45);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                service.createScheduleInterval(DOCTOR_ID, TEST_DAY, START_TIME, invalidEndTime));

        assertTrue(exception.getMessage().contains("Time is not valid") ||
                exception.getMessage().contains("Start time can't be set after end time"));

        verify(repository).findOverlappingSchedule(DOCTOR_ID, TEST_DAY, START_TIME, invalidEndTime);
        verify(repository, never()).save(any(CaregiverSchedule.class));
    }

    @Test
    void testChangeScheduleSuccess() {
        testSchedule.setIdPacilian("PAT12345");
        testSchedule.setState(new RequestedState());

        when(repository.findById(SCHEDULE_ID)).thenReturn(testSchedule);
        when(repository.save(any(CaregiverSchedule.class))).thenReturn(testSchedule);

        DayOfWeek newDay = DayOfWeek.WEDNESDAY;
        LocalTime newStartTime = LocalTime.of(14, 0);
        LocalTime newEndTime = LocalTime.of(15, 0);
        String message = "Jadwal diganti karena ada operasi";

        boolean result = service.changeSchedule(SCHEDULE_ID, newDay, newStartTime, newEndTime, message);

        assertTrue(result);
        assertEquals(newDay, testSchedule.getDay());
        assertEquals(newStartTime, testSchedule.getStartTime());
        assertEquals(newEndTime, testSchedule.getEndTime());
        assertEquals(message, testSchedule.getMessage());
        assertTrue(testSchedule.isChangeSchedule());
        assertTrue(testSchedule.getCurrentState() instanceof ChangeScheduleState);

        verify(repository).findById(SCHEDULE_ID);
        verify(repository).save(testSchedule);
    }

    @Test
    void testChangeScheduleWhereScheduleNotFound() {
        when(repository.findById(SCHEDULE_ID)).thenReturn(null);
        boolean result = service.changeSchedule(SCHEDULE_ID, TEST_DAY, START_TIME, END_TIME, "");
        assertFalse(result);
        verify(repository).findById(SCHEDULE_ID);
        verify(repository, never()).save(any(CaregiverSchedule.class));
    }

    @Test
    void testApproveScheduleSuccess() {
        when(repository.findById(SCHEDULE_ID)).thenReturn(testSchedule);
        when(repository.save(any(CaregiverSchedule.class))).thenReturn(testSchedule);
        testSchedule.request("PAT12345", "");
        boolean result = service.approveSchedule(SCHEDULE_ID);
        assertTrue(result);
        assertEquals("APPROVED", testSchedule.getStatusCaregiver());
        verify(repository).findById(SCHEDULE_ID);
        verify(repository).save(testSchedule);
    }

    @Test
    void testApproveScheduleWhereScheduleNotFound() {
        when(repository.findById(SCHEDULE_ID)).thenReturn(null);
        boolean result = service.approveSchedule(SCHEDULE_ID);
        assertFalse(result);
        verify(repository).findById(SCHEDULE_ID);
        verify(repository, never()).save(any(CaregiverSchedule.class));
    }

    @Test
    void testRejectScheduleSuccess() {
        when(repository.findById(SCHEDULE_ID)).thenReturn(testSchedule);
        when(repository.save(any(CaregiverSchedule.class))).thenReturn(testSchedule);
        testSchedule.request("PAT12345", "");
        boolean result = service.rejectSchedule(SCHEDULE_ID);
        assertTrue(result);
        assertEquals("REJECTED", testSchedule.getStatusCaregiver());
        assertEquals("Jadwal tidak sesuai", testSchedule.getMessage());
        verify(repository).findById(SCHEDULE_ID);
        verify(repository).save(testSchedule);
    }

    @Test
    void testRejectScheduleWhereScheduleNotFound() {
        when(repository.findById(SCHEDULE_ID)).thenReturn(null);
        boolean result = service.rejectSchedule(SCHEDULE_ID);
        assertFalse(result);
        verify(repository).findById(SCHEDULE_ID);
        verify(repository, never()).save(any(CaregiverSchedule.class));
    }

    @Test
    void testFindByIdCaregiver() {
        List<CaregiverSchedule> expectedSchedules = Arrays.asList(testSchedule);
        when(repository.findByIdCaregiver(DOCTOR_ID)).thenReturn(expectedSchedules);
        List<CaregiverSchedule> result = service.findByIdCaregiver(DOCTOR_ID);
        assertEquals(expectedSchedules, result);
        verify(repository).findByIdCaregiver(DOCTOR_ID);
    }

    @Test
    void testFindByIdCaregiverAndDay() {
        List<CaregiverSchedule> expectedSchedules = Arrays.asList(testSchedule);
        when(repository.findByIdCaregiverAndDay(DOCTOR_ID, TEST_DAY)).thenReturn(expectedSchedules);
        List<CaregiverSchedule> result = service.findByIdCaregiverAndDay(DOCTOR_ID, TEST_DAY);
        assertEquals(expectedSchedules, result);
        verify(repository).findByIdCaregiverAndDay(DOCTOR_ID, TEST_DAY);
    }

    @Test
    void testFindOverlappingSchedule() {
        List<CaregiverSchedule> expectedSchedules = Arrays.asList(testSchedule);
        when(repository.findOverlappingSchedule(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME))
                .thenReturn(expectedSchedules);
        List<CaregiverSchedule> result = service.findOverlappingSchedule(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME);
        assertEquals(expectedSchedules, result);
        verify(repository).findOverlappingSchedule(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME);
    }

    @Test
    void testFindByIdCaregiverAndStatus() {
        List<CaregiverSchedule> expectedSchedules = Arrays.asList(testSchedule);
        when(repository.findByIdCaregiverAndStatus(DOCTOR_ID, "AVAILABLE")).thenReturn(expectedSchedules);
        List<CaregiverSchedule> result = service.findByIdCaregiverAndStatus(DOCTOR_ID, "AVAILABLE");
        assertEquals(expectedSchedules, result);
        verify(repository).findByIdCaregiverAndStatus(DOCTOR_ID, "AVAILABLE");
    }

    @Test
    void testFindById() {
        when(repository.findById(SCHEDULE_ID)).thenReturn(testSchedule);
        CaregiverSchedule result = service.findById(SCHEDULE_ID);
        assertEquals(testSchedule, result);
        verify(repository).findById(SCHEDULE_ID);
    }
}