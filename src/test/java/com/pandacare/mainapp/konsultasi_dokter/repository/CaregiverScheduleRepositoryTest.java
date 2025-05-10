package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.model.state.ApprovedState;
import com.pandacare.mainapp.konsultasi_dokter.model.state.AvailableState;
import com.pandacare.mainapp.konsultasi_dokter.model.state.RequestedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CaregiverScheduleRepositoryTest {
    private CaregiverScheduleRepository repository;
    private final String DOCTOR_ID_1 = "DOC12345";
    private final String DOCTOR_ID_2 = "DOC67890";
    private final String SCHEDULE_ID_1 = UUID.randomUUID().toString();
    private final String SCHEDULE_ID_2 = UUID.randomUUID().toString();
    private final String SCHEDULE_ID_3 = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        repository = new CaregiverScheduleRepository();
        CaregiverSchedule schedule1 = createSchedule(SCHEDULE_ID_1, DOCTOR_ID_1, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0), new AvailableState());
        CaregiverSchedule schedule2 = createSchedule(SCHEDULE_ID_2, DOCTOR_ID_1, DayOfWeek.MONDAY,
                LocalTime.of(10, 30), LocalTime.of(11, 30), new RequestedState());
        CaregiverSchedule schedule3 = createSchedule(SCHEDULE_ID_3, DOCTOR_ID_2, DayOfWeek.WEDNESDAY,
                LocalTime.of(13, 0), LocalTime.of(14, 0), new ApprovedState());

        repository.save(schedule1);
        repository.save(schedule2);
        repository.save(schedule3);
    }

    private CaregiverSchedule createSchedule(String id, String doctorId, DayOfWeek day,
                                             LocalTime startTime, LocalTime endTime,
                                             com.pandacare.mainapp.konsultasi_dokter.model.state.StatusCaregiver state) {
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(id);
        schedule.setIdCaregiver(doctorId);
        schedule.setDay(day);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setState(state);
        return schedule;
    }

    @Test
    void testSaveNewSchedule() {
        CaregiverSchedule newSchedule = new CaregiverSchedule();
        newSchedule.setIdCaregiver(DOCTOR_ID_1);
        newSchedule.setDay(DayOfWeek.TUESDAY);
        newSchedule.setStartTime(LocalTime.of(9, 0));
        newSchedule.setEndTime(LocalTime.of(10, 0));

        CaregiverSchedule savedSchedule = repository.save(newSchedule);

        assertNotNull(savedSchedule.getId());
        assertEquals(newSchedule, repository.findById(savedSchedule.getId()));
    }

    @Test
    void testSaveExistingSchedule() {
        CaregiverSchedule existingSchedule = repository.findById(SCHEDULE_ID_1);

        LocalTime newStartTime = LocalTime.of(8, 30);
        existingSchedule.setStartTime(newStartTime);

        CaregiverSchedule updatedSchedule = repository.save(existingSchedule);

        assertEquals(SCHEDULE_ID_1, updatedSchedule.getId());
        assertEquals(newStartTime, repository.findById(SCHEDULE_ID_1).getStartTime());

        assertEquals(2, repository.findByIdCaregiver(DOCTOR_ID_1).size());
        assertEquals(1, repository.findByIdCaregiver(DOCTOR_ID_2).size());
    }

    @Test
    void testFindById() {
        CaregiverSchedule foundSchedule = repository.findById(SCHEDULE_ID_1);

        assertNotNull(foundSchedule);
        assertEquals(DOCTOR_ID_1, foundSchedule.getIdCaregiver());
        assertEquals(DayOfWeek.MONDAY, foundSchedule.getDay());

        assertNull(repository.findById("NON_EXISTENT_ID"));
    }

    @Test
    void testFindByIdCaregiver() {
        List<CaregiverSchedule> doctorSchedules = repository.findByIdCaregiver(DOCTOR_ID_1);

        assertEquals(2, doctorSchedules.size());
        assertTrue(doctorSchedules.stream().allMatch(s -> DOCTOR_ID_1.equals(s.getIdCaregiver())));

        List<CaregiverSchedule> nonExistentDoctorSchedules = repository.findByIdCaregiver("NON_EXISTENT_DOCTOR");
        assertTrue(nonExistentDoctorSchedules.isEmpty());
    }

    @Test
    void testFindByIdCaregiverAndDay() {
        List<CaregiverSchedule> schedules = repository.findByIdCaregiverAndDay(DOCTOR_ID_1, DayOfWeek.MONDAY);

        assertEquals(2, schedules.size());
        assertTrue(schedules.stream().allMatch(s ->
                DOCTOR_ID_1.equals(s.getIdCaregiver()) && DayOfWeek.MONDAY.equals(s.getDay())));

        List<CaregiverSchedule> emptySchedules = repository.findByIdCaregiverAndDay(DOCTOR_ID_1, DayOfWeek.FRIDAY);
        assertTrue(emptySchedules.isEmpty());
    }

    @Test
    void testFindOverlappingSchedule() {
        List<CaregiverSchedule> overlappingSchedules = repository.findOverlappingSchedule(
                DOCTOR_ID_1, DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30));

        assertEquals(1, overlappingSchedules.size());
        assertEquals(SCHEDULE_ID_1, overlappingSchedules.get(0).getId());

        List<CaregiverSchedule> nonOverlappingSchedules = repository.findOverlappingSchedule(
                DOCTOR_ID_1, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(8, 45));

        assertTrue(nonOverlappingSchedules.isEmpty());

        List<CaregiverSchedule> touchingSchedules = repository.findOverlappingSchedule(
                DOCTOR_ID_1, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0));

        assertTrue(touchingSchedules.isEmpty());
    }

    @Test
    void testFindByIdCaregiverAndStatus() {
        List<CaregiverSchedule> availableSchedules = repository.findByIdCaregiverAndStatus(DOCTOR_ID_1, "AVAILABLE");

        assertEquals(1, availableSchedules.size());
        assertEquals(SCHEDULE_ID_1, availableSchedules.get(0).getId());

        List<CaregiverSchedule> requestedSchedules = repository.findByIdCaregiverAndStatus(DOCTOR_ID_1, "REQUESTED");

        assertEquals(1, requestedSchedules.size());
        assertEquals(SCHEDULE_ID_2, requestedSchedules.get(0).getId());

        List<CaregiverSchedule> nonExistentStatusSchedules = repository.findByIdCaregiverAndStatus(DOCTOR_ID_1, "UNKNOWN_STATUS");
        assertTrue(nonExistentStatusSchedules.isEmpty());
    }
}