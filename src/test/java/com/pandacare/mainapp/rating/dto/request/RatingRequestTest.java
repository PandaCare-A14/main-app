package com.pandacare.mainapp.rating.dto.request;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RatingRequestTest {

    @Test
    void testEquals() {
        UUID uuid = UUID.randomUUID();
        RatingRequest request1 = new RatingRequest(5, "Great service", uuid);
        RatingRequest request2 = new RatingRequest(5, "Great service", uuid);
        RatingRequest request3 = new RatingRequest(4, "Good service", uuid);

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());
    }

    @Test
    void testHashCode() {
        UUID uuid = UUID.randomUUID();
        RatingRequest request1 = new RatingRequest(5, "Great service", uuid);
        RatingRequest request2 = new RatingRequest(5, "Great service", uuid);

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testCanEqual() {
        RatingRequest request1 = new RatingRequest();
        RatingRequest request2 = new RatingRequest();
        Object other = new Object();

        assertTrue(request1.canEqual(request2));
        assertFalse(request1.canEqual(other));
    }

    @Test
    void testToString() {
        UUID uuid = UUID.randomUUID();
        RatingRequest request = new RatingRequest(5, "Great service", uuid);
        String result = request.toString();

        assertTrue(result.contains("RatingRequest"));
        assertTrue(result.contains("ratingScore=5"));
        assertTrue(result.contains("ulasan=Great service"));
        assertTrue(result.contains("idJadwalKonsultasi=" + uuid));
    }

    @Test
    void testSetRatingScore() {
        RatingRequest request = new RatingRequest();
        request.setRatingScore(4);

        assertEquals(4, request.getRatingScore());
    }

    @Test
    void testSetUlasan() {
        RatingRequest request = new RatingRequest();
        request.setUlasan("Good service");

        assertEquals("Good service", request.getUlasan());
    }
}