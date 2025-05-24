package com.pandacare.mainapp.reservasi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandacare.mainapp.reservasi.dto.UpdateStatusDTO;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.caregiver.CaregiverReservationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CaregiverReservationController.class,
           excludeAutoConfiguration = {
               org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
               org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
           })
@AutoConfigureMockMvc(addFilters = false)
public class CaregiverReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CaregiverReservationService reservationService;
    @MockBean
    private com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository doctorProfileRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private ReservasiKonsultasi mockReservation;
    private UUID reservationId;
    private UUID caregiverId;

    @BeforeEach
    void setUp() {
        reservationId = UUID.randomUUID();
        caregiverId = UUID.randomUUID();

        mockReservation = new ReservasiKonsultasi();
        mockReservation.setId(reservationId);
        mockReservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
    }

    @Test
    void testGetAllReservations() throws Exception {
        when(reservationService.getReservationsForCaregiver(caregiverId)).thenReturn(List.of(mockReservation));        mockMvc.perform(get("/api/caregivers/{caregiverId}/reservations", caregiverId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservationId.toString()));
    }

    @Test
    void testGetReservationsWithStatusFilter() throws Exception {
        when(reservationService.getWaitingReservations(caregiverId)).thenReturn(List.of(mockReservation));

        mockMvc.perform(get("/api/caregivers/{caregiverId}/reservations", caregiverId)
                        .param("status", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statusReservasi").value("WAITING"));
    }    @Test
    void testGetReservationById_Success() throws Exception {
        when(reservationService.getReservationOrThrow(reservationId)).thenReturn(mockReservation);

        mockMvc.perform(get("/api/caregivers/reservations/{reservationId}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId.toString()));
    }

    @Test    void testGetReservationById_NotFound() throws Exception {
        when(reservationService.getReservationOrThrow(reservationId))
                .thenThrow(new EntityNotFoundException());

        mockMvc.perform(get("/api/caregivers/reservations/{reservationId}", reservationId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateStatus_Approve() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(StatusReservasiKonsultasi.APPROVED);        when(reservationService.approveReservation(reservationId)).thenReturn(mockReservation);

        mockMvc.perform(patch("/api/caregivers/reservations/{reservationId}/status", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStatus_Reject() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(StatusReservasiKonsultasi.REJECTED);        when(reservationService.rejectReservation(reservationId)).thenReturn(mockReservation);

        mockMvc.perform(patch("/api/caregivers/reservations/{reservationId}/status", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStatusChangeSchedule() throws Exception {
        UUID newScheduleId = UUID.randomUUID();
        UpdateStatusDTO dto = new UpdateStatusDTO();

        dto.setStatus(StatusReservasiKonsultasi.ON_RESCHEDULE);
        dto.setNewScheduleId(newScheduleId);        when(reservationService.changeSchedule(reservationId, newScheduleId)).thenReturn(mockReservation);

        mockMvc.perform(patch("/api/caregivers/reservations/{reservationId}/status", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStatus_BadRequestWhenMissingScheduleId() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();        dto.setStatus(StatusReservasiKonsultasi.ON_RESCHEDULE);

        mockMvc.perform(patch("/api/caregivers/reservations/{reservationId}/status", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStatus_IllegalState() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(StatusReservasiKonsultasi.APPROVED);

        when(reservationService.approveReservation(reservationId))                .thenThrow(new IllegalStateException());

        mockMvc.perform(patch("/api/caregivers/reservations/{reservationId}/status", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateStatus_NotFound() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(StatusReservasiKonsultasi.APPROVED);

        when(reservationService.approveReservation(reservationId))
                .thenThrow(new EntityNotFoundException());        mockMvc.perform(patch("/api/caregivers/reservations/{reservationId}/status", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}