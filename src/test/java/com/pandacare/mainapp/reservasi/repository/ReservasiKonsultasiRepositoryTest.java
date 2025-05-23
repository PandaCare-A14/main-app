package com.pandacare.mainapp.reservasi.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class ReservasiKonsultasiRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ReservasiKonsultasiRepository repository;

    @Test
    void testFindAllByIdPasien() {
        String pasienId = "PAT12345";
        UUID caregiverId = UUID.randomUUID();

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setIdCaregiver(caregiverId);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedule.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(schedule);

        ReservasiKonsultasi reservation = new ReservasiKonsultasi();
        reservation.setIdPacilian(pasienId);
        reservation.setIdSchedule(schedule);
        reservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        entityManager.persist(reservation);

        entityManager.flush();
        entityManager.clear();

        List<ReservasiKonsultasi> found = repository.findAllByIdPasien(pasienId);

        assertEquals(1, found.size());
        assertEquals(pasienId, found.getFirst().getIdPacilian());
    }

    @Test
    void testFindByCaregiverId() {
        UUID caregiverId = UUID.randomUUID();

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setIdCaregiver(caregiverId);
        schedule.setDay(DayOfWeek.TUESDAY);
        schedule.setStartTime(LocalTime.of(14, 0));
        schedule.setEndTime(LocalTime.of(15, 0));
        schedule.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(schedule);

        ReservasiKonsultasi reservation = new ReservasiKonsultasi();
        reservation.setIdPacilian("PAT67890");
        reservation.setIdSchedule(schedule);
        reservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        entityManager.persist(reservation);

        entityManager.flush();
        entityManager.clear();

        List<ReservasiKonsultasi> found = repository.findByCaregiverId(caregiverId);

        assertEquals(1, found.size());
        assertEquals(caregiverId, found.getFirst().getIdSchedule().getIdCaregiver());
    }

    @Test
    void testFindByCaregiverIdAndStatus() {
        UUID caregiverId = UUID.randomUUID();

        CaregiverSchedule schedule1 = new CaregiverSchedule();
        schedule1.setIdCaregiver(caregiverId);
        schedule1.setDay(DayOfWeek.MONDAY);
        schedule1.setStartTime(LocalTime.of(10, 0));
        schedule1.setEndTime(LocalTime.of(11, 0));
        schedule1.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(schedule1);

        CaregiverSchedule schedule2 = new CaregiverSchedule();
        schedule2.setIdCaregiver(caregiverId);
        schedule2.setDay(DayOfWeek.WEDNESDAY);
        schedule2.setStartTime(LocalTime.of(11, 0));
        schedule2.setEndTime(LocalTime.of(12, 0));
        schedule2.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(schedule2);

        ReservasiKonsultasi reservation1 = new ReservasiKonsultasi();
        reservation1.setIdPacilian("PAT12345");
        reservation1.setIdSchedule(schedule1);
        reservation1.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        entityManager.persist(reservation1);

        ReservasiKonsultasi reservation2 = new ReservasiKonsultasi();
        reservation2.setIdPacilian("PAT67890");
        reservation2.setIdSchedule(schedule2);
        reservation2.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        entityManager.persist(reservation2);

        entityManager.flush();
        entityManager.clear();

        List<ReservasiKonsultasi> waitingReservations = repository.findByCaregiverIdAndStatus(caregiverId, StatusReservasiKonsultasi.WAITING);

        assertEquals(1, waitingReservations.size());
        assertEquals(StatusReservasiKonsultasi.WAITING, waitingReservations.getFirst().getStatusReservasi());

        List<ReservasiKonsultasi> approvedReservations = repository.findByCaregiverIdAndStatus(caregiverId, StatusReservasiKonsultasi.APPROVED);

        assertEquals(1, approvedReservations.size());
        assertEquals(StatusReservasiKonsultasi.APPROVED, approvedReservations.getFirst().getStatusReservasi());
    }

    @Test
    void testFindByCaregiverIdAndDay() {
        UUID caregiverId = UUID.randomUUID();

        CaregiverSchedule mondaySchedule = new CaregiverSchedule();
        mondaySchedule.setIdCaregiver(caregiverId);
        mondaySchedule.setDay(DayOfWeek.MONDAY);
        mondaySchedule.setStartTime(LocalTime.of(9, 0));
        mondaySchedule.setEndTime(LocalTime.of(10, 0));
        mondaySchedule.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(mondaySchedule);

        CaregiverSchedule wednesdaySchedule = new CaregiverSchedule();
        wednesdaySchedule.setIdCaregiver(caregiverId);
        wednesdaySchedule.setDay(DayOfWeek.WEDNESDAY);
        wednesdaySchedule.setStartTime(LocalTime.of(14, 0));
        wednesdaySchedule.setEndTime(LocalTime.of(15, 0));
        wednesdaySchedule.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(wednesdaySchedule);

        ReservasiKonsultasi mondayReservation = new ReservasiKonsultasi();
        mondayReservation.setIdPacilian("PAT54321");
        mondayReservation.setIdSchedule(mondaySchedule);
        mondayReservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        entityManager.persist(mondayReservation);

        ReservasiKonsultasi wednesdayReservation = new ReservasiKonsultasi();
        wednesdayReservation.setIdPacilian("PAT98765");
        wednesdayReservation.setIdSchedule(wednesdaySchedule);
        wednesdayReservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        entityManager.persist(wednesdayReservation);

        entityManager.flush();
        entityManager.clear();

        List<ReservasiKonsultasi> mondayReservations = repository.findByCaregiverIdAndDay(caregiverId, DayOfWeek.MONDAY);

        assertEquals(1, mondayReservations.size());
        assertEquals(DayOfWeek.MONDAY, mondayReservations.getFirst().getIdSchedule().getDay());

        List<ReservasiKonsultasi> wednesdayReservations = repository.findByCaregiverIdAndDay(caregiverId, DayOfWeek.WEDNESDAY);

        assertEquals(1, wednesdayReservations.size());
        assertEquals(DayOfWeek.WEDNESDAY, wednesdayReservations.getFirst().getIdSchedule().getDay());

        List<ReservasiKonsultasi> fridayReservations = repository.findByCaregiverIdAndDay(caregiverId, DayOfWeek.FRIDAY);

        assertTrue(fridayReservations.isEmpty());
    }

    @Test
    void testNoReservationsFound() {
        UUID caregiverIdInvalid = UUID.randomUUID();

        List<ReservasiKonsultasi> emptyResult1 = repository.findAllByIdPasien("NO_PAT");
        List<ReservasiKonsultasi> emptyResult2 = repository.findByCaregiverId(caregiverIdInvalid);
        List<ReservasiKonsultasi> emptyResult3 = repository.findByCaregiverIdAndStatus(caregiverIdInvalid, StatusReservasiKonsultasi.WAITING);
        List<ReservasiKonsultasi> emptyResult4 = repository.findByCaregiverIdAndDay(caregiverIdInvalid, DayOfWeek.MONDAY);

        assertTrue(emptyResult1.isEmpty());
        assertTrue(emptyResult2.isEmpty());
        assertTrue(emptyResult3.isEmpty());
        assertTrue(emptyResult4.isEmpty());
    }
}