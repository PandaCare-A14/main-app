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
