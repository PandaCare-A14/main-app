package com.pandacare.mainapp.jadwal.model;

import com.pandacare.mainapp.jadwal.enums.StatusJadwalPacilian;
import lombok.Data;

@Data
public class JadwalKonsultasi {
    private String id;
    private String idDokter;
    private String day;
    private String startTime;
    private String endTime;
    private StatusJadwalPacilian statusPacilian;
    private boolean changeSchedule;
    private String newDay;
    private String newStartTime;
    private String newEndTime;
}