package com.pandacare.mainapp.rating.repository;

import com.pandacare.mainapp.rating.model.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RatingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RatingRepository ratingRepository;

    private Rating testRating1;
    private Rating testRating2;
    private Rating testRating3;

    // Use constant UUIDs for testing to avoid random generation issues
    private final UUID DOCTOR_ID_1 = UUID.fromString("a0000000-0000-0000-0000-000000000001");
    private final UUID DOCTOR_ID_2 = UUID.fromString("a0000000-0000-0000-0000-000000000002");
    private final UUID PATIENT_ID_1 = UUID.fromString("b0000000-0000-0000-0000-000000000001");
    private final UUID PATIENT_ID_2 = UUID.fromString("b0000000-0000-0000-0000-000000000002");
    private final UUID CONSULTATION_ID_1 = UUID.fromString("c0000000-0000-0000-0000-000000000001");
    private final UUID CONSULTATION_ID_2 = UUID.fromString("c0000000-0000-0000-0000-000000000002");
    private final UUID CONSULTATION_ID_3 = UUID.fromString("c0000000-0000-0000-0000-000000000003");

    @BeforeEach
    void setUp() {
        // Clear previous test data
        ratingRepository.deleteAll();

        // Create test rating 1
        testRating1 = new Rating();
        testRating1.setId(UUID.fromString("d0000000-0000-0000-0000-000000000001"));
        testRating1.setIdDokter(DOCTOR_ID_1);
        testRating1.setIdPasien(PATIENT_ID_1);
        testRating1.setIdJadwalKonsultasi(CONSULTATION_ID_1);
        testRating1.setRatingScore(5);
        testRating1.setCreatedAt(LocalDateTime.now());
        entityManager.persist(testRating1);

        // Create test rating 2
        testRating2 = new Rating();
        testRating2.setId(UUID.fromString("d0000000-0000-0000-0000-000000000002"));
        testRating2.setIdDokter(DOCTOR_ID_1);
        testRating2.setIdPasien(PATIENT_ID_2);
        testRating2.setIdJadwalKonsultasi(CONSULTATION_ID_2);
        testRating2.setRatingScore(4);
        testRating2.setCreatedAt(LocalDateTime.now());
        entityManager.persist(testRating2);

        // Create test rating 3
        testRating3 = new Rating();
        testRating3.setId(UUID.fromString("d0000000-0000-0000-0000-000000000003"));
        testRating3.setIdDokter(DOCTOR_ID_2);
        testRating3.setIdPasien(PATIENT_ID_1);
        testRating3.setIdJadwalKonsultasi(CONSULTATION_ID_3);
        testRating3.setRatingScore(3);
        testRating3.setCreatedAt(LocalDateTime.now());
        entityManager.persist(testRating3);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find ratings by doctor ID")
    void findByIdDokter_ShouldReturnRatings() {
        // Act
        List<Rating> ratings = ratingRepository.findByIdDokter(DOCTOR_ID_1);

        // Assert
        assertEquals(2, ratings.size());
        assertTrue(ratings.stream().anyMatch(rating -> rating.getId().equals(testRating1.getId())));
        assertTrue(ratings.stream().anyMatch(rating -> rating.getId().equals(testRating2.getId())));
    }

    @Test
    @DisplayName("Should find ratings by patient ID")
    void findByIdPasien_ShouldReturnRatings() {
        // Act
        List<Rating> ratings = ratingRepository.findByIdPasien(PATIENT_ID_1);

        // Assert
        assertEquals(2, ratings.size());
        assertTrue(ratings.stream().anyMatch(rating -> rating.getId().equals(testRating1.getId())));
        assertTrue(ratings.stream().anyMatch(rating -> rating.getId().equals(testRating3.getId())));
    }

    @Test
    @DisplayName("Should find rating by patient ID and consultation ID")
    void findByIdPasienAndIdJadwalKonsultasi_ShouldReturnRating() {
        // Act
        Optional<Rating> rating = ratingRepository.findByIdPasienAndIdJadwalKonsultasi(PATIENT_ID_1, CONSULTATION_ID_1);

        // Assert
        assertTrue(rating.isPresent());
        assertEquals(testRating1.getId(), rating.get().getId());
    }

    @Test
    @DisplayName("Should calculate average rating by doctor")
    void calculateAverageRatingByDokter_ShouldReturnCorrectAverage() {
        // Act
        Double avgRating = ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID_1);

        // Assert
        assertNotNull(avgRating);
        assertEquals(4.5, avgRating, 0.01); // Average of 5 and 4
    }

    @Test
    @DisplayName("Should count ratings by doctor")
    void countRatingsByDokter_ShouldReturnCorrectCount() {
        // Act
        Integer count = ratingRepository.countRatingsByDokter(DOCTOR_ID_1);

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should check if rating exists by patient and consultation")
    void existsByIdPasienAndIdJadwalKonsultasi_ShouldReturnTrue() {
        // Act
        boolean exists = ratingRepository.existsByIdPasienAndIdJadwalKonsultasi(PATIENT_ID_1, CONSULTATION_ID_1);

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false for non-existent rating")
    void existsByIdPasienAndIdJadwalKonsultasi_ShouldReturnFalse() {
        // Act
        boolean exists = ratingRepository.existsByIdPasienAndIdJadwalKonsultasi(PATIENT_ID_2, CONSULTATION_ID_1);

        // Assert
        assertFalse(exists);
    }
}