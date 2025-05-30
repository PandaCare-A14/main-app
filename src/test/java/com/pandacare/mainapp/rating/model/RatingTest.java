package com.pandacare.mainapp.rating.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class RatingTest {

    private UUID testDokterId;
    private UUID testPasienId;
    private UUID testJadwalId;
    private UUID testRatingId;

    @BeforeEach
    void setUp() {
        testDokterId = UUID.randomUUID();
        testPasienId = UUID.randomUUID();
        testJadwalId = UUID.randomUUID();
        testRatingId = UUID.randomUUID();
    }

    @Test
    void givenValidValues_whenCreateRating_thenSuccess() {
        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Great doctor!"
        );

        assertEquals(testDokterId, rating.getIdDokter());
        assertEquals(testPasienId, rating.getIdPasien());
        assertEquals(testJadwalId, rating.getIdJadwalKonsultasi());
        assertEquals(4, rating.getRatingScore());
        assertEquals("Great doctor!", rating.getUlasan());
        assertNotNull(rating.getCreatedAt());
        assertNotNull(rating.getUpdatedAt());
    }

    @Test
    void givenMinimumRatingScore_whenCreateRating_thenSuccess() {
        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                1,
                "Minimum rating"
        );

        assertEquals(1, rating.getRatingScore());
    }

    @Test
    void givenMaximumRatingScore_whenCreateRating_thenSuccess() {
        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                5,
                "Maximum rating"
        );

        assertEquals(5, rating.getRatingScore());
    }

    @Test
    void givenNullUlasan_whenCreateRating_thenSuccess() {
        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                null
        );

        assertEquals(4, rating.getRatingScore());
        assertNull(rating.getUlasan());
    }

    @Test
    void givenEmptyUlasan_whenCreateRating_thenSuccess() {
        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                ""
        );

        assertEquals(4, rating.getRatingScore());
        assertEquals("", rating.getUlasan());
    }

    @Test
    void givenInvalidRatingScore_whenSetRatingScore_thenThrowsException() {
        Rating rating = new Rating();

        Exception exceptionLow = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(0);
        });
        assertTrue(exceptionLow.getMessage().contains("Rating score harus di antara 1 dan 5"));

        Exception exceptionHigh = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(6);
        });
        assertTrue(exceptionHigh.getMessage().contains("Rating score harus di antara 1 dan 5"));

        Exception exceptionNull = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(null);
        });
        assertTrue(exceptionNull.getMessage().contains("Rating score cannot be null"));
    }

    @Test
    void givenNegativeRatingScore_whenSetRatingScore_thenThrowsException() {
        Rating rating = new Rating();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(-1);
        });
        assertTrue(exception.getMessage().contains("Rating score harus di antara 1 dan 5"));
    }

    @Test
    void givenLargeRatingScore_whenSetRatingScore_thenThrowsException() {
        Rating rating = new Rating();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(100);
        });
        assertTrue(exception.getMessage().contains("Rating score harus di antara 1 dan 5"));
    }

    @Test
    void givenValidRatingScore_whenSetRatingScore_thenSuccess() {
        Rating rating = new Rating();

        assertDoesNotThrow(() -> rating.setRatingScore(1));
        assertEquals(1, rating.getRatingScore());

        assertDoesNotThrow(() -> rating.setRatingScore(3));
        assertEquals(3, rating.getRatingScore());

        assertDoesNotThrow(() -> rating.setRatingScore(5));
        assertEquals(5, rating.getRatingScore());
    }

    @Test
    void givenExistingRating_whenUpdateFrom_thenCorrectlyUpdated() {
        Rating original = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Original review"
        );
        original.setId(testRatingId);
        original.setCreatedAt(LocalDateTime.now().minusDays(1));
        original.setUpdatedAt(LocalDateTime.now().minusDays(1));

        Rating updated = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                5,
                "Updated review"
        );

        LocalDateTime beforeUpdate = LocalDateTime.now();

        original.updateFrom(updated);

        assertEquals(testRatingId, original.getId());
        assertEquals(testDokterId, original.getIdDokter());
        assertEquals(testPasienId, original.getIdPasien());
        assertEquals(testJadwalId, original.getIdJadwalKonsultasi());
        assertEquals(5, original.getRatingScore());
        assertEquals("Updated review", original.getUlasan());
        assertTrue(original.getUpdatedAt().isAfter(beforeUpdate) || original.getUpdatedAt().isEqual(beforeUpdate));
    }

    @Test
    void givenExistingRating_whenUpdateFromWithNullUlasan_thenUlasanUpdatedToNull() {
        Rating original = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Original review"
        );

        Rating updated = new Rating();
        updated.setRatingScore(5);
        updated.setUlasan(null);

        original.updateFrom(updated);

        assertEquals(5, original.getRatingScore());
        assertNull(original.getUlasan());
    }

    @Test
    void givenExistingRating_whenUpdateFromWithEmptyUlasan_thenUlasanUpdatedToEmpty() {
        Rating original = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Original review"
        );

        Rating updated = new Rating();
        updated.setRatingScore(5);
        updated.setUlasan("");

        original.updateFrom(updated);

        assertEquals(5, original.getRatingScore());
        assertEquals("", original.getUlasan());
    }

    @Test
    void givenNewRating_whenPrePersist_thenIdIsGenerated() {
        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Great doctor!"
        );

        rating.onCreate();

        assertNotNull(rating.getId());
        assertTrue(rating.getId() instanceof UUID);
    }

    @Test
    void givenRatingWithExistingId_whenPrePersist_thenIdNotChanged() {
        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Great doctor!"
        );
        rating.setId(testRatingId);

        rating.onCreate();

        assertEquals(testRatingId, rating.getId());
    }

    @Test
    void givenRatingWithExistingTimestamps_whenPrePersist_thenTimestampsNotChanged() {
        LocalDateTime existingCreatedAt = LocalDateTime.now().minusDays(1);
        LocalDateTime existingUpdatedAt = LocalDateTime.now().minusHours(1);

        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Great doctor!"
        );
        rating.setCreatedAt(existingCreatedAt);
        rating.setUpdatedAt(existingUpdatedAt);

        rating.onCreate();

        assertEquals(existingCreatedAt, rating.getCreatedAt());
        assertEquals(existingUpdatedAt, rating.getUpdatedAt());
    }

    @Test
    void givenRatingWithNullTimestamps_whenPrePersist_thenTimestampsGenerated() {
        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Great doctor!"
        );
        rating.setCreatedAt(null);
        rating.setUpdatedAt(null);

        LocalDateTime beforeOnCreate = LocalDateTime.now();
        rating.onCreate();
        LocalDateTime afterOnCreate = LocalDateTime.now();

        assertNotNull(rating.getCreatedAt());
        assertNotNull(rating.getUpdatedAt());
        assertTrue(rating.getCreatedAt().isAfter(beforeOnCreate) || rating.getCreatedAt().isEqual(beforeOnCreate));
        assertTrue(rating.getCreatedAt().isBefore(afterOnCreate) || rating.getCreatedAt().isEqual(afterOnCreate));
    }

    @Test
    void givenNoArgsConstructor_whenCreateRating_thenAllFieldsNull() {
        Rating rating = new Rating();

        assertNull(rating.getId());
        assertNull(rating.getIdDokter());
        assertNull(rating.getIdPasien());
        assertNull(rating.getIdJadwalKonsultasi());
        assertNull(rating.getRatingScore());
        assertNull(rating.getUlasan());
        assertNull(rating.getCreatedAt());
        assertNull(rating.getUpdatedAt());
    }

    @Test
    void givenAllArgsConstructor_whenCreateRating_thenAllFieldsSet() {
        LocalDateTime now = LocalDateTime.now();

        Rating rating = new Rating(
                testRatingId,
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Test review",
                now,
                now
        );

        assertEquals(testRatingId, rating.getId());
        assertEquals(testDokterId, rating.getIdDokter());
        assertEquals(testPasienId, rating.getIdPasien());
        assertEquals(testJadwalId, rating.getIdJadwalKonsultasi());
        assertEquals(4, rating.getRatingScore());
        assertEquals("Test review", rating.getUlasan());
        assertEquals(now, rating.getCreatedAt());
        assertEquals(now, rating.getUpdatedAt());
    }

    @Test
    void givenSettersAndGetters_whenUsed_thenWorkCorrectly() {
        Rating rating = new Rating();
        LocalDateTime now = LocalDateTime.now();

        rating.setId(testRatingId);
        rating.setIdDokter(testDokterId);
        rating.setIdPasien(testPasienId);
        rating.setIdJadwalKonsultasi(testJadwalId);
        rating.setRatingScore(4);
        rating.setUlasan("Test review");
        rating.setCreatedAt(now);
        rating.setUpdatedAt(now);

        assertEquals(testRatingId, rating.getId());
        assertEquals(testDokterId, rating.getIdDokter());
        assertEquals(testPasienId, rating.getIdPasien());
        assertEquals(testJadwalId, rating.getIdJadwalKonsultasi());
        assertEquals(4, rating.getRatingScore());
        assertEquals("Test review", rating.getUlasan());
        assertEquals(now, rating.getCreatedAt());
        assertEquals(now, rating.getUpdatedAt());
    }

    @Test
    void givenTwoRatingsWithSameData_whenCompareEquals_thenEqual() {
        LocalDateTime now = LocalDateTime.now();

        Rating rating1 = new Rating(
                testRatingId,
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Test review",
                now,
                now
        );

        Rating rating2 = new Rating(
                testRatingId,
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Test review",
                now,
                now
        );

        assertEquals(rating1, rating2);
        assertEquals(rating1.hashCode(), rating2.hashCode());
    }

    @Test
    void givenTwoRatingsWithDifferentData_whenCompareEquals_thenNotEqual() {
        Rating rating1 = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Test review"
        );

        Rating rating2 = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                5,
                "Test review"
        );

        assertNotEquals(rating1, rating2);
    }

    @Test
    void givenRating_whenToString_thenContainsAllFields() {
        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                "Test review"
        );

        String toString = rating.toString();

        assertTrue(toString.contains("Rating"));
        assertTrue(toString.contains(testDokterId.toString()));
        assertTrue(toString.contains(testPasienId.toString()));
        assertTrue(toString.contains(testJadwalId.toString()));
        assertTrue(toString.contains("4"));
        assertTrue(toString.contains("Test review"));
    }

    @Test
    void givenLongUlasan_whenCreateRating_thenSuccess() {
        String longUlasan = "A".repeat(1000);

        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                4,
                longUlasan
        );

        assertEquals(longUlasan, rating.getUlasan());
    }

    @Test
    void givenSpecialCharactersInUlasan_whenCreateRating_thenSuccess() {
        String specialUlasan = "Great doctor! 👨‍⚕️ Very professional & knowledgeable. 5/5 ⭐⭐⭐⭐⭐";

        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                5,
                specialUlasan
        );

        assertEquals(specialUlasan, rating.getUlasan());
    }

    @Test
    void givenMultipleUpdates_whenUpdateFrom_thenUpdatedAtChanges() {
        Rating original = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                3,
                "Original"
        );

        Rating update1 = new Rating();
        update1.setRatingScore(4);
        update1.setUlasan("First update");

        original.updateFrom(update1);
        LocalDateTime firstUpdateTime = original.getUpdatedAt();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Rating update2 = new Rating();
        update2.setRatingScore(5);
        update2.setUlasan("Second update");

        original.updateFrom(update2);

        assertEquals(5, original.getRatingScore());
        assertEquals("Second update", original.getUlasan());
        assertTrue(original.getUpdatedAt().isAfter(firstUpdateTime) ||
                original.getUpdatedAt().isEqual(firstUpdateTime));
    }

    @Test
    void givenCustomConstructor_whenNullUUIDValues_thenSuccess() {
        Rating rating = new Rating(
                null,
                null,
                null,
                4,
                "Test"
        );

        assertNull(rating.getIdDokter());
        assertNull(rating.getIdPasien());
        assertNull(rating.getIdJadwalKonsultasi());
        assertEquals(4, rating.getRatingScore());
        assertEquals("Test", rating.getUlasan());
    }

    @Test
    void givenRating_whenUpdateRatingScoreDirectly_thenValidationApplied() {
        Rating rating = new Rating(
                testDokterId,
                testPasienId,
                testJadwalId,
                3,
                "Test"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            rating.setRatingScore(6);
        });

        assertDoesNotThrow(() -> {
            rating.setRatingScore(4);
        });
        assertEquals(4, rating.getRatingScore());
    }
}