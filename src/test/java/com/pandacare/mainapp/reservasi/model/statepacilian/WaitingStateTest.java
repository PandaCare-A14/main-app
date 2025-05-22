import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.statepacilian.WaitingState;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WaitingStateTest {

    @Test
    void edit_shouldUpdateDayAndTime() {
        ReservasiKonsultasi reservasi = new ReservasiKonsultasi();
        WaitingState state = new WaitingState();

        state.edit(reservasi, "TUESDAY", "10:00", "11:00");

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
