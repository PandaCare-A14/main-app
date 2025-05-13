package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class CreateManualStrategy implements CreateScheduleStrategy {
    private static final int MAX_WEEKS = 12;

    @Override
    public CaregiverSchedule create(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        validateBasicParams(idCaregiver, day, startTime, endTime);

        LocalDate nextOccurrence = LocalDate.now().with(TemporalAdjusters.nextOrSame(day));

        return createWithDate(idCaregiver, day, nextOccurrence, startTime, endTime);
    }

    @Override
    public CaregiverSchedule createWithDate(String idCaregiver, DayOfWeek day, LocalDate date, LocalTime startTime, LocalTime endTime) {
        validateBasicParams(idCaregiver, day, startTime, endTime);

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDay(day);
        schedule.setDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        return schedule;
    }

    @Override
    public List<CaregiverSchedule> createRepeated(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime, int recurrenceCount) {
        validateWeeks(recurrenceCount);
        validateBasicParams(idCaregiver, day, startTime, endTime);

        List<CaregiverSchedule> scheduleList = new ArrayList<>();
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(day));

        for (int i = 0; i < recurrenceCount; i++) {
            CaregiverSchedule schedule = createWithDate(idCaregiver, day, startDate, startTime, endTime);
            scheduleList.add(schedule);
            startDate = startDate.plusWeeks(1);
        }

        return scheduleList;
    }

    protected void validateBasicParams(String idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        if (idCaregiver == null || idCaregiver.trim().isEmpty() || day == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Field can't be empty.");
        }
    }

    private void validateWeeks(int weeks) {
        if (weeks <= 0) {
            throw new IllegalArgumentException("Number of weeks must be greater than 0");
        }

        if (weeks > MAX_WEEKS) {
            throw new IllegalArgumentException("Maximum allowed number of weeks is " + MAX_WEEKS);
        }
    }
}