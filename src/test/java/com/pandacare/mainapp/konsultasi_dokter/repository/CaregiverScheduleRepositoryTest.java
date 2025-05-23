package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class CaregiverScheduleRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CaregiverScheduleRepository repository;

    private CaregiverSchedule schedule1;
    private UUID caregiverId;

    @BeforeEach
    public void setup() {
        caregiverId = UUID.randomUUID();

        schedule1 = createSchedule(caregiverId, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), ScheduleStatus.AVAILABLE);
        CaregiverSchedule schedule2 = createSchedule(caregiverId, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), ScheduleStatus.UNAVAILABLE);
        CaregiverSchedule schedule3 = createSchedule(caregiverId, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), ScheduleStatus.AVAILABLE);

        entityManager.persist(schedule1);
        entityManager.persist(schedule2);
        entityManager.persist(schedule3);
        entityManager.flush();
    }

    private CaregiverSchedule createSchedule(UUID caregiverId, DayOfWeek day, LocalTime startTime, LocalTime endTime, ScheduleStatus status) {
        UUID scheduleId = UUID.randomUUID();

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setIdCaregiver(caregiverId);
        schedule.setDay(day);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setStatus(status);
        schedule.setDate(LocalDate.now().plusWeeks(1));
        return schedule;
    }

    @Test
    public void testFindByIdCaregiver() {
        List<CaregiverSchedule> found = repository.findByIdCaregiver(caregiverId);

        assertThat(found).isNotEmpty();
        assertThat(found).allMatch(s -> caregiverId.equals(s.getIdCaregiver()));
    }

    @Test
    public void testFindByIdCaregiverAndStatus() {
        List<CaregiverSchedule> availableSchedules = repository.findByIdCaregiverAndStatus(caregiverId, ScheduleStatus.AVAILABLE);
        List<CaregiverSchedule> unavailableSchedules = repository.findByIdCaregiverAndStatus(caregiverId, ScheduleStatus.UNAVAILABLE);

        assertThat(availableSchedules).allMatch(s -> s.getStatus() == ScheduleStatus.AVAILABLE);
        assertThat(unavailableSchedules).allMatch(s -> s.getStatus() == ScheduleStatus.UNAVAILABLE);
    }

    @Test
    public void testFindByIdCaregiverAndDay() {
        List<CaregiverSchedule> mondaySchedules = repository.findByIdCaregiverAndDay(caregiverId, DayOfWeek.MONDAY);
        List<CaregiverSchedule> tuesdaySchedules = repository.findByIdCaregiverAndDay(caregiverId, DayOfWeek.TUESDAY);
        List<CaregiverSchedule> wednesdaySchedules = repository.findByIdCaregiverAndDay(caregiverId, DayOfWeek.WEDNESDAY);

        assertThat(mondaySchedules).allMatch(s -> s.getDay() == DayOfWeek.MONDAY);
        assertThat(tuesdaySchedules).allMatch(s -> s.getDay() == DayOfWeek.TUESDAY);
        assertThat(wednesdaySchedules).isEmpty();
    }

    @Test
    public void testFindByIdCaregiverNotFound() {
        List<CaregiverSchedule> found = repository.findByIdCaregiver(UUID.randomUUID());
        assertThat(found).isEmpty();
    }

    @Test
    public void testSaveSchedule() {
        UUID newCaregiverId = UUID.randomUUID();
        CaregiverSchedule newSchedule = createSchedule(newCaregiverId, DayOfWeek.THURSDAY,
                LocalTime.of(14, 0), LocalTime.of(15, 0), ScheduleStatus.AVAILABLE);

        CaregiverSchedule saved = repository.save(newSchedule);

        CaregiverSchedule found = entityManager.find(CaregiverSchedule.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getIdCaregiver()).isEqualTo(newCaregiverId);
        assertThat(found.getDay()).isEqualTo(DayOfWeek.THURSDAY);
    }

    @Test
    public void testFindById() {
        CaregiverSchedule found = repository.findById(schedule1.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(schedule1.getId());
    }

    @Test
    public void testFindByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        boolean exists = repository.findById(nonExistentId).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    public void testFindAll() {
        List<CaregiverSchedule> found = repository.findAll();
        long expected = repository.count();
        assertThat(found).hasSize((int) expected);
    }

    @Test
    public void testUpdateSchedule() {
        CaregiverSchedule toUpdate = entityManager.find(CaregiverSchedule.class, schedule1.getId());
        toUpdate.setStatus(ScheduleStatus.UNAVAILABLE);
        repository.save(toUpdate);
        CaregiverSchedule updated = entityManager.find(CaregiverSchedule.class, schedule1.getId());
        assertThat(updated.getStatus()).isEqualTo(ScheduleStatus.UNAVAILABLE);
    }

    @Test
    public void testSaveAll() {
        UUID newCaregiverId = UUID.randomUUID();

        CaregiverSchedule newSchedule1 = createSchedule(newCaregiverId, DayOfWeek.FRIDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0), ScheduleStatus.AVAILABLE);
        CaregiverSchedule newSchedule2 = createSchedule(newCaregiverId, DayOfWeek.SATURDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0), ScheduleStatus.AVAILABLE);
        List<CaregiverSchedule> newSchedules = List.of(newSchedule1, newSchedule2);
        List<CaregiverSchedule> saved = repository.saveAll(newSchedules);
        assertThat(saved).hasSize(2);

        List<CaregiverSchedule> found = repository.findByIdCaregiver(newCaregiverId);
        assertThat(found).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    public void testCount() {
        long countFromRepo = repository.count();
        List<CaregiverSchedule> allSchedules = repository.findAll();
        assertThat(allSchedules).hasSize((int) countFromRepo);
    }
}
