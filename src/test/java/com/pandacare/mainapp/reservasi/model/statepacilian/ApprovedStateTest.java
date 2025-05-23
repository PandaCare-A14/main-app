import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.statePacilian.ApprovedState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApprovedStateTest {

    @Test
    void edit_shouldThrowException() {
        ApprovedState state = new ApprovedState();
        assertThrows(IllegalStateException.class, () ->
                state.edit(new ReservasiKonsultasi(), "MONDAY", "09:00", "10:00")
        );
    }

    @Test
    void acceptChange_shouldThrowException() {
        ApprovedState state = new ApprovedState();
        assertThrows(IllegalStateException.class, () ->
                state.acceptChange(new ReservasiKonsultasi())
        );
    }

    @Test
    void rejectChange_shouldThrowException() {
        ApprovedState state = new ApprovedState();
        assertThrows(IllegalStateException.class, () ->
                state.rejectChange(new ReservasiKonsultasi())
        );
    }
}
