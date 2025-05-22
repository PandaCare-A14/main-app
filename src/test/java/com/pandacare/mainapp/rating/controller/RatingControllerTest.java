package com.pandacare.mainapp.rating.controller;

import com.pandacare.mainapp.rating.dto.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RatingController ratingController;

    private RatingRequest ratingRequest;
    private RatingResponse ratingResponse;
    private RatingListResponse ratingListResponse;

    private final String ID_JADWAL_KONSULTASI = "jadwal-123";
    private final String ID_PASIEN = "pasien-123";
    private final String ID_DOKTER = "dokter-123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        ratingRequest = new RatingRequest();
        ratingRequest.setIdJadwalKonsultasi(ID_JADWAL_KONSULTASI);
        ratingRequest.setRatingScore(5);
        ratingRequest.setUlasan("Dokter sangat membantu");

        ratingResponse = new RatingResponse();
        ratingResponse.setId("rating-123");
        ratingResponse.setIdDokter(ID_DOKTER);
        ratingResponse.setIdPasien(ID_PASIEN);
        ratingResponse.setIdJadwalKonsultasi(ID_JADWAL_KONSULTASI);
        ratingResponse.setRatingScore(5);
        ratingResponse.setUlasan("Dokter sangat membantu");
        ratingResponse.setCreatedAt(LocalDateTime.now());
        ratingResponse.setUpdatedAt(LocalDateTime.now());

        List<RatingResponse> ratingList = new ArrayList<>();
        ratingList.add(ratingResponse);

        ratingListResponse = new RatingListResponse(4.5, 1, ratingList);
    }

    @Test
    void addRating_Success() {
        // Arrange
        when(ratingService.addRating(anyString(), any(RatingRequest.class))).thenReturn(ratingResponse);

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest, ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));

        // Verify that service was called with correct parameters
        verify(ratingService, times(1)).addRating(eq(ID_PASIEN), any(RatingRequest.class));
    }

    @Test
    void addRating_BadRequest() {
        // Arrange
        when(ratingService.addRating(anyString(), any(RatingRequest.class)))
                .thenThrow(new IllegalArgumentException("Rating error"));

        // Act
        ResponseEntity<?> response = ratingController.addRating(ID_JADWAL_KONSULTASI, ratingRequest, ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Rating error", body.get("message"));
    }

    @Test
    void updateRating_Success() {
        // Arrange
        when(ratingService.updateRating(anyString(), any(RatingRequest.class))).thenReturn(ratingResponse);

        // Act
        ResponseEntity<?> response = ratingController.updateRating(ID_JADWAL_KONSULTASI, ratingRequest, ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));

        // Verify that service was called with correct parameters
        verify(ratingService, times(1)).updateRating(eq(ID_PASIEN), any(RatingRequest.class));
    }

    @Test
    void updateRating_BadRequest() {
        // Arrange
        when(ratingService.updateRating(anyString(), any(RatingRequest.class)))
                .thenThrow(new IllegalArgumentException("Update error"));

        // Act
        ResponseEntity<?> response = ratingController.updateRating(ID_JADWAL_KONSULTASI, ratingRequest, ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Update error", body.get("message"));
    }

    @Test
    void deleteRating_Success() {
        // Arrange
        doNothing().when(ratingService).deleteRating(anyString(), anyString());

        // Act
        ResponseEntity<?> response = ratingController.deleteRating(ID_JADWAL_KONSULTASI, ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals("Rating berhasil dihapus", body.get("message"));

        // Verify that service was called with correct parameters
        verify(ratingService, times(1)).deleteRating(ID_PASIEN, ID_JADWAL_KONSULTASI);
    }

    @Test
    void deleteRating_BadRequest() {
        // Arrange
        doThrow(new IllegalArgumentException("Delete error")).when(ratingService).deleteRating(anyString(), anyString());

        // Act
        ResponseEntity<?> response = ratingController.deleteRating(ID_JADWAL_KONSULTASI, ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Delete error", body.get("message"));
    }

    @Test
    void getRatingsByDokter_Success() {
        // Arrange
        when(ratingService.getRatingsByDokter(anyString())).thenReturn(ratingListResponse);

        // Act
        ResponseEntity<?> response = ratingController.getRatingsByDokter(ID_DOKTER);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertNotNull(body.get("data"));

        // Verify that service was called
        verify(ratingService, times(1)).getRatingsByDokter(ID_DOKTER);
    }

    @Test
    void getRatingsByPasien_Success() {
        // Arrange
        when(ratingService.getRatingsByPasien(anyString())).thenReturn(ratingListResponse);

        // Act
        ResponseEntity<?> response = ratingController.getRatingsByPasien(ID_PASIEN, ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertNotNull(body.get("data"));

        // Verify that service was called
        verify(ratingService, times(1)).getRatingsByPasien(ID_PASIEN);
    }

    @Test
    void getRatingsByPasien_Forbidden() {
        // Act - attempt to access ratings of a different patient
        ResponseEntity<?> response = ratingController.getRatingsByPasien(ID_PASIEN, "different-pasien");

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Anda tidak memiliki izin untuk melihat rating pasien lain", body.get("message"));

        // Verify that service was not called
        verify(ratingService, never()).getRatingsByPasien(anyString());
    }

    @Test
    void hasRatedKonsultasi_Success() {
        // Arrange
        when(ratingService.hasRatedKonsultasi(anyString(), anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = ratingController.hasRatedKonsultasi(ID_JADWAL_KONSULTASI, ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        Map<String, Object> dataMap = (Map<String, Object>) body.get("data");
        assertTrue((Boolean) dataMap.get("hasRated"));

        // Verify that service was called
        verify(ratingService, times(1)).hasRatedKonsultasi(ID_PASIEN, ID_JADWAL_KONSULTASI);
    }

    @Test
    void getRatingByKonsultasi_Success() {
        // Arrange
        when(ratingService.getRatingByKonsultasi(anyString(), anyString())).thenReturn(ratingResponse);

        // Act
        ResponseEntity<?> response = ratingController.getRatingByKonsultasi(ID_JADWAL_KONSULTASI, ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("success", body.get("status"));
        assertNotNull(body.get("data"));

        // Verify that service was called
        verify(ratingService, times(1)).getRatingByKonsultasi(ID_PASIEN, ID_JADWAL_KONSULTASI);
    }

    @Test
    void getRatingByKonsultasi_NotFound() {
        // Arrange
        when(ratingService.getRatingByKonsultasi(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Rating tidak ditemukan"));

        // Act
        ResponseEntity<?> response = ratingController.getRatingByKonsultasi(ID_JADWAL_KONSULTASI, ID_PASIEN);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("Rating tidak ditemukan", body.get("message"));
    }
}