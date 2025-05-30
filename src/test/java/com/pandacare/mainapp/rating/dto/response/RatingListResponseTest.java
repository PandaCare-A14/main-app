package com.pandacare.mainapp.rating.dto.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RatingListResponseTest {

    @Test
    void testEquals() {
        List<RatingResponse> ratings = Arrays.asList(
                new RatingResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, "Great", LocalDateTime.now(), LocalDateTime.now())
        );

        RatingListResponse response1 = new RatingListResponse(4.5, 10, ratings);
        RatingListResponse response2 = new RatingListResponse(4.5, 10, ratings);
        RatingListResponse response3 = new RatingListResponse(3.5, 5, ratings);

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertNotEquals(response1, null);
        assertNotEquals(response1, new Object());
    }

    @Test
    void testHashCode() {
        List<RatingResponse> ratings = Arrays.asList(
                new RatingResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 4, "Good", LocalDateTime.now(), LocalDateTime.now())
        );

        RatingListResponse response1 = new RatingListResponse(4.0, 8, ratings);
        RatingListResponse response2 = new RatingListResponse(4.0, 8, ratings);

        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testCanEqual() {
        RatingListResponse response1 = new RatingListResponse();
        RatingListResponse response2 = new RatingListResponse();
        Object other = new Object();

        assertTrue(response1.canEqual(response2));
        assertFalse(response1.canEqual(other));
    }

    @Test
    void testToString() {
        List<RatingResponse> ratings = Arrays.asList(
                new RatingResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 3, "Average", LocalDateTime.now(), LocalDateTime.now())
        );

        RatingListResponse response = new RatingListResponse(3.8, 15, ratings);
        String result = response.toString();

        assertTrue(result.contains("RatingListResponse"));
        assertTrue(result.contains("averageRating=3.8"));
        assertTrue(result.contains("totalRatings=15"));
        assertTrue(result.contains("ratings="));
    }

    @Test
    void testSetAverageRating() {
        RatingListResponse response = new RatingListResponse();
        response.setAverageRating(4.2);

        assertEquals(4.2, response.getAverageRating());
    }

    @Test
    void testSetTotalRatings() {
        RatingListResponse response = new RatingListResponse();
        response.setTotalRatings(25);

        assertEquals(25, response.getTotalRatings());
    }

    @Test
    void testSetRatings() {
        RatingListResponse response = new RatingListResponse();
        List<RatingResponse> ratings = Arrays.asList(
                new RatingResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, "Excellent", LocalDateTime.now(), LocalDateTime.now()),
                new RatingResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 4, "Good", LocalDateTime.now(), LocalDateTime.now())
        );

        response.setRatings(ratings);

        assertEquals(ratings, response.getRatings());
        assertEquals(2, response.getRatings().size());
    }

    @Test
    void testNoArgsConstructor() {
        RatingListResponse response = new RatingListResponse();

        assertNull(response.getAverageRating());
        assertNull(response.getTotalRatings());
        assertNull(response.getRatings());
    }

    @Test
    void testAllArgsConstructor() {
        List<RatingResponse> ratings = Arrays.asList(
                new RatingResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 2, "Poor", LocalDateTime.now(), LocalDateTime.now())
        );

        RatingListResponse response = new RatingListResponse(2.5, 6, ratings);

        assertEquals(2.5, response.getAverageRating());
        assertEquals(6, response.getTotalRatings());
        assertEquals(ratings, response.getRatings());
        assertEquals(1, response.getRatings().size());
    }

    @Test
    void testGettersWithNullValues() {
        RatingListResponse response = new RatingListResponse(null, null, null);

        assertNull(response.getAverageRating());
        assertNull(response.getTotalRatings());
        assertNull(response.getRatings());
    }

    @Test
    void testSettersWithNullValues() {
        RatingListResponse response = new RatingListResponse();

        response.setAverageRating(null);
        response.setTotalRatings(null);
        response.setRatings(null);

        assertNull(response.getAverageRating());
        assertNull(response.getTotalRatings());
        assertNull(response.getRatings());
    }
}