package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CreateIntervalStrategy implements CreateScheduleStrategy {
    private static final int DURATION_MINUTES = 30;

    @Override
    public CaregiverSchedule create(String idCaregiver, LocalDate date, LocalTime startTime, LocalTime endTime) {
        LocalTime calculatedEndTime = startTime.plusMinutes(DURATION_MINUTES);

        if (calculatedEndTime.isAfter(endTime)) {
            calculatedEndTime = endTime;
        }

        return createScheduleWithStartTime(idCaregiver, date, startTime, calculatedEndTime);
    }

    private CaregiverSchedule createScheduleWithStartTime(String idCaregiver, LocalDate date,
                                                        LocalTime startTime, LocalTime endTime) {
        if (startTime.equals(endTime)) {
            throw new IllegalArgumentException("Start time can't be equal to end time.");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time can't be set after end time.");
        }

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        return schedule;
    }

    public List<CaregiverSchedule> createMultipleSlots(String idCaregiver, LocalDate date,
                                                       LocalTime startTime, LocalTime endTime) {
        if (startTime.equals(endTime)) {
            throw new IllegalArgumentException("Start time can't be equal to end time.");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time can't be set after end time.");
        }

        int totalMinutes = endTime.toSecondOfDay() / 60 - startTime.toSecondOfDay() / 60;
        if (totalMinutes % DURATION_MINUTES != 0) {
            throw new IllegalArgumentException("Time is not valid.");
        }

        List<CaregiverSchedule> scheduleList = new ArrayList<>();

        LocalTime currentStartTime = startTime;
        while (currentStartTime.plus(Duration.ofMinutes(DURATION_MINUTES)).isBefore(endTime) ||
                currentStartTime.plus(Duration.ofMinutes(DURATION_MINUTES)).equals(endTime)) {

            LocalTime slotEndTime = currentStartTime.plusMinutes(DURATION_MINUTES);
            CaregiverSchedule schedule = createScheduleWithStartTime(idCaregiver, date, currentStartTime, slotEndTime);
            scheduleList.add(schedule);

            currentStartTime = currentStartTime.plusMinutes(DURATION_MINUTES);
        }
        return scheduleList;
    }
}