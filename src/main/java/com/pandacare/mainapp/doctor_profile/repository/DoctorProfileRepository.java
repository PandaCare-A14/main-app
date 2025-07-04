package com.pandacare.mainapp.doctor_profile.repository;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.authentication.repository.CaregiverRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DoctorProfileRepository extends CaregiverRepository {
    List<Caregiver> findByNameContainingIgnoreCaseAndSpecialityContainingIgnoreCase(
            @Param("name") String name,
            @Param("speciality") String speciality);
    List<Caregiver> findByNameContainingIgnoreCase(String name);
    List<Caregiver> findBySpecialityContainingIgnoreCase(String speciality);    @Query("SELECT DISTINCT c FROM Caregiver c " +
            "JOIN c.workingSchedules s " +
            "WHERE s.day = :day " +
            "AND s.status = 'AVAILABLE' " +
            "AND s.startTime < :searchEnd " +
            "AND s.endTime > :searchStart")
    List<Caregiver> findByWorkingSchedulesAvailable(
            @Param("day") DayOfWeek day,
            @Param("searchStart") LocalTime searchStart,
            @Param("searchEnd") LocalTime searchEnd);
}