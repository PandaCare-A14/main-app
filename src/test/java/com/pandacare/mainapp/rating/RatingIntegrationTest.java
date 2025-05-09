package com.pandacare.mainapp.rating;

import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandacare.mainapp.rating.dto.RatingRequest;
import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.repository.RatingRepository;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.ReservasiKonsultasiServiceImpl;

/**
 * Integration test for rating functionality with mocked repository
 */

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
@AutoConfigureMockMvc
public class RatingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private RatingRepository ratingRepository;
    
    @MockBean
    private ReservasiKonsultasiServiceImpl reservasiService;
    
    private Rating testRating1;
    private Rating testRating2;
    private Rating testRating3;
    private ReservasiKonsultasi testConsultation;
    
    @BeforeEach
    public void setup() {
        // Create test ratings
        testRating1 = new Rating(
            "RTG12345",
            "DOC12345",
            "PAT7890",
            5,
            "Dokter sangat profesional dan ramah. Penjelasan diagnosa sangat detail.",
            LocalDateTime.now().minusDays(5),
            LocalDateTime.now().minusDays(5)
        );
        
        testRating2 = new Rating(
            "RTG12346",
            "DOC12345",
            "PAT7891",
            4,
            "Pelayanan memuaskan dan dokter memiliki pengetahuan yang luas.",
            LocalDateTime.now().minusDays(3),
            LocalDateTime.now().minusDays(3)
        );
        
        testRating3 = new Rating(
            "RTG12347",
            "DOC12345",
            "PAT7892",
            4,
            "Dokter responsif dan memberikan solusi yang tepat.",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );
        
        // Create test consultation
        testConsultation = new ReservasiKonsultasi();
        testConsultation.setId("JDW12345");
        testConsultation.setIdDokter("DOC12347");
        testConsultation.setIdPasien("PAT7893");
    }
    
    @Test
    public void testFullRatingFlow() throws Exception {
        // Setup mock responses
        when(ratingRepository.findByIdDokter("DOC12345")).thenReturn(Arrays.asList(testRating1, testRating2, testRating3));
        when(ratingRepository.countByIdDokter("DOC12345")).thenReturn(3L);
        when(ratingRepository.calculateAverageRatingByDokter("DOC12345")).thenReturn(4.33);
        
        when(reservasiService.findAllByPasien("PAT7893")).thenReturn(Collections.singletonList(testConsultation));
        
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7893", "DOC12347")).thenReturn(Optional.empty());
        
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            Rating savedRating = invocation.getArgument(0);
            if (savedRating.getId() == null) {
                savedRating.setId("RTG12348");
            }
            return savedRating;
        });
        
        // 1. Check existing ratings for doctor
        MvcResult result = mockMvc.perform(get("/api/v1/doctors/DOC12345/ratings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.totalRatings").value(3))
            .andReturn();
        
        // 2. Add a new rating (with a valid consultation)
        RatingRequest newRating = new RatingRequest(5, "Excellent doctor, very attentive");
        
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7893", "DOC12347")).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/api/v1/doctors/DOC12347/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRating))
                .header("X-User-ID", "PAT7893"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.ratingScore").value(5));
        
        // 3. Update the rating
        RatingRequest updatedRating = new RatingRequest(4, "Good doctor, but long waiting time");
        
        Rating existingRating = new Rating(
            "RTG12348", 
            "DOC12347", 
            "PAT7893", 
            5, 
            "Excellent doctor, very attentive",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7893", "DOC12347")).thenReturn(Optional.of(existingRating));
        
        mockMvc.perform(put("/api/v1/doctors/DOC12347/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRating))
                .header("X-User-ID", "PAT7893"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.ratingScore").value(4))
            .andExpect(jsonPath("$.data.ulasan").value("Good doctor, but long waiting time"));
        
        // 4. Get the rating by patient and doctor
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7893", "DOC12347")).thenReturn(Optional.of(existingRating));
        
        mockMvc.perform(get("/api/v1/patients/PAT7893/doctors/DOC12347/ratings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"));
        
        // 5. Get all ratings by patient
        when(ratingRepository.findByIdPasien("PAT7893")).thenReturn(Collections.singletonList(existingRating));
        
        mockMvc.perform(get("/api/v1/patients/PAT7893/ratings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.length()").value(1));
        
        // 6. Delete the rating
        when(ratingRepository.findByIdPasienAndIdDokter("PAT7893", "DOC12347")).thenReturn(Optional.of(existingRating));
        
        mockMvc.perform(delete("/api/v1/doctors/DOC12347/ratings")
                .header("X-User-ID", "PAT7893"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.message").value("Rating berhasil dihapus"));
    }
    
    @Test
    public void testValidationErrors() throws Exception {
        // Test invalid rating score (too low)
        RatingRequest invalidRating = new RatingRequest(0, "Invalid rating");
        
        mockMvc.perform(post("/api/v1/doctors/DOC12345/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRating))
                .header("X-User-ID", "PAT7893"))
            .andExpect(status().isBadRequest());
        
        // Test invalid rating score (too high)
        invalidRating.setRatingScore(6);
        
        mockMvc.perform(post("/api/v1/doctors/DOC12345/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRating))
                .header("X-User-ID", "PAT7893"))
            .andExpect(status().isBadRequest());
        
        // Test missing ulasan
        invalidRating = new RatingRequest(4, "");
        
        mockMvc.perform(post("/api/v1/doctors/DOC12345/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRating))
                .header("X-User-ID", "PAT7893"))
            .andExpect(status().isBadRequest());
    }
}