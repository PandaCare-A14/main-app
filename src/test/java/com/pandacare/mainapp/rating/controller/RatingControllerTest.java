package com.pandacare.mainapp.rating.controller;

import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
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

    @InjectMocks
    private RatingController ratingController;

    private RatingRequest ratingRequest;
    private RatingResponse ratingResponse;
    private RatingListResponse ratingListResponse;

    private final UUID ID_PASIEN = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID ID_DOKTER = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID ID_JADWAL_KONSULTASI = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @BeforeEach
    void setUp() {
        // Initialize request
        ratingRequest = new RatingRequest();
        ratingRequest.setIdJadwalKonsultasi(ID_JADWAL_KONSULTASI);
        ratingRequest.setRatingScore(5);

        // Initialize response
        ratingResponse = new RatingResponse();
        ratingResponse.setId(UUID.randomUUID());
        ratingResponse.setIdDokter(ID_DOKTER);
        ratingResponse.setIdPasien(ID_PASIEN);
        ratingResponse.setIdJadwalKonsultasi(ID_JADWAL_KONSULTASI);
        ratingResponse.setRatingScore(5);

        // Initialize list response
        ratingListResponse = new RatingListResponse();
    }

    @Test
    void addRating_Success() {
        // Arrange
        when(ratingService.addRating(eq(ID_PASIEN), any(RatingRequest.class))).thenReturn(ratingResponse);

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));

        // Verify service was called with correct parameters
        verify(ratingService, times(1)).addRating(eq(ID_PASIEN), any(RatingRequest.class));
    }

    @Test
    void addRating_BadRequest() {
        // Arrange
        when(ratingService.addRating(eq(ID_PASIEN), any(RatingRequest.class)))
                .thenThrow(new IllegalArgumentException("Rating error"));

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Rating error", body.get("message"));
    }

    @Test
    void updateRating_Success() {
        // Arrange
        when(ratingService.updateRating(eq(ID_PASIEN), any(RatingRequest.class))).thenReturn(ratingResponse);

        // Act
        ResponseEntity<?> response = ratingController.updateRating(ID_JADWAL_KONSULTASI, ratingRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));

        // Verify service was called with correct parameters
        verify(ratingService, times(1)).updateRating(eq(ID_PASIEN), any(RatingRequest.class));
    }

    @Test
    void updateRating_BadRequest() {
        // Arrange
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
    void deleteRating_Success() {
        // Arrange
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
    void getRatingsByPasien_Success() {
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
    void getRatingsByPasien_Forbidden() {
        // Different UUID for requested vs authenticated patient
        UUID otherPatientId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        // Act - attempt to access ratings of a different patient
        ResponseEntity<?> response = ratingController.getRatingsByPatient(otherPatientId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Anda tidak memiliki izin untuk melihat rating pasien lain", body.get("message"));

        // Verify service was not called
        verify(ratingService, never()).getRatingsByPasien(any());
    }

    @Test
    void hasRatedKonsultasi_Success() {
        // Arrange
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
    void getRatingByKonsultasi_Success() {
        // Arrange
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
}