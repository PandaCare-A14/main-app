package com.pandacare.mainapp.rating.observer;

import com.pandacare.mainapp.rating.model.DoctorStatistics;
import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.repository.DoctorStatisticsRepository;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private UUID DOCTOR_ID;
    private UUID PATIENT_ID;
    private UUID CONSULTATION_ID;
    private UUID RATING_ID;
    private final Double AVERAGE_RATING = 4.5;
    private final Integer TOTAL_RATINGS = 10;

    @BeforeEach
    void setUp() {
        DOCTOR_ID = UUID.randomUUID();
        PATIENT_ID = UUID.randomUUID();
        CONSULTATION_ID = UUID.randomUUID();
        RATING_ID = UUID.randomUUID();

        testRating = new Rating();
        testRating.setId(RATING_ID);
        testRating.setIdDokter(DOCTOR_ID);
        testRating.setIdPasien(PATIENT_ID);
        testRating.setIdJadwalKonsultasi(CONSULTATION_ID);
        testRating.setRatingScore(5);
        testRating.setUlasan("Excellent service");
        testRating.setCreatedAt(LocalDateTime.now());
        testRating.setUpdatedAt(LocalDateTime.now());

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
        try (MockedStatic<RatingSubject> mockedStatic = mockStatic(RatingSubject.class)) {
            mockedStatic.when(RatingSubject::getInstance).thenReturn(ratingSubject);

            new DoctorStatisticsObserver(doctorStatisticsRepository, ratingRepository);

            verify(ratingSubject).attach(any(DoctorStatisticsObserver.class));
        }
    }

    @Test
    @DisplayName("Should update existing statistics when rating is created")
    void shouldUpdateExistingStatistics_WhenRatingIsCreated() {
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.of(existingStatistics));
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(existingStatistics);

        observer.onRatingCreated(testRating);

        verify(ratingRepository).calculateAverageRatingByDokter(DOCTOR_ID);
        verify(ratingRepository).countRatingsByDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).findByIdDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should create new statistics when rating is created for new doctor")
    void shouldCreateNewStatistics_WhenRatingIsCreatedForNewDoctor() {
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(new DoctorStatistics());

        observer.onRatingCreated(testRating);

        verify(ratingRepository).calculateAverageRatingByDokter(DOCTOR_ID);
        verify(ratingRepository).countRatingsByDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).findByIdDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should handle null average rating gracefully")
    void shouldHandleNullAverageRating_Gracefully() {
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(null);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(new DoctorStatistics());

        observer.onRatingCreated(testRating);

        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should handle null total ratings gracefully")
    void shouldHandleNullTotalRatings_Gracefully() {
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(null);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(new DoctorStatistics());

        observer.onRatingCreated(testRating);

        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should update statistics when rating is updated")
    void shouldUpdateStatistics_WhenRatingIsUpdated() {
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.of(existingStatistics));
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(existingStatistics);

        observer.onRatingUpdated(testRating);

        verify(ratingRepository).calculateAverageRatingByDokter(DOCTOR_ID);
        verify(ratingRepository).countRatingsByDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should update statistics when rating is deleted")
    void shouldUpdateStatistics_WhenRatingIsDeleted() {
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.of(existingStatistics));
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(existingStatistics);

        observer.onRatingDeleted(testRating);

        verify(ratingRepository).calculateAverageRatingByDokter(DOCTOR_ID);
        verify(ratingRepository).countRatingsByDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should not throw exception when repository operations fail")
    void shouldNotThrowException_WhenRepositoryOperationsFail() {
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID))
                .thenThrow(new RuntimeException("Database error"));

        observer.onRatingCreated(testRating);

        verify(ratingRepository).calculateAverageRatingByDokter(DOCTOR_ID);
        verify(doctorStatisticsRepository, never()).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should handle repository save failure gracefully")
    void shouldHandleRepositorySaveFailure_Gracefully() {
        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenThrow(new RuntimeException("Save failed"));

        observer.onRatingCreated(testRating);

        verify(doctorStatisticsRepository).save(any(DoctorStatistics.class));
    }

    @Test
    @DisplayName("Should preserve original created timestamp when updating existing statistics")
    void shouldPreserveOriginalCreatedTimestamp_WhenUpdatingExistingStatistics() {
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(30);
        existingStatistics.setCreatedAt(originalCreatedAt);

        when(ratingRepository.calculateAverageRatingByDokter(DOCTOR_ID)).thenReturn(AVERAGE_RATING);
        when(ratingRepository.countRatingsByDokter(DOCTOR_ID)).thenReturn(TOTAL_RATINGS);
        when(doctorStatisticsRepository.findByIdDokter(DOCTOR_ID)).thenReturn(Optional.of(existingStatistics));
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class))).thenReturn(existingStatistics);

        observer.onRatingCreated(testRating);

        verify(doctorStatisticsRepository).save(argThat(stats ->
                stats.getCreatedAt().equals(originalCreatedAt)
        ));
    }
}
