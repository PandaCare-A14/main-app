package com.pandacare.mainapp.rating.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RatingBuilder using TDD approach
 */
@ActiveProfiles("test")
class RatingBuilderTest {

    private RatingBuilder ratingBuilder;
    private final UUID VALID_DOCTOR_ID = UUID.randomUUID();
    private final UUID VALID_PATIENT_ID = UUID.randomUUID();
    private final UUID VALID_CONSULTATION_ID = UUID.randomUUID();
    private final Integer VALID_RATING_SCORE = 5;
    private final String VALID_REVIEW = "Excellent service";

    @BeforeEach
    void setUp() {
        ratingBuilder = new RatingBuilder();
    }

    @Test
    @DisplayName("Should create valid Rating when all required fields are provided")
    void shouldCreateValidRating_WhenAllRequiredFieldsProvided() {
        // Act
        Rating rating = ratingBuilder
                .withIdDokter(VALID_DOCTOR_ID)
                .withIdPasien(VALID_PATIENT_ID)
                .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                .withRatingScore(VALID_RATING_SCORE)
                .withUlasan(VALID_REVIEW)
                .build();

        // Assert
        assertNotNull(rating);
        assertEquals(VALID_DOCTOR_ID, rating.getIdDokter());
        assertEquals(VALID_PATIENT_ID, rating.getIdPasien());
        assertEquals(VALID_CONSULTATION_ID, rating.getIdJadwalKonsultasi());
        assertEquals(VALID_RATING_SCORE, rating.getRatingScore());
        assertEquals(VALID_REVIEW, rating.getUlasan());
        assertNotNull(rating.getId());
        assertNotNull(rating.getCreatedAt());
        assertNotNull(rating.getUpdatedAt());
    }

    @Test
    @DisplayName("Should generate UUID automatically when no ID provided")
    void shouldGenerateUuidAutomatically_WhenNoIdProvided() {
        // Act
        Rating rating = ratingBuilder
                .withIdDokter(VALID_DOCTOR_ID)
                .withIdPasien(VALID_PATIENT_ID)
                .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                .withRatingScore(VALID_RATING_SCORE)
                .withUlasan(VALID_REVIEW)
                .build();

        // Assert
        assertNotNull(rating.getId());
        assertTrue(rating.getId() instanceof UUID);
    }

    @Test
    @DisplayName("Should use provided ID when explicitly set")
    void shouldUseProvidedId_WhenExplicitlySet() {
        // Arrange
        UUID customId = UUID.randomUUID();

        // Act
        Rating rating = ratingBuilder
                .withId(customId)
                .withIdDokter(VALID_DOCTOR_ID)
                .withIdPasien(VALID_PATIENT_ID)
                .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                .withRatingScore(VALID_RATING_SCORE)
                .withUlasan(VALID_REVIEW)
                .build();

        // Assert
        assertEquals(customId, rating.getId());
    }

    @Test
    @DisplayName("Should throw exception when doctor ID is null")
    void shouldThrowException_WhenDoctorIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingBuilder
                        .withIdDokter(null)
                        .withIdPasien(VALID_PATIENT_ID)
                        .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                        .withRatingScore(VALID_RATING_SCORE)
                        .withUlasan(VALID_REVIEW)
                        .build()
        );

        assertEquals("idDokter cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when patient ID is null")
    void shouldThrowException_WhenPatientIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingBuilder
                        .withIdDokter(VALID_DOCTOR_ID)
                        .withIdPasien(null)
                        .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                        .withRatingScore(VALID_RATING_SCORE)
                        .withUlasan(VALID_REVIEW)
                        .build()
        );

        assertEquals("idPasien cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when consultation ID is null")
    void shouldThrowException_WhenConsultationIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingBuilder
                        .withIdDokter(VALID_DOCTOR_ID)
                        .withIdPasien(VALID_PATIENT_ID)
                        .withIdJadwalKonsultasi(null)
                        .withRatingScore(VALID_RATING_SCORE)
                        .withUlasan(VALID_REVIEW)
                        .build()
        );

        assertEquals("idJadwalKonsultasi cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when rating score is null")
    void shouldThrowException_WhenRatingScoreIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingBuilder
                        .withIdDokter(VALID_DOCTOR_ID)
                        .withIdPasien(VALID_PATIENT_ID)
                        .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                        .withRatingScore(null)
                        .withUlasan(VALID_REVIEW)
                        .build()
        );

        assertEquals("ratingScore cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when rating score is less than 1")
    void shouldThrowException_WhenRatingScoreIsLessThanOne() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingBuilder
                        .withIdDokter(VALID_DOCTOR_ID)
                        .withIdPasien(VALID_PATIENT_ID)
                        .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                        .withRatingScore(0)
                        .withUlasan(VALID_REVIEW)
                        .build()
        );

        assertEquals("ratingScore must be between 1 and 5", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when rating score is greater than 5")
    void shouldThrowException_WhenRatingScoreIsGreaterThanFive() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ratingBuilder
                        .withIdDokter(VALID_DOCTOR_ID)
                        .withIdPasien(VALID_PATIENT_ID)
                        .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                        .withRatingScore(6)
                        .withUlasan(VALID_REVIEW)
                        .build()
        );

        assertEquals("ratingScore must be between 1 and 5", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept valid rating scores from 1 to 5")
    void shouldAcceptValidRatingScores_FromOneToFive() {
        // Test each valid score
        for (int score = 1; score <= 5; score++) {
            Rating rating = ratingBuilder
                    .withIdDokter(VALID_DOCTOR_ID)
                    .withIdPasien(VALID_PATIENT_ID)
                    .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                    .withRatingScore(score)
                    .withUlasan(VALID_REVIEW)
                    .build();

            assertEquals(score, rating.getRatingScore());

            // Reset builder for next iteration
            ratingBuilder = new RatingBuilder();
        }
    }

    @Test
    @DisplayName("Should set timestamps automatically when not provided")
    void shouldSetTimestampsAutomatically_WhenNotProvided() {
        // Arrange
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        // Act
        Rating rating = ratingBuilder
                .withIdDokter(VALID_DOCTOR_ID)
                .withIdPasien(VALID_PATIENT_ID)
                .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                .withRatingScore(VALID_RATING_SCORE)
                .withUlasan(VALID_REVIEW)
                .build();

        // Assert
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        assertTrue(rating.getCreatedAt().isAfter(beforeCreation));
        assertTrue(rating.getCreatedAt().isBefore(afterCreation));
        assertTrue(rating.getUpdatedAt().isAfter(beforeCreation));
        assertTrue(rating.getUpdatedAt().isBefore(afterCreation));
    }

    @Test
    @DisplayName("Should use provided timestamps when explicitly set")
    void shouldUseProvidedTimestamps_WhenExplicitlySet() {
        // Arrange
        LocalDateTime customCreatedAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
        LocalDateTime customUpdatedAt = LocalDateTime.of(2023, 1, 2, 12, 0, 0);

        // Act
        Rating rating = ratingBuilder
                .withIdDokter(VALID_DOCTOR_ID)
                .withIdPasien(VALID_PATIENT_ID)
                .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                .withRatingScore(VALID_RATING_SCORE)
                .withUlasan(VALID_REVIEW)
                .withCreatedAt(customCreatedAt)
                .withUpdatedAt(customUpdatedAt)
                .build();

        // Assert
        assertEquals(customCreatedAt, rating.getCreatedAt());
        assertEquals(customUpdatedAt, rating.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle null review gracefully")
    void shouldHandleNullReview_Gracefully() {
        // Act
        Rating rating = ratingBuilder
                .withIdDokter(VALID_DOCTOR_ID)
                .withIdPasien(VALID_PATIENT_ID)
                .withIdJadwalKonsultasi(VALID_CONSULTATION_ID)
                .withRatingScore(VALID_RATING_SCORE)
                .withUlasan(null)
                .build();

        // Assert
        assertNull(rating.getUlasan());
    }
}