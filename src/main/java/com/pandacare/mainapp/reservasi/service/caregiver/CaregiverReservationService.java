package com.pandacare.mainapp.reservasi.service.caregiver;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface CaregiverReservationService {
    List<ReservasiKonsultasi> getReservationsForCaregiver(UUID caregiverId);
    List<ReservasiKonsultasi> getReservationsByCaregiverAndStatus(UUID caregiverId, StatusReservasiKonsultasi status);
    List<ReservasiKonsultasi> getReservationsByCaregiverAndDay(UUID caregiverId, DayOfWeek day);
    default List<ReservasiKonsultasi> getWaitingReservations(UUID caregiverId) {
        return getReservationsByCaregiverAndStatus(caregiverId, StatusReservasiKonsultasi.WAITING);
    }
    ReservasiKonsultasi approveReservation(UUID reservationId);
    ReservasiKonsultasi rejectReservation(UUID reservationId);
    ReservasiKonsultasi changeSchedule(UUID reservationId, UUID newScheduleId);
    ReservasiKonsultasi getReservationOrThrow(UUID reservationId);
}