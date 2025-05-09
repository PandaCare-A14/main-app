package com.pandacare.mainapp.rating.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import com.pandacare.mainapp.rating.repository.UserRepository;
import com.pandacare.mainapp.rating.service.RatingServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;
    
    @Mock
    private UserRepository userRepository;
    
    private RatingServiceImpl ratingService;
    
    @BeforeEach
    void setUp() {
        ratingService = new RatingServiceImpl(ratingRepository, userRepository);
    }
    
    @Test
    void testAddRating_Success() {
        // Arrange
        Rating newRating = new Rating(null, "DOK001", "PAC001", 4, "Pelayanan baik", null, null);
        Rating savedRating = new Rating(1L, "DOK001", "PAC001", 4, "Pelayanan baik", 
                                        LocalDateTime.now(), LocalDateTime.now());
        
        when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);
        when(userRepository.existsById("DOK001")).thenReturn(true);
        when(userRepository.existsById("PAC001")).thenReturn(true);
        
        // Act
        Rating result = ratingService.addRating(newRating);
        
        // Assert
        assertNotNull(result);
        assertEquals(savedRating.getId(), result.getId());
        verify(ratingRepository).save(any(Rating.class));
        verify(userRepository).existsById("DOK001");
        verify(userRepository).existsById("PAC001");
    }
    
    @Test
    void testAddRating_InvalidUser() {
        // Arrange
        Rating newRating = new Rating(null, "DOK001", "PAC001", 4, "Pelayanan baik", null, null);
        
        when(userRepository.existsById("DOK001")).thenReturn(false);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ratingService.addRating(newRating);
        });
        
        assertEquals("Dokter dengan ID DOK001 tidak ditemukan", exception.getMessage());
        verify(userRepository).existsById("DOK001");
        verify(ratingRepository, never()).save(any(Rating.class));
    }
    
    @Test
    void testUpdateRating_Success() {
        // Arrange
        Rating updateRating = new Rating(1L, "DOK001", "PAC001", 5, "Pelayanan sangat baik", null, null);
        Rating existingRating = new Rating(1L, "DOK001", "PAC001", 4, "Pelayanan baik", 
                                          LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        Rating updatedRating = new Rating(1L, "DOK001", "PAC001", 5, "Pelayanan sangat baik", 
                                         existingRating.getCreatedAt(), LocalDateTime.now());
        
        List<Rating> ratingList = Arrays.asList(existingRating);
        when(ratingRepository.findByOwnerId("PAC001")).thenReturn(Optional.of(ratingList));
        when(ratingRepository.save(any(Rating.class))).thenReturn(updatedRating);
        
        // Act
        Rating result = ratingService.updateRating(updateRating);
        
        // Assert
        assertNotNull(result);
        assertEquals(5, result.getRatingScore());
        assertEquals("Pelayanan sangat baik", result.getUlasan());
        verify(ratingRepository).findByOwnerId("PAC001");
        verify(ratingRepository).save(any(Rating.class));
    }
    
    @Test
    void testUpdateRating_RatingNotFound() {
        // Arrange
        Rating updateRating = new Rating(1L, "DOK002", "PAC001", 5, "Pelayanan sangat baik", null, null);
        Rating existingRating = new Rating(1L, "DOK001", "PAC001", 4, "Pelayanan baik", 
                                          LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        
        List<Rating> ratingList = Arrays.asList(existingRating);
        when(ratingRepository.findByOwnerId("PAC001")).thenReturn(Optional.of(ratingList));
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ratingService.updateRating(updateRating);
        });
        
        assertEquals("Rating untuk dokter dengan ID DOK002 tidak ditemukan", exception.getMessage());
        verify(ratingRepository).findByOwnerId("PAC001");
        verify(ratingRepository, never()).save(any(Rating.class));
    }
    
    @Test
    void testDeleteRating_Success() {
        // Arrange
        String idPacillian = "PAC001";
        String idDokter = "DOK001";
        Rating deletedRating = new Rating(1L, idDokter, idPacillian, 4, "Pelayanan baik", 
                                         LocalDateTime.now(), LocalDateTime.now());
        
        when(ratingRepository.deleteById(idPacillian, idDokter)).thenReturn(Optional.of(deletedRating));
        
        // Act
        Rating result = ratingService.deleteRating(idPacillian, idDokter);
        
        // Assert
        assertNotNull(result);
        assertEquals(idPacillian, result.getIdPacillian());
        assertEquals(idDokter, result.getIdDokter());
        verify(ratingRepository).deleteById(idPacillian, idDokter);
    }
    
    @Test
    void testDeleteRating_RatingNotFound() {
        // Arrange
        String idPacillian = "PAC001";
        String idDokter = "DOK001";
        
        when(ratingRepository.deleteById(idPacillian, idDokter)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ratingService.deleteRating(idPacillian, idDokter);
        });
        
        assertEquals("Rating tidak ditemukan", exception.getMessage());
        verify(ratingRepository).deleteById(idPacillian, idDokter);
    }
    
    @Test
    void testGetRatingsByOwnerId_Success() {
        // Arrange
        String idPacillian = "PAC001";
        List<Rating> expectedRatings = Arrays.asList(
            new Rating(1L, "DOK001", idPacillian, 4, "Pelayanan baik", LocalDateTime.now(), LocalDateTime.now()),
            new Rating(2L, "DOK002", idPacillian, 5, "Sangat puas", LocalDateTime.now(), LocalDateTime.now())
        );
        
        when(ratingRepository.findByOwnerId(idPacillian)).thenReturn(Optional.of(expectedRatings));
        
        // Act
        List<Rating> result = ratingService.getRatingsByOwnerId(idPacillian);
        
        // Assert
        assertEquals(2, result.size());
        assertEquals("DOK001", result.get(0).getIdDokter());
        assertEquals("DOK002", result.get(1).getIdDokter());
        verify(ratingRepository).findByOwnerId(idPacillian);
    }
    
    @Test
    void testGetRatingsByOwnerId_NotFound() {
        // Arrange
        String idPacillian = "PAC001";
        
        when(ratingRepository.findByOwnerId(idPacillian)).thenReturn(Optional.empty());
        
        // Act
        List<Rating> result = ratingService.getRatingsByOwnerId(idPacillian);
        
        // Assert
        assertTrue(result.isEmpty());
        verify(ratingRepository).findByOwnerId(idPacillian);
    }
    
    @Test
    void testGetRatingsByIdDokter_Success() {
        // Arrange
        String idDokter = "DOK001";
        List<Rating> expectedRatings = Arrays.asList(
            new Rating(1L, idDokter, "PAC001", 4, "Pelayanan baik", LocalDateTime.now(), LocalDateTime.now()),
            new Rating(2L, idDokter, "PAC002", 5, "Sangat puas", LocalDateTime.now(), LocalDateTime.now())
        );
        
        when(ratingRepository.findByIdDokter(idDokter)).thenReturn(Optional.of(expectedRatings));
        
        // Act
        List<Rating> result = ratingService.getRatingsByIdDokter(idDokter);
        
        // Assert
        assertEquals(2, result.size());
        assertEquals("PAC001", result.get(0).getIdPacillian());
        assertEquals("PAC002", result.get(1).getIdPacillian());
        verify(ratingRepository).findByIdDokter(idDokter);
    }
    
    @Test
    void testGetRatingsByIdDokter_NotFound() {
        // Arrange
        String idDokter = "DOK001";
        
        when(ratingRepository.findByIdDokter(idDokter)).thenReturn(Optional.empty());
        
        // Act
        List<Rating> result = ratingService.getRatingsByIdDokter(idDokter);
        
        // Assert
        assertTrue(result.isEmpty());
        verify(ratingRepository).findByIdDokter(idDokter);
    }
}