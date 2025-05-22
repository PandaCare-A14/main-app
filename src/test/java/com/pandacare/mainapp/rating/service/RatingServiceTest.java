package com.pandacare.mainapp.rating.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pandacare.mainapp.common.exception.BusinessException;
import com.pandacare.mainapp.common.exception.ResourceNotFoundException;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.ReservasiKonsultasiServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ReservasiKonsultasiServiceImpl reservasiService;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private Rating testRating;
    private Rating testRating2;
    private RatingRequest testRatingRequest;
    private ReservasiKonsultasi completedConsultation;

    @BeforeEach
    public void setup() {
        // Setup test data
        testRating = new Rating(
                "RTG12345",
                "DOC12345",
                "PAT7890",
                4,
                "Dokter sangat ramah",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        testRating2 = new Rating(
                "RTG12346",
                "DOC12345",
                "PAT7891",
                5,
                "Pelayanan memuaskan",
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(2)
        );

        testRatingRequest = new RatingRequest(5, "Updated review");

        // Setup completed consultation
        completedConsultation = new ReservasiKonsultasi();
        completedConsultation.setIdReservasi("JDW12345");
        completedConsultation.setIdCareGiver("DOC12345");
        completedConsultation.setIdPacilian("PAT7890");
    }

    @Test
    public void testGetRatingsByDokter() {
        // Arrange
        when(ratingRepository.findByIdDokter("DOC12345")).thenReturn(Arrays.asList(testRating, testRating2));
        when(ratingRepository.countByIdDokter("DOC12345")).thenReturn(2L);
        when(ratingRepository.calculateAverageRatingByDokter("DOC12345")).thenReturn(4.5);

        // Act
        RatingListResponse response = ratingService.getRatingsByDokter("DOC12345");

        // Assert
        assertEquals(4.5, response.getAverageRating());
        assertEquals(2, response.getTotalRatings());
        assertEquals(2, response.getRatings().size());
    }

    @Test
    public void testGetRatingsByDokterNoRatings() {
        // Arrange
        when(ratingRepository.findByIdDokter("DOC12345")).thenReturn(Collections.emptyList());
        when(ratingRepository.countByIdDokter("DOC12345")).thenReturn(0L);
        when(ratingRepository.calculateAverageRatingByDokter("DOC12345")).thenReturn(null);

        // Act
        RatingListResponse response = ratingService.getRatingsByDokter("DOC12345");

        // Assert
        assertEquals(0.0, response.getAverageRating());
        assertEquals(0, response.getTotalRatings());
        assertEquals(0, response.getRatings().size());
    }

    @Test
    public void testGetRatingsByPasien() {
        // Arrange
        when(ratingRepository.findByIdPasien("PAT7890")).thenReturn(Collections.singletonList(testRating));

        // Act
        List<RatingResponse> ratings = ratingService.getRatingsByPasien("PAT7890");

        // Assert
        assertEquals(1, ratings.size());
        assertEquals("DOC12345", ratings.get(0).getIdDokter());
        assertEquals("PAT7890", ratings.get(0).getIdPasien());
    }

    @Test
    public void testGetRatingByPasienAndDokter() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345")).thenReturn(Optional.of(testRating));

        // Act
        RatingResponse rating = ratingService.getRatingByPasienAndDokter("PAT7890", "DOC12345");

        // Assert
        assertNotNull(rating);
        assertEquals("DOC12345", rating.getIdDokter());
        assertEquals("PAT7890", rating.getIdPasien());
    }

    @Test
    public void testGetRatingByPasienAndDokterNotFound() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.getRatingByPasienAndDokter("PAT7890", "DOC12345");
        });
    }

    @Test
    public void testAddRating() {
        // Arrange
        List<ReservasiKonsultasi> completedConsultations = Collections.singletonList(completedConsultation);
        when(reservasiService.findAllByPasien("PAT7890")).thenReturn(completedConsultations);
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345")).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            Rating savedRating = invocation.getArgument(0);
            if (savedRating.getId() == null) {
                savedRating.setId("RTG12345");
            }
            return savedRating;
        });

        // Act
        RatingResponse response = ratingService.addRating("PAT7890", "DOC12345", testRatingRequest);

        // Assert
        assertNotNull(response);
        assertEquals("RTG12345", response.getId());
        assertEquals("DOC12345", response.getIdDokter());
        assertEquals("PAT7890", response.getIdPasien());
        assertEquals(5, response.getRatingScore());
    }

    @Test
    public void testAddRatingNoConsultation() {
        // Arrange
        when(reservasiService.findAllByPasien("PAT7890")).thenReturn(Collections.emptyList());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ratingService.addRating("PAT7890", "DOC12345", testRatingRequest);
        });

        assertEquals("Pasien belum pernah melakukan konsultasi dengan dokter ini", exception.getMessage());
    }

    @Test
    public void testAddRatingAlreadyExists() {
        // Arrange
        List<ReservasiKonsultasi> completedConsultations = Collections.singletonList(completedConsultation);
        when(reservasiService.findAllByPasien("PAT7890")).thenReturn(completedConsultations);
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345")).thenReturn(Optional.of(testRating));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ratingService.addRating("PAT7890", "DOC12345", testRatingRequest);
        });

        assertEquals("Rating untuk dokter ini sudah diberikan. Silakan gunakan endpoint update untuk mengubah rating", exception.getMessage());
    }

    @Test
    public void testUpdateRating() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345")).thenReturn(Optional.of(testRating));
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RatingResponse response = ratingService.updateRating("PAT7890", "DOC12345", testRatingRequest);

        // Assert
        assertNotNull(response);
        assertEquals("RTG12345", response.getId());
        assertEquals("DOC12345", response.getIdDokter());
        assertEquals("PAT7890", response.getIdPasien());
        assertEquals(5, response.getRatingScore());
        assertEquals("Updated review", response.getUlasan());
    }

    @Test
    public void testUpdateRatingNotFound() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.updateRating("PAT7890", "DOC12345", testRatingRequest);
        });

        assertEquals("Rating tidak ditemukan", exception.getMessage());
    }

    @Test
    public void testDeleteRating() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345")).thenReturn(Optional.of(testRating));
        doNothing().when(ratingRepository).deleteByIdPasienAndIdDokter(anyString(), anyString());

        // Act
        ratingService.deleteRating("PAT7890", "DOC12345");

        // Assert
        verify(ratingRepository, times(1)).deleteByIdPasienAndIdDokter("PAT7890", "DOC12345");
    }

    @Test
    public void testDeleteRatingNotFound() {
        // Arrange
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7890", "DOC12345")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.deleteRating("PAT7890", "DOC12345");
        });

        assertEquals("Rating tidak ditemukan", exception.getMessage());
        verify(ratingRepository, never()).deleteByIdPasienAndIdDokter(anyString(), anyString());
    }
}