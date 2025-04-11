package com.pandacare.mainapp.konsultasi_dokter.service.impl;

import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.model.StatusJadwalDokter;
import com.pandacare.mainapp.konsultasi_dokter.repository.JadwalDokterRepository;
import com.pandacare.mainapp.konsultasi_dokter.service.JadwalDokterService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JadwalDokterServiceImpl implements JadwalDokterService {
    private final JadwalDokterRepository jadwalRepository;

    public JadwalDokterServiceImpl(JadwalDokterRepository jadwalRepository) {
        this.jadwalRepository = jadwalRepository;
    }

    @Override
    public void createJadwal(Long idDokter, String day, String startTime, String endTime) {
        JadwalKonsultasi jadwal = new JadwalKonsultasi();
        jadwal.setId(UUID.randomUUID().toString());
        jadwal.setIdDokter(idDokter);
        jadwal.setDay(day);
        jadwal.setStartTime(startTime);
        jadwal.setEndTime(endTime);
        jadwal.setStatusDokter(StatusJadwalDokter.AVAILABLE);

        jadwalRepository.save(jadwal);
    }

    @Override
    public boolean changeJadwal(String idJadwal, String day, String startTime, String endTime) {
        JadwalKonsultasi jadwal = jadwalRepository.findById(idJadwal);
        if (jadwal == null)
            return false;

        jadwal.setDay(day);
        jadwal.setStartTime(startTime);
        jadwal.setEndTime(endTime);
        jadwalRepository.save(jadwal);
        return true;
    }

    @Override
    public boolean approveJadwal(String idJadwal) {
        JadwalKonsultasi jadwal = jadwalRepository.findById(idJadwal);
        if (jadwal == null)
            return false;

        jadwal.setStatusDokter(StatusJadwalDokter.APPROVED);
        jadwalRepository.save(jadwal);
        return true;
    }

    @Override
    public boolean rejectJadwal(String idJadwal) {
        JadwalKonsultasi jadwal = jadwalRepository.findById(idJadwal);
        if (jadwal == null)
            return false;

        jadwal.setStatusDokter(StatusJadwalDokter.REJECTED);
        jadwalRepository.save(jadwal);
        return true;
    }
}
