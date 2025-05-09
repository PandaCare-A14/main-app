package com.pandacare.mainapp.reservasi.controller;

import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.ReservasiKonsultasiServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservasiKonsultasiController.class)
class ReservasiKonsultasiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservasiKonsultasiServiceImpl reservasiService;

    @Test
    void testRequestReservasi_success() throws Exception {
        ReservasiKonsultasi dummy = new ReservasiKonsultasi();
        dummy.setId("RSV001");
        dummy.setIdDokter("dok123");
        dummy.setIdPasien("pac123");
        dummy.setDay("MONDAY");
        dummy.setStartTime("09:00");
        dummy.setEndTime("10:00");
        dummy.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        when(reservasiService.requestReservasi(any(), any(), any(), any(), any())).thenReturn(dummy);

        mockMvc.perform(post("/api/reservasi-konsultasi/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "idDokter": "dok123",
                                "idPasien": "pac123",
                                "day": "MONDAY",
                                "startTime": "09:00",
                                "endTime": "10:00"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Jadwal konsultasi berhasil diajukan"))
                .andExpect(jsonPath("$.reservasi.idDokter").value("dok123"));
    }

    @Test
    void testEditReservasi_success() throws Exception {
        // Membuat objek dummy untuk reservasi yang sudah ada
        ReservasiKonsultasi originalReservasi = new ReservasiKonsultasi();
        originalReservasi.setId("RSV001");
        originalReservasi.setIdDokter("dok123");
        originalReservasi.setIdPasien("pac123");
        originalReservasi.setDay("MONDAY");
        originalReservasi.setStartTime("09:00");
        originalReservasi.setEndTime("10:00");
        originalReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        // Membuat objek untuk reservasi setelah diedit
        ReservasiKonsultasi updatedReservasi = new ReservasiKonsultasi();
        updatedReservasi.setId("RSV001");
        updatedReservasi.setIdDokter("dok123");
        updatedReservasi.setIdPasien("pac123");
        updatedReservasi.setDay("TUESDAY");
        updatedReservasi.setStartTime("10:00");
        updatedReservasi.setEndTime("11:00");
        updatedReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        when(reservasiService.editReservasi(
                eq("RSV001"),
                eq("TUESDAY"),
                eq("10:00"),
                eq("11:00")
        )).thenReturn(updatedReservasi);

        mockMvc.perform(post("/api/reservasi-konsultasi/{id}/edit", "RSV001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "day": "TUESDAY",
                            "startTime": "10:00",
                            "endTime": "11:00"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Jadwal berhasil diperbarui"))
                .andExpect(jsonPath("$.reservasi.day").value("TUESDAY"))
                .andExpect(jsonPath("$.reservasi.startTime").value("10:00"))
                .andExpect(jsonPath("$.reservasi.endTime").value("11:00"));

        verify(reservasiService).editReservasi("RSV001", "TUESDAY", "10:00", "11:00");
    }

    @Test
    void testEditReservasi_error_invalidStatus() throws Exception {
        ReservasiKonsultasi dummy = new ReservasiKonsultasi();
        dummy.setId("RSV001");
        dummy.setIdDokter("dok123");
        dummy.setIdPasien("pac123");
        dummy.setDay("MONDAY");
        dummy.setStartTime("09:00");
        dummy.setEndTime("10:00");
        dummy.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);

        when(reservasiService.editReservasi(any(), any(), any(), any())).thenThrow(new IllegalStateException("Hanya jadwal dengan status WAITING yang bisa diedit"));

        mockMvc.perform(post("/api/reservasi-konsultasi/{id}/edit", "RSV001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "day": "TUESDAY",
                                "startTime": "10:00",
                                "endTime": "11:00"
                            }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Hanya jadwal dengan status WAITING yang bisa diedit"));
    }

    @Test
    void testGetAllReservasiByIdPasien_success() throws Exception {
        ReservasiKonsultasi reservasi1 = new ReservasiKonsultasi();
        reservasi1.setId("RSV001");
        reservasi1.setIdDokter("dokA");
        reservasi1.setIdPasien("pac123");
        reservasi1.setDay("MONDAY");
        reservasi1.setStartTime("09:00");
        reservasi1.setEndTime("10:00");
        reservasi1.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        ReservasiKonsultasi reservasi2 = new ReservasiKonsultasi();
        reservasi2.setId("RSV002");
        reservasi2.setIdDokter("dokB");
        reservasi2.setIdPasien("pac123");
        reservasi2.setDay("TUESDAY");
        reservasi2.setStartTime("11:00");
        reservasi2.setEndTime("12:00");
        reservasi2.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);

        when(reservasiService.findAllByPasien("pac123")).thenReturn(List.of(reservasi1, reservasi2));

        mockMvc.perform(get("/api/reservasi-konsultasi/pac123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("RSV001"))
                .andExpect(jsonPath("$[1].id").value("RSV002"))
                .andExpect(jsonPath("$[0].idPasien").value("pac123"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(reservasiService).findAllByPasien("pac123");
    }

    @Test
    void testAcceptChangeReservasi_success() throws Exception {
        ReservasiKonsultasi updated = new ReservasiKonsultasi();
        updated.setId("RSV001");
        updated.setIdDokter("dok123");
        updated.setIdPasien("pac123");
        updated.setDay("THURSDAY");
        updated.setStartTime("15:00");
        updated.setEndTime("16:00");
        updated.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        when(reservasiService.acceptChangeReservasi("RSV001")).thenReturn(updated);

        mockMvc.perform(post("/api/reservasi-konsultasi/RSV001/accept-change"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Perubahan reservasi diterima"))
                .andExpect(jsonPath("$.reservasi.day").value("THURSDAY"))
                .andExpect(jsonPath("$.reservasi.startTime").value("15:00"));
    }

    @Test
    void testRejectChangeReservasi_success() throws Exception {
        doNothing().when(reservasiService).rejectChangeReservasi("RSV001");

        mockMvc.perform(post("/api/reservasi-konsultasi/RSV001/reject-change"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Perubahan jadwal ditolak"));

        verify(reservasiService).rejectChangeReservasi("RSV001");
    }

    @Test
    void testRejectChangeReservasi_fail() throws Exception {
        doThrow(new IllegalStateException("No change request exists for this schedule"))
                .when(reservasiService).rejectChangeReservasi("RSV001");

        mockMvc.perform(post("/api/reservasi-konsultasi/RSV001/reject-change"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No change request exists for this schedule"));

        verify(reservasiService).rejectChangeReservasi("RSV001");
    }

    @Test
    void testRequestReservasi_invalidTime_shouldReturn400() throws Exception {
        when(reservasiService.requestReservasi(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Start time must be before end time"));

        mockMvc.perform(post("/api/reservasi-konsultasi/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "idDokter": "dok123",
                            "idPasien": "pac123",
                            "day": "MONDAY",
                            "startTime": "10:00",
                            "endTime": "09:00"
                        }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAcceptChangeReservasi_notFound_shouldReturn400() throws Exception {
        when(reservasiService.acceptChangeReservasi("not_found"))
                .thenThrow(new IllegalArgumentException("Schedule not found"));

        mockMvc.perform(post("/api/reservasi-konsultasi/not_found/accept-change"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Schedule not found"));
    }

    @Test
    void testRejectChangeReservasi_notFound_shouldReturn400() throws Exception {
        doThrow(new IllegalArgumentException("Schedule not found"))
                .when(reservasiService).rejectChangeReservasi("not_found");

        mockMvc.perform(post("/api/reservasi-konsultasi/not_found/reject-change"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Schedule not found"));
    }

    @Test
    void testEditReservasi_notFound_shouldReturn400() throws Exception {
        when(reservasiService.editReservasi(eq("not_found"), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Schedule not found"));

        mockMvc.perform(post("/api/reservasi-konsultasi/not_found/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "day": "MONDAY",
                            "startTime": "09:00",
                            "endTime": "10:00"
                        }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Schedule not found"));
    }
}