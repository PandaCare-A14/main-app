package com.pandacare.mainapp.reservasi.model.statepacilian;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;

public class StateFactory {
    public static ReservasiStatePacilian from(StatusReservasiKonsultasi status) {
        return switch (status) {
            case WAITING -> new WaitingState();
            case APPROVED -> new ApprovedState();
            case REJECTED -> new RejectedState();
            case ON_RESCHEDULE -> new OnReScheduleState();
            default -> throw new IllegalArgumentException("Status tidak dikenali: " + status);
        };
    }
}
