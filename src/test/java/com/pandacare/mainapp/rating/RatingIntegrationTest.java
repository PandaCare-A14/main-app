package com.pandacare.mainapp.rating;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandacare.mainapp.rating.controller.RatingController;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.service.RatingService;

/**
 * Integration test for rating functionality using WebMvcTest
 */
@WebMvcTest(RatingController.class)
public class RatingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RatingService ratingService;

    private RatingResponse testRatingResponse1;
    private RatingResponse testRatingResponse2;
    private RatingResponse testRatingResponse3;
    private RatingListResponse testRatingListResponse;

    @BeforeEach
    public void setup() {
        // Setup test rating responses
        testRatingResponse1 = new RatingResponse();
        testRatingResponse1.setId("RTG12345");
        testRatingResponse1.setIdDokter("DOC12345");
        testRatingResponse1.setIdPasien("PAT7890");
        testRatingResponse1.setRatingScore(5);
        testRatingResponse1.setUlasan("Dokter sangat profesional dan ramah.");
        testRatingResponse1.setCreatedAt(LocalDateTime.now().minusDays(5));
        testRatingResponse1.setUpdatedAt(LocalDateTime.now().minusDays(5));

        testRatingResponse2 = new RatingResponse();
        testRatingResponse2.setId("RTG12346");
        testRatingResponse2.setIdDokter("DOC12345");
        testRatingResponse2.setIdPasien("PAT7891");
        testRatingResponse2.setRatingScore(4);
        testRatingResponse2.setUlasan("Pelayanan memuaskan.");
        testRatingResponse2.setCreatedAt(LocalDateTime.now().minusDays(3));
        testRatingResponse2.setUpdatedAt(LocalDateTime.now().minusDays(3));

        testRatingResponse3 = new RatingResponse();
        testRatingResponse3.setId("RTG12347");
        testRatingResponse3.setIdDokter("DOC12345");
        testRatingResponse3.setIdPasien("PAT7892");
        testRatingResponse3.setRatingScore(4);
        testRatingResponse3.setUlasan("Dokter responsif.");
        testRatingResponse3.setCreatedAt(LocalDateTime.now().minusDays(1));
        testRatingResponse3.setUpdatedAt(LocalDateTime.now().minusDays(1));

        testRatingListResponse = new RatingListResponse();
        testRatingListResponse.setAverageRating(4.33);
        testRatingListResponse.setTotalRatings(3);
        testRatingListResponse.setRatings(Arrays.asList(testRatingResponse1, testRatingResponse2, testRatingResponse3));
    }

    @Test
    public void testFullRatingFlow() throws Exception {
        // Mock service responses
        when(ratingService.getRatingsByDokter("DOC12345")).thenReturn(testRatingListResponse);
        when(ratingService.getRatingsByPasien("PAT7890")).thenReturn(Arrays.asList(testRatingResponse1));
        when(ratingService.getRatingByPasienAndDokter("PAT7890", "DOC12345")).thenReturn(testRatingResponse1);

        // Mock adding a new rating
        RatingRequest newRatingRequest = new RatingRequest(5, "Excellent doctor");
        when(ratingService.addRating(anyString(), anyString(), any(RatingRequest.class))).thenReturn(testRatingResponse1);

        // Mock updating rating
        RatingRequest updateRatingRequest = new RatingRequest(4, "Good doctor");
        when(ratingService.updateRating(anyString(), anyString(), any(RatingRequest.class))).thenReturn(testRatingResponse2);

        // 1. Get ratings for a doctor
        mockMvc.perform(get("/api/v1/doctors/DOC12345/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRatings").value(3))
                .andExpect(jsonPath("$.ratings.length()").value(3));

        // 2. Get ratings by patient
        mockMvc.perform(get("/api/v1/patients/PAT7890/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("RTG12345"));

        // 3. Get specific rating
        mockMvc.perform(get("/api/v1/patients/PAT7890/doctors/DOC12345/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("RTG12345"));

        // 4. Add new rating
        mockMvc.perform(post("/api/v1/doctors/DOC12345/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRatingRequest))
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("RTG12345"));

        // 5. Update rating
        mockMvc.perform(put("/api/v1/doctors/DOC12345/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRatingRequest))
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("RTG12346"));

        // 6. Delete rating
        mockMvc.perform(delete("/api/v1/doctors/DOC12345/ratings")
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isOk());
    }

    @Test
    public void testValidationErrors() throws Exception {
        // Test invalid rating score (too low)
        RatingRequest invalidRating = new RatingRequest(0, "Invalid rating");

        mockMvc.perform(post("/api/v1/doctors/DOC12345/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRating))
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isBadRequest());

        // Test invalid rating score (too high)
        invalidRating.setRatingScore(6);

        mockMvc.perform(post("/api/v1/doctors/DOC12345/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRating))
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isBadRequest());

        // Test missing ulasan
        invalidRating = new RatingRequest(4, "");

        mockMvc.perform(post("/api/v1/doctors/DOC12345/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRating))
                        .header("X-User-ID", "PAT7890"))
                .andExpect(status().isBadRequest());
    }
}