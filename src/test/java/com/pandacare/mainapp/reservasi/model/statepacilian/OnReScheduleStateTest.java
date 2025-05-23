package com.pandacare.mainapp.reservasi.model.statepacilian;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OnReScheduleStateTest {

    @Test
    void acceptChange_shouldApproveChange() {
        CaregiverSchedule proposedSchedule = new CaregiverSchedule();
        proposedSchedule.setId(UUID.randomUUID());
        proposedSchedule.setDay(DayOfWeek.THURSDAY);
        proposedSchedule.setStartTime(LocalTime.of(14, 0));
        proposedSchedule.setEndTime(LocalTime.of(15, 0));

        // Set up reservation with the proposed schedule already set
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setIdSchedule(proposedSchedule); // Schedule is already updated
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.ON_RESCHEDULE);

        OnReScheduleState state = new OnReScheduleState();
        state.acceptChange(reservasi);

        // Just check the status is updated to APPROVED
        // The schedule should already be THURSDAY as set above
        assertEquals(StatusReservasiKonsultasi.APPROVED, reservasi.getStatusReservasi());
        assertEquals(DayOfWeek.THURSDAY.toString(), reservasi.getDay());
        assertEquals(LocalTime.of(14, 0), reservasi.getStartTime());
        assertEquals(LocalTime.of(15, 0), reservasi.getEndTime());
    }

    @Test
    void rejectChange_shouldRejectChange() {
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        OnReScheduleState state = new OnReScheduleState();

        state.rejectChange(reservasi);

        assertEquals(StatusReservasiKonsultasi.REJECTED, reservasi.getStatusReservasi());
    }

    @Test
    void edit_shouldThrowException() {
        OnReScheduleState state = new OnReScheduleState();
        assertThrows(IllegalStateException.class, () ->
                state.edit(new ReservasiKonsultasi(), "TUESDAY", "10:00", "11:00")
        );
    }
}
