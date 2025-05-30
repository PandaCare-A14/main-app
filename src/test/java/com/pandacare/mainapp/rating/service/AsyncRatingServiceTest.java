package com.pandacare.mainapp.rating.service;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.repository.CaregiverScheduleRepository;
import com.pandacare.mainapp.rating.dto.request.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.repository.ReservasiKonsultasiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AsyncRatingServiceTest {

    @Mock
    private RatingService ratingService;

    @Mock
    private ReservasiKonsultasiRepository reservasiKonsultasiRepository;

    @Mock
    private CaregiverScheduleRepository caregiverScheduleRepository;

    private AsyncRatingService asyncRatingService;

    private UUID testConsultationId;
    private UUID testPacillianId;
    private UUID testCaregiverId;
    private RatingRequest testRatingRequest;
    private RatingResponse testRatingResponse;
    private ReservasiKonsultasi testReservasi;
    private CaregiverSchedule testSchedule;

    @BeforeEach
    void setUp() {
        asyncRatingService = new AsyncRatingService(
                ratingService,
                reservasiKonsultasiRepository,
                caregiverScheduleRepository
        );

        testConsultationId = UUID.randomUUID();
        testPacillianId = UUID.randomUUID();
        testCaregiverId = UUID.randomUUID();

        testRatingRequest = new RatingRequest();
        testRatingRequest.setRatingScore(5);
        testRatingRequest.setUlasan("Excellent service");
        testRatingRequest.setIdJadwalKonsultasi(testConsultationId);

        testRatingResponse = new RatingResponse();
        testRatingResponse.setId(UUID.randomUUID());
        testRatingResponse.setRatingScore(5);
        testRatingResponse.setUlasan("Excellent service");
        testRatingResponse.setIdJadwalKonsultasi(testConsultationId);

        testSchedule = new CaregiverSchedule();
        testSchedule.setId(UUID.randomUUID());
        testSchedule.setIdCaregiver(testCaregiverId);

        testReservasi = new ReservasiKonsultasi();
        testReservasi.setId(testConsultationId);
        testReservasi.setIdPacilian(testPacillianId);
        testReservasi.setIdSchedule(testSchedule);
    }

    @Test
    void shouldAddRatingSuccessfully() throws ExecutionException, InterruptedException {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.addRating(eq(testPacillianId), any(RatingRequest.class)))
                .thenReturn(testRatingResponse);

        CompletableFuture<RatingResponse> result = asyncRatingService.addRatingAsync(testConsultationId, testRatingRequest);

        assertNotNull(result);
        RatingResponse response = result.get();
        assertNotNull(response);
        assertEquals(testRatingResponse.getId(), response.getId());
        assertEquals(5, response.getRatingScore());
        assertEquals("Excellent service", response.getUlasan());
        assertEquals(testConsultationId, response.getIdJadwalKonsultasi());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).addRating(eq(testPacillianId), any(RatingRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenConsultationNotFoundOnAdd() {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.empty());

        CompletableFuture<RatingResponse> result = asyncRatingService.addRatingAsync(testConsultationId, testRatingRequest);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Consultation not found", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService, never()).addRating(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenScheduleNotFoundOnAdd() {
        testReservasi.setIdSchedule(null);
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));

        CompletableFuture<RatingResponse> result = asyncRatingService.addRatingAsync(testConsultationId, testRatingRequest);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Schedule not found for this consultation", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService, never()).addRating(any(), any());
    }

    @Test
    void shouldHandleRatingServiceExceptionOnAdd() {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.addRating(eq(testPacillianId), any(RatingRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        CompletableFuture<RatingResponse> result = asyncRatingService.addRatingAsync(testConsultationId, testRatingRequest);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Database error", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).addRating(eq(testPacillianId), any(RatingRequest.class));
    }

    @Test
    void shouldUpdateRatingSuccessfully() throws ExecutionException, InterruptedException {
        testRatingResponse.setRatingScore(4);
        testRatingResponse.setUlasan("Good service, updated");

        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.updateRating(eq(testPacillianId), any(RatingRequest.class)))
                .thenReturn(testRatingResponse);

        CompletableFuture<RatingResponse> result = asyncRatingService.updateRatingAsync(testConsultationId, testRatingRequest);

        assertNotNull(result);
        RatingResponse response = result.get();
        assertNotNull(response);
        assertEquals(4, response.getRatingScore());
        assertEquals("Good service, updated", response.getUlasan());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).updateRating(eq(testPacillianId), any(RatingRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenConsultationNotFoundOnUpdate() {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.empty());

        CompletableFuture<RatingResponse> result = asyncRatingService.updateRatingAsync(testConsultationId, testRatingRequest);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Consultation not found", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService, never()).updateRating(any(), any());
    }

    @Test
    void shouldHandleRatingServiceExceptionOnUpdate() {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.updateRating(eq(testPacillianId), any(RatingRequest.class)))
                .thenThrow(new RuntimeException("Update failed"));

        CompletableFuture<RatingResponse> result = asyncRatingService.updateRatingAsync(testConsultationId, testRatingRequest);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Update failed", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).updateRating(eq(testPacillianId), any(RatingRequest.class));
    }

    @Test
    void shouldDeleteRatingSuccessfully() throws ExecutionException, InterruptedException {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        doNothing().when(ratingService).deleteRating(testPacillianId, testConsultationId);

        CompletableFuture<Void> result = asyncRatingService.deleteRatingAsync(testConsultationId);

        assertNotNull(result);
        Void response = result.get();
        assertNull(response);

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).deleteRating(testPacillianId, testConsultationId);
    }

    @Test
    void shouldThrowExceptionWhenConsultationNotFoundOnDelete() {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.empty());

        CompletableFuture<Void> result = asyncRatingService.deleteRatingAsync(testConsultationId);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Consultation not found", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService, never()).deleteRating(any(), any());
    }

    @Test
    void shouldHandleRatingServiceExceptionOnDelete() {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        doThrow(new RuntimeException("Delete failed"))
                .when(ratingService).deleteRating(testPacillianId, testConsultationId);

        CompletableFuture<Void> result = asyncRatingService.deleteRatingAsync(testConsultationId);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Delete failed", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).deleteRating(testPacillianId, testConsultationId);
    }

    @Test
    void shouldGetRatingByConsultationSuccessfully() throws ExecutionException, InterruptedException {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.getRatingByKonsultasi(testPacillianId, testConsultationId))
                .thenReturn(testRatingResponse);

        CompletableFuture<RatingResponse> result = asyncRatingService.getRatingByKonsultasiAsync(testConsultationId);

        assertNotNull(result);
        RatingResponse response = result.get();
        assertNotNull(response);
        assertEquals(testRatingResponse.getId(), response.getId());
        assertEquals(5, response.getRatingScore());
        assertEquals("Excellent service", response.getUlasan());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).getRatingByKonsultasi(testPacillianId, testConsultationId);
    }

    @Test
    void shouldThrowExceptionWhenConsultationNotFoundOnGet() {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.empty());

        CompletableFuture<RatingResponse> result = asyncRatingService.getRatingByKonsultasiAsync(testConsultationId);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Consultation not found", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService, never()).getRatingByKonsultasi(any(), any());
    }

    @Test
    void shouldHandleRatingServiceExceptionOnGet() {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.getRatingByKonsultasi(testPacillianId, testConsultationId))
                .thenThrow(new RuntimeException("Rating not found"));

        CompletableFuture<RatingResponse> result = asyncRatingService.getRatingByKonsultasiAsync(testConsultationId);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Rating not found", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).getRatingByKonsultasi(testPacillianId, testConsultationId);
    }

    @Test
    void shouldCheckRatingExistsSuccessfully() throws ExecutionException, InterruptedException {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.hasRatedKonsultasi(testPacillianId, testConsultationId))
                .thenReturn(true);

        CompletableFuture<Boolean> result = asyncRatingService.hasRatedKonsultasiAsync(testConsultationId);

        assertNotNull(result);
        Boolean hasRated = result.get();
        assertTrue(hasRated);

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).hasRatedKonsultasi(testPacillianId, testConsultationId);
    }

    @Test
    void shouldReturnFalseWhenRatingNotExists() throws ExecutionException, InterruptedException {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.hasRatedKonsultasi(testPacillianId, testConsultationId))
                .thenReturn(false);

        CompletableFuture<Boolean> result = asyncRatingService.hasRatedKonsultasiAsync(testConsultationId);

        assertNotNull(result);
        Boolean hasRated = result.get();
        assertFalse(hasRated);

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).hasRatedKonsultasi(testPacillianId, testConsultationId);
    }

    @Test
    void shouldThrowExceptionWhenConsultationNotFoundOnCheck() {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.empty());

        CompletableFuture<Boolean> result = asyncRatingService.hasRatedKonsultasiAsync(testConsultationId);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Consultation not found", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService, never()).hasRatedKonsultasi(any(), any());
    }

    @Test
    void shouldHandleRatingServiceExceptionOnCheck() {
        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.hasRatedKonsultasi(testPacillianId, testConsultationId))
                .thenThrow(new RuntimeException("Check failed"));

        CompletableFuture<Boolean> result = asyncRatingService.hasRatedKonsultasiAsync(testConsultationId);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Check failed", exception.getCause().getMessage());

        verify(reservasiKonsultasiRepository).findById(testConsultationId);
        verify(ratingService).hasRatedKonsultasi(testPacillianId, testConsultationId);
    }

    @Test
    void shouldGetRatingsByCaregiverSuccessfully() throws ExecutionException, InterruptedException {
        List<RatingResponse> ratings = Arrays.asList(testRatingResponse);
        RatingListResponse ratingListResponse = new RatingListResponse();
        ratingListResponse.setRatings(ratings);
        ratingListResponse.setAverageRating(4.5);
        ratingListResponse.setTotalRatings(1);

        when(ratingService.getRatingsByDokter(testCaregiverId))
                .thenReturn(ratingListResponse);

        CompletableFuture<RatingListResponse> result = asyncRatingService.getRatingsByDokterAsync(testCaregiverId);

        assertNotNull(result);
        RatingListResponse response = result.get();
        assertNotNull(response);
        assertEquals(1, response.getTotalRatings());
        assertEquals(4.5, response.getAverageRating());
        assertEquals(1, response.getRatings().size());

        verify(ratingService).getRatingsByDokter(testCaregiverId);
    }

    @Test
    void shouldGetEmptyRatingsByCaregiverWhenNoneExist() throws ExecutionException, InterruptedException {
        RatingListResponse emptyResponse = new RatingListResponse();
        emptyResponse.setRatings(Collections.emptyList());
        emptyResponse.setAverageRating(0.0);
        emptyResponse.setTotalRatings(0);

        when(ratingService.getRatingsByDokter(testCaregiverId))
                .thenReturn(emptyResponse);

        CompletableFuture<RatingListResponse> result = asyncRatingService.getRatingsByDokterAsync(testCaregiverId);

        assertNotNull(result);
        RatingListResponse response = result.get();
        assertNotNull(response);
        assertEquals(0, response.getTotalRatings());
        assertEquals(0.0, response.getAverageRating());
        assertTrue(response.getRatings().isEmpty());

        verify(ratingService).getRatingsByDokter(testCaregiverId);
    }

    @Test
    void shouldHandleRatingServiceExceptionOnGetByCaregiver() {
        when(ratingService.getRatingsByDokter(testCaregiverId))
                .thenThrow(new RuntimeException("Service error"));

        CompletableFuture<RatingListResponse> result = asyncRatingService.getRatingsByDokterAsync(testCaregiverId);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Service error", exception.getCause().getMessage());

        verify(ratingService).getRatingsByDokter(testCaregiverId);
    }

    @Test
    void shouldGetRatingsByPacillianSuccessfully() throws ExecutionException, InterruptedException {
        List<RatingResponse> ratings = Arrays.asList(testRatingResponse);
        RatingListResponse ratingListResponse = new RatingListResponse();
        ratingListResponse.setRatings(ratings);
        ratingListResponse.setTotalRatings(1);

        when(ratingService.getRatingsByPasien(testPacillianId))
                .thenReturn(ratingListResponse);

        CompletableFuture<RatingListResponse> result = asyncRatingService.getRatingsByPasienAsync(testPacillianId);

        assertNotNull(result);
        RatingListResponse response = result.get();
        assertNotNull(response);
        assertEquals(1, response.getTotalRatings());
        assertEquals(1, response.getRatings().size());

        verify(ratingService).getRatingsByPasien(testPacillianId);
    }

    @Test
    void shouldGetEmptyRatingsByPacillianWhenNoneExist() throws ExecutionException, InterruptedException {
        RatingListResponse emptyResponse = new RatingListResponse();
        emptyResponse.setRatings(Collections.emptyList());
        emptyResponse.setTotalRatings(0);

        when(ratingService.getRatingsByPasien(testPacillianId))
                .thenReturn(emptyResponse);

        CompletableFuture<RatingListResponse> result = asyncRatingService.getRatingsByPasienAsync(testPacillianId);

        assertNotNull(result);
        RatingListResponse response = result.get();
        assertNotNull(response);
        assertEquals(0, response.getTotalRatings());
        assertTrue(response.getRatings().isEmpty());

        verify(ratingService).getRatingsByPasien(testPacillianId);
    }

    @Test
    void shouldHandleRatingServiceExceptionOnGetByPacillian() {
        when(ratingService.getRatingsByPasien(testPacillianId))
                .thenThrow(new RuntimeException("Database error"));

        CompletableFuture<RatingListResponse> result = asyncRatingService.getRatingsByPasienAsync(testPacillianId);

        assertNotNull(result);
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Database error", exception.getCause().getMessage());

        verify(ratingService).getRatingsByPasien(testPacillianId);
    }

    @Test
    void shouldSetConsultationIdInRequestOnAdd() throws ExecutionException, InterruptedException {
        RatingRequest requestWithoutId = new RatingRequest();
        requestWithoutId.setRatingScore(5);
        requestWithoutId.setUlasan("Excellent service");

        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.addRating(eq(testPacillianId), any(RatingRequest.class)))
                .thenReturn(testRatingResponse);

        CompletableFuture<RatingResponse> result = asyncRatingService.addRatingAsync(testConsultationId, requestWithoutId);

        assertNotNull(result);
        result.get();

        verify(ratingService).addRating(eq(testPacillianId), argThat(request ->
                request.getIdJadwalKonsultasi().equals(testConsultationId)
        ));
    }

    @Test
    void shouldSetConsultationIdInRequestOnUpdate() throws ExecutionException, InterruptedException {
        RatingRequest requestWithoutId = new RatingRequest();
        requestWithoutId.setRatingScore(4);
        requestWithoutId.setUlasan("Updated review");

        when(reservasiKonsultasiRepository.findById(testConsultationId))
                .thenReturn(Optional.of(testReservasi));
        when(ratingService.updateRating(eq(testPacillianId), any(RatingRequest.class)))
                .thenReturn(testRatingResponse);

        CompletableFuture<RatingResponse> result = asyncRatingService.updateRatingAsync(testConsultationId, requestWithoutId);

        assertNotNull(result);
        result.get();

        verify(ratingService).updateRating(eq(testPacillianId), argThat(request ->
                request.getIdJadwalKonsultasi().equals(testConsultationId)
        ));
    }
}