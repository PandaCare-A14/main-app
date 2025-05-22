package com.pandacare.mainapp.reservasi.controller;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.reservasi.enums.StatusReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.model.ReservasiKonsultasi;
import com.pandacare.mainapp.reservasi.service.ReservasiKonsultasiServiceImpl;
import com.pandacare.mainapp.reservasi.service.caregiver.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
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

    @MockBean
    private ScheduleService scheduleService;

    private UUID scheduleId;
    private CaregiverSchedule schedule;
    private ReservasiKonsultasi waitingReservasi;

    @BeforeEach
    void setup() {
        scheduleId = UUID.randomUUID();

        schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));  // Use LocalTime instead of String
        schedule.setEndTime(LocalTime.of(10, 0));
        schedule.setStatus(ScheduleStatus.AVAILABLE);

        waitingReservasi = new ReservasiKonsultasi();
        waitingReservasi.setId("RSV001");
        waitingReservasi.setIdDokter("dok123");
        waitingReservasi.setIdPacilian("pac123");
        waitingReservasi.setDay("MONDAY");
        waitingReservasi.setStartTime(LocalTime.of(9, 0));  // Use LocalTime instead of String
        waitingReservasi.setEndTime(LocalTime.of(10, 0));
        waitingReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        waitingReservasi.setIdSchedule(schedule);
    }

    @Test
    void testRequestReservasi_success() throws Exception {
        when(reservasiService.requestReservasi(any(UUID.class), anyString())).thenReturn(waitingReservasi);

        mockMvc.perform(post("/api/reservasi-konsultasi/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                            {
                                "idSchedule": "%s",
                                "idPacilian": "pac123"
                            }
                        """, scheduleId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Jadwal konsultasi berhasil diajukan"))
                .andExpect(jsonPath("$.reservasi.idDokter").value("dok123"));

        verify(reservasiService).requestReservasi(any(UUID.class), eq("pac123"));
    }

    @Test
    void testEditReservasi_success() throws Exception {
        UUID newScheduleId = UUID.randomUUID();
        CaregiverSchedule newSchedule = new CaregiverSchedule();
        newSchedule.setId(newScheduleId);
        newSchedule.setDay(DayOfWeek.TUESDAY);
        newSchedule.setStartTime(LocalTime.of(10, 0));
        newSchedule.setEndTime(LocalTime.of(11, 0));
        newSchedule.setStatus(ScheduleStatus.AVAILABLE);

        ReservasiKonsultasi updatedReservasi = new ReservasiKonsultasi();
        updatedReservasi.setId("RSV001");
        updatedReservasi.setIdDokter("dok123");
        updatedReservasi.setIdPacilian("pac123");
        updatedReservasi.setDay("TUESDAY");
        updatedReservasi.setStartTime(LocalTime.of(10, 0));
        updatedReservasi.setEndTime(LocalTime.of(11, 0));
        updatedReservasi.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        updatedReservasi.setIdSchedule(newSchedule);

        when(reservasiService.editReservasi(eq("RSV001"), any(UUID.class)))
                .thenReturn(updatedReservasi);

        mockMvc.perform(post("/api/reservasi-konsultasi/{id}/edit", "RSV001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                {
                    "idSchedule": "%s"
                }
                """, newScheduleId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reservasi updated successfully"))
                .andExpect(jsonPath("$.reservasi.day").value("TUESDAY"))
                .andExpect(jsonPath("$.reservasi.startTime").value("10:00:00"))
                .andExpect(jsonPath("$.reservasi.endTime").value("11:00:00"));

        verify(reservasiService).editReservasi("RSV001", newScheduleId);
    }

    @Test
    void testEditReservasi_error_invalidStatus() throws Exception {
        when(reservasiService.editReservasi(any(), any(UUID.class)))
                .thenThrow(new IllegalStateException("Only schedules with status WAITING can be edited"));

        mockMvc.perform(post("/api/reservasi-konsultasi/{id}/edit", "RSV001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                    {
                        "idSchedule": "%s"
                    }
                    """, UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Only schedules with status WAITING can be edited"));
    }

    @Test
    void testGetAllReservasiByIdPasien_success() throws Exception {
        ReservasiKonsultasi reservasi2 = new ReservasiKonsultasi();
        reservasi2.setId("RSV002");
        reservasi2.setIdDokter("dokB");
        reservasi2.setIdPacilian("pac123");
        reservasi2.setDay("TUESDAY");
        reservasi2.setStartTime(LocalTime.of(11, 0));
        reservasi2.setEndTime(LocalTime.of(12, 0));
        reservasi2.setStatusReservasi(StatusReservasiKonsultasi.APPROVED);
        reservasi2.setIdSchedule(schedule);

        when(reservasiService.findAllByPasien("pac123")).thenReturn(List.of(waitingReservasi, reservasi2));

        mockMvc.perform(get("/api/reservasi-konsultasi/pac123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("RSV001"))
                .andExpect(jsonPath("$[1].id").value("RSV002"))
                .andExpect(jsonPath("$[0].idPacilian").value("pac123"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(reservasiService).findAllByPasien("pac123");
    }

    @Test
    void testAcceptChangeReservasi_success() throws Exception {
        ReservasiKonsultasi updated = new ReservasiKonsultasi();
        updated.setId("RSV001");
        updated.setIdDokter("dok123");
        updated.setIdPacilian("pac123");
        updated.setDay("THURSDAY");
        updated.setStartTime(LocalTime.of(15, 0));
        updated.setEndTime(LocalTime.of(16, 0));
        updated.setStatusReservasi(StatusReservasiKonsultasi.WAITING);
        updated.setIdSchedule(schedule);

        when(reservasiService.acceptChangeReservasi("RSV001")).thenReturn(updated);

        mockMvc.perform(post("/api/reservasi-konsultasi/RSV001/accept-change"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Perubahan reservasi diterima"))
                .andExpect(jsonPath("$.reservasi.day").value("THURSDAY"))
                .andExpect(jsonPath("$.reservasi.startTime").value("15:00:00"));
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
        when(reservasiService.requestReservasi(any(UUID.class), anyString()))
                .thenThrow(new IllegalArgumentException("Selected schedule is not available"));

        mockMvc.perform(post("/api/reservasi-konsultasi/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                        {
                            "idSchedule": "%s",
                            "idPacilian": "pac123"
                        }
                    """, scheduleId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Selected schedule is not available"));
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
        when(reservasiService.editReservasi(eq("not_found"), any(UUID.class)))
                .thenThrow(new IllegalArgumentException("Schedule not found"));

        mockMvc.perform(post("/api/reservasi-konsultasi/not_found/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                {
                    "idSchedule": "%s"
                }
                """, UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Schedule not found"));
    }
}