package com.pandacare.mainapp.rating.service;

import com.pandacare.mainapp.rating.model.DoctorStatistics;
import com.pandacare.mainapp.rating.repository.DoctorStatisticsRepository;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorStatisticsServiceTest {

    @Mock
    private DoctorStatisticsRepository doctorStatisticsRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private DoctorStatisticsService doctorStatisticsService;

    private UUID testDoctorId;
    private DoctorStatistics existingStatistics;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDoctorId = UUID.randomUUID();
        testDateTime = LocalDateTime.now().minusDays(1);

        existingStatistics = new DoctorStatistics();
        existingStatistics.setIdDokter(testDoctorId);
        existingStatistics.setAverageRating(4.2);
        existingStatistics.setTotalRatings(15);
        existingStatistics.setCreatedAt(testDateTime);
        existingStatistics.setUpdatedAt(testDateTime);
    }

    // ========== getStatisticsByDoctor Tests ==========

    @Test
    void getStatisticsByDoctor_WhenStatisticsExist_ShouldReturnStatistics() {
        // Given
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.of(existingStatistics));

        // When
        Optional<DoctorStatistics> result = doctorStatisticsService.getStatisticsByDoctor(testDoctorId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getIdDokter()).isEqualTo(testDoctorId);
        assertThat(result.get().getAverageRating()).isEqualTo(4.2);
        assertThat(result.get().getTotalRatings()).isEqualTo(15);

        verify(doctorStatisticsRepository).findByIdDokter(testDoctorId);
    }

    @Test
    void getStatisticsByDoctor_WhenStatisticsNotExist_ShouldReturnEmpty() {
        // Given
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.empty());

        // When
        Optional<DoctorStatistics> result = doctorStatisticsService.getStatisticsByDoctor(testDoctorId);

        // Then
        assertThat(result).isEmpty();
        verify(doctorStatisticsRepository).findByIdDokter(testDoctorId);
    }

    @Test
    void getStatisticsByDoctor_WithNullId_ShouldCallRepository() {
        // Given
        UUID nullId = null;
        when(doctorStatisticsRepository.findByIdDokter(nullId))
                .thenReturn(Optional.empty());

        // When
        Optional<DoctorStatistics> result = doctorStatisticsService.getStatisticsByDoctor(nullId);

        // Then
        assertThat(result).isEmpty();
        verify(doctorStatisticsRepository).findByIdDokter(nullId);
    }

    @Test
    void updateStatistics_WhenExistingRecordExists_ShouldUpdateExistingRecord() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(4.5);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(20);
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.of(existingStatistics));
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorStatistics result = doctorStatisticsService.updateStatistics(testDoctorId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdDokter()).isEqualTo(testDoctorId);
        assertThat(result.getAverageRating()).isEqualTo(4.5);
        assertThat(result.getTotalRatings()).isEqualTo(20);
        assertThat(result.getCreatedAt()).isEqualTo(testDateTime); // Should preserve original createdAt
        assertThat(result.getUpdatedAt()).isAfter(testDateTime); // Should update updatedAt

        verify(ratingRepository).calculateAverageRatingByDokter(testDoctorId);
        verify(ratingRepository).countRatingsByDokter(testDoctorId);
        verify(doctorStatisticsRepository).findByIdDokter(testDoctorId);
        verify(doctorStatisticsRepository).save(existingStatistics);
    }

    @Test
    void updateStatistics_WhenNoExistingRecord_ShouldCreateNewRecord() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(3.8);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(12);
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorStatistics result = doctorStatisticsService.updateStatistics(testDoctorId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdDokter()).isEqualTo(testDoctorId);
        assertThat(result.getAverageRating()).isEqualTo(3.8);
        assertThat(result.getTotalRatings()).isEqualTo(12);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();

        ArgumentCaptor<DoctorStatistics> captor = ArgumentCaptor.forClass(DoctorStatistics.class);
        verify(doctorStatisticsRepository).save(captor.capture());

        DoctorStatistics savedStats = captor.getValue();
        assertThat(savedStats.getIdDokter()).isEqualTo(testDoctorId);
        assertThat(savedStats.getAverageRating()).isEqualTo(3.8);
        assertThat(savedStats.getTotalRatings()).isEqualTo(12);
    }

    @Test
    void updateStatistics_WhenRatingRepositoryReturnsNull_ShouldHandleGracefully() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(null);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(null);
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorStatistics result = doctorStatisticsService.updateStatistics(testDoctorId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAverageRating()).isEqualTo(0.0);
        assertThat(result.getTotalRatings()).isEqualTo(0);
    }

    @Test
    void updateStatistics_WhenAverageRatingIsNullButCountIsNot_ShouldHandlePartialNulls() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(null);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(5);
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorStatistics result = doctorStatisticsService.updateStatistics(testDoctorId);

        // Then
        assertThat(result.getAverageRating()).isEqualTo(0.0);
        assertThat(result.getTotalRatings()).isEqualTo(5);
    }

    @Test
    void updateStatistics_WithZeroRatings_ShouldCreateValidRecord() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(0.0);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(0);
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorStatistics result = doctorStatisticsService.updateStatistics(testDoctorId);

        // Then
        assertThat(result.getAverageRating()).isEqualTo(0.0);
        assertThat(result.getTotalRatings()).isEqualTo(0);
    }

    @Test
    void updateStatistics_WithHighRatings_ShouldHandleCorrectly() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(4.95);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(1000);
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorStatistics result = doctorStatisticsService.updateStatistics(testDoctorId);

        // Then
        assertThat(result.getAverageRating()).isEqualTo(4.95);
        assertThat(result.getTotalRatings()).isEqualTo(1000);
    }

    @Test
    void updateStatistics_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(4.0);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(10);
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        assertThatThrownBy(() -> doctorStatisticsService.updateStatistics(testDoctorId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection failed");
    }

    @Test
    void updateStatistics_MultipleConsecutiveCalls_ShouldUpdateCorrectly() {
        // Given - Create separate objects for each call
        DoctorStatistics stats1 = new DoctorStatistics();
        stats1.setIdDokter(testDoctorId);
        stats1.setAverageRating(3.0);
        stats1.setTotalRatings(5);
        stats1.setCreatedAt(testDateTime);
        stats1.setUpdatedAt(testDateTime);

        DoctorStatistics stats2 = new DoctorStatistics();
        stats2.setIdDokter(testDoctorId);
        stats2.setAverageRating(4.0); // Updated from first call
        stats2.setTotalRatings(10);  // Updated from first call
        stats2.setCreatedAt(testDateTime);
        stats2.setUpdatedAt(LocalDateTime.now());

        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId))
                .thenReturn(4.0)
                .thenReturn(4.2);
        when(ratingRepository.countRatingsByDokter(testDoctorId))
                .thenReturn(10)
                .thenReturn(12);
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.of(stats1))
                .thenReturn(Optional.of(stats2));
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorStatistics firstUpdate = doctorStatisticsService.updateStatistics(testDoctorId);
        DoctorStatistics secondUpdate = doctorStatisticsService.updateStatistics(testDoctorId);

        // Then
        assertThat(firstUpdate.getAverageRating()).isEqualTo(4.0);
        assertThat(firstUpdate.getTotalRatings()).isEqualTo(10);

        assertThat(secondUpdate.getAverageRating()).isEqualTo(4.2);
        assertThat(secondUpdate.getTotalRatings()).isEqualTo(12);

        verify(doctorStatisticsRepository, times(2)).save(any(DoctorStatistics.class));
    }

    @Test
    void calculateStatistics_WithValidData_ShouldReturnCalculatedStatistics() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(4.3);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(18);

        // When
        DoctorStatistics result = doctorStatisticsService.calculateStatistics(testDoctorId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdDokter()).isEqualTo(testDoctorId);
        assertThat(result.getAverageRating()).isEqualTo(4.3);
        assertThat(result.getTotalRatings()).isEqualTo(18);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(ratingRepository).calculateAverageRatingByDokter(testDoctorId);
        verify(ratingRepository).countRatingsByDokter(testDoctorId);
        verifyNoInteractions(doctorStatisticsRepository); // Should not save to database
    }

    @Test
    void calculateStatistics_WithZeroRatings_ShouldReturnZeroStatistics() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(0.0);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(0);

        // When
        DoctorStatistics result = doctorStatisticsService.calculateStatistics(testDoctorId);

        // Then
        assertThat(result.getAverageRating()).isEqualTo(0.0);
        assertThat(result.getTotalRatings()).isEqualTo(0);
    }

    @Test
    void calculateStatistics_WithDecimalRating_ShouldPreserveDecimalPrecision() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(3.75);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(8);

        // When
        DoctorStatistics result = doctorStatisticsService.calculateStatistics(testDoctorId);

        // Then
        assertThat(result.getAverageRating()).isEqualTo(3.75);
        assertThat(result.getTotalRatings()).isEqualTo(8);
    }

    @Test
    void calculateStatistics_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId))
                .thenThrow(new RuntimeException("Database query failed"));

        // When & Then
        assertThatThrownBy(() -> doctorStatisticsService.calculateStatistics(testDoctorId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database query failed");
    }

    @Test
    void calculateStatistics_WithLargeNumbers_ShouldHandleCorrectly() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(4.999);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(999999);

        // When
        DoctorStatistics result = doctorStatisticsService.calculateStatistics(testDoctorId);

        // Then
        assertThat(result.getAverageRating()).isEqualTo(4.999);
        assertThat(result.getTotalRatings()).isEqualTo(999999);
    }

    // ========== Edge Cases and Integration Tests ==========

    @Test
    void calculateStatistics_MultipleCalls_ShouldReturnConsistentResults() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(4.1);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(14);

        // When
        DoctorStatistics first = doctorStatisticsService.calculateStatistics(testDoctorId);
        DoctorStatistics second = doctorStatisticsService.calculateStatistics(testDoctorId);

        // Then
        assertThat(first.getAverageRating()).isEqualTo(second.getAverageRating());
        assertThat(first.getTotalRatings()).isEqualTo(second.getTotalRatings());
        assertThat(first.getIdDokter()).isEqualTo(second.getIdDokter());

        verify(ratingRepository, times(2)).calculateAverageRatingByDokter(testDoctorId);
        verify(ratingRepository, times(2)).countRatingsByDokter(testDoctorId);
    }

    @Test
    void updateStatistics_AfterCalculateStatistics_ShouldHaveConsistentData() {
        // Given
        when(ratingRepository.calculateAverageRatingByDokter(testDoctorId)).thenReturn(4.4);
        when(ratingRepository.countRatingsByDokter(testDoctorId)).thenReturn(25);
        when(doctorStatisticsRepository.findByIdDokter(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorStatistics calculated = doctorStatisticsService.calculateStatistics(testDoctorId);
        DoctorStatistics updated = doctorStatisticsService.updateStatistics(testDoctorId);

        // Then
        assertThat(calculated.getAverageRating()).isEqualTo(updated.getAverageRating());
        assertThat(calculated.getTotalRatings()).isEqualTo(updated.getTotalRatings());
        assertThat(calculated.getIdDokter()).isEqualTo(updated.getIdDokter());
    }

    @Test
    void updateStatistics_WithDifferentDoctorIds_ShouldHandleCorrectly() {
        // Given
        UUID doctorId1 = UUID.randomUUID();
        UUID doctorId2 = UUID.randomUUID();

        when(ratingRepository.calculateAverageRatingByDokter(doctorId1)).thenReturn(4.5);
        when(ratingRepository.countRatingsByDokter(doctorId1)).thenReturn(20);
        when(ratingRepository.calculateAverageRatingByDokter(doctorId2)).thenReturn(3.8);
        when(ratingRepository.countRatingsByDokter(doctorId2)).thenReturn(15);

        when(doctorStatisticsRepository.findByIdDokter(any(UUID.class)))
                .thenReturn(Optional.empty());
        when(doctorStatisticsRepository.save(any(DoctorStatistics.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DoctorStatistics stats1 = doctorStatisticsService.updateStatistics(doctorId1);
        DoctorStatistics stats2 = doctorStatisticsService.updateStatistics(doctorId2);

        // Then
        assertThat(stats1.getIdDokter()).isEqualTo(doctorId1);
        assertThat(stats1.getAverageRating()).isEqualTo(4.5);
        assertThat(stats1.getTotalRatings()).isEqualTo(20);

        assertThat(stats2.getIdDokter()).isEqualTo(doctorId2);
        assertThat(stats2.getAverageRating()).isEqualTo(3.8);
        assertThat(stats2.getTotalRatings()).isEqualTo(15);

        verify(doctorStatisticsRepository, times(2)).save(any(DoctorStatistics.class));
    }

    @Test
    void getStatisticsByDoctor_WithDifferentDoctorIds_ShouldReturnCorrectData() {
        // Given
        UUID doctorId1 = UUID.randomUUID();
        UUID doctorId2 = UUID.randomUUID();

        DoctorStatistics stats1 = new DoctorStatistics();
        stats1.setIdDokter(doctorId1);
        stats1.setAverageRating(4.0);

        DoctorStatistics stats2 = new DoctorStatistics();
        stats2.setIdDokter(doctorId2);
        stats2.setAverageRating(3.5);

        when(doctorStatisticsRepository.findByIdDokter(doctorId1))
                .thenReturn(Optional.of(stats1));
        when(doctorStatisticsRepository.findByIdDokter(doctorId2))
                .thenReturn(Optional.of(stats2));

        // When
        Optional<DoctorStatistics> result1 = doctorStatisticsService.getStatisticsByDoctor(doctorId1);
        Optional<DoctorStatistics> result2 = doctorStatisticsService.getStatisticsByDoctor(doctorId2);

        // Then
        assertThat(result1).isPresent();
        assertThat(result1.get().getIdDokter()).isEqualTo(doctorId1);
        assertThat(result1.get().getAverageRating()).isEqualTo(4.0);

        assertThat(result2).isPresent();
        assertThat(result2.get().getIdDokter()).isEqualTo(doctorId2);
        assertThat(result2.get().getAverageRating()).isEqualTo(3.5);
    }
}