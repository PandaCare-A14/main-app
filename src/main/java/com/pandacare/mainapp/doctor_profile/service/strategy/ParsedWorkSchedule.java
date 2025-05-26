package com.pandacare.mainapp.doctor_profile.service.strategy;

import java.time.DayOfWeek;
import java.time.LocalTime;

// Data class to hold parsed results
public record ParsedWorkSchedule(DayOfWeek day, LocalTime startTime, LocalTime endTime) {}
