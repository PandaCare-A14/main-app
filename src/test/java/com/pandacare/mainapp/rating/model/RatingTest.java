package com.pandacare.mainapp.rating.model;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class RatingTest {

    @Test
    void givenValidValues_whenCreateRating_thenSuccess() {
        // Arrange
        UUID dokterId = UUID.randomUUID();
        UUID pasienId = UUID.randomUUID();
        UUID jadwalId = UUID.randomUUID();

        // Act
        Rating rating = new Rating(
                dokterId,
                pasienId,
                jadwalId,
                4,
                "Great doctor!"
        );

        // Assert
        assertEquals(dokterId, rating.getIdDokter());
        assertEquals(pasienId, rating.getIdPasien());
        assertEquals(jadwalId, rating.getIdJadwalKonsultasi());
        assertEquals(4, rating.getRatingScore());
        assertEquals("Great doctor!", rating.getUlasan());
        assertNotNull(rating.getCreatedAt());
        assertNotNull(rating.getUpdatedAt());
    }

    @Test
    void givenInvalidRatingScore_whenSetRatingScore_thenThrowsException() {
        // Arrange
        Rating rating = new Rating();

        // Act & Assert - Test score below minimum
        Exception exceptionLow = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(0);
        });
        assertTrue(exceptionLow.getMessage().contains("Rating score harus di antara 1 dan 5"));

        // Act & Assert - Test score above maximum
        Exception exceptionHigh = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(6);
        });
        assertTrue(exceptionHigh.getMessage().contains("Rating score harus di antara 1 dan 5"));

        // Act & Assert - Test null score
        Exception exceptionNull = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(null);
        });
        assertTrue(exceptionNull.getMessage().contains("Rating score cannot be null"));
    }

    @Test
    void givenExistingRating_whenUpdateFrom_thenCorrectlyUpdated() {
        // Arrange
        UUID ratingId = UUID.randomUUID();
        UUID dokterId = UUID.randomUUID();
        UUID pasienId = UUID.randomUUID();
        UUID jadwalId = UUID.randomUUID();

        Rating original = new Rating(
                dokterId,
                pasienId,
                jadwalId,
                4,
                "Original review"
        );
        original.setId(ratingId);
        original.setCreatedAt(LocalDateTime.now().minusDays(1));
        original.setUpdatedAt(LocalDateTime.now().minusDays(1));

        Rating updated = new Rating(
                dokterId,
                pasienId,
                jadwalId,
                5,
                "Updated review"
        );

        LocalDateTime beforeUpdate = LocalDateTime.now();

        // Act
        original.updateFrom(updated);

        // Assert
        assertEquals(ratingId, original.getId()); // ID should not change
        assertEquals(dokterId, original.getIdDokter());
        assertEquals(pasienId, original.getIdPasien());
        assertEquals(jadwalId, original.getIdJadwalKonsultasi());
        assertEquals(5, original.getRatingScore());
        assertEquals("Updated review", original.getUlasan());
        assertTrue(original.getUpdatedAt().isAfter(beforeUpdate) || original.getUpdatedAt().isEqual(beforeUpdate));
    }

    @Test
    void givenNewRating_whenPrePersist_thenIdIsGenerated() {
        // Arrange
        Rating rating = new Rating(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                4,
                "Great doctor!"
        );

        // Act
        rating.onCreate();

        // Assert
        assertNotNull(rating.getId());
        assertTrue(rating.getId() instanceof UUID);
    }
}
