package com.pandacare.mainapp.rating.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class RatingTest {

    @Test
    public void testRatingConstructor() {
        // Arrange
        String id = "RTG12345";
        String idDokter = "DOC12345";
        String idPasien = "PAT7890";
        Integer ratingScore = 4;
        String ulasan = "Dokter sangat ramah dan profesional";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        Rating rating = new Rating(id, idDokter, idPasien, ratingScore, ulasan, createdAt, updatedAt);

        // Assert
        assertEquals(id, rating.getId());
        assertEquals(idDokter, rating.getIdDokter());
        assertEquals(idPasien, rating.getIdPasien());
        assertEquals(ratingScore, rating.getRatingScore());
        assertEquals(ulasan, rating.getUlasan());
        assertEquals(createdAt, rating.getCreatedAt());
        assertEquals(updatedAt, rating.getUpdatedAt());
    }

    @Test
    public void testConstructorWithoutIdAndDates() {
        // Arrange
        String idDokter = "DOC12345";
        String idPasien = "PAT7890";
        Integer ratingScore = 4;
        String ulasan = "Dokter sangat ramah dan profesional";

        // Act
        Rating rating = new Rating(idDokter, idPasien, ratingScore, ulasan);

        // Assert
        assertNull(rating.getId());  // ID should be null
        assertNotNull(rating.getCreatedAt());  // Created date should be set
        assertNotNull(rating.getUpdatedAt());  // Updated date should be set
        assertEquals(idDokter, rating.getIdDokter());
        assertEquals(idPasien, rating.getIdPasien());
        assertEquals(ratingScore, rating.getRatingScore());
        assertEquals(ulasan, rating.getUlasan());
    }

    @Test
    public void testRatingScoreValidation() {
        // Arrange
        Rating rating = new Rating();

        // Act & Assert - Valid ratings
        rating.setRatingScore(1);
        assertEquals(1, rating.getRatingScore());

        rating.setRatingScore(5);
        assertEquals(5, rating.getRatingScore());

        // Act & Assert - Invalid ratings
        Exception exceptionLow = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(0);
        });

        Exception exceptionHigh = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(6);
        });

        assertTrue(exceptionLow.getMessage().contains("Rating score harus di antara 1 dan 5"));
        assertTrue(exceptionHigh.getMessage().contains("Rating score harus di antara 1 dan 5"));
    }

    @Test
    public void testUpdateFrom() {
        // Arrange
        Rating rating = new Rating("RTG12345", "DOC12345", "PAT7890", 4, "Original review",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));

        Rating newRating = new Rating();
        newRating.setRatingScore(5);
        newRating.setUlasan("Updated review");

        // Act
        rating.updateFrom(newRating);

        // Assert
        assertEquals(5, rating.getRatingScore());
        assertEquals("Updated review", rating.getUlasan());
        assertTrue(rating.getUpdatedAt().isAfter(rating.getCreatedAt()));
    }

    @Test
    public void testSetRatingScoreNull() {
        // Arrange
        Rating rating = new Rating();

        // Act & Assert - Null value for ratingScore should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(null);
        });

        assertTrue(exception.getMessage().contains("Rating score cannot be null"));
    }

    @Test
    void testEqualsAndHashCode() {
        Rating r1 = new Rating(
                "RTG0001", "DOC1", "PAT1", 5, "Great!",
                LocalDateTime.now(), LocalDateTime.now()
        );
        Rating r2 = new Rating(
                "RTG0001", "DOC1", "PAT1", 5, "Great!",
                r1.getCreatedAt(), r1.getUpdatedAt()
        );
        Rating r3 = new Rating(
                "RTG0002", "DOC2", "PAT2", 3, "Okay",
                LocalDateTime.now(), LocalDateTime.now()
        );

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToString() {
        Rating r = new Rating(
                "RTG0001", "DOC1", "PAT1", 5, "Great!",
                LocalDateTime.now(), LocalDateTime.now()
        );
        String result = r.toString();
        assertNotNull(result);
        assertTrue(result.contains("RTG0001")); // check ID in string
    }
}
