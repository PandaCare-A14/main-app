package com.pandacare.mainapp.rating.repository;

import com.pandacare.mainapp.rating.model.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RatingRepository using TDD approach
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class RatingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RatingRepository ratingRepository;

    private Rating testRating1;
    private Rating testRating2;
    private Rating testRating3;

    private final String DOCTOR_ID_1 = "dr001";
    private final String DOCTOR_ID_2 = "dr002";
    private final String PATIENT_ID_1 = "p001";
    private final String PATIENT_ID_2 = "p002";
    private final String CONSULTATION_ID_1 = "cons001";
    private final String CONSULTATION_ID_2 = "cons002";
    private final String CONSULTATION_ID_3 = "cons003";

    @BeforeEach
    void setUp() {
        // Create test ratings
        testRating1 = new Rating();
        testRating1.setId("rating001");
        testRating1.setIdDokter(DOCTOR_ID_1);
        testRating1.setIdPasien(PATIENT_ID_1);
        testRating1.setIdJadwalKonsultasi(CONSULTATION_ID_1);
        testRating1.setRatingScore(5);
        testRating1.setUlasan("Excellent service");
        testRating1.setCreatedAt(LocalDateTime.now());
        testRating1.setUpdatedAt(LocalDateTime.now());

        testRating2 = new Rating();
        testRating2.setId("rating002");
        testRating2.setIdDokter(DOCTOR_ID_1);
        testRating2.setIdPasien(PATIENT_ID_2);
        testRating2.setIdJadwalKonsultasi(CONSULTATION_ID_2);
        testRating2.setRatingScore(4);
        testRating2.setUlasan("Good service");
        testRating2.setCreatedAt(LocalDateTime.now());
        testRating2.setUpdatedAt(LocalDateTime.now());

        testRating3 = new Rating();
        testRating3.setId("rating003");
        testRating3.setIdDokter(DOCTOR_ID_2);
        testRating3.setIdPasien(PATIENT_ID_1);
        testRating3.setIdJadwalKonsultasi(CONSULTATION_ID_3);
        testRating3.setRatingScore(3);
        testRating3.setUlasan("Average service");
        testRating3.setCreatedAt(LocalDateTime.now());
        testRating3.setUpdatedAt(LocalDateTime.now());

        // Persist test data
        entityManager.persistAndFlush(testRating1);
        entityManager.persistAndFlush(testRating2);
        entityManager.persistAndFlush(testRating3);
    }

    @Test
    @DisplayName("Should find ratings by doctor ID")
    void shouldFindRatingsByDoctorId() {
        // Act
        List<Rating> ratings = ratingRepository.findByIdDokter(DOCTOR_ID_1);

        // Assert
        assertEquals(2, ratings.size());
        assertTrue(ratings.stream().allMatch(r -> r.getIdDokter().equals(DOCTOR_ID_1)));
        assertTrue(ratings.stream().anyMatch(r -> r.getId().equals("rating001")));
        assertTrue(ratings.stream().anyMatch(r -> r.getId().equals("rating002")));
    }

    @Test
    @DisplayName("Should return empty list when doctor has no ratings")
    void shouldReturnEmptyList_WhenDoctorHasNoRatings() {
        // Act
        List<Rating> ratings = ratingRepository.findByIdDokter("nonexistent-doctor");

        // Assert
        assertTrue(ratings.isEmpty());
    }

    @Test
    @DisplayName("Should find ratings by patient ID")
    void shouldFindRatingsByPatientId() {
        // Act
        List<Rating> ratings = ratingRepository.findByIdPasien(PATIENT_ID_1);

        // Assert
        assertEquals(2, ratings.size());
        assertTrue(ratings.stream().allMatch(r -> r.getIdPasien().equals(PATIENT_ID_1)));
        assertTrue(ratings.stream().anyMatch(r -> r.getId().equals("rating001")));
        assertTrue(ratings.stream().anyMatch(r -> r.getId().equals("rating003")));
    }

    @Test
    @DisplayName("Should return empty list when patient has no ratings")
    void shouldReturnEmptyList_WhenPatientHasNoRatings() {
        // Act
        List<Rating> ratings = ratingRepository.findByIdPasien("nonexistent-patient");

        // Assert
        assertTrue(ratings.isEmpty());
    }

    @Test
    @DisplayName("Should find rating by patient and consultation ID")
    void shouldFindRatingByPatientAndConsultationId() {
        // Act
        Optional<Rating> rating = ratingRepository.findByIdPasienAndIdJadwalKonsultasi(
                PATIENT_ID_1, CONSULTATION_ID_1);

        // Assert
        assertTrue(rating.isPresent());
        assertEquals("rating001", rating.get().getId());
        assertEquals(PATIENT_ID_1, rating.get().getIdPasien());
        assertEquals(CONSULTATION_ID_1, rating.get().getIdJadwalKonsultasi());
    }

    @Test
    @DisplayName("Should return empty when rating by patient and consultation not found")
    void shouldReturnEmpty_WhenRatingByPatientAndConsultationNotFound() {
        // Act
        Optional<Rating> rating = ratingRepository.findByIdPasienAndIdJadwalKonsultasi(
                "nonexistent-patient", "nonexistent-consultation");

        // Assert
        assertFalse(rating.isPresent());
    }

    @Test
    @DisplayName("Should calculate average rating for doctor")
    void shouldCalculateAverageRatingForDoctor() {
        // Act
        Double averageRating = ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID_1);

        // Assert
        assertNotNull(averageRating);
        assertEquals(4.5, averageRating, 0.01); // (5 + 4) / 2 = 4.5
    }

    @Test
    @DisplayName("Should return null when calculating average for doctor with no ratings")
    void shouldReturnNull_WhenCalculatingAverageForDoctorWithNoRatings() {
        // Act
        Double averageRating = ratingRepository.calculateAverageRatingByDokter("nonexistent-doctor");

        // Assert
        assertNull(averageRating);
    }

    @Test
    @DisplayName("Should count ratings for doctor")
    void shouldCountRatingsForDoctor() {
        // Act
        Integer count = ratingRepository.countRatingsByDokter(DOCTOR_ID_1);

        // Assert
        assertNotNull(count);
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should return zero when counting ratings for doctor with no ratings")
    void shouldReturnZero_WhenCountingRatingsForDoctorWithNoRatings() {
        // Act
        Integer count = ratingRepository.countRatingsByDokter("nonexistent-doctor");

        // Assert
        assertNotNull(count);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Should return true when rating exists by patient and consultation")
    void shouldReturnTrue_WhenRatingExistsByPatientAndConsultation() {
        // Act
        boolean exists = ratingRepository.existsByIdPasienAndIdJadwalKonsultasi(
                PATIENT_ID_1, CONSULTATION_ID_1);

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when rating does not exist by patient and consultation")
    void shouldReturnFalse_WhenRatingDoesNotExistByPatientAndConsultation() {
        // Act
        boolean exists = ratingRepository.existsByIdPasienAndIdJadwalKonsultasi(
                "nonexistent-patient", "nonexistent-consultation");

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should save and retrieve rating correctly")
    void shouldSaveAndRetrieveRatingCorrectly() {
        // Arrange
        Rating newRating = new Rating();
        newRating.setId("new-rating");
        newRating.setIdDokter("dr999");
        newRating.setIdPasien("p999");
        newRating.setIdJadwalKonsultasi("cons999");
        newRating.setRatingScore(5);
        newRating.setUlasan("New rating");
        newRating.setCreatedAt(LocalDateTime.now());
        newRating.setUpdatedAt(LocalDateTime.now());

        // Act
        Rating savedRating = ratingRepository.save(newRating);
        Optional<Rating> retrievedRating = ratingRepository.findById("new-rating");

        // Assert
        assertEquals(newRating.getId(), savedRating.getId());
        assertTrue(retrievedRating.isPresent());
        assertEquals("New rating", retrievedRating.get().getUlasan());
        assertEquals(5, retrievedRating.get().getRatingScore());
    }

    @Test
    @DisplayName("Should delete rating correctly")
    void shouldDeleteRatingCorrectly() {
        // Arrange
        assertTrue(ratingRepository.existsById("rating001"));

        // Act
        ratingRepository.deleteById("rating001");

        // Assert
        assertFalse(ratingRepository.existsById("rating001"));

        // Verify it doesn't affect other ratings
        assertTrue(ratingRepository.existsById("rating002"));
        assertTrue(ratingRepository.existsById("rating003"));
    }

    @Test
    @DisplayName("Should update rating correctly")
    void shouldUpdateRatingCorrectly() {
        // Arrange
        Optional<Rating> existingRating = ratingRepository.findById("rating001");
        assertTrue(existingRating.isPresent());

        Rating rating = existingRating.get();
        rating.setRatingScore(1);
        rating.setUlasan("Updated review");
        rating.setUpdatedAt(LocalDateTime.now());

        // Act
        Rating updatedRating = ratingRepository.save(rating);

        // Assert
        assertEquals(1, updatedRating.getRatingScore());
        assertEquals("Updated review", updatedRating.getUlasan());

        // Verify in database
        Optional<Rating> retrievedRating = ratingRepository.findById("rating001");
        assertTrue(retrievedRating.isPresent());
        assertEquals(1, retrievedRating.get().getRatingScore());
        assertEquals("Updated review", retrievedRating.get().getUlasan());
    }
}