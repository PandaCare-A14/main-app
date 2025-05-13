package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
public class CaregiverReservationServiceImpl implements CaregiverReservationService {
    private final ReservasiKonsultasiRepository reservasiRepository;
    private final ScheduleService scheduleService;

    @Autowired
    public CaregiverReservationServiceImpl(
            ReservasiKonsultasiRepository reservasiRepository,
            ScheduleService scheduleService) {
        this.reservasiRepository = reservasiRepository;
        this.scheduleService = scheduleService;
    }

    @Override
    public List<ReservasiKonsultasi> getReservationsForCaregiver(String caregiverId) {
        return reservasiRepository.findByCaregiverId(caregiverId);
    }

    @Override
    public List<ReservasiKonsultasi> getReservationsByCaregiverAndStatus(String caregiverId, StatusReservasiKonsultasi status) {
        return reservasiRepository.findByCaregiverIdAndStatus(caregiverId, status);
    }

    @Override
    public List<ReservasiKonsultasi> getReservationsByCaregiverAndDay(String caregiverId, DayOfWeek day) {
        return reservasiRepository.findByCaregiverIdAndDay(caregiverId, day);
    }

    @Override
    @Transactional
    public ReservasiKonsultasi approveReservation(String reservationId) {
        ReservasiKonsultasi reservation = getReservationOrThrow(reservationId);

        validateReservationStatusForAction(reservation, "approve");

        CaregiverSchedule schedule = reservation.getIdSchedule();
        if (schedule != null) {
            scheduleService.updateScheduleStatus(schedule, ScheduleStatus.UNAVAILABLE);
        }

        reservation.approve();

        return reservasiRepository.save(reservation);
    }

    @Override
    @Transactional
    public ReservasiKonsultasi rejectReservation(String reservationId) {
        ReservasiKonsultasi reservation = getReservationOrThrow(reservationId);
        validateReservationStatusForAction(reservation, "reject");

        CaregiverSchedule schedule = reservation.getIdSchedule();
        if (schedule != null) {
            scheduleService.updateScheduleStatus(schedule, ScheduleStatus.AVAILABLE);
        }

        reservation.reject();

        return reservasiRepository.save(reservation);
    }

    @Override
    @Transactional
    public ReservasiKonsultasi changeSchedule(String reservationId, String newScheduleId) {
        ReservasiKonsultasi reservation = getReservationOrThrow(reservationId);
        validateReservationStatusForAction(reservation, "change schedule");

        CaregiverSchedule oldSchedule = reservation.getIdSchedule();
        if (oldSchedule != null) {
            scheduleService.updateScheduleStatus(oldSchedule, ScheduleStatus.AVAILABLE);
            reservation.setIdSchedule(null);
            reservasiRepository.saveAndFlush(reservation);
        }
        CaregiverSchedule newSchedule = scheduleService.getById(newScheduleId);
        scheduleService.updateScheduleStatus(newSchedule, ScheduleStatus.UNAVAILABLE);
        reservation.ensureStateInitialized(scheduleService);
        reservation.handleChangeSchedule(newScheduleId);

        return reservasiRepository.save(reservation);
    }

    private void validateReservationStatusForAction(ReservasiKonsultasi reservation, String actionName) {
        if (reservation.getStatusReservasi() != StatusReservasiKonsultasi.WAITING &&
                reservation.getStatusReservasi() != StatusReservasiKonsultasi.ON_RESCHEDULE) {
            throw new IllegalStateException("Cannot " + actionName + " reservation with status: " + reservation.getStatusReservasi());
        }
    }

    @Override
    public ReservasiKonsultasi getReservationOrThrow(String reservationId) {
        return reservasiRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("Reservation with ID " + reservationId + " not found"));
    }
}