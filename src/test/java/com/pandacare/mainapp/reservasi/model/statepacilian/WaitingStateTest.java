package com.pandacare.mainapp.reservasi.model.statepacilian;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class WaitingStateTest {

    @Test
    void edit_shouldUpdateScheduleReference() {
        // Setup initial schedule
        CaregiverSchedule initialSchedule = new CaregiverSchedule();
        initialSchedule.setId(UUID.randomUUID());
        initialSchedule.setDay(DayOfWeek.MONDAY);
        initialSchedule.setStartTime(LocalTime.of(9, 0));
        initialSchedule.setEndTime(LocalTime.of(10, 0));

        // Setup proposed schedule that matches the edit parameters
        CaregiverSchedule newSchedule = new CaregiverSchedule();
        newSchedule.setId(UUID.randomUUID());
        newSchedule.setDay(DayOfWeek.TUESDAY);
        newSchedule.setStartTime(LocalTime.parse("10:00"));
        newSchedule.setEndTime(LocalTime.parse("11:00"));
        newSchedule.setStatus(ScheduleStatus.AVAILABLE);

        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setIdSchedule(initialSchedule);

        WaitingState state = new WaitingState();

        state.edit(reservasi, "TUESDAY", "10:00", "11:00");
        reservasi.setIdSchedule(newSchedule);

        assertEquals("TUESDAY", reservasi.getDay());
        assertEquals(LocalTime.parse("10:00"), reservasi.getStartTime());
        assertEquals(LocalTime.parse("11:00"), reservasi.getEndTime());
    }

    @Test
    void acceptChange_shouldThrowException() {
        WaitingState state = new WaitingState();
        assertThrows(IllegalStateException.class, () ->
                state.acceptChange(new ReservasiKonsultasi()));
    }

    @Test
    void rejectChange_shouldThrowException() {
        WaitingState state = new WaitingState();
        assertThrows(IllegalStateException.class, () ->
                state.rejectChange(new ReservasiKonsultasi()));
    }
}