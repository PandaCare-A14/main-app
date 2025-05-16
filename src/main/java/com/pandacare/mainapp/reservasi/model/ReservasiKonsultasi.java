package com.pandacare.mainapp.reservasi.model;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
public class ReservasiKonsultasi {
    @Id
    @Column(name = "id")
    private String id;
    private String idDokter;
    private String idPasien;

    @Column(name = "appointment_day")
    private String day;
    private String startTime;
    private String endTime;

    @Enumerated(EnumType.STRING)
    private StatusReservasiKonsultasi statusReservasi;

    private boolean changeReservasi;

    @Column(name = "new_appointment_day")
    private String newDay;
    private String newStartTime;
    private String newEndTime;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}