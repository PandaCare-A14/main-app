package com.pandacare.mainapp.reservasi.repository;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;

public interface ReservasiKonsultasiRepository extends JpaRepository<ReservasiKonsultasi, String> {
    @Query("SELECT r FROM ReservasiKonsultasi r WHERE r.idPacilian = :pasienId")
    List<ReservasiKonsultasi> findAllByIdPasien(@Param("pasienId") String pasienId);
    @Query("SELECT r FROM ReservasiKonsultasi r JOIN r.idSchedule s WHERE s.idCaregiver = :caregiverId")
    List<ReservasiKonsultasi> findByCaregiverId(@Param("caregiverId") String caregiverId);
    @Query("SELECT r FROM ReservasiKonsultasi r JOIN r.idSchedule s WHERE s.idCaregiver = :caregiverId AND r.statusReservasi = :status")
    List<ReservasiKonsultasi> findByCaregiverIdAndStatus(
            @Param("caregiverId") String caregiverId,
            @Param("status") StatusReservasiKonsultasi status);
    @Query("SELECT r FROM ReservasiKonsultasi r JOIN r.idSchedule s WHERE s.idCaregiver = :caregiverId AND s.day = :day")
    List<ReservasiKonsultasi> findByCaregiverIdAndDay(
            @Param("caregiverId") String caregiverId,
            @Param("day") DayOfWeek day);
}
