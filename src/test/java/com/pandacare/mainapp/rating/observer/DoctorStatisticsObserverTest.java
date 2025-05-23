package com.pandacare.mainapp.rating.observer;

import com.pandacare.mainapp.rating.model.DoctorStatistics;
import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.repository.DoctorStatisticsRepository;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DoctorStatisticsObserver using TDD approach
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class DoctorStatisticsObserverTest {

    @Mock
    private DoctorStatisticsRepository doctorStatisticsRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingSubject ratingSubject;

    @InjectMocks
    private DoctorStatisticsObserver observer;

    private Rating testRating;
    private DoctorStatistics existingStatistics;

    private final String DOCTOR_ID = "dr001";
    private final String PATIENT_ID = "p001";
    private final String CONSULTATION_ID = "cons001";
    private final Double AVERAGE_RATING = 4.5;
    private final Integer TOTAL_RATINGS = 10;

    @BeforeEach
    void setUp() {
        // Setup test rating
        testRating = new Rating();
        testRating.setId("rating001");
        testRating.setIdDokter(DOCTOR_ID);
        testRating.setIdPasien(PATIENT_ID);
        testRating.setIdJadwalKonsultasi(CONSULTATION_ID);
        testRating.setRatingScore(5);
        testRating.setUlasan("Excellent service");
        testRating.setCreatedAt(LocalDateTime.now());
        testRating.setUpdatedAt(LocalDateTime.now());

        // Setup existing statistics
        existingStatistics = new DoctorStatistics();
        existingStatistics.setIdDokter(DOCTOR_ID);
        existingStatistics.setAverageRating(4.0);
        existingStatistics.setTotalRatings(9);
        existingStatistics.setCreatedAt(LocalDateTime.now().minusDays(30));
        existingStatistics.setUpdatedAt(LocalDateTime.now().minusHours(1));
    }

    @Test
    @DisplayName("Should register itself with RatingSubject during construction")
    void shouldRegisterItselfWithRatingSubject_DuringConstruction() {
        // Arrange & Act handled by @InjectMocks
        try (MockedStatic<RatingSubject> mockedStatic = mockStatic(RatingSubject.class)) {
            mockedStatic.when(RatingSubject::getInstance).thenReturn(ratingSubject);

            // Create new instance to test registration
            new DoctorStatisticsObserver(doctorStatisticsRepository, ratingRepository);

            // Assert
            verify(ratingSubject).attach(any(DoctorStatisticsObserver.class));
        }
    }

    @Test
    @DisplayName("Should update existing statistics when rating is created")
    void shouldUpdateExistingStatistics_WhenRatingIsCreated() {
        // Arrange
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.of(existingStatistics));
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(existingStatistics);

        // Act
        observer.onRatingCreated(testRating);

        // Assert
        verify(ratingRepository).calculateAverageRatingByDokter(DOCTOR_ID);
        verify(ratingRepository).countRatingsByDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).findByIdDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should create new statistics when rating is created for new doctor")
    void shouldCreateNewStatistics_WhenRatingIsCreatedForNewDoctor() {
        // Arrange
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(new DoctorStatistics());

        // Act
        observer.onRatingCreated(testRating);

        // Assert
        verify(ratingRepository).calculateAverageRatingByDokter(DOCTOR_ID);
        verify(ratingRepository).countRatingsByDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).findByIdDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should handle null average rating gracefully")
    void shouldHandleNullAverageRating_Gracefully() {
        // Arrange
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(null);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(new DoctorStatistics());

        // Act
        observer.onRatingCreated(testRating);

        // Assert - Should not throw exception and should save with 0.0 average
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should handle null total ratings gracefully")
    void shouldHandleNullTotalRatings_Gracefully() {
        // Arrange
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(null);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(new DoctorStatistics());

        // Act
        observer.onRatingCreated(testRating);

        // Assert - Should not throw exception and should save with 0 total
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should update statistics when rating is updated")
    void shouldUpdateStatistics_WhenRatingIsUpdated() {
        // Arrange
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.of(existingStatistics));
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(existingStatistics);

        // Act
        observer.onRatingUpdated(testRating);

        // Assert
        verify(ratingRepository).calculateAverageRatingByDokter(DOCTOR_ID);
        verify(ratingRepository).countRatingsByDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should update statistics when rating is deleted")
    void shouldUpdateStatistics_WhenRatingIsDeleted() {
        // Arrange
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.of(existingStatistics));
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(existingStatistics);

        // Act
        observer.onRatingDeleted(testRating);

        // Assert
        verify(ratingRepository).calculateAverageRatingByDokter(DOCTOR_ID);
        verify(ratingRepository).countRatingsByDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should not throw exception when repository operations fail")
    void shouldNotThrowException_WhenRepositoryOperationsFail() {
        // Arrange
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert - Should not throw exception
        observer.onRatingCreated(testRating);

        // Verify that error was handled gracefully
        verify(ratingRepository).calculateAverageRatingByDokter(DOCTOR_ID);
        // Should not proceed to save since calculation failed
        verify(doctorStatisticsRepository, never()).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should handle repository save failure gracefully")
    void shouldHandleRepositorySaveFailure_Gracefully() {
        // Arrange
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenThrow(new RuntimeException("Save failed"));

        // Act & Assert - Should not throw exception
        observer.onRatingCreated(testRating);

        // Verify that error was handled gracefully
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should preserve original created timestamp when updating existing statistics")
    void shouldPreserveOriginalCreatedTimestamp_WhenUpdatingExistingStatistics() {
        // Arrange
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(30);
        existingStatistics.setCreatedAt(originalCreatedAt);

        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.of(existingStatistics));
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(existingStatistics);

        // Act
        observer.onRatingCreated(testRating);

        // Assert
        verify(doctorStatisticsRepository).save(argThat(stats ->
                stats.getCreatedAt().equals(originalCreatedAt)
        ));
    }
}