package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateIntervalStrategy implements CreateJadwalStrategy {
    private static final int DURATION_MINUTES = 30;

    @Override
    public JadwalKonsultasi create(String idDokter, LocalDate date, LocalTime startTime, LocalTime endTime) {
        LocalTime calculatedEndTime = startTime.plusMinutes(DURATION_MINUTES);

        if (calculatedEndTime.isAfter(endTime)) {
            calculatedEndTime = endTime;
        }

        return createJadwalWithStartTime(idDokter, date, startTime, calculatedEndTime);
    }

    private JadwalKonsultasi createJadwalWithStartTime(String idDokter, LocalDate date,
                                                       LocalTime startTime, LocalTime endTime) {
        if (startTime.equals(endTime)) {
            throw new IllegalArgumentException("Waktu mulai tidak boleh sama dengan waktu selesai");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Waktu mulai tidak boleh setelah waktu selesai");
        }

        JadwalKonsultasi jadwal = new JadwalKonsultasi();
        jadwal.setId(UUID.randomUUID().toString());
        jadwal.setIdDokter(idDokter);
        jadwal.setDate(date);
        jadwal.setStartTime(startTime);
        jadwal.setEndTime(endTime);
        return jadwal;
    }

    public List<JadwalKonsultasi> createMultipleSlots(String idDokter, LocalDate date,
                                                      LocalTime startTime, LocalTime endTime) {
        if (startTime.equals(endTime)) {
            throw new IllegalArgumentException("Waktu mulai tidak boleh sama dengan waktu selesai");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Waktu mulai tidak boleh setelah waktu selesai");
        }

        int totalMinutes = endTime.toSecondOfDay() / 60 - startTime.toSecondOfDay() / 60;
        if (totalMinutes % DURATION_MINUTES != 0) {
            throw new IllegalArgumentException("Rentang waktu harus habis dibagi " + DURATION_MINUTES + " menit");
        }

        List<JadwalKonsultasi> jadwalList = new ArrayList<>();

        LocalTime currentStartTime = startTime;
        while (currentStartTime.plus(Duration.ofMinutes(DURATION_MINUTES)).isBefore(endTime) ||
                currentStartTime.plus(Duration.ofMinutes(DURATION_MINUTES)).equals(endTime)) {

            LocalTime slotEndTime = currentStartTime.plusMinutes(DURATION_MINUTES);
            JadwalKonsultasi jadwal = createJadwalWithStartTime(idDokter, date, currentStartTime, slotEndTime);
            jadwalList.add(jadwal);

            currentStartTime = currentStartTime.plusMinutes(DURATION_MINUTES);
        }
        return jadwalList;
    }
}