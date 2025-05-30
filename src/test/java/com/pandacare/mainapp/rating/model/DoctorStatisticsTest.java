package com.pandacare.mainapp.rating.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DoctorStatisticsTest {

    private UUID testDoctorId;
    private Double testAverageRating;
    private Integer testTotalRatings;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDoctorId = UUID.randomUUID();
        testAverageRating = 4.5;
        testTotalRatings = 25;
        testDateTime = LocalDateTime.now();
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyObject() {
        DoctorStatistics stats = new DoctorStatistics();

        assertNotNull(stats);
        assertNull(stats.getIdDokter());
        assertNull(stats.getAverageRating());
        assertNull(stats.getTotalRatings());
        assertNull(stats.getCreatedAt());
        assertNull(stats.getUpdatedAt());
    }

    @Test
    void allArgsConstructor_ShouldCreateObjectWithAllFields() {
        DoctorStatistics stats = new DoctorStatistics(
                testDoctorId,
                testAverageRating,
                testTotalRatings,
                testDateTime,
                testDateTime
        );

        assertEquals(testDoctorId, stats.getIdDokter());
        assertEquals(testAverageRating, stats.getAverageRating());
        assertEquals(testTotalRatings, stats.getTotalRatings());
        assertEquals(testDateTime, stats.getCreatedAt());
        assertEquals(testDateTime, stats.getUpdatedAt());
    }

    @Test
    void customConstructor_ShouldCreateObjectWithTimestamps() {
        LocalDateTime beforeCreation = LocalDateTime.now();

        DoctorStatistics stats = new DoctorStatistics(
                testDoctorId,
                testAverageRating,
                testTotalRatings
        );

        LocalDateTime afterCreation = LocalDateTime.now();

        assertEquals(testDoctorId, stats.getIdDokter());
        assertEquals(testAverageRating, stats.getAverageRating());
        assertEquals(testTotalRatings, stats.getTotalRatings());
        assertNotNull(stats.getCreatedAt());
        assertNotNull(stats.getUpdatedAt());
        assertTrue(stats.getCreatedAt().isAfter(beforeCreation) || stats.getCreatedAt().isEqual(beforeCreation));
        assertTrue(stats.getCreatedAt().isBefore(afterCreation) || stats.getCreatedAt().isEqual(afterCreation));

        // Check that createdAt and updatedAt are very close (within 1 second)
        long timeDifferenceNanos = Math.abs(
                stats.getCreatedAt().getNano() - stats.getUpdatedAt().getNano()
        );
        assertTrue(timeDifferenceNanos < 1000000000, "CreatedAt and updatedAt should be very close in time");
        assertEquals(stats.getCreatedAt().truncatedTo(java.time.temporal.ChronoUnit.SECONDS),
                stats.getUpdatedAt().truncatedTo(java.time.temporal.ChronoUnit.SECONDS));
    }

    @Test
    void updateFrom_ShouldUpdateAverageRatingAndTotalRatings() {
        DoctorStatistics originalStats = new DoctorStatistics(
                testDoctorId,
                4.0,
                20
        );

        DoctorStatistics newStats = new DoctorStatistics(
                UUID.randomUUID(),
                4.8,
                30
        );

        LocalDateTime originalCreatedAt = originalStats.getCreatedAt();
        LocalDateTime beforeUpdate = LocalDateTime.now();

        originalStats.updateFrom(newStats);

        LocalDateTime afterUpdate = LocalDateTime.now();

        assertEquals(testDoctorId, originalStats.getIdDokter());
        assertEquals(4.8, originalStats.getAverageRating());
        assertEquals(30, originalStats.getTotalRatings());
        assertEquals(originalCreatedAt, originalStats.getCreatedAt());
        assertNotEquals(originalCreatedAt, originalStats.getUpdatedAt());
        assertTrue(originalStats.getUpdatedAt().isAfter(beforeUpdate) || originalStats.getUpdatedAt().isEqual(beforeUpdate));
        assertTrue(originalStats.getUpdatedAt().isBefore(afterUpdate) || originalStats.getUpdatedAt().isEqual(afterUpdate));
    }

    @Test
    void updateFrom_WithNullValues_ShouldUpdateToNull() {
        DoctorStatistics originalStats = new DoctorStatistics(
                testDoctorId,
                4.0,
                20
        );

        DoctorStatistics newStats = new DoctorStatistics();
        newStats.setAverageRating(null);
        newStats.setTotalRatings(null);

        originalStats.updateFrom(newStats);

        assertNull(originalStats.getAverageRating());
        assertNull(originalStats.getTotalRatings());
        assertEquals(testDoctorId, originalStats.getIdDokter());
    }

    @Test
    void updateFrom_WithZeroValues_ShouldUpdateToZero() {
        DoctorStatistics originalStats = new DoctorStatistics(
                testDoctorId,
                4.0,
                20
        );

        DoctorStatistics newStats = new DoctorStatistics();
        newStats.setAverageRating(0.0);
        newStats.setTotalRatings(0);

        originalStats.updateFrom(newStats);

        assertEquals(0.0, originalStats.getAverageRating());
        assertEquals(0, originalStats.getTotalRatings());
        assertEquals(testDoctorId, originalStats.getIdDokter());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        DoctorStatistics stats = new DoctorStatistics();

        stats.setIdDokter(testDoctorId);
        stats.setAverageRating(testAverageRating);
        stats.setTotalRatings(testTotalRatings);
        stats.setCreatedAt(testDateTime);
        stats.setUpdatedAt(testDateTime);

        assertEquals(testDoctorId, stats.getIdDokter());
        assertEquals(testAverageRating, stats.getAverageRating());
        assertEquals(testTotalRatings, stats.getTotalRatings());
        assertEquals(testDateTime, stats.getCreatedAt());
        assertEquals(testDateTime, stats.getUpdatedAt());
    }

    @Test
    void equals_SameAllFields_ShouldBeEqual() {
        DoctorStatistics stats1 = new DoctorStatistics(
                testDoctorId,
                4.0,
                20,
                testDateTime,
                testDateTime
        );

        DoctorStatistics stats2 = new DoctorStatistics(
                testDoctorId,
                4.0,
                20,
                testDateTime,
                testDateTime
        );

        assertEquals(stats1, stats2);
    }

    @Test
    void equals_DifferentAverageRating_ShouldNotBeEqual() {
        DoctorStatistics stats1 = new DoctorStatistics(
                testDoctorId,
                4.0,
                20
        );

        DoctorStatistics stats2 = new DoctorStatistics(
                testDoctorId,
                4.5,
                20
        );

        assertNotEquals(stats1, stats2);
    }

    @Test
    void equals_DifferentTotalRatings_ShouldNotBeEqual() {
        DoctorStatistics stats1 = new DoctorStatistics(
                testDoctorId,
                4.0,
                20
        );

        DoctorStatistics stats2 = new DoctorStatistics(
                testDoctorId,
                4.0,
                25
        );

        assertNotEquals(stats1, stats2);
    }

    @Test
    void equals_DifferentDoctorId_ShouldNotBeEqual() {
        DoctorStatistics stats1 = new DoctorStatistics(
                testDoctorId,
                4.0,
                20
        );

        DoctorStatistics stats2 = new DoctorStatistics(
                UUID.randomUUID(),
                4.0,
                20
        );

        assertNotEquals(stats1, stats2);
    }

    @Test
    void hashCode_SameAllFields_ShouldBeSame() {
        DoctorStatistics stats1 = new DoctorStatistics(
                testDoctorId,
                4.0,
                20,
                testDateTime,
                testDateTime
        );

        DoctorStatistics stats2 = new DoctorStatistics(
                testDoctorId,
                4.0,
                20,
                testDateTime,
                testDateTime
        );

        assertEquals(stats1.hashCode(), stats2.hashCode());
    }

    @Test
    void hashCode_DifferentFields_ShouldBeDifferent() {
        DoctorStatistics stats1 = new DoctorStatistics(
                testDoctorId,
                4.0,
                20
        );

        DoctorStatistics stats2 = new DoctorStatistics(
                testDoctorId,
                4.5,
                25
        );

        assertNotEquals(stats1.hashCode(), stats2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        DoctorStatistics stats = new DoctorStatistics(
                testDoctorId,
                testAverageRating,
                testTotalRatings
        );

        String toString = stats.toString();

        assertTrue(toString.contains(testDoctorId.toString()));
        assertTrue(toString.contains(testAverageRating.toString()));
        assertTrue(toString.contains(testTotalRatings.toString()));
        assertTrue(toString.contains("DoctorStatistics"));
    }

    @Test
    void updateFrom_MultipleUpdates_ShouldWorkCorrectly() {
        DoctorStatistics stats = new DoctorStatistics(
                testDoctorId,
                4.0,
                20
        );

        LocalDateTime firstCreatedAt = stats.getCreatedAt();

        DoctorStatistics update1 = new DoctorStatistics();
        update1.setAverageRating(4.2);
        update1.setTotalRatings(22);

        stats.updateFrom(update1);
        LocalDateTime firstUpdateTime = stats.getUpdatedAt();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        DoctorStatistics update2 = new DoctorStatistics();
        update2.setAverageRating(4.5);
        update2.setTotalRatings(25);

        stats.updateFrom(update2);

        assertEquals(4.5, stats.getAverageRating());
        assertEquals(25, stats.getTotalRatings());
        assertEquals(firstCreatedAt, stats.getCreatedAt());
        assertTrue(stats.getUpdatedAt().isAfter(firstUpdateTime) || stats.getUpdatedAt().isEqual(firstUpdateTime));
    }

    @Test
    void customConstructor_WithNullValues_ShouldCreateObjectWithNulls() {
        DoctorStatistics stats = new DoctorStatistics(
                testDoctorId,
                null,
                null
        );

        assertEquals(testDoctorId, stats.getIdDokter());
        assertNull(stats.getAverageRating());
        assertNull(stats.getTotalRatings());
        assertNotNull(stats.getCreatedAt());
        assertNotNull(stats.getUpdatedAt());
    }

    @Test
    void customConstructor_WithDecimalRating_ShouldStoreCorrectly() {
        Double decimalRating = 4.567;

        DoctorStatistics stats = new DoctorStatistics(
                testDoctorId,
                decimalRating,
                testTotalRatings
        );

        assertEquals(decimalRating, stats.getAverageRating());
    }

    @Test
    void updateFrom_WithLargeNumbers_ShouldHandleCorrectly() {
        DoctorStatistics stats = new DoctorStatistics(
                testDoctorId,
                4.0,
                20
        );

        DoctorStatistics largeUpdate = new DoctorStatistics();
        largeUpdate.setAverageRating(4.999999);
        largeUpdate.setTotalRatings(Integer.MAX_VALUE);

        stats.updateFrom(largeUpdate);

        assertEquals(4.999999, stats.getAverageRating());
        assertEquals(Integer.MAX_VALUE, stats.getTotalRatings());
    }

    @Test
    void updateFrom_WithMinimumValues_ShouldHandleCorrectly() {
        DoctorStatistics stats = new DoctorStatistics(
                testDoctorId,
                4.0,
                20
        );

        DoctorStatistics minUpdate = new DoctorStatistics();
        minUpdate.setAverageRating(0.0);
        minUpdate.setTotalRatings(0);

        stats.updateFrom(minUpdate);

        assertEquals(0.0, stats.getAverageRating());
        assertEquals(0, stats.getTotalRatings());
    }
}