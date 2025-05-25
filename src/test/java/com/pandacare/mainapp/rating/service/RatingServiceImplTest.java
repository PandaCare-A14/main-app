package com.pandacare.mainapp.rating.service;

import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.observer.RatingSubject;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RatingServiceImpl using TDD approach
 * Note: Observer notification is tested separately since it uses Singleton pattern
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ReservasiKonsultasiRepository reservasiKonsultasiRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private RatingRequest validRatingRequest;
    private ReservasiKonsultasi validKonsultasi;
    private Rating validRating;

    private final UUID VALID_PATIENT_ID = UUID.randomUUID();
    private final UUID VALID_DOCTOR_ID = UUID.randomUUID();
    private final UUID VALID_CONSULTATION_ID = UUID.randomUUID();
    private final Integer VALID_RATING_SCORE = 5;
    private final String VALID_REVIEW = "Excellent service";

    @BeforeEach
    void setUp() {
        // Setup valid rating request
        validRatingRequest = new RatingRequest();
        validRatingRequest.setIdJadwalKonsultasi(VALID_CONSULTATION_ID);
        validRatingRequest.setRatingScore(VALID_RATING_SCORE);
        validRatingRequest.setUlasan(VALID_REVIEW);

        // Setup valid consultation
        validKonsultasi = new ReservasiKonsultasi();
        validKonsultasi.setId(VALID_CONSULTATION_ID);
        validKonsultasi.setIdCaregiver(VALID_DOCTOR_ID);
        validKonsultasi.setIdPasien(VALID_PATIENT_ID);
        validKonsultasi.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);

        // Setup valid rating
        validRating = new Rating();
        validRating.setId(UUID.randomUUID());
        validRating.setIdDokter(VALID_DOCTOR_ID);
        validRating.setIdPasien(VALID_PATIENT_ID);
        validRating.setIdJadwalKonsultasi(VALID_CONSULTATION_ID);
        validRating.setRatingScore(VALID_RATING_SCORE);
        validRating.setUlasan(VALID_REVIEW);
        validRating.setCreatedAt(LocalDateTime.now());
        validRating.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should successfully add rating when all conditions are met")
    void shouldSuccessfullyAddRating_WhenAllConditionsAreMet() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(validKonsultasi));
        when(ratingRepository.existsByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(false);
        when(ratingRepository.save(any(Rating.class))).thenReturn(validRating);

        // Act
        RatingResponse result = ratingService.addRating(VALID_PATIENT_ID, validRatingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(validRating.getId(), result.getId());
        assertEquals(VALID_DOCTOR_ID, result.getIdDokter());
        assertEquals(VALID_PATIENT_ID, result.getIdPasien());
        assertEquals(VALID_CONSULTATION_ID, result.getIdJadwalKonsultasi());
        assertEquals(VALID_RATING_SCORE, result.getRatingScore());
        assertEquals(VALID_REVIEW, result.getUlasan());

        // Verify interactions (excluding observer notifications as they use singleton)
        verify(reservasiKonsultasiRepository).findById(VALID_CONSULTATION_ID);
        verify(ratingRepository).existsByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID);
        verify(ratingRepository).save(any(Rating.class));
        verify(ratingRepository).flush();
    }

    @Test
    @DisplayName("Should throw exception when consultation not found")
    void shouldThrowException_WhenConsultationNotFound() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingService.addRating(VALID_PATIENT_ID, validRatingRequest)
        );

        assertEquals("Jadwal konsultasi tidak ditemukan", exception.getMessage());

        // Verify no rating was saved
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should throw exception when patient doesn't match consultation")
    void shouldThrowException_WhenPatientDoesntMatchConsultation() {
        // Arrange
        UUID differentPatientId = UUID.randomUUID();
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(validKonsultasi));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingService.addRating(differentPatientId, validRatingRequest)
        );

        assertEquals("Jadwal konsultasi ini bukan milik pasien ini", exception.getMessage());

        // Verify no rating was saved
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should throw exception when consultation is not approved")
    void shouldThrowException_WhenConsultationIsNotApproved() {
        // Arrange
        validKonsultasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(validKonsultasi));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingService.addRating(VALID_PATIENT_ID, validRatingRequest)
        );

        assertEquals("Rating hanya dapat diberikan untuk konsultasi yang telah disetujui", exception.getMessage());

        // Verify no rating was saved
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should throw exception when rating already exists")
    void shouldThrowException_WhenRatingAlreadyExists() {
        // Arrange
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(validKonsultasi));
        when(ratingRepository.existsByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingService.addRating(VALID_PATIENT_ID, validRatingRequest)
        );

        assertEquals("Rating untuk jadwal konsultasi ini sudah diberikan", exception.getMessage());

        // Verify no rating was saved
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should successfully update rating when rating exists")
    void shouldSuccessfullyUpdateRating_WhenRatingExists() {
        // Arrange
        Rating existingRating = new Rating();
        existingRating.setId(UUID.randomUUID());
        existingRating.setIdDokter(VALID_DOCTOR_ID);
        existingRating.setIdPasien(VALID_PATIENT_ID);
        existingRating.setIdJadwalKonsultasi(VALID_CONSULTATION_ID);
        existingRating.setRatingScore(3);
        existingRating.setUlasan("Old review");
        existingRating.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(ratingRepository.findByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(existingRating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(validRating);

        // Act
        RatingResponse result = ratingService.updateRating(VALID_PATIENT_ID, validRatingRequest);

        // Assert
        assertNotNull(result);
        verify(ratingRepository).save(any(Rating.class));
        // Observer notification tested separately due to singleton pattern
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent rating")
    void shouldThrowException_WhenUpdatingNonExistentRating() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingService.updateRating(VALID_PATIENT_ID, validRatingRequest)
        );

        assertEquals("Rating tidak ditemukan", exception.getMessage());

        // Verify no rating was saved
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should successfully delete rating when rating exists")
    void shouldSuccessfullyDeleteRating_WhenRatingExists() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(validRating));

        // Act
        ratingService.deleteRating(VALID_PATIENT_ID, VALID_CONSULTATION_ID);

        // Assert
        verify(ratingRepository).delete(validRating);
        // Observer notification tested separately due to singleton pattern
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent rating")
    void shouldThrowException_WhenDeletingNonExistentRating() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingService.deleteRating(VALID_PATIENT_ID, VALID_CONSULTATION_ID)
        );

        assertEquals("Rating tidak ditemukan", exception.getMessage());

        // Verify no rating was deleted
        verify(ratingRepository, never()).delete(any(Rating.class));
    }

    @Test
    @DisplayName("Should return ratings list for doctor")
    void shouldReturnRatingsList_ForDoctor() {
        // Arrange
        List<Rating> ratings = Arrays.asList(validRating);
        when(ratingRepository.findByIdDokter(VALID_DOCTOR_ID)).thenReturn(ratings);
        when(ratingRepository.calculateAverageRatingByDokter(VALID_DOCTOR_ID)).thenReturn(4.5);
        when(ratingRepository.countRatingsByDokter(VALID_DOCTOR_ID)).thenReturn(1);

        // Act
        RatingListResponse result = ratingService.getRatingsByDokter(VALID_DOCTOR_ID);

        // Assert
        assertNotNull(result);
        assertEquals(4.5, result.getAverageRating());
        assertEquals(1, result.getTotalRatings());
        assertEquals(1, result.getRatings().size());
    }

    @Test
    @DisplayName("Should return zero values when doctor has no ratings")
    void shouldReturnZeroValues_WhenDoctorHasNoRatings() {
        // Arrange
        when(ratingRepository.findByIdDokter(VALID_DOCTOR_ID)).thenReturn(Arrays.asList());
        when(ratingRepository.calculateAverageRatingByDokter(VALID_DOCTOR_ID)).thenReturn(null);
        when(ratingRepository.countRatingsByDokter(VALID_DOCTOR_ID)).thenReturn(0);

        // Act
        RatingListResponse result = ratingService.getRatingsByDokter(VALID_DOCTOR_ID);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getAverageRating());
        assertEquals(0, result.getTotalRatings());
        assertEquals(0, result.getRatings().size());
    }

    @Test
    @DisplayName("Should return ratings list for patient")
    void shouldReturnRatingsList_ForPatient() {
        // Arrange
        List<Rating> ratings = Arrays.asList(validRating);
        when(ratingRepository.findByIdPasien(VALID_PATIENT_ID)).thenReturn(ratings);

        // Act
        RatingListResponse result = ratingService.getRatingsByPasien(VALID_PATIENT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getAverageRating()); // Should be 0 for patient ratings
        assertEquals(1, result.getTotalRatings());
        assertEquals(1, result.getRatings().size());
    }

    @Test
    @DisplayName("Should return rating by consultation")
    void shouldReturnRating_ByConsultation() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(validRating));

        // Act
        RatingResponse result = ratingService.getRatingByKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID);

        // Assert
        assertNotNull(result);
        assertEquals(validRating.getId(), result.getId());
        assertEquals(VALID_DOCTOR_ID, result.getIdDokter());
        assertEquals(VALID_PATIENT_ID, result.getIdPasien());
    }

    @Test
    @DisplayName("Should throw exception when rating by consultation not found")
    void shouldThrowException_WhenRatingByConsultationNotFound() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingService.getRatingByKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID)
        );

        assertEquals("Rating tidak ditemukan", exception.getMessage());
    }

    @Test
    @DisplayName("Should return true when patient has rated consultation")
    void shouldReturnTrue_WhenPatientHasRatedConsultation() {
        // Arrange
        when(ratingRepository.existsByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(true);

        // Act
        boolean result = ratingService.hasRatedKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when patient has not rated consultation")
    void shouldReturnFalse_WhenPatientHasNotRatedConsultation() {
        // Arrange
        when(ratingRepository.existsByIdPasienAndIdJadwalKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(false);

        // Act
        boolean result = ratingService.hasRatedKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID);

        // Assert
        assertFalse(result);
    }
}