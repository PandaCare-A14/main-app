package com.pandacare.mainapp.reservasi.service;

import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ReservasiKonsultasiService {
    ReservasiKonsultasi requestReservasi(UUID idSchedule, UUID idPacilian, String pacilianNote);
    ReservasiKonsultasi editReservasi(UUID id, UUID newScheduleId);
    ReservasiKonsultasi acceptChangeReservasi(UUID id);
    void rejectChangeReservasi(UUID id);
    ReservasiKonsultasi findById(UUID id);
    void deleteById(UUID id);
    CompletableFuture<List<ReservasiKonsultasi>> findAllByPacilian(UUID idPacilian);
}