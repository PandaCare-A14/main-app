package com.pandacare.mainapp.rating.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DoctorStatisticsBuilder using TDD approach
 */
@ActiveProfiles("test")
class DoctorStatisticsBuilderTest {

    private DoctorStatisticsBuilder builder;
    private final String VALID_DOCTOR_ID = "dr001";
    private final Double VALID_AVERAGE_RATING = 4.5;
    private final Integer VALID_TOTAL_RATINGS = 10;

    @BeforeEach
    void setUp() {
        builder = new DoctorStatisticsBuilder();
    }

    @Test
    @DisplayName("Should create valid DoctorStatistics when all fields are provided")
    void shouldCreateValidDoctorStatistics_WhenAllFieldsProvided() {
        // Act
        DoctorStatistics stats = builder
                .withIdDokter(VALID_DOCTOR_ID)
                .withAverageRating(VALID_AVERAGE_RATING)
                .withTotalRatings(VALID_TOTAL_RATINGS)
                .build();

        // Assert
        assertNotNull(stats);
        assertEquals(VALID_DOCTOR_ID, stats.getIdDokter());
        assertEquals(VALID_AVERAGE_RATING, stats.getAverageRating());
        assertEquals(VALID_TOTAL_RATINGS, stats.getTotalRatings());
        assertNotNull(stats.getCreatedAt());
        assertNotNull(stats.getUpdatedAt());
    }

    @Test
    @DisplayName("Should use default values when optional fields are not provided")
    void shouldUseDefaultValues_WhenOptionalFieldsNotProvided() {
        // Act
        DoctorStatistics stats = builder
                .withIdDokter(VALID_DOCTOR_ID)
                .build();

        // Assert
        assertEquals(VALID_DOCTOR_ID, stats.getIdDokter());
        assertEquals(0.0, stats.getAverageRating());
        assertEquals(0, stats.getTotalRatings());
        assertNotNull(stats.getCreatedAt());
        assertNotNull(stats.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when doctor ID is null")
    void shouldThrowException_WhenDoctorIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                builder.withIdDokter(null).build()
        );

        assertEquals("idDokter cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when doctor ID is empty")
    void shouldThrowException_WhenDoctorIdIsEmpty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                builder.withIdDokter("").build()
        );

        assertEquals("idDokter cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle null average rating by setting to default")
    void shouldHandleNullAverageRating_BySettingToDefault() {
        // Act
        DoctorStatistics stats = builder
                .withIdDokter(VALID_DOCTOR_ID)
                .withAverageRating(null)
                .build();

        // Assert
        assertEquals(0.0, stats.getAverageRating());
    }

    @Test
    @DisplayName("Should handle null total ratings by setting to default")
    void shouldHandleNullTotalRatings_BySettingToDefault() {
        // Act
        DoctorStatistics stats = builder
                .withIdDokter(VALID_DOCTOR_ID)
                .withTotalRatings(null)
                .build();

        // Assert
        assertEquals(0, stats.getTotalRatings());
    }

    @Test
    @DisplayName("Should set timestamps automatically when not provided")
    void shouldSetTimestampsAutomatically_WhenNotProvided() {
        // Arrange
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        // Act
        DoctorStatistics stats = builder
                .withIdDokter(VALID_DOCTOR_ID)
                .build();

        // Assert
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        assertTrue(stats.getCreatedAt().isAfter(beforeCreation));
        assertTrue(stats.getCreatedAt().isBefore(afterCreation));
        assertTrue(stats.getUpdatedAt().isAfter(beforeCreation));
        assertTrue(stats.getUpdatedAt().isBefore(afterCreation));
    }

    @Test
    @DisplayName("Should use provided timestamps when explicitly set")
    void shouldUseProvidedTimestamps_WhenExplicitlySet() {
        // Arrange
        LocalDateTime customCreatedAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
        LocalDateTime customUpdatedAt = LocalDateTime.of(2023, 1, 2, 12, 0, 0);

        // Act
        DoctorStatistics stats = builder
                .withIdDokter(VALID_DOCTOR_ID)
                .withCreatedAt(customCreatedAt)
                .withUpdatedAt(customUpdatedAt)
                .build();

        // Assert
        assertEquals(customCreatedAt, stats.getCreatedAt());
        assertEquals(customUpdatedAt, stats.getUpdatedAt());
    }

    @Test
    @DisplayName("Should accept negative average rating")
    void shouldAcceptNegativeAverageRating() {
        // Act
        DoctorStatistics stats = builder
                .withIdDokter(VALID_DOCTOR_ID)
                .withAverageRating(-1.0)
                .build();

        // Assert
        assertEquals(-1.0, stats.getAverageRating());
    }

    @Test
    @DisplayName("Should accept negative total ratings")
    void shouldAcceptNegativeTotalRatings() {
        // Act
        DoctorStatistics stats = builder
                .withIdDokter(VALID_DOCTOR_ID)
                .withTotalRatings(-1)
                .build();

        // Assert
        assertEquals(-1, stats.getTotalRatings());
    }

    @Test
    @DisplayName("Should accept very high values")
    void shouldAcceptVeryHighValues() {
        // Arrange
        Double highRating = 999999.99;
        Integer highTotal = Integer.MAX_VALUE;

        // Act
        DoctorStatistics stats = builder
                .withIdDokter(VALID_DOCTOR_ID)
                .withAverageRating(highRating)
                .withTotalRatings(highTotal)
                .build();

        // Assert
        assertEquals(highRating, stats.getAverageRating());
        assertEquals(highTotal, stats.getTotalRatings());
    }

    @Test
    @DisplayName("Should handle precision in average rating")
    void shouldHandlePrecisionInAverageRating() {
        // Arrange
        Double preciseRating = 4.123456789;

        // Act
        DoctorStatistics stats = builder
                .withIdDokter(VALID_DOCTOR_ID)
                .withAverageRating(preciseRating)
                .build();

        // Assert
        assertEquals(preciseRating, stats.getAverageRating());
    }
}