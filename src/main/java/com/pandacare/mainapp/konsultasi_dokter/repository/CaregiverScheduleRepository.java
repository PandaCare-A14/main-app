package com.pandacare.mainapp.konsultasi_dokter.repository;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaregiverScheduleRepository extends JpaRepository<CaregiverSchedule, UUID> {
    
    @Query("SELECT s FROM CaregiverSchedule s WHERE s.idCaregiver.id = :caregiverId")
    List<CaregiverSchedule> findByIdCaregiver(@Param("caregiverId") UUID idCaregiver);
    
    @Query("SELECT s FROM CaregiverSchedule s WHERE s.idCaregiver.id = :caregiverId AND s.id = :scheduleId")
    Optional<CaregiverSchedule> findByIdCaregiverAndIdSchedule(
            @Param("caregiverId") UUID caregiverId,
            @Param("scheduleId") UUID scheduleId);
    
    @Query("SELECT s FROM CaregiverSchedule s WHERE s.idCaregiver.id = :caregiverId AND s.status = :status")
    List<CaregiverSchedule> findByIdCaregiverAndStatus(
            @Param("caregiverId") UUID idCaregiver, 
            @Param("status") ScheduleStatus status);
    
    @Query("SELECT s FROM CaregiverSchedule s WHERE s.idCaregiver.id = :caregiverId AND s.day = :day")
    List<CaregiverSchedule> findByIdCaregiverAndDay(
            @Param("caregiverId") UUID idCaregiver,
            @Param("day") DayOfWeek day);
    
    @Query("SELECT COUNT(c) > 0 FROM CaregiverSchedule c " + 
            "WHERE c.idCaregiver.id = :caregiverId " +
            "AND c.day = :day " + 
            "AND c.startTime < :endTime " + 
            "AND c.endTime > :startTime")
    boolean existsOverlappingSchedule(
            @Param("caregiverId") UUID caregiverId,
            @Param("day") DayOfWeek day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
    
    @Query("SELECT COUNT(s) > 0 FROM CaregiverSchedule s " +
            "WHERE s.idCaregiver.id = :idCaregiver AND s.day = :day AND s.date = :date AND s.status != 'INACTIVE' AND " +
            "((s.startTime <= :endTime AND s.endTime >= :startTime))")
    boolean existsOverlappingScheduleWithDate(
            @Param("idCaregiver") UUID idCaregiver,
            @Param("day") DayOfWeek day,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}