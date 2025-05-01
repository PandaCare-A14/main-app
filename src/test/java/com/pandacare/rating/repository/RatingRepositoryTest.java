package com.pandacare.rating.repository;

import com.pandacare.rating.model.Rating;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RatingRepositoryTest {

    @Mock
    private RatingRepository ratingRepository;

    @Test
    void testFindByOwnerId() {
        // Arrange
        String idPacillian = "PAC001";
        Rating rating1 = new Rating(1L, "DOK001", idPacillian, 4, "Bagus", LocalDateTime.now(), LocalDateTime.now());
        Rating rating2 = new Rating(2L, "DOK002", idPacillian, 5, "Sangat bagus", LocalDateTime.now(), LocalDateTime.now());
        List<Rating> expectedRatings = Arrays.asList(rating1, rating2);
        
        when(ratingRepository.findByOwnerId(idPacillian)).thenReturn(Optional.of(expectedRatings));

        // Act
        Optional<List<Rating>> result = ratingRepository.findByOwnerId(idPacillian);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertEquals(expectedRatings, result.get());
        verify(ratingRepository).findByOwnerId(idPacillian);
    }

    @Test
    void testDeleteById() {
        // Arrange
        String idPacillian = "PAC001";
        String idDokter = "DOK001";
        Optional<Rating> expectedRating = Optional.of(
            new Rating(1L, idDokter, idPacillian, 4, "Bagus", LocalDateTime.now(), LocalDateTime.now())
        );
        
        when(ratingRepository.deleteById(idPacillian, idDokter)).thenReturn(expectedRating);

        // Act
        Optional<Rating> result = ratingRepository.deleteById(idPacillian, idDokter);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(idPacillian, result.get().getIdPacillian());
        assertEquals(idDokter, result.get().getIdDokter());
        verify(ratingRepository).deleteById(idPacillian, idDokter);
    }

    @Test
    void testSave() {
        // Arrange
        Rating ratingToSave = new Rating(null, "DOK001", "PAC001", 4, "Bagus", LocalDateTime.now(), LocalDateTime.now());
        Rating savedRating = new Rating(1L, "DOK001", "PAC001", 4, "Bagus", LocalDateTime.now(), LocalDateTime.now());
        
        when(ratingRepository.save(ratingToSave)).thenReturn(savedRating);

        // Act
        Rating result = ratingRepository.save(ratingToSave);

        // Assert
        assertNotNull(result.getId());
        assertEquals("DOK001", result.getIdDokter());
        assertEquals("PAC001", result.getIdPacillian());
        assertEquals(4, result.getRatingScore());
        verify(ratingRepository).save(ratingToSave);
    }
}