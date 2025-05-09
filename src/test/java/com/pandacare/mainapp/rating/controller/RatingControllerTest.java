package com.pandacare.mainapp.rating.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandacare.mainapp.common.exception.BusinessException;
import com.pandacare.mainapp.common.exception.ResourceNotFoundException;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.RatingService;

@WebMvcTest(RatingController.class)
public class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RatingService ratingService;

    private RatingResponse testRatingResponse;
    private RatingRequest testRatingRequest;
    private RatingListResponse testRatingListResponse;

    @BeforeEach
    public void setup() {
        // Setup test data
        testRatingResponse = new RatingResponse();
        testRatingResponse.setId("RTG12345");
        testRatingResponse.setIdDokter("DOC12345");
        testRatingResponse.setIdPasien("PAT7890");
        testRatingResponse.setRatingScore(4);
        testRatingResponse.setUlasan("Dokter sangat ramah");
        testRatingResponse.setCreatedAt(LocalDateTime.now().minusDays(1));
        testRatingResponse.setUpdatedAt(LocalDateTime.now().minusDays(1));

        RatingResponse testRatingResponse2 = new RatingResponse();
        testRatingResponse2.setId("RTG12346");
        testRatingResponse2.setIdDokter("DOC12345");
        testRatingResponse2.setIdPasien("PAT7891");
        testRatingResponse2.setRatingScore(5);
        testRatingResponse2.setUlasan("Pelayanan memuaskan");
        testRatingResponse2.setCreatedAt(LocalDateTime.now().minusDays(2));
        testRatingResponse2.setUpdatedAt(LocalDateTime.now().minusDays(2));

        List<RatingResponse> testRatingResponses = Arrays.asList(testRatingResponse, testRatingResponse2);

        testRatingListResponse = new RatingListResponse(4.5, 2, testRatingResponses);

        testRatingRequest = new RatingRequest(5, "Updated review");
    }

    @Test
    public void testGetRatingsByDokter() throws Exception {
        // Arrange
        when(ratingService.getRatingsByDokter("DOC12345")).thenReturn(testRatingListResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/doctors/DOC12345/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.totalRatings").value(2))
                .andExpect(jsonPath("$.ratings.length()").value(2));
    }

    @Test
    public void testGetRatingsByDokterError() throws Exception {
        // Arrange
        when(ratingService.getRatingsByDokter("DOC12345")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/doctors/DOC12345/ratings"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetRatingsByPasien() throws Exception {
        // Arrange
        List<RatingResponse> ratings = Arrays.asList(testRatingResponse);
        when(ratingService.getRatingsByPasien("PAT7890")).thenReturn(ratings);

        // Act & Assert
        mockMvc.perform(get("/api/v1/patients/PAT7890/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("RTG12345"));
    }

    @Test
    public void testGetRatingByPasienAndDokter() throws Exception {
        // Arrange
        when(ratingService.getRatingByPasienAndDokter("PAT7890", "DOC12345")).thenReturn(testRatingResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/patients/PAT7890/doctors/DOC12345/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("RTG12345"));
    }

    @Test
    public void testGetRatingByPasienAndDokterNotFound() throws Exception {
        // Arrange
        when(ratingService.getRatingByPasienAndDokter("PAT7890", "DOC12345"))
                .thenThrow(new ResourceNotFoundException("Rating tidak ditemukan"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/patients/PAT7890/doctors/DOC12345/ratings"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddRating() throws Exception {
        // Arrange
        when(ratingService.addRating(anyString(), anyString(), any(RatingRequest.class)))
                .thenReturn(testRatingResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/doctors/DOC12345/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRatingRequest))
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("RTG12345"));
    }

    @Test
    public void testAddRatingNoConsultation() throws Exception {
        // Arrange
        when(ratingService.addRating(anyString(), anyString(), any(RatingRequest.class)))
                .thenThrow(new BusinessException("Pasien belum pernah melakukan konsultasi dengan dokter ini"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/doctors/DOC12345/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRatingRequest))
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateRating() throws Exception {
        // Arrange
        when(ratingService.updateRating(anyString(), anyString(), any(RatingRequest.class)))
                .thenReturn(testRatingResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/doctors/DOC12345/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRatingRequest))
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("RTG12345"));
    }

    @Test
    public void testUpdateRatingNotFound() throws Exception {
        // Arrange
        when(ratingService.updateRating(anyString(), anyString(), any(RatingRequest.class)))
                .thenThrow(new ResourceNotFoundException("Rating tidak ditemukan"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/doctors/DOC12345/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRatingRequest))
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteRating() throws Exception {
        // Arrange
        doNothing().when(ratingService).deleteRating(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/doctors/DOC12345/ratings")
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteRatingError() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(ratingService).deleteRating(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/doctors/DOC12345/ratings")
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testValidationError() throws Exception {
        // Arrange - Invalid request with rating=0
        RatingRequest invalidRequest = new RatingRequest(0, "Invalid rating");

        // Act & Assert
        mockMvc.perform(post("/api/v1/doctors/DOC12345/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isBadRequest());
    }
}