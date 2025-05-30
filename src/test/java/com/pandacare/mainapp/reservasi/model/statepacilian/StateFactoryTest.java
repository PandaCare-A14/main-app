package com.pandacare.mainapp.reservasi.model.statepacilian;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class StateFactoryTest {

    @Test
    void testStateFactoryConstructor() {
        StateFactory factory = new StateFactory();
        assertNotNull(factory);
    }

    @Test
    void testFromMethodWithAllStatusValues() {
        assertEquals(WaitingState.class, StateFactory.from(StatusReservasiKonsultasi.WAITING).getClass());
        assertEquals(ApprovedState.class, StateFactory.from(StatusReservasiKonsultasi.APPROVED).getClass());
        assertEquals(RejectedState.class, StateFactory.from(StatusReservasiKonsultasi.REJECTED).getClass());
        assertEquals(OnReScheduleState.class, StateFactory.from(StatusReservasiKonsultasi.ON_RESCHEDULE).getClass());
    }

    @Test
    void testFromMethodWithUnhandledStatus() {
        assertThrows(IllegalArgumentException.class, () -> StateFactory.from(StatusReservasiKonsultasi.COMPLETED));
    }
}