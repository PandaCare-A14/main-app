package com.pandacare.mainapp.rating.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.pandacare.mainapp.rating.model.Rating;

/**
 * Test class for RatingRepository
 */
@ExtendWith(SpringExtension.class)
public class RatingRepositoryTest {

    private RatingRepository ratingRepository;

    @BeforeEach
    public void setup() {
        ratingRepository = new RatingRepositoryImpl();

        // Populate with test data
        Rating rating1 = new Rating(
                "RTG12345",
                "DOC12345",
                "PAT7890",
                5,
                "Dokter sangat profesional dan ramah. Penjelasan diagnosa sangat detail.",
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5)
        );

        Rating rating2 = new Rating(
                "RTG12346",
                "DOC12345",
                "PAT7891",
                4,
                "Pelayanan memuaskan dan dokter memiliki pengetahuan yang luas.",
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(3)
        );

        Rating rating3 = new Rating(
                "RTG12347",
                "DOC12345",
                "PAT7892",
                4,
                "Dokter responsif dan memberikan solusi yang tepat.",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        Rating rating4 = new Rating(
                "RTG12348",
                "DOC12346",
                "PAT7890",
                4,
                "Dokter memberikan saran yang bermanfaat.",
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(2)
        );

        // Save ratings to repository
        ratingRepository.save(rating1);
        ratingRepository.save(rating2);
        ratingRepository.save(rating3);
        ratingRepository.save(rating4);
    }

    @Test
    @DisplayName("Should find ratings by patient ID")
    public void testFindByIdPasien() {
        // Act
        List<Rating> ratings = ratingRepository.findByIdPasien("PAT7890");

        // Assert
        assertEquals(2, ratings.size());
        assertTrue(ratings.stream().allMatch(r -> r.getIdPasien().equals("PAT7890")));
    }

    @Test
    @DisplayName("Should find ratings by doctor ID")
    public void testFindByIdDokter() {
        // Act
        List<Rating> ratings = ratingRepository.findByIdDokter("DOC12345");

        // Assert
        assertEquals(3, ratings.size());
        assertTrue(ratings.stream().allMatch(r -> r.getIdDokter().equals("DOC12345")));
    }

    @Test
    @DisplayName("Should find rating by patient ID and doctor ID")
    public void testFindByIdPasienAndIdDokter() {
        // Act
        Optional<Rating> rating = ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345");

        // Assert
        assertTrue(rating.isPresent());
        assertEquals("PAT7890", rating.get().getIdPasien());
        assertEquals("DOC12345", rating.get().getIdDokter());
    }

    @Test
    @DisplayName("Should calculate average rating for a doctor")
    public void testCalculateAverageRatingByDokter() {
        // Act
        Double averageRating = ratingRepository.calculateAverageRatingByDokter("DOC12345");

        // Assert
        assertNotNull(averageRating);
        assertEquals(4.33, averageRating, 0.01); // (5 + 4 + 4) / 3 = 4.33
    }

    @Test
    @DisplayName("Should count ratings for a doctor")
    public void testCountByIdDokter() {
        // Act
        long count = ratingRepository.countByIdDokter("DOC12345");

        // Assert
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should save new rating and assign ID")
    public void testSaveRating() {
        // Arrange
        Rating rating = new Rating(
                null,
                "DOC12347",
                "PAT7893",
                5,
                "Great doctor!",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act
        Rating savedRating = ratingRepository.save(rating);

        // Assert
        assertNotNull(savedRating.getId());
        assertEquals("DOC12347", savedRating.getIdDokter());
        assertEquals("PAT7893", savedRating.getIdPasien());
        assertEquals(5, savedRating.getRatingScore());
        assertEquals("Great doctor!", savedRating.getUlasan());

        // Verify it can be retrieved
        Optional<Rating> retrievedRating = ratingRepository.findByIdPasienAndIdDokter("PAT7893", "DOC12347");
        assertTrue(retrievedRating.isPresent());
        assertEquals(savedRating.getId(), retrievedRating.get().getId());
    }

    @Test
    @DisplayName("Should delete rating by patient ID and doctor ID")
    public void testDeleteByIdPasienAndIdDokter() {
        // Arrange - Verify rating exists before delete
        Optional<Rating> ratingBeforeDelete = ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345");
        assertTrue(ratingBeforeDelete.isPresent());

        // Act
        ratingRepository.deleteByIdPasienAndIdDokter("PAT7890", "DOC12345");

        // Assert
        Optional<Rating> ratingAfterDelete = ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345");
        assertFalse(ratingAfterDelete.isPresent());
    }
}