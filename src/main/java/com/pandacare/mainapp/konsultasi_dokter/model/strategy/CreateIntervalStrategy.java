package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateIntervalStrategy implements CreateScheduleStrategy {
    private static final int DURATION_MINUTES = 30;
    private static final int MAX_WEEKS = 12;

    @Override
    public CaregiverSchedule create(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        validateTimeParams(startTime, endTime);

        LocalDate nextOccurrence = LocalDate.now().with(TemporalAdjusters.nextOrSame(day));

        return createWithDate(idCaregiver, day, nextOccurrence, startTime, endTime);
    }

    @Override
    public CaregiverSchedule createWithDate(UUID idCaregiver, DayOfWeek day, LocalDate date, LocalTime startTime, LocalTime endTime) {
        validateTimeParams(startTime, endTime);

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDay(day);
        schedule.setDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        return schedule;
    }

    public List<CaregiverSchedule> createMultipleSlots(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        return createMultipleSlotsInternal(idCaregiver, day, null, startTime, endTime);
    }

    public List<CaregiverSchedule> createMultipleSlotsWithDate(UUID idCaregiver, DayOfWeek day, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return createMultipleSlotsInternal(idCaregiver, day, date, startTime, endTime);
    }

    private List<CaregiverSchedule> createMultipleSlotsInternal(UUID idCaregiver, DayOfWeek day, LocalDate date, LocalTime startTime, LocalTime endTime) {
        validateTimeParams(startTime, endTime);
        validateTimeInterval(startTime, endTime);

        List<CaregiverSchedule> scheduleList = new ArrayList<>();
        LocalTime currentStartTime = startTime;

        while (currentStartTime.plus(Duration.ofMinutes(DURATION_MINUTES)).isBefore(endTime) ||
                currentStartTime.plus(Duration.ofMinutes(DURATION_MINUTES)).equals(endTime)) {

            LocalTime slotEndTime = currentStartTime.plusMinutes(DURATION_MINUTES);
            CaregiverSchedule schedule;

            if (date != null) {
                schedule = createWithDate(idCaregiver, day, date, currentStartTime, slotEndTime);
            } else {
                schedule = create(idCaregiver, day, currentStartTime, slotEndTime);
            }

            scheduleList.add(schedule);
            currentStartTime = currentStartTime.plusMinutes(DURATION_MINUTES);
        }

        return scheduleList;
    }

    @Override
    public List<CaregiverSchedule> createRepeated(UUID idCaregiver, DayOfWeek day, LocalTime startTime, LocalTime endTime, int recurrenceCount) {
        validateTimeParams(startTime, endTime);
        validateWeeks(recurrenceCount);

        List<CaregiverSchedule> allSchedules = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(day));

        for (int i = 0; i < recurrenceCount; i++) {
            List<CaregiverSchedule> slotsForThisDate = createMultipleSlotsWithDate(
                    idCaregiver, day, currentDate, startTime, endTime);
            allSchedules.addAll(slotsForThisDate);
            currentDate = currentDate.plusWeeks(1);
        }

        return allSchedules;
    }

    protected void validateTimeParams(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time can't be null.");
        }

        if (startTime.equals(endTime)) {
            throw new IllegalArgumentException("Start time can't be equal to end time.");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time can't be set after end time.");
        }
    }

    protected void validateTimeInterval(LocalTime startTime, LocalTime endTime) {
        int totalMinutes = endTime.toSecondOfDay() / 60 - startTime.toSecondOfDay() / 60;
        if (totalMinutes % DURATION_MINUTES != 0) {
            throw new IllegalArgumentException("Time is not valid. The interval must be a multiple of " + DURATION_MINUTES + " minutes.");
        }
    }

    private void validateWeeks(int weeks) {
        if (weeks <= 0) {
            throw new IllegalArgumentException("Minimum week(s) added is 1.");
        }

        if (weeks > MAX_WEEKS) {
            throw new IllegalArgumentException("Maximum allowed number of weeks is " + MAX_WEEKS);
        }
    }
}