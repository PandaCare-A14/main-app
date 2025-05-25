package com.pandacare.mainapp.reservasi.repository;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
    private ReservasiKonsultasiRepository repository;    @Test
    void testFindAllByIdPasien() {
        UUID pasienId = UUID.randomUUID();
        
        // Create and persist a Caregiver entity first
        Caregiver caregiver = new Caregiver("Dr. Test", "1234567890", "08123456789", "Test Address", "General Practice");
        caregiver.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        entityManager.persistAndFlush(caregiver);

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setIdCaregiver(caregiver);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedule.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(schedule);

        ReservasiKonsultasi reservation = new ReservasiKonsultasi();
        reservation.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        reservation.setIdPacilian(pasienId);
        reservation.setIdSchedule(schedule);
        reservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        entityManager.persist(reservation);

        entityManager.flush();
        entityManager.clear();

        List<ReservasiKonsultasi> found = repository.findAllByIdPasien(pasienId);

        assertEquals(1, found.size());
        assertEquals(pasienId, found.get(0).getIdPacilian());
    }    @Test
    void testFindByCaregiverId() {
        // Create and persist a Caregiver entity first
        Caregiver caregiver = new Caregiver("Dr. Test 2", "1234567891", "08123456790", "Test Address 2", "Cardiology");
        caregiver.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        entityManager.persistAndFlush(caregiver);

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setIdCaregiver(caregiver);
        schedule.setDay(DayOfWeek.TUESDAY);
        schedule.setStartTime(LocalTime.of(14, 0));
        schedule.setEndTime(LocalTime.of(15, 0));
        schedule.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(schedule);

        ReservasiKonsultasi reservation = new ReservasiKonsultasi();
        reservation.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        reservation.setIdPacilian(UUID.randomUUID());
        reservation.setIdSchedule(schedule);
        reservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        entityManager.persist(reservation);

        entityManager.flush();
        entityManager.clear();

        List<ReservasiKonsultasi> found = repository.findByCaregiverId(caregiver.getId());

        assertEquals(1, found.size());
        assertEquals(caregiver.getId(), found.get(0).getIdSchedule().getIdCaregiver());
    }    @Test
    void testFindByCaregiverIdAndStatus() {
        // Create and persist a Caregiver entity first
        Caregiver caregiver = new Caregiver("Dr. Test 3", "1234567892", "08123456791", "Test Address 3", "Neurology");
        caregiver.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        entityManager.persistAndFlush(caregiver);

        CaregiverSchedule schedule1 = new CaregiverSchedule();
        schedule1.setIdCaregiver(caregiver);
        schedule1.setDay(DayOfWeek.MONDAY);
        schedule1.setStartTime(LocalTime.of(10, 0));
        schedule1.setEndTime(LocalTime.of(11, 0));
        schedule1.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(schedule1);

        CaregiverSchedule schedule2 = new CaregiverSchedule();
        schedule2.setIdCaregiver(caregiver);        schedule2.setDay(DayOfWeek.WEDNESDAY);
        schedule2.setStartTime(LocalTime.of(11, 0));
        schedule2.setEndTime(LocalTime.of(12, 0));
        schedule2.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(schedule2);

        ReservasiKonsultasi reservation1 = new ReservasiKonsultasi();
        reservation1.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        reservation1.setIdPacilian(UUID.randomUUID());
        reservation1.setIdSchedule(schedule1);
        reservation1.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        entityManager.persist(reservation1);

        ReservasiKonsultasi reservation2 = new ReservasiKonsultasi();
        reservation2.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        reservation2.setIdPacilian(UUID.randomUUID());
        reservation2.setIdSchedule(schedule2);
        reservation2.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        entityManager.persist(reservation2);

        entityManager.flush();
        entityManager.clear();

        List<ReservasiKonsultasi> waitingReservations = repository.findByCaregiverIdAndStatus(caregiver.getId(), StatusReservasiKonsultasi.WAITING);

        assertEquals(1, waitingReservations.size());
        assertEquals(StatusReservasiKonsultasi.WAITING, waitingReservations.get(0).getStatusReservasi());

        List<ReservasiKonsultasi> approvedReservations = repository.findByCaregiverIdAndStatus(caregiver.getId(), StatusReservasiKonsultasi.APPROVED);

        assertEquals(1, approvedReservations.size());
        assertEquals(StatusReservasiKonsultasi.APPROVED, approvedReservations.get(0).getStatusReservasi());
    }    @Test
    void testFindByCaregiverIdAndDay() {
        // Create and persist a Caregiver entity first
        Caregiver caregiver = new Caregiver("Dr. Test 4", "1234567893", "08123456792", "Test Address 4", "Pediatrics");
        caregiver.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        entityManager.persistAndFlush(caregiver);

        CaregiverSchedule mondaySchedule = new CaregiverSchedule();
        mondaySchedule.setIdCaregiver(caregiver);
        mondaySchedule.setDay(DayOfWeek.MONDAY);
        mondaySchedule.setStartTime(LocalTime.of(9, 0));
        mondaySchedule.setEndTime(LocalTime.of(10, 0));
        mondaySchedule.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(mondaySchedule);

        CaregiverSchedule wednesdaySchedule = new CaregiverSchedule();
        wednesdaySchedule.setIdCaregiver(caregiver);
        wednesdaySchedule.setDay(DayOfWeek.WEDNESDAY);
        wednesdaySchedule.setStartTime(LocalTime.of(14, 0));
        wednesdaySchedule.setEndTime(LocalTime.of(15, 0));
        wednesdaySchedule.setStatus(ScheduleStatus.AVAILABLE);
        entityManager.persist(wednesdaySchedule);

        ReservasiKonsultasi mondayReservation = new ReservasiKonsultasi();
        mondayReservation.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        mondayReservation.setIdPacilian(UUID.randomUUID());
        mondayReservation.setIdSchedule(mondaySchedule);
        mondayReservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        entityManager.persist(mondayReservation);

        ReservasiKonsultasi wednesdayReservation = new ReservasiKonsultasi();
        wednesdayReservation.setId(UUID.randomUUID()); // Set ID manually since no auto-generation
        wednesdayReservation.setIdPacilian(UUID.randomUUID());
        wednesdayReservation.setIdSchedule(wednesdaySchedule);
        wednesdayReservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        entityManager.persist(wednesdayReservation);

        entityManager.flush();
        entityManager.clear();

        List<ReservasiKonsultasi> mondayReservations = repository.findByCaregiverIdAndDay(caregiver.getId(), DayOfWeek.MONDAY);

        assertEquals(1, mondayReservations.size());
        assertEquals(DayOfWeek.MONDAY, mondayReservations.get(0).getIdSchedule().getDay());

        List<ReservasiKonsultasi> wednesdayReservations = repository.findByCaregiverIdAndDay(caregiver.getId(), DayOfWeek.WEDNESDAY);

        assertEquals(1, wednesdayReservations.size());
        assertEquals(DayOfWeek.WEDNESDAY, wednesdayReservations.get(0).getIdSchedule().getDay());

        List<ReservasiKonsultasi> fridayReservations = repository.findByCaregiverIdAndDay(caregiver.getId(), DayOfWeek.FRIDAY);

        assertTrue(fridayReservations.isEmpty());
    }    @Test
    void testNoReservationsFound() {
        UUID nonExistentCaregiverId = UUID.randomUUID();
        UUID nonExistentPasienId = UUID.randomUUID();

        List<ReservasiKonsultasi> emptyResult1 = repository.findAllByIdPasien(nonExistentPasienId);
        List<ReservasiKonsultasi> emptyResult2 = repository.findByCaregiverId(nonExistentCaregiverId);
        List<ReservasiKonsultasi> emptyResult3 = repository.findByCaregiverIdAndStatus(nonExistentCaregiverId, StatusReservasiKonsultasi.WAITING);
        List<ReservasiKonsultasi> emptyResult4 = repository.findByCaregiverIdAndDay(nonExistentCaregiverId, DayOfWeek.MONDAY);

        assertTrue(emptyResult1.isEmpty());
        assertTrue(emptyResult2.isEmpty());
        assertTrue(emptyResult3.isEmpty());
        assertTrue(emptyResult4.isEmpty());
    }
}