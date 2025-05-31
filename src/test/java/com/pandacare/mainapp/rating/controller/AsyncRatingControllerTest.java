package com.pandacare.mainapp.rating.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.AsyncRatingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AsyncRatingControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private AsyncRatingService asyncRatingService;
    
    @InjectMocks
    private AsyncRatingController asyncRatingController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(asyncRatingController)
            .setAsyncRequestTimeout(10000) // Important for async tests
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addRating_Success() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setRatingScore(5);
        ratingRequest.setUlasan("Great service!");
        
        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setId(UUID.randomUUID());
        ratingResponse.setRatingScore(5);
        ratingResponse.setUlasan("Great service!");
        
        when(asyncRatingService.addRatingAsync(eq(consultationId), any(RatingRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(ratingResponse));
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(post("/api/async/consultations/" + consultationId + "/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequest)))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.rating").exists())
                .andExpect(jsonPath("$.message", is("Rating berhasil ditambahkan")));
    }
    
    @Test
    void addRating_BadRequest() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setRatingScore(5);
        ratingRequest.setUlasan("Great service!");
        
        CompletableFuture<RatingResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalArgumentException("Invalid rating"));
        
        when(asyncRatingService.addRatingAsync(eq(consultationId), any(RatingRequest.class)))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(post("/api/async/consultations/" + consultationId + "/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequest)))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result  
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Invalid rating")));
    }
    
    @Test
    void updateRating_Success() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setRatingScore(4);
        ratingRequest.setUlasan("Updated comment");
        
        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setId(UUID.randomUUID());
        ratingResponse.setRatingScore(4);
        ratingResponse.setUlasan("Updated comment");
        
        when(asyncRatingService.updateRatingAsync(eq(consultationId), any(RatingRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(ratingResponse));
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(put("/api/async/consultations/" + consultationId + "/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequest)))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.rating").exists())
                .andExpect(jsonPath("$.message", is("Rating berhasil diperbarui")));
    }
    
    @Test
    void deleteRating_Success() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        
        when(asyncRatingService.deleteRatingAsync(consultationId))
            .thenReturn(CompletableFuture.completedFuture(null));
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(delete("/api/async/consultations/" + consultationId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Rating berhasil dihapus")));
    }
    
    @Test
    void getRatingByKonsultasi_Success() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        
        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setId(UUID.randomUUID());
        ratingResponse.setRatingScore(5);
        ratingResponse.setUlasan("Great service!");
        
        when(asyncRatingService.getRatingByKonsultasiAsync(consultationId))
                .thenReturn(CompletableFuture.completedFuture(ratingResponse));
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/consultations/" + consultationId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.rating").exists());
    }
    
    @Test
    void getRatingByKonsultasi_NotFound() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        
        CompletableFuture<RatingResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalArgumentException("Rating not found"));
        
        when(asyncRatingService.getRatingByKonsultasiAsync(consultationId))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/consultations/" + consultationId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Rating not found")));
    }
    
    @Test
    void hasRatedKonsultasi_Success() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        
        when(asyncRatingService.hasRatedKonsultasiAsync(consultationId))
                .thenReturn(CompletableFuture.completedFuture(true));
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/consultations/" + consultationId + "/rating/status"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.hasRated", is(true)));
    }
    
    @Test
    void getRatingsByDokter_Success() throws Exception {
        // Arrange
        UUID caregiverId = UUID.randomUUID();
        
        RatingResponse rating1 = new RatingResponse();
        rating1.setId(UUID.randomUUID());
        rating1.setRatingScore(5);
        
        RatingResponse rating2 = new RatingResponse();
        rating2.setId(UUID.randomUUID());
        rating2.setRatingScore(4);
        
        List<RatingResponse> ratings = List.of(rating1, rating2);
        
        RatingListResponse ratingListResponse = new RatingListResponse();
        ratingListResponse.setRatings(ratings);
        ratingListResponse.setAverageRating(4.5);

        when(asyncRatingService.getRatingsByDokterAsync(caregiverId))
            .thenReturn(CompletableFuture.completedFuture(ratingListResponse));
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/caregivers/" + caregiverId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data").exists());
    }
    
    @Test
    void getRatingsByPatient_Success() throws Exception {
        // Arrange
        UUID pacilianId = UUID.randomUUID();
        
        RatingResponse rating1 = new RatingResponse();
        rating1.setId(UUID.randomUUID());
        rating1.setRatingScore(5);
        
        RatingResponse rating2 = new RatingResponse();
        rating2.setId(UUID.randomUUID());
        rating2.setRatingScore(4);
        
        List<RatingResponse> ratings = List.of(rating1, rating2);
        
        RatingListResponse ratingListResponse = new RatingListResponse();
        ratingListResponse.setRatings(ratings);

        when(asyncRatingService.getRatingsByPasienAsync(pacilianId))
            .thenReturn(CompletableFuture.completedFuture(ratingListResponse));
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/pacillians/" + pacilianId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data").exists());
    }
    
    @Test
    void testSystemError() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        
        CompletableFuture<RatingResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("System error"));
        
        when(asyncRatingService.getRatingByKonsultasiAsync(consultationId))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/consultations/" + consultationId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message").exists());
    }
    
    // You might need to implement this class to handle async returns
    
    // Add these tests to improve exception handling coverage

    @Test
    void updateRating_IllegalArgumentException() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setRatingScore(4);
        ratingRequest.setUlasan("Updated comment");
        
        CompletableFuture<RatingResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalArgumentException("Cannot update rating for this consultation"));
        
        when(asyncRatingService.updateRatingAsync(eq(consultationId), any(RatingRequest.class)))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(put("/api/async/consultations/" + consultationId + "/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequest)))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Cannot update rating for this consultation")));
    }

    @Test
    void updateRating_SystemError() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setRatingScore(4);
        ratingRequest.setUlasan("Updated comment");
        
        CompletableFuture<RatingResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Database connection failed"));
        
        when(asyncRatingService.updateRatingAsync(eq(consultationId), any(RatingRequest.class)))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(put("/api/async/consultations/" + consultationId + "/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequest)))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Terjadi kesalahan sistem: java.lang.RuntimeException: Database connection failed")));
    }

    @Test
    void deleteRating_IllegalArgumentException() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        
        CompletableFuture<Void> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalArgumentException("Rating not found for deletion"));
        
        when(asyncRatingService.deleteRatingAsync(consultationId))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(delete("/api/async/consultations/" + consultationId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Rating not found for deletion")));
    }

    @Test
    void deleteRating_SystemError() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        
        CompletableFuture<Void> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Database error"));
        
        when(asyncRatingService.deleteRatingAsync(consultationId))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(delete("/api/async/consultations/" + consultationId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Terjadi kesalahan sistem: java.lang.RuntimeException: Database error")));
    }

    @Test
    void hasRatedKonsultasi_IllegalArgumentException() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        
        CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalArgumentException("Consultation not found"));
        
        when(asyncRatingService.hasRatedKonsultasiAsync(consultationId))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/consultations/" + consultationId + "/rating/status"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Consultation not found")));
    }

    @Test
    void hasRatedKonsultasi_SystemError() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        
        CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("System unavailable"));
        
        when(asyncRatingService.hasRatedKonsultasiAsync(consultationId))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/consultations/" + consultationId + "/rating/status"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Terjadi kesalahan sistem: java.lang.RuntimeException: System unavailable")));
    }

    @Test
    void getRatingsByDokter_SystemError() throws Exception {
        // Arrange
        UUID caregiverId = UUID.randomUUID();
        
        CompletableFuture<RatingListResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Failed to retrieve doctor ratings"));
        
        when(asyncRatingService.getRatingsByDokterAsync(caregiverId))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/caregivers/" + caregiverId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Terjadi kesalahan sistem: java.lang.RuntimeException: Failed to retrieve doctor ratings")));
    }

    @Test
    void getRatingsByPasien_SystemError() throws Exception {
        // Arrange
        UUID pacilianId = UUID.randomUUID();
        
        CompletableFuture<RatingListResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Failed to retrieve patient ratings"));
        
        when(asyncRatingService.getRatingsByPasienAsync(pacilianId))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/pacillians/" + pacilianId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Terjadi kesalahan sistem: java.lang.RuntimeException: Failed to retrieve patient ratings")));
    }

    // Additional test for nested exceptions
    @Test
    void handleNestedExceptions() throws Exception {
        // Arrange
        UUID consultationId = UUID.randomUUID();
        
        // Create a nested exception (e.g., IllegalArgumentException wrapped in CompletionException)
        IllegalArgumentException innerEx = new IllegalArgumentException("Inner validation error");
        Exception outerEx = new RuntimeException("Outer wrapper", innerEx);
        
        CompletableFuture<RatingResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(outerEx);
        
        when(asyncRatingService.getRatingByKonsultasiAsync(consultationId))
                .thenReturn(failedFuture);
        
        // Act - Get MvcResult first
        MvcResult mvcResult = mockMvc.perform(get("/api/async/consultations/" + consultationId + "/ratings"))
                .andReturn();
        
        // Assert - Use asyncDispatch to get the actual result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message").exists());
    }
}