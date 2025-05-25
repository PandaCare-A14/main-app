package com.pandacare.mainapp.rating.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandacare.mainapp.config.TestSecurityConfig;
import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for RatingController using TDD approach
 */

@ActiveProfiles("test")
@WebMvcTest(RatingController.class)
@Import(TestSecurityConfig.class)
class RatingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RatingService ratingService;

    private RatingRequest validRatingRequest;
    private RatingResponse validRatingResponse;

    private final UUID VALID_CONSULTATION_ID = UUID.randomUUID();
    private final UUID VALID_PATIENT_ID = UUID.randomUUID();
    private final UUID VALID_DOCTOR_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Setup valid rating request
        validRatingRequest = new RatingRequest();
        validRatingRequest.setRatingScore(5);
        validRatingRequest.setUlasan("Excellent service");

        // Setup valid rating response
        validRatingResponse = new RatingResponse();
        validRatingResponse.setId(UUID.randomUUID());
        validRatingResponse.setIdDokter(VALID_DOCTOR_ID);
        validRatingResponse.setIdPasien(VALID_PATIENT_ID);
        validRatingResponse.setIdJadwalKonsultasi(VALID_CONSULTATION_ID);
        validRatingResponse.setRatingScore(5);
        validRatingResponse.setUlasan("Excellent service");
        validRatingResponse.setCreatedAt(LocalDateTime.now());
        validRatingResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return 201 when adding valid rating")
    void shouldReturn201_WhenAddingValidRating() throws Exception {
        // Arrange
        when(ratingService.addRating(eq(VALID_PATIENT_ID), any(RatingRequest.class)))
                .thenReturn(validRatingResponse);

        // Act & Assert
        mockMvc.perform(post("/api/consultations/{idJadwalKonsultasi}/ratings", VALID_CONSULTATION_ID)
                        .header("X-User-ID", VALID_PATIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRatingRequest)))                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Rating berhasil ditambahkan")))
                .andExpect(jsonPath("$.data.rating.id").exists())
                .andExpect(jsonPath("$.data.rating.idDokter", is(VALID_DOCTOR_ID.toString())))
                .andExpect(jsonPath("$.data.rating.idPasien", is(VALID_PATIENT_ID.toString())))
                .andExpect(jsonPath("$.data.rating.ratingScore", is(5)))
                .andExpect(jsonPath("$.data.rating.ulasan", is("Excellent service")));
    }

    @Test
    @DisplayName("Should return 400 when rating score is invalid")
    void shouldReturn400_WhenRatingScoreIsInvalid() throws Exception {
        // Arrange
        validRatingRequest.setRatingScore(6); // Invalid: should be 1-5

        // Act & Assert
        mockMvc.perform(post("/api/consultations/{idJadwalKonsultasi}/ratings", VALID_CONSULTATION_ID)
                        .header("X-User-ID", VALID_PATIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRatingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Validasi gagal")))
                .andExpect(jsonPath("$.errors.ratingScore", is("Rating score harus di antara 1 dan 5")));
    }

    @Test
    @DisplayName("Should return 400 when rating score is null")
    void shouldReturn400_WhenRatingScoreIsNull() throws Exception {
        // Arrange
        validRatingRequest.setRatingScore(null);

        // Act & Assert
        mockMvc.perform(post("/api/consultations/{idJadwalKonsultasi}/ratings", VALID_CONSULTATION_ID)
                        .header("X-User-ID", VALID_PATIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRatingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Validasi gagal")))
                .andExpect(jsonPath("$.errors.ratingScore", is("Rating score harus diisi")));
    }

    @Test
    @DisplayName("Should return 400 when review is empty")
    void shouldReturn400_WhenReviewIsEmpty() throws Exception {
        // Arrange
        validRatingRequest.setUlasan("");}}

