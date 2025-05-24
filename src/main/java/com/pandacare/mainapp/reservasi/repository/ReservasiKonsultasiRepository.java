package com.pandacare.mainapp.reservasi.repository;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservasiKonsultasiRepository extends JpaRepository<ReservasiKonsultasi, UUID> {
    @Query("SELECT r FROM ReservasiKonsultasi r WHERE r.idPacilian = :pasienId")
    List<ReservasiKonsultasi> findAllByIdPasien(@Param("pasienId") UUID pasienId);    @Query("SELECT r FROM ReservasiKonsultasi r JOIN r.idSchedule s WHERE s.idCaregiver.id = :caregiverId")
    List<ReservasiKonsultasi> findByCaregiverId(@Param("caregiverId") UUID caregiverId);
    @Query("SELECT r FROM ReservasiKonsultasi r JOIN r.idSchedule s WHERE s.idCaregiver.id = :caregiverId AND r.statusReservasi = :status")
    List<ReservasiKonsultasi> findByCaregiverIdAndStatus(
            @Param("caregiverId") UUID caregiverId,
            @Param("status") StatusReservasiKonsultasi status);
    @Query("SELECT r FROM ReservasiKonsultasi r JOIN r.idSchedule s WHERE s.idCaregiver.id = :caregiverId AND s.day = :day")
    List<ReservasiKonsultasi> findByCaregiverIdAndDay(
            @Param("caregiverId") UUID caregiverId,
            @Param("day") DayOfWeek day);
}
