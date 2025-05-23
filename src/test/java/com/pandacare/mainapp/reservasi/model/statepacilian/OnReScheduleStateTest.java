import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.statepacilian.OnReScheduleState;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OnReScheduleStateTest {

    @Test
    void acceptChange_shouldApproveChange() {
        CaregiverSchedule currentSchedule = new CaregiverSchedule();
        currentSchedule.setId(UUID.randomUUID());
        currentSchedule.setDay(DayOfWeek.MONDAY);
        currentSchedule.setStartTime(LocalTime.of(9, 0));
        currentSchedule.setEndTime(LocalTime.of(10, 0));

        CaregiverSchedule proposedSchedule = new CaregiverSchedule();
        proposedSchedule.setId(UUID.randomUUID());
        proposedSchedule.setDay(DayOfWeek.THURSDAY);
        proposedSchedule.setStartTime(LocalTime.of(14, 0));
        proposedSchedule.setEndTime(LocalTime.of(15, 0));

        // Set up reservation
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setIdSchedule(currentSchedule);
        reservasi.setProposedSchedule(proposedSchedule);
        reservasi.setStatusReservasi(StatusReservasiKonsultasi.ON_RESCHEDULE);

        OnReScheduleState state = new OnReScheduleState();
        state.acceptChange(reservasi);

        // Check that the proposed schedule is now the active schedule
        assertEquals("THURSDAY", reservasi.getDay());
        assertEquals(LocalTime.of(14, 0), reservasi.getStartTime());
        assertEquals(LocalTime.of(15, 0), reservasi.getEndTime());
        assertEquals(StatusReservasiKonsultasi.WAITING, reservasi.getStatusReservasi());
        assertNull(reservasi.getProposedSchedule());
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
