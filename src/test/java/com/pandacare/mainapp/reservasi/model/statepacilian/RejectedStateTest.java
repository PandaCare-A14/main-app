import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.statePacilian.RejectedState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RejectedStateTest {

    @Test
    void edit_shouldThrowException() {
        RejectedState state = new RejectedState();
        assertThrows(IllegalStateException.class, () ->
                state.edit(new ReservasiKonsultasi(), "MONDAY", "09:00", "10:00")
        );
    }

    @Test
    void acceptChange_shouldThrowException() {
        RejectedState state = new RejectedState();
        assertThrows(IllegalStateException.class, () ->
                state.acceptChange(new ReservasiKonsultasi())
        );
    }

    @Test
    void rejectChange_shouldThrowException() {
        RejectedState state = new RejectedState();
        assertThrows(IllegalStateException.class, () ->
                state.rejectChange(new ReservasiKonsultasi())
        );
    }
}
