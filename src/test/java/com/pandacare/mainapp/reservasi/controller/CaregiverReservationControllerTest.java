package com.pandacare.mainapp.reservasi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandacare.mainapp.reservasi.dto.UpdateStatusDTO;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.caregiver.CaregiverReservationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(CaregiverReservationController.class)
public class CaregiverReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CaregiverReservationService reservationService;
    @Autowired
    private ObjectMapper objectMapper;
    private ReservasiKonsultasi mockReservation;
    private String reservationId;
    private UUID caregiverId;

    @BeforeEach
    void setUp() {
        reservationId = "RES-001";
        caregiverId = UUID.randomUUID();

        mockReservation = new ReservasiKonsultasi();
        mockReservation.setId("RES-001");
        mockReservation.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
    }

    @Test
    void testGetAllReservations() throws Exception {
        when(reservationService.getReservationsForCaregiver(caregiverId)).thenReturn(List.of(mockReservation));

        mockMvc.perform(get("/api/doctors/{caregiverId}/reservations", caregiverId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservationId));
    }

    @Test
    void testGetReservationsWithStatusFilter() throws Exception {
        when(reservationService.getWaitingReservations(caregiverId)).thenReturn(List.of(mockReservation));

        mockMvc.perform(get("/api/doctors/{caregiverId}/reservations", caregiverId)
                        .param("status", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statusReservasi").value("WAITING"));
    }

    @Test
    void testGetReservationById_Success() throws Exception {
        when(reservationService.getReservationOrThrow("RES-001")).thenReturn(mockReservation);

        mockMvc.perform(get("/api/doctors/reservations/RES-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("RES-001"));
    }

    @Test
    void testGetReservationById_NotFound() throws Exception {
        when(reservationService.getReservationOrThrow("RES-404"))
                .thenThrow(new EntityNotFoundException());

        mockMvc.perform(get("/api/doctors/reservations/RES-404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateStatus_Approve() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(StatusReservasiKonsultasi.APPROVED);

        when(reservationService.approveReservation("RES-001")).thenReturn(mockReservation);

        mockMvc.perform(patch("/api/doctors/reservations/RES-001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStatus_Reject() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(StatusReservasiKonsultasi.REJECTED);

        when(reservationService.rejectReservation("RES-001")).thenReturn(mockReservation);

        mockMvc.perform(patch("/api/doctors/reservations/RES-001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStatusChangeSchedule() throws Exception {
        UUID newScheduleId = UUID.randomUUID();
        UpdateStatusDTO dto = new UpdateStatusDTO();

        dto.setStatus(StatusReservasiKonsultasi.ON_RESCHEDULE);
        dto.setNewScheduleId(newScheduleId);

        when(reservationService.changeSchedule("RES-001", newScheduleId)).thenReturn(mockReservation);

        mockMvc.perform(patch("/api/doctors/reservations/RES-001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStatus_BadRequestWhenMissingScheduleId() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(StatusReservasiKonsultasi.ON_RESCHEDULE);

        mockMvc.perform(patch("/api/doctors/reservations/RES-001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStatus_IllegalState() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(StatusReservasiKonsultasi.APPROVED);

        when(reservationService.approveReservation("RES-001"))
                .thenThrow(new IllegalStateException());

        mockMvc.perform(patch("/api/doctors/reservations/RES-001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateStatus_NotFound() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(StatusReservasiKonsultasi.APPROVED);

        when(reservationService.approveReservation("RES-001"))
                .thenThrow(new EntityNotFoundException());

        mockMvc.perform(patch("/api/doctors/reservations/RES-001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}
