package com.pandacare.mainapp.doctor_profile.service.strategy;

public interface WorkScheduleParser {
    ParsedWorkSchedule parse(String workSchedule) throws IllegalArgumentException;
}