package com.pandacare.mainapp.rating.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalDate;
import java.util.Optional;
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
@WebMvcTest(controllers = RatingController.class, 
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
    })
class RatingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;    @MockBean
    private RatingService ratingService;

    @MockBean
    private ReservasiKonsultasiRepository reservasiKonsultasiRepository;

    @MockBean
    private com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository caregiverScheduleRepository;

    private RatingRequest validRatingRequest;
    private RatingResponse validRatingResponse;
    private ReservasiKonsultasi mockReservasi;
    private CaregiverSchedule mockSchedule;

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

        // Setup mock schedule with past end time
        mockSchedule = new CaregiverSchedule();
        mockSchedule.setDate(LocalDate.now().minusDays(1));
        mockSchedule.setEndTime(LocalTime.now().minusHours(1)); // Past time
        mockSchedule.setIdCaregiver(VALID_DOCTOR_ID);

        // Setup mock reservation
        mockReservasi = new ReservasiKonsultasi();
        mockReservasi.setId(VALID_CONSULTATION_ID);
        mockReservasi.setIdPacilian(VALID_PATIENT_ID);
        mockReservasi.setIdSchedule(mockSchedule);
        mockReservasi.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
    }

    @Test
    @DisplayName("Should return 201 when adding valid rating")
    void shouldReturn201_WhenAddingValidRating() throws Exception {
        // Arrange
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(mockReservasi));
        when(ratingService.addRating(eq(VALID_PATIENT_ID), any(RatingRequest.class)))
                .thenReturn(validRatingResponse);

        // Act & Assert
        mockMvc.perform(post("/api/consultations/{idJadwalKonsultasi}/ratings", VALID_CONSULTATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRatingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Rating berhasil ditambahkan")))
                .andExpect(jsonPath("$.data.rating.id").exists())
                .andExpect(jsonPath("$.data.rating.idDokter", is(VALID_DOCTOR_ID.toString())))
                .andExpect(jsonPath("$.data.rating.idPasien", is(VALID_PATIENT_ID.toString())))
                .andExpect(jsonPath("$.data.rating.ratingScore", is(5)))
                .andExpect(jsonPath("$.data.rating.ulasan", is("Excellent service")));
    }

    @Test
    @DisplayName("Should return 400 when consultation not found")
    void shouldReturn400_WhenConsultationNotFound() throws Exception {
        // Arrange
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/consultations/{idJadwalKonsultasi}/ratings", VALID_CONSULTATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRatingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Consultation not found")));
    }    @Test
    @DisplayName("Should return 400 when trying to rate future consultation")
    void shouldReturn400_WhenRatingFutureConsultation() throws Exception {
        // Arrange - Setup future consultation
        mockSchedule.setDate(LocalDate.now().plusDays(1)); // Future date
        mockSchedule.setEndTime(LocalTime.now().plusHours(1)); // Future time
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(mockReservasi));

        // Act & Assert
        mockMvc.perform(post("/api/consultations/{idJadwalKonsultasi}/ratings", VALID_CONSULTATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRatingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Cannot rate future consultation")));
    }

    @Test
    @DisplayName("Should return 200 when updating existing rating")
    void shouldReturn200_WhenUpdatingExistingRating() throws Exception {
        // Arrange
        validRatingRequest.setRatingScore(4);
        validRatingRequest.setUlasan("Good service");

        RatingResponse updatedResponse = new RatingResponse();
        updatedResponse.setId(validRatingResponse.getId());
        updatedResponse.setIdDokter(VALID_DOCTOR_ID);
        updatedResponse.setIdPasien(VALID_PATIENT_ID);
        updatedResponse.setIdJadwalKonsultasi(VALID_CONSULTATION_ID);
        updatedResponse.setRatingScore(4);
        updatedResponse.setUlasan("Good service");
        updatedResponse.setCreatedAt(LocalDateTime.now());
        updatedResponse.setUpdatedAt(LocalDateTime.now());

        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(mockReservasi));
        when(ratingService.updateRating(eq(VALID_PATIENT_ID), any(RatingRequest.class)))
                .thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/consultations/{idJadwalKonsultasi}/ratings", VALID_CONSULTATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRatingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Rating berhasil diperbarui")))
                .andExpect(jsonPath("$.data.rating.ratingScore", is(4)))
                .andExpect(jsonPath("$.data.rating.ulasan", is("Good service")));
    }

    @Test
    @DisplayName("Should return 200 when deleting rating")
    void shouldReturn200_WhenDeletingRating() throws Exception {
        // Arrange
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(mockReservasi));

        // Act & Assert
        mockMvc.perform(delete("/api/consultations/{idJadwalKonsultasi}/ratings", VALID_CONSULTATION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Rating berhasil dihapus")));
    }

    @Test
    @DisplayName("Should return 200 when checking rating status")
    void shouldReturn200_WhenCheckingRatingStatus() throws Exception {
        // Arrange
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(mockReservasi));
        when(ratingService.hasRatedKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/consultations/{idJadwalKonsultasi}/rating/status", VALID_CONSULTATION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.hasRated", is(true)));
    }

    @Test
    @DisplayName("Should return 200 when getting rating by consultation")
    void shouldReturn200_WhenGettingRatingByConsultation() throws Exception {
        // Arrange
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(mockReservasi));
        when(ratingService.getRatingByKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenReturn(validRatingResponse);

        // Act & Assert
        mockMvc.perform(get("/api/consultations/{idJadwalKonsultasi}/ratings", VALID_CONSULTATION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.rating.id").exists())
                .andExpect(jsonPath("$.data.rating.ratingScore", is(5)))
                .andExpect(jsonPath("$.data.rating.ulasan", is("Excellent service")));
    }

    @Test
    @DisplayName("Should return 404 when rating not found for consultation")
    void shouldReturn404_WhenRatingNotFoundForConsultation() throws Exception {
        // Arrange
        when(reservasiKonsultasiRepository.findById(VALID_CONSULTATION_ID))
                .thenReturn(Optional.of(mockReservasi));
        when(ratingService.getRatingByKonsultasi(VALID_PATIENT_ID, VALID_CONSULTATION_ID))
                .thenThrow(new IllegalArgumentException("Rating not found"));

        // Act & Assert
        mockMvc.perform(get("/api/consultations/{idJadwalKonsultasi}/ratings", VALID_CONSULTATION_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Rating not found")));
    }
}