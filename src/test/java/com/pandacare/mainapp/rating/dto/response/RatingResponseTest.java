package com.pandacare.mainapp.rating.dto.response;

import com.pandacare.mainapp.rating.model.Rating;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RatingResponseTest {

    @Test
    void testEquals() {
        UUID id = UUID.randomUUID();
        UUID idDokter = UUID.randomUUID();
        UUID idPasien = UUID.randomUUID();
        UUID idJadwal = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        RatingResponse response1 = new RatingResponse(id, idDokter, idPasien, idJadwal, 5, "Great", now, now);
        RatingResponse response2 = new RatingResponse(id, idDokter, idPasien, idJadwal, 5, "Great", now, now);
        RatingResponse response3 = new RatingResponse(UUID.randomUUID(), idDokter, idPasien, idJadwal, 5, "Great", now, now);

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertNotEquals(response1, null);
        assertNotEquals(response1, new Object());
    }

    @Test
    void testHashCode() {
        UUID id = UUID.randomUUID();
        UUID idDokter = UUID.randomUUID();
        UUID idPasien = UUID.randomUUID();
        UUID idJadwal = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        RatingResponse response1 = new RatingResponse(id, idDokter, idPasien, idJadwal, 5, "Great", now, now);
        RatingResponse response2 = new RatingResponse(id, idDokter, idPasien, idJadwal, 5, "Great", now, now);

        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testCanEqual() {
        RatingResponse response1 = new RatingResponse();
        RatingResponse response2 = new RatingResponse();
        Object other = new Object();

        assertTrue(response1.canEqual(response2));
        assertFalse(response1.canEqual(other));
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        RatingResponse response = new RatingResponse(id, null, null, null, 5, "Great", null, null);
        String result = response.toString();

        assertTrue(result.contains("RatingResponse"));
        assertTrue(result.contains("id=" + id));
        assertTrue(result.contains("ratingScore=5"));
        assertTrue(result.contains("ulasan=Great"));
    }

    @Test
    void testConstructorFromRating() {
        UUID id = UUID.randomUUID();
        UUID idDokter = UUID.randomUUID();
        UUID idPasien = UUID.randomUUID();
        UUID idJadwal = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Rating rating = new Rating();
        rating.setId(id);
        rating.setIdDokter(idDokter);
        rating.setIdPasien(idPasien);
        rating.setIdJadwalKonsultasi(idJadwal);
        rating.setRatingScore(4);
        rating.setUlasan("Good service");
        rating.setCreatedAt(createdAt);
        rating.setUpdatedAt(updatedAt);

        RatingResponse response = new RatingResponse(rating);

        assertEquals(id, response.getId());
        assertEquals(idDokter, response.getIdDokter());
        assertEquals(idPasien, response.getIdPasien());
        assertEquals(idJadwal, response.getIdJadwalKonsultasi());
        assertEquals(4, response.getRatingScore());
        assertEquals("Good service", response.getUlasan());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        RatingResponse response = new RatingResponse();
        UUID id = UUID.randomUUID();
        UUID idDokter = UUID.randomUUID();
        UUID idPasien = UUID.randomUUID();
        UUID idJadwal = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        response.setId(id);
        response.setIdDokter(idDokter);
        response.setIdPasien(idPasien);
        response.setIdJadwalKonsultasi(idJadwal);
        response.setRatingScore(3);
        response.setUlasan("Average");
        response.setCreatedAt(now);
        response.setUpdatedAt(now);

        assertEquals(id, response.getId());
        assertEquals(idDokter, response.getIdDokter());
        assertEquals(idPasien, response.getIdPasien());
        assertEquals(idJadwal, response.getIdJadwalKonsultasi());
        assertEquals(3, response.getRatingScore());
        assertEquals("Average", response.getUlasan());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        RatingResponse response = new RatingResponse();

        assertNull(response.getId());
        assertNull(response.getIdDokter());
        assertNull(response.getIdPasien());
        assertNull(response.getIdJadwalKonsultasi());
        assertNull(response.getRatingScore());
        assertNull(response.getUlasan());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID idDokter = UUID.randomUUID();
        UUID idPasien = UUID.randomUUID();
        UUID idJadwal = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        RatingResponse response = new RatingResponse(id, idDokter, idPasien, idJadwal, 5, "Excellent", now, now);

        assertEquals(id, response.getId());
        assertEquals(idDokter, response.getIdDokter());
        assertEquals(idPasien, response.getIdPasien());
        assertEquals(idJadwal, response.getIdJadwalKonsultasi());
        assertEquals(5, response.getRatingScore());
        assertEquals("Excellent", response.getUlasan());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }
}