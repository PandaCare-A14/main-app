package com.pandacare.mainapp.jadwal.controller;

import com.pandacare.mainapp.jadwal.service.JadwalPacilianServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(JadwalPacilianController.class)
class JadwalPacilianControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JadwalPacilianServiceImpl jadwalService;

    @Test
    void testRequestJadwal_success() throws Exception {
        JadwalKonsultasi dummy = new JadwalKonsultasi();
        dummy.setId("JK001");
        dummy.setIdDokter("dok123");
        dummy.setIdPasien("pac123");
        dummy.setDay("MONDAY");
        dummy.setStartTime("09:00");
        dummy.setEndTime("10:00");

        when(jadwalService.requestJadwal(any(), any(), any(), any(), any())).thenReturn(dummy);

        mockMvc.perform(post("/api/jadwal-konsultasi/request")
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
                .andExpect(jsonPath("$.jadwal.idDokter").value("dok123"));
    }
}