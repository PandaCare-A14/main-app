package com.pandacare.mainapp.doctor_profile.repository;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, String> {

    List<DoctorProfile> findByNameContainingIgnoreCase(String name);
    List<DoctorProfile> findBySpecialityContainingIgnoreCase(String speciality);

    @Query("SELECT d FROM DoctorProfile d WHERE " +
            "FUNCTION('JSON_EXTRACT', d.workSchedule, :day) IS NOT NULL AND " +
            "( " +
            "  (FUNCTION('PARSE_TIME', FUNCTION('JSON_EXTRACT', d.workSchedule, :day, 'start')) <= :searchStart AND " +
            "  (FUNCTION('PARSE_TIME', FUNCTION('JSON_EXTRACT', d.workSchedule, :day, 'end')) >= :searchStart) AND " +
            "  FUNCTION('TIMESTAMPDIFF', MINUTE, :searchStart, FUNCTION('PARSE_TIME', FUNCTION('JSON_EXTRACT', d.workSchedule, :day, 'end'))) >= 30 " +
            ") OR ( " +
            "  (FUNCTION('PARSE_TIME', FUNCTION('JSON_EXTRACT', d.workSchedule, :day, 'start')) <= :searchEnd AND " +
            "  (FUNCTION('PARSE_TIME', FUNCTION('JSON_EXTRACT', d.workSchedule, :day, 'end')) >= :searchEnd) AND " +
            "  FUNCTION('TIMESTAMPDIFF', MINUTE, FUNCTION('PARSE_TIME', FUNCTION('JSON_EXTRACT', d.workSchedule, :day, 'start')), :searchEnd) >= 30 " +
            ")")
    List<DoctorProfile> findByWorkScheduleAvailable(
        @Param("day") String day,
        @Param("searchStart") LocalTime searchStart,
        @Param("searchEnd") LocalTime searchEnd);
}