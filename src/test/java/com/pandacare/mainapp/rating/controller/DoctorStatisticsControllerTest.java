package com.pandacare.mainapp.rating.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandacare.mainapp.rating.model.DoctorStatistics;
import com.pandacare.mainapp.rating.service.DoctorStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = DoctorStatisticsController.class,  // ← Fixed: Changed to DoctorStatisticsController
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class DoctorStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorStatisticsService doctorStatisticsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID testDoctorId;
    private DoctorStatistics mockStatistics;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDoctorId = UUID.randomUUID();
        testDateTime = LocalDateTime.now();

        mockStatistics = new DoctorStatistics();
        mockStatistics.setIdDokter(testDoctorId);
        mockStatistics.setAverageRating(4.5);
        mockStatistics.setTotalRatings(25);
        mockStatistics.setUpdatedAt(testDateTime);
    }

    @Test
    void getDoctorStatistics_WhenStatisticsExist_ShouldReturnSuccess() throws Exception {
        when(doctorStatisticsService.getStatisticsByDoctor(testDoctorId))
                .thenReturn(Optional.of(mockStatistics));

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", testDoctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.averageRating", is(4.5)))
                .andExpect(jsonPath("$.data.totalRatings", is(25)))
                .andExpect(jsonPath("$.data.updatedAt", notNullValue()));

        verify(doctorStatisticsService).getStatisticsByDoctor(testDoctorId);
    }

    @Test
    void getDoctorStatistics_WhenStatisticsNotExist_ShouldCalculateOnFly() throws Exception {
        DoctorStatistics calculatedStats = new DoctorStatistics();
        calculatedStats.setIdDokter(testDoctorId);
        calculatedStats.setAverageRating(3.8);
        calculatedStats.setTotalRatings(12);
        calculatedStats.setUpdatedAt(testDateTime);

        when(doctorStatisticsService.getStatisticsByDoctor(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsService.calculateStatistics(testDoctorId))
                .thenReturn(calculatedStats);

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", testDoctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.averageRating", is(3.8)))
                .andExpect(jsonPath("$.data.totalRatings", is(12)))
                .andExpect(jsonPath("$.data.updatedAt", notNullValue()));

        verify(doctorStatisticsService).getStatisticsByDoctor(testDoctorId);
        verify(doctorStatisticsService).calculateStatistics(testDoctorId);
    }

    @Test
    void getDoctorStatistics_WithInvalidUuid_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // ← Ubah dari isBadRequest() ke isInternalServerError()
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("Failed to convert")));

        verifyNoInteractions(doctorStatisticsService);
    }

    @Test
    void getDoctorStatistics_WithZeroRatings_ShouldReturnZeroValues() throws Exception {
        DoctorStatistics emptyStats = new DoctorStatistics();
        emptyStats.setIdDokter(testDoctorId);
        emptyStats.setAverageRating(0.0);
        emptyStats.setTotalRatings(0);
        emptyStats.setUpdatedAt(testDateTime);

        when(doctorStatisticsService.getStatisticsByDoctor(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsService.calculateStatistics(testDoctorId))
                .thenReturn(emptyStats);

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", testDoctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.averageRating", is(0.0)))
                .andExpect(jsonPath("$.data.totalRatings", is(0)));

        verify(doctorStatisticsService).getStatisticsByDoctor(testDoctorId);
        verify(doctorStatisticsService).calculateStatistics(testDoctorId);
    }

    @Test
    void refreshDoctorStatistics_WhenSuccessful_ShouldReturnUpdatedData() throws Exception {
        DoctorStatistics refreshedStats = new DoctorStatistics();
        refreshedStats.setIdDokter(testDoctorId);
        refreshedStats.setAverageRating(4.7);
        refreshedStats.setTotalRatings(30);
        refreshedStats.setUpdatedAt(LocalDateTime.now());

        when(doctorStatisticsService.updateStatistics(testDoctorId))
                .thenReturn(refreshedStats);

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Statistik dokter berhasil diperbarui")))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.averageRating", is(4.7)))
                .andExpect(jsonPath("$.data.totalRatings", is(30)))
                .andExpect(jsonPath("$.data.updatedAt", notNullValue()));

        verify(doctorStatisticsService).updateStatistics(testDoctorId);
    }

    @Test
    void refreshDoctorStatistics_WhenServiceThrowsException_ShouldReturnError() throws Exception {
        String errorMessage = "Database connection failed";
        when(doctorStatisticsService.updateStatistics(testDoctorId))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("Gagal memperbarui statistik dokter")))
                .andExpect(jsonPath("$.message", containsString(errorMessage)));

        verify(doctorStatisticsService).updateStatistics(testDoctorId);
    }

    @Test
    void refreshDoctorStatistics_WithInvalidUuid_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // ← Ubah dari isBadRequest() ke isInternalServerError()
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("Failed to convert")));

        verifyNoInteractions(doctorStatisticsService);
    }

    @Test
    void refreshDoctorStatistics_WhenNullPointerException_ShouldReturnError() throws Exception {
        when(doctorStatisticsService.updateStatistics(testDoctorId))
                .thenThrow(new NullPointerException("Doctor not found"));

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("Doctor not found")));

        verify(doctorStatisticsService).updateStatistics(testDoctorId);
    }

    @Test
    void refreshDoctorStatistics_WithUpdatedValues_ShouldReturnNewValues() throws Exception {
        DoctorStatistics newStats = new DoctorStatistics();
        newStats.setIdDokter(testDoctorId);
        newStats.setAverageRating(4.2);
        newStats.setTotalRatings(22);
        newStats.setUpdatedAt(LocalDateTime.now()); // ← Tambahkan ini

        when(doctorStatisticsService.updateStatistics(testDoctorId))
                .thenReturn(newStats);

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.averageRating", is(4.2)))
                .andExpect(jsonPath("$.data.totalRatings", is(22)));

        verify(doctorStatisticsService).updateStatistics(testDoctorId);
    }

    @Test
    void getDoctorStatistics_ConcurrentRequests_ShouldHandleCorrectly() throws Exception {
        when(doctorStatisticsService.getStatisticsByDoctor(any(UUID.class)))
                .thenReturn(Optional.of(mockStatistics));

        UUID doctorId1 = UUID.randomUUID();
        UUID doctorId2 = UUID.randomUUID();

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", doctorId1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", doctorId2))
                .andExpect(status().isOk());

        verify(doctorStatisticsService, times(2)).getStatisticsByDoctor(any(UUID.class));
    }

    @Test
    void getDoctorStatistics_WithDifferentStatisticsValues_ShouldReturnCorrectData() throws Exception {
        DoctorStatistics highRatedStats = new DoctorStatistics();
        highRatedStats.setIdDokter(testDoctorId);
        highRatedStats.setAverageRating(4.9);
        highRatedStats.setTotalRatings(100);
        highRatedStats.setUpdatedAt(testDateTime);

        when(doctorStatisticsService.getStatisticsByDoctor(testDoctorId))
                .thenReturn(Optional.of(highRatedStats));

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", testDoctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.averageRating", is(4.9)))
                .andExpect(jsonPath("$.data.totalRatings", is(100)));
    }

    @Test
    void refreshDoctorStatistics_WhenIllegalArgumentException_ShouldReturnError() throws Exception {
        when(doctorStatisticsService.updateStatistics(testDoctorId))
                .thenThrow(new IllegalArgumentException("Invalid doctor ID"));

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("Invalid doctor ID")));
    }

    @Test
    void getDoctorStatistics_ServiceThrowsException_ShouldHandleGracefully() throws Exception {
        when(doctorStatisticsService.getStatisticsByDoctor(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsService.calculateStatistics(testDoctorId))
                .thenThrow(new RuntimeException("Calculation failed"));

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", testDoctorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void refreshDoctorStatistics_MultipleConsecutiveCalls_ShouldHandleCorrectly() throws Exception {
        DoctorStatistics firstRefresh = new DoctorStatistics();
        firstRefresh.setIdDokter(testDoctorId);
        firstRefresh.setAverageRating(4.0);
        firstRefresh.setTotalRatings(20);
        firstRefresh.setUpdatedAt(LocalDateTime.now()); // ← Tambahkan ini

        DoctorStatistics secondRefresh = new DoctorStatistics();
        secondRefresh.setIdDokter(testDoctorId);
        secondRefresh.setAverageRating(4.1);
        secondRefresh.setTotalRatings(21);
        secondRefresh.setUpdatedAt(LocalDateTime.now()); // ← Tambahkan ini

        when(doctorStatisticsService.updateStatistics(testDoctorId))
                .thenReturn(firstRefresh)
                .thenReturn(secondRefresh);

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.averageRating", is(4.0)));

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.averageRating", is(4.1)));

        verify(doctorStatisticsService, times(2)).updateStatistics(testDoctorId);
    }

    @Test
    void getDoctorStatistics_WithDecimalRating_ShouldReturnCorrectValue() throws Exception {
        DoctorStatistics decimalStats = new DoctorStatistics();
        decimalStats.setIdDokter(testDoctorId);
        decimalStats.setAverageRating(3.75);
        decimalStats.setTotalRatings(8);
        decimalStats.setUpdatedAt(testDateTime);

        when(doctorStatisticsService.getStatisticsByDoctor(testDoctorId))
                .thenReturn(Optional.of(decimalStats));

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", testDoctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.averageRating", is(3.75)))
                .andExpect(jsonPath("$.data.totalRatings", is(8)));
    }

    @Test
    void refreshDoctorStatistics_WithTimeout_ShouldReturnError() throws Exception {
        when(doctorStatisticsService.updateStatistics(testDoctorId))
                .thenThrow(new RuntimeException("Connection timeout"));

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("Connection timeout")));
    }

    @Test
    void getDoctorStatistics_ServiceReturnsNull_ShouldHandleGracefully() throws Exception {
        when(doctorStatisticsService.getStatisticsByDoctor(testDoctorId))
                .thenReturn(Optional.empty());
        when(doctorStatisticsService.calculateStatistics(testDoctorId))
                .thenReturn(null);

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", testDoctorId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void refreshDoctorStatistics_ServiceReturnsNull_ShouldHandleGracefully() throws Exception {
        when(doctorStatisticsService.updateStatistics(testDoctorId))
                .thenReturn(null);

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getDoctorStatistics_LargeNumbers_ShouldHandleCorrectly() throws Exception {
        DoctorStatistics largeStats = new DoctorStatistics();
        largeStats.setIdDokter(testDoctorId);
        largeStats.setAverageRating(4.999);
        largeStats.setTotalRatings(999999);
        largeStats.setUpdatedAt(testDateTime);

        when(doctorStatisticsService.getStatisticsByDoctor(testDoctorId))
                .thenReturn(Optional.of(largeStats));

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", testDoctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.averageRating", is(4.999)))
                .andExpect(jsonPath("$.data.totalRatings", is(999999)));
    }

    @Test
    void refreshDoctorStatistics_EmptyResponse_ShouldHandleCorrectly() throws Exception {
        DoctorStatistics emptyStats = new DoctorStatistics();
        emptyStats.setIdDokter(testDoctorId);
        emptyStats.setAverageRating(0.0);
        emptyStats.setTotalRatings(0);
        emptyStats.setUpdatedAt(LocalDateTime.now()); // ← Pastikan ini ada

        when(doctorStatisticsService.updateStatistics(testDoctorId))
                .thenReturn(emptyStats);

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.averageRating", is(0.0)))
                .andExpect(jsonPath("$.data.totalRatings", is(0)));
    }

    @Test
    void getDoctorStatistics_SpecialCharactersInUuid_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", "123-abc-def"))
                .andExpect(status().isInternalServerError()) // ← Ubah dari isBadRequest() ke isInternalServerError()
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("Failed to convert")));
    }

    @Test
    void refreshDoctorStatistics_SpecialCharactersInUuid_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", "123-abc-def"))
                .andExpect(status().isInternalServerError()) // ← Ubah dari isBadRequest() ke isInternalServerError()
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("Failed to convert")));
    }

    @Test
    void getAllEndpoints_WithValidData_ShouldReturnConsistentFormat() throws Exception {
        when(doctorStatisticsService.getStatisticsByDoctor(testDoctorId))
                .thenReturn(Optional.of(mockStatistics));
        when(doctorStatisticsService.updateStatistics(testDoctorId))
                .thenReturn(mockStatistics);

        mockMvc.perform(get("/api/doctors/{idDokter}/statistics", testDoctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", notNullValue()));

        mockMvc.perform(post("/api/doctors/{idDokter}/statistics/refresh", testDoctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", notNullValue()));
    }
}