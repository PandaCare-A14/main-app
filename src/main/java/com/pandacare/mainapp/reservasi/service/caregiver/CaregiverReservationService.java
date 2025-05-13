package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

import java.time.DayOfWeek;
import java.util.List;

public interface CaregiverReservationService {
    List<ReservasiKonsultasi> getReservationsForCaregiver(String caregiverId);
    List<ReservasiKonsultasi> getReservationsByCaregiverAndStatus(String caregiverId, StatusReservasiKonsultasi status);
    List<ReservasiKonsultasi> getReservationsByCaregiverAndDay(String caregiverId, DayOfWeek day);
    default List<ReservasiKonsultasi> getWaitingReservations(String caregiverId) {
        return getReservationsByCaregiverAndStatus(caregiverId, StatusReservasiKonsultasi.WAITING);
    }
    ReservasiKonsultasi approveReservation(String reservationId);
    ReservasiKonsultasi rejectReservation(String reservationId);
    ReservasiKonsultasi changeSchedule(String reservationId, String newScheduleId);
    ReservasiKonsultasi getReservationOrThrow(String reservationId);
}