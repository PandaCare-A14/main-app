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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        originalReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);  // Status awal

        // Membuat objek untuk reservasi setelah diedit
        ReservasiKonsultasi updatedReservasi = new ReservasiKonsultasi();
        updatedReservasi.setId("RSV001");
        updatedReservasi.setIdDokter("dok123");
        updatedReservasi.setIdPasien("pac123");
        updatedReservasi.setDay("TUESDAY");       // Updated value
        updatedReservasi.setStartTime("10:00");   // Updated value
        updatedReservasi.setEndTime("11:00");     // Updated value
        updatedReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);

        // Mock service untuk mengedit reservasi dengan detail yang lebih spesifik
        when(reservasiService.editReservasi(
                eq("RSV001"),
                eq("TUESDAY"),
                eq("10:00"),
                eq("11:00")
        )).thenReturn(updatedReservasi);

        // Melakukan request POST ke endpoint /{id}/edit
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

        // Verifikasi bahwa metode service dipanggil dengan parameter yang benar
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
}