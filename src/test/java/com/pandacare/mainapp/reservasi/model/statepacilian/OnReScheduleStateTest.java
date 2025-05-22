import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.statepacilian.OnReScheduleState;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class OnReScheduleStateTest {

    @Test
    void acceptChange_shouldApproveChange() {
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        reservasi.setNewDay("THURSDAY");
        reservasi.setNewStartTime(LocalTime.of(14, 0));
        reservasi.setNewEndTime(LocalTime.of(15, 0));

        OnReScheduleState state = new OnReScheduleState();
        state.acceptChange(reservasi);

        assertEquals("THURSDAY", reservasi.getDay());
        assertEquals(LocalTime.of(14, 0), reservasi.getStartTime());
        assertEquals(LocalTime.of(15, 0), reservasi.getEndTime());
        assertEquals(StatusReservasiKonsultasi.APPROVED, reservasi.getStatusReservasi());
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
