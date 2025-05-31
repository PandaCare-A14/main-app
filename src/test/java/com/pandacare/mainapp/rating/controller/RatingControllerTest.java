package com.pandacare.mainapp.rating.controller;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @Mock
    private ReservasiKonsultasiRepository reservasiKonsultasiRepository;

    @InjectMocks
    private RatingController ratingController;

    private RatingRequest ratingRequest;
    private RatingResponse ratingResponse;
    private RatingListResponse ratingListResponse;
    private ReservasiKonsultasi reservasiKonsultasi;
    private ReservasiKonsultasi futureReservasiKonsultasi;
    private CaregiverSchedule pastSchedule;
    private CaregiverSchedule futureSchedule;

    private CaregiverSchedule todayNotEndedSchedule;
    private ReservasiKonsultasi todayNotEndedReservasi;
    
    private CaregiverSchedule nullDateSchedule;
    private ReservasiKonsultasi nullDateReservasi;
    
    private CaregiverSchedule nullDateTimeSchedule;
    private ReservasiKonsultasi nullDateTimeReservasi;

    private final UUID ID_PASIEN = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID ID_DOKTER = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID ID_JADWAL_KONSULTASI = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @BeforeEach
    void setUp() {
        // Initialize request
        ratingRequest = new RatingRequest();
        ratingRequest.setIdJadwalKonsultasi(ID_JADWAL_KONSULTASI);
        ratingRequest.setRatingScore(5);
        ratingRequest.setUlasan("Excellent service");

        // Initialize response
        ratingResponse = new RatingResponse();
        ratingResponse.setId(UUID.randomUUID());
        ratingResponse.setIdDokter(ID_DOKTER);
        ratingResponse.setIdPasien(ID_PASIEN);
        ratingResponse.setIdJadwalKonsultasi(ID_JADWAL_KONSULTASI);
        ratingResponse.setRatingScore(5);
        ratingResponse.setUlasan("Excellent service");
        ratingResponse.setCreatedAt(LocalDateTime.now());
        ratingResponse.setUpdatedAt(LocalDateTime.now());

        // Initialize list response
        ratingListResponse = new RatingListResponse();        // Initialize CaregiverSchedule for past consultation
        pastSchedule = new CaregiverSchedule();
        pastSchedule.setIdCaregiver(ID_DOKTER);
        pastSchedule.setDate(LocalDate.now().minusDays(1)); // Yesterday
        pastSchedule.setStartTime(LocalTime.of(9, 0)); // 09:00
        pastSchedule.setEndTime(LocalTime.of(10, 0));  // 10:00 (past time)// Initialize CaregiverSchedule for future consultation
        futureSchedule = new CaregiverSchedule();
        futureSchedule.setIdCaregiver(ID_DOKTER);
        futureSchedule.setDate(LocalDate.now().plusDays(1)); // Tomorrow
        futureSchedule.setStartTime(LocalTime.of(10, 0));  // 10:00
        futureSchedule.setEndTime(LocalTime.of(11, 0));   // 11:00

        // Initialize reservasi konsultasi yang sudah selesai (past consultation)
        reservasiKonsultasi = new ReservasiKonsultasi();
        reservasiKonsultasi.setId(ID_JADWAL_KONSULTASI);
        reservasiKonsultasi.setIdPacilian(ID_PASIEN);
        reservasiKonsultasi.setIdSchedule(pastSchedule);

        // Initialize future reservasi konsultasi
        futureReservasiKonsultasi = new ReservasiKonsultasi();
        futureReservasiKonsultasi.setId(ID_JADWAL_KONSULTASI);
        futureReservasiKonsultasi.setIdPacilian(ID_PASIEN);
        futureReservasiKonsultasi.setIdSchedule(futureSchedule);

        // Initialize CaregiverSchedule for today's consultation that hasn't ended yet
        todayNotEndedSchedule = new CaregiverSchedule();
        todayNotEndedSchedule.setIdCaregiver(ID_DOKTER);
        todayNotEndedSchedule.setDate(LocalDate.now()); // Today
        todayNotEndedSchedule.setStartTime(LocalTime.of(9, 0)); // 09:00
        todayNotEndedSchedule.setEndTime(LocalTime.now().plusHours(1)); // Current time + 1 hour
        
        // Initialize reservasi for today's consultation that hasn't ended
        todayNotEndedReservasi = new ReservasiKonsultasi();
        todayNotEndedReservasi.setId(ID_JADWAL_KONSULTASI);
        todayNotEndedReservasi.setIdPacilian(ID_PASIEN);
        todayNotEndedReservasi.setIdSchedule(todayNotEndedSchedule);
        
        // Initialize CaregiverSchedule with null date but future time
        nullDateSchedule = new CaregiverSchedule();
        nullDateSchedule.setIdCaregiver(ID_DOKTER);
        nullDateSchedule.setDate(null); // Null date
        nullDateSchedule.setStartTime(LocalTime.of(9, 0)); // 09:00
        nullDateSchedule.setEndTime(LocalTime.now().plusHours(1)); // Current time + 1 hour
        
        // Initialize reservasi for null date schedule
        nullDateReservasi = new ReservasiKonsultasi();
        nullDateReservasi.setId(ID_JADWAL_KONSULTASI);
        nullDateReservasi.setIdPacilian(ID_PASIEN);
        nullDateReservasi.setIdSchedule(nullDateSchedule);
        
        // Initialize CaregiverSchedule with null date and null time (legacy data)
        nullDateTimeSchedule = new CaregiverSchedule();
        nullDateTimeSchedule.setIdCaregiver(ID_DOKTER);
        nullDateTimeSchedule.setDate(null); // Null date
        nullDateTimeSchedule.setStartTime(null); // Null start time
        nullDateTimeSchedule.setEndTime(null); // Null end time
        
        // Initialize reservasi for null date and time schedule
        nullDateTimeReservasi = new ReservasiKonsultasi();
        nullDateTimeReservasi.setId(ID_JADWAL_KONSULTASI);
        nullDateTimeReservasi.setIdPacilian(ID_PASIEN);
        nullDateTimeReservasi.setIdSchedule(nullDateTimeSchedule);
    }

    @Test
    void addRating_Success() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        when(ratingService.addRating(eq(ID_PASIEN), any(RatingRequest.class))).thenReturn(ratingResponse);

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals("Rating berhasil ditambahkan", body.get("message"));

        // Verify service was called with correct parameters
        verify(ratingService, times(1)).addRating(eq(ID_PASIEN), any(RatingRequest.class));
    }

    @Test
    void addRating_ConsultationNotFound() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Consultation not found", body.get("message"));
    }

    @Test
    void addRating_FutureConsultation() {
        // Arrange - menggunakan konsultasi masa depan
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(futureReservasiKonsultasi));

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Cannot rate future consultation", body.get("message"));
    }

    @Test
    void addRating_ServiceException() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        when(ratingService.addRating(eq(ID_PASIEN), any(RatingRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertTrue(body.get("message").toString().contains("Terjadi kesalahan sistem"));
    }

    @Test
    void updateRating_Success() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        when(ratingService.updateRating(eq(ID_PASIEN), any(RatingRequest.class))).thenReturn(ratingResponse);

        // Act
        ResponseEntity<?> response = ratingController.updateRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals("Rating berhasil diperbarui", body.get("message"));

        // Verify service was called with correct parameters
        verify(ratingService, times(1)).updateRating(eq(ID_PASIEN), any(RatingRequest.class));
    }

    @Test
    void updateRating_BadRequest() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        when(ratingService.updateRating(eq(ID_PASIEN), any(RatingRequest.class)))
                .thenThrow(new IllegalArgumentException("Update error"));

        // Act
        ResponseEntity<?> response = ratingController.updateRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Update error", body.get("message"));
    }

    @Test
    void updateRating_InternalServerError() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        when(ratingService.updateRating(eq(ID_PASIEN), any(RatingRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<?> response = ratingController.updateRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertTrue(body.get("message").toString().contains("Terjadi kesalahan sistem"));
    }

    @Test
    void deleteRating_Success() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        doNothing().when(ratingService).deleteRating(eq(ID_PASIEN), eq(ID_JADWAL_KONSULTASI));

        // Act
        ResponseEntity<?> response = ratingController.deleteRating(ID_JADWAL_KONSULTASI);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals("Rating berhasil dihapus", body.get("message"));

        // Verify service was called with correct parameters
        verify(ratingService, times(1)).deleteRating(ID_PASIEN, ID_JADWAL_KONSULTASI);
    }

    @Test
    void deleteRating_BadRequest() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        doThrow(new IllegalArgumentException("Delete error")).when(ratingService).deleteRating(eq(ID_PASIEN), eq(ID_JADWAL_KONSULTASI));

        // Act
        ResponseEntity<?> response = ratingController.deleteRating(ID_JADWAL_KONSULTASI);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Delete error", body.get("message"));
    }

    @Test
    void deleteRating_InternalServerError() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        doThrow(new RuntimeException("Service error")).when(ratingService).deleteRating(eq(ID_PASIEN), eq(ID_JADWAL_KONSULTASI));

        // Act
        ResponseEntity<?> response = ratingController.deleteRating(ID_JADWAL_KONSULTASI);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertTrue(body.get("message").toString().contains("Terjadi kesalahan sistem"));
    }

    @Test
    void getRatingsByDokter_Success() {
        // Arrange
        when(ratingService.getRatingsByDokter(eq(ID_DOKTER))).thenReturn(ratingListResponse);

        // Act
        ResponseEntity<?> response = ratingController.getRatingsByDokter(ID_DOKTER);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertNotNull(body.get("data"));

        // Verify service was called with correct parameters
        verify(ratingService, times(1)).getRatingsByDokter(ID_DOKTER);
    }

    @Test
    void getRatingsByPatient_Success() {
        // Arrange
        when(ratingService.getRatingsByPasien(eq(ID_PASIEN))).thenReturn(ratingListResponse);

        // Act
        ResponseEntity<?> response = ratingController.getRatingsByPatient(ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertNotNull(body.get("data"));

        // Verify service was called with correct parameters
        verify(ratingService, times(1)).getRatingsByPasien(ID_PASIEN);
    }

    @Test
    void hasRatedKonsultasi_Success() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        when(ratingService.hasRatedKonsultasi(eq(ID_PASIEN), eq(ID_JADWAL_KONSULTASI))).thenReturn(true);

        // Act
        ResponseEntity<?> response = ratingController.hasRatedKonsultasi(ID_JADWAL_KONSULTASI);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        Map<String, Object> dataMap = (Map<String, Object>) body.get("data");
        assertTrue((Boolean) dataMap.get("hasRated"));

        // Verify service was called with correct parameters
        verify(ratingService, times(1)).hasRatedKonsultasi(ID_PASIEN, ID_JADWAL_KONSULTASI);
    }

    @Test
    void hasRatedKonsultasi_ConsultationNotFound() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = ratingController.hasRatedKonsultasi(ID_JADWAL_KONSULTASI);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Consultation not found", body.get("message"));
    }

    @Test
    void getRatingByKonsultasi_Success() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        when(ratingService.getRatingByKonsultasi(eq(ID_PASIEN), eq(ID_JADWAL_KONSULTASI))).thenReturn(ratingResponse);

        // Act
        ResponseEntity<?> response = ratingController.getRatingByKonsultasi(ID_JADWAL_KONSULTASI);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertNotNull(body.get("data"));

        // Verify service was called with correct parameters
        verify(ratingService, times(1)).getRatingByKonsultasi(ID_PASIEN, ID_JADWAL_KONSULTASI);
    }

    @Test
    void getRatingByKonsultasi_NotFound() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiKonsultasi));
        when(ratingService.getRatingByKonsultasi(eq(ID_PASIEN), eq(ID_JADWAL_KONSULTASI)))
                .thenThrow(new IllegalArgumentException("Rating tidak ditemukan"));

        // Act
        ResponseEntity<?> response = ratingController.getRatingByKonsultasi(ID_JADWAL_KONSULTASI);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Rating tidak ditemukan", body.get("message"));
    }
    
    @Test
    void addRating_TodayConsultationNotEnded() {
        // Arrange - using today's consultation that hasn't ended yet
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(todayNotEndedReservasi));

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Cannot rate consultation that hasn't ended yet", body.get("message"));
    }
    
    @Test
    void addRating_NullDateButFutureTime() {
        // Arrange - using consultation with null date but future time
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(nullDateReservasi));

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Tidak dapat menilai konsultasi yang belum berakhir", body.get("message"));
    }
    
    @Test
    void addRating_NullDateTimeAllowed() {
        // Arrange - using legacy consultation with null date and time
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(nullDateTimeReservasi));
        when(ratingService.addRating(eq(ID_PASIEN), any(RatingRequest.class))).thenReturn(ratingResponse);

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals("Rating berhasil ditambahkan", body.get("message"));
        
        // Verify service was called with correct parameters
        verify(ratingService, times(1)).addRating(eq(ID_PASIEN), any(RatingRequest.class));
    }
    
    @Test
    void addRating_ScheduleNotFound() {
        // Arrange - reservation exists but has null schedule
        ReservasiKonsultasi reservasiNoSchedule = new ReservasiKonsultasi();
        reservasiNoSchedule.setId(ID_JADWAL_KONSULTASI);
        reservasiNoSchedule.setIdPacilian(ID_PASIEN);
        reservasiNoSchedule.setIdSchedule(null); // null schedule
        
        when(reservasiKonsultasiRepository.findById(ID_JADWAL_KONSULTASI))
                .thenReturn(Optional.of(reservasiNoSchedule));

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Schedule not found for this consultation", body.get("message"));
    }
}