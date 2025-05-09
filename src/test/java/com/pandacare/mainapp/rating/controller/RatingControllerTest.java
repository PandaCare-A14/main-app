package com.pandacare.mainapp.rating.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.pandacare.mainapp.rating.controller.RatingController;
import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.service.RatingService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RatingControllerTest {

    @Mock
    private RatingService ratingService;
    
    private RatingController ratingController;
    
    @BeforeEach
    void setUp() {
        ratingController = new RatingController(ratingService);
    }
    
    @Test
    void testAddRating_Success() {
        // Arrange
        Rating newRating = new Rating(null, "DOK001", "PAC001", 4, "Pelayanan baik", null, null);
        Rating savedRating = new Rating(1L, "DOK001", "PAC001", 4, "Pelayanan baik", 
                                        LocalDateTime.now(), LocalDateTime.now());
        
        when(ratingService.addRating(newRating)).thenReturn(savedRating);
        
        // Act
        ResponseEntity<Rating> response = ratingController.addRating(newRating);
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedRating, response.getBody());
        verify(ratingService).addRating(newRating);
    }
    
    @Test
    void testAddRating_Failure() {
        // Arrange
        Rating newRating = new Rating(null, "DOK001", "PAC001", 4, "Pelayanan baik", null, null);
        
        when(ratingService.addRating(newRating)).thenThrow(new IllegalArgumentException("Validasi gagal"));
        
        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ratingController.addRating(newRating);
        });
        
        // Assert
        assertEquals("Validasi gagal", exception.getMessage());
        verify(ratingService).addRating(newRating);
    }
    
    @Test
    void testUpdateRating_Success() {
        // Arrange
        Rating updateRating = new Rating(1L, "DOK001", "PAC001", 5, "Pelayanan sangat baik", null, null);
        Rating updatedRating = new Rating(1L, "DOK001", "PAC001", 5, "Pelayanan sangat baik", 
                                         LocalDateTime.now(), LocalDateTime.now());
        
        when(ratingService.updateRating(updateRating)).thenReturn(updatedRating);
        
        // Act
        ResponseEntity<Rating> response = ratingController.updateRating(updateRating);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRating, response.getBody());
        verify(ratingService).updateRating(updateRating);
    }
    
    @Test
    void testDeleteRating_Success() {
        // Arrange
        String idPacillian = "PAC001";
        String idDokter = "DOK001";
        Rating deletedRating = new Rating(1L, idDokter, idPacillian, 4, "Pelayanan baik", 
                                         LocalDateTime.now(), LocalDateTime.now());
        
        when(ratingService.deleteRating(idPacillian, idDokter)).thenReturn(deletedRating);
        
        // Act
        ResponseEntity<Rating> response = ratingController.deleteRating(idPacillian, idDokter);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(deletedRating, response.getBody());
        verify(ratingService).deleteRating(idPacillian, idDokter);
    }
    
    @Test
    void testGetRatingsByOwnerId_Success() {
        // Arrange
        String idPacillian = "PAC001";
        List<Rating> expectedRatings = Arrays.asList(
            new Rating(1L, "DOK001", idPacillian, 4, "Pelayanan baik", LocalDateTime.now(), LocalDateTime.now()),
            new Rating(2L, "DOK002", idPacillian, 5, "Sangat puas", LocalDateTime.now(), LocalDateTime.now())
        );
        
        when(ratingService.getRatingsByOwnerId(idPacillian)).thenReturn(expectedRatings);
        
        // Act
        ResponseEntity<List<Rating>> response = ratingController.getRatingsByOwnerId(idPacillian);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(ratingService).getRatingsByOwnerId(idPacillian);
    }
    
    @Test
    void testGetRatingsByIdDokter_Success() {
        // Arrange
        String idDokter = "DOK001";
        List<Rating> expectedRatings = Arrays.asList(
            new Rating(1L, idDokter, "PAC001", 4, "Pelayanan baik", LocalDateTime.now(), LocalDateTime.now()),
            new Rating(2L, idDokter, "PAC002", 5, "Sangat puas", LocalDateTime.now(), LocalDateTime.now())
        );
        
        when(ratingService.getRatingsByIdDokter(idDokter)).thenReturn(expectedRatings);
        
        // Act
        ResponseEntity<List<Rating>> response = ratingController.getRatingsByIdDokter(idDokter);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(ratingService).getRatingsByIdDokter(idDokter);
    }
}