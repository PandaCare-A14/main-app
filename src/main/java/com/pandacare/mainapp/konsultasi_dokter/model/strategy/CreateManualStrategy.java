package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.model.state.AvailableState;

import java.time.LocalTime;
import java.time.LocalDate;

public class CreateManualStrategy implements CreateScheduleStrategy {

    @Override
    public CaregiverSchedule create(String idCaregiver, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (idCaregiver == null || idCaregiver.isBlank()
                || date == null
                || startTime == null
                || endTime == null) {
            throw new IllegalArgumentException("Field can't be empty.");
        }

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setState(new AvailableState());
        schedule.setChangeSchedule(false);

        return schedule;
    }
}