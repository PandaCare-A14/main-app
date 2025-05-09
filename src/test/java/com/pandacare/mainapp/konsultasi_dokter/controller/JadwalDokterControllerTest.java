package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pandacare.mainapp.konsultasi_dokter.model.JadwalKonsultasi;
import com.pandacare.mainapp.konsultasi_dokter.service.JadwalDokterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class JadwalDokterControllerTest {
    private MockMvc mockMvc;
    @Mock
    private JadwalDokterService service;
    @InjectMocks
    private JadwalDokterController controller;

    private final String DOCTOR_ID = "DOC12345";
    private final String SCHEDULE_ID = "SCHED12345";
    private final String PATIENT_ID = "PAT12345";

    private JadwalKonsultasi jadwal;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        jadwal = new JadwalKonsultasi();
        jadwal.setId(SCHEDULE_ID);
        jadwal.setIdDokter(DOCTOR_ID);
        jadwal.setIdPasien(PATIENT_ID);
        jadwal.setDate(LocalDate.of(2025, 5, 10));
        jadwal.setStartTime(LocalTime.of(9, 0));
        jadwal.setEndTime(LocalTime.of(9, 30));

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    @Test
    public void testCreateJadwal() {
        Map<String, String> body = new HashMap<>();
        body.put("date", "2025-05-10");
        body.put("startTime", "09:00");
        body.put("endTime", "09:30");

        when(service.createJadwal(DOCTOR_ID,
                LocalDate.of(2025, 5, 10),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30))).thenReturn(jadwal);

        ResponseEntity<JadwalKonsultasi> response = controller.createJadwal(DOCTOR_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(SCHEDULE_ID, response.getBody().getId());
        verify(service).createJadwal(DOCTOR_ID,
                LocalDate.of(2025, 5, 10),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30));
    }

    @Test
    public void testCreateJadwalWithInvalidInput() {
        Map<String, String> body = new HashMap<>();
        body.put("date", "invalid");
        body.put("startTime", "09:00");
        body.put("endTime", "09:30");

        ResponseEntity<JadwalKonsultasi> response = controller.createJadwal(DOCTOR_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(service, never()).createJadwal(anyString(), any(), any(), any());
    }

    @Test
    public void testCreateJadwalIntervalWithDefaultDuration() {
        Map<String, String> body = new HashMap<>();
        body.put("date", "2025-05-10");
        body.put("startTime", "09:00");
        body.put("endTime", "10:00");

        List<JadwalKonsultasi> jadwalList = Arrays.asList(jadwal, jadwal);
        when(service.createJadwalInterval(DOCTOR_ID,
                LocalDate.of(2025, 5, 10),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0))).thenReturn(jadwalList);

        ResponseEntity<List<JadwalKonsultasi>> response = controller.createJadwalInterval(DOCTOR_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(service).createJadwalInterval(DOCTOR_ID,
                LocalDate.of(2025, 5, 10),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0));
    }

    @Test
    public void testCreateJadwalIntervalWithInvalidInput() {
        Map<String, String> body = new HashMap<>();
        body.put("date", "2025-05-10");
        body.put("startTime", "invalid");
        body.put("endTime", "10:00");

        ResponseEntity<List<JadwalKonsultasi>> response = controller.createJadwalInterval(DOCTOR_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(service, never()).createJadwalInterval(anyString(), any(), any(), any());
    }

    @Test
    public void testGetJadwalByDokterWithoutStatus() {
        List<JadwalKonsultasi> jadwalList = Arrays.asList(jadwal, jadwal);
        when(service.findByIdDokter(DOCTOR_ID)).thenReturn(jadwalList);

        ResponseEntity<List<JadwalKonsultasi>> response = controller.getJadwalByDokter(DOCTOR_ID, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(service).findByIdDokter(DOCTOR_ID);
        verify(service, never()).findByIdDokterAndStatus(anyString(), anyString());
    }

    @Test
    public void testGetJadwalByDokterWithValidStatus() {
        List<JadwalKonsultasi> jadwalList = Arrays.asList(jadwal, jadwal);
        when(service.findByIdDokterAndStatus(DOCTOR_ID, "AVAILABLE")).thenReturn(jadwalList);

        ResponseEntity<List<JadwalKonsultasi>> response = controller.getJadwalByDokter(DOCTOR_ID, "available");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(service).findByIdDokterAndStatus(DOCTOR_ID, "AVAILABLE");
        verify(service, never()).findByIdDokter(anyString());
    }

    @Test
    public void testGetJadwalByDokterServiceException() {
        when(service.findByIdDokter(DOCTOR_ID)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<List<JadwalKonsultasi>> response = controller.getJadwalByDokter(DOCTOR_ID, null);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(service).findByIdDokter(DOCTOR_ID);
    }

    @Test
    public void testGetJadwalByDokterIllegalArgumentException() {
        when(service.findByIdDokter(DOCTOR_ID)).thenThrow(new IllegalArgumentException("Invalid doctor ID"));

        ResponseEntity<List<JadwalKonsultasi>> response = controller.getJadwalByDokter(DOCTOR_ID, null);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(service).findByIdDokter(DOCTOR_ID);
    }

    @Test
    public void testFindById() {
        when(service.findById(SCHEDULE_ID)).thenReturn(jadwal);

        ResponseEntity<JadwalKonsultasi> response = controller.findById(SCHEDULE_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SCHEDULE_ID, response.getBody().getId());
        verify(service).findById(SCHEDULE_ID);
    }

    @Test
    public void testFindByIdNotFound() {
        when(service.findById(SCHEDULE_ID)).thenReturn(null);

        ResponseEntity<JadwalKonsultasi> response = controller.findById(SCHEDULE_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(service).findById(SCHEDULE_ID);
    }

    @Test
    public void testUpdateStatusApproved() {
        Map<String, String> body = new HashMap<>();
        body.put("statusDokter", "APPROVED");

        when(service.approveJadwal(SCHEDULE_ID)).thenReturn(true);
        when(service.findById(SCHEDULE_ID)).thenReturn(jadwal);

        ResponseEntity<JadwalKonsultasi> response = controller.updateStatus(SCHEDULE_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SCHEDULE_ID, response.getBody().getId());
        verify(service).approveJadwal(SCHEDULE_ID);
        verify(service).findById(SCHEDULE_ID);
    }

    @Test
    public void testUpdateStatusApprovedFailed() {
        Map<String, String> body = new HashMap<>();
        body.put("statusDokter", "APPROVED");

        when(service.approveJadwal(SCHEDULE_ID)).thenReturn(false);

        ResponseEntity<JadwalKonsultasi> response = controller.updateStatus(SCHEDULE_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(service).approveJadwal(SCHEDULE_ID);
        verify(service, never()).findById(anyString());
    }

    @Test
    public void testUpdateStatusRejected() {
        Map<String, String> body = new HashMap<>();
        body.put("statusDokter", "REJECTED");

        when(service.rejectJadwal(SCHEDULE_ID)).thenReturn(true);
        when(service.findById(SCHEDULE_ID)).thenReturn(jadwal);

        ResponseEntity<JadwalKonsultasi> response = controller.updateStatus(SCHEDULE_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SCHEDULE_ID, response.getBody().getId());
        verify(service).rejectJadwal(SCHEDULE_ID);
        verify(service).findById(SCHEDULE_ID);
    }

    @Test
    public void testUpdateStatusChangeSchedule() {
        Map<String, String> body = new HashMap<>();
        body.put("statusDokter", "CHANGE_SCHEDULE");
        body.put("date", "2025-05-15");
        body.put("startTime", "14:00");
        body.put("endTime", "14:30");
        body.put("message", null);

        when(service.changeJadwal(
                SCHEDULE_ID,
                LocalDate.of(2025, 5, 15),
                LocalTime.of(14, 0),
                LocalTime.of(14, 30),
                null)).thenReturn(true);
        when(service.findById(SCHEDULE_ID)).thenReturn(jadwal);

        ResponseEntity<JadwalKonsultasi> response = controller.updateStatus(SCHEDULE_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SCHEDULE_ID, response.getBody().getId());
        verify(service).changeJadwal(
                SCHEDULE_ID,
                LocalDate.of(2025, 5, 15),
                LocalTime.of(14, 0),
                LocalTime.of(14, 30),
                null);
        verify(service).findById(SCHEDULE_ID);
    }

    @Test
    public void testUpdateStatusInvalid() {
        Map<String, String> body = new HashMap<>();
        body.put("statusDokter", "INVALID");

        ResponseEntity<JadwalKonsultasi> response = controller.updateStatus(SCHEDULE_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(service, never()).approveJadwal(anyString());
        verify(service, never()).rejectJadwal(anyString());
        verify(service, never()).changeJadwal(anyString(), any(), any(), any(), anyString());
        verify(service, never()).findById(anyString());
    }

    @Test
    public void testUpdateStatusMissingStatus() {
        Map<String, String> body = new HashMap<>();
        ResponseEntity<JadwalKonsultasi> response = controller.updateStatus(SCHEDULE_ID, body);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(service, never()).approveJadwal(anyString());
        verify(service, never()).rejectJadwal(anyString());
        verify(service, never()).changeJadwal(anyString(), any(), any(), any(), anyString());
        verify(service, never()).findById(anyString());
    }

    @Test
    public void testUpdateStatusIllegalArgumentException() {
        Map<String, String> body = new HashMap<>();
        body.put("statusDokter", "APPROVED");

        when(service.approveJadwal(SCHEDULE_ID)).thenThrow(new IllegalArgumentException("Invalid ID"));

        ResponseEntity<JadwalKonsultasi> response = controller.updateStatus(SCHEDULE_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(service).approveJadwal(SCHEDULE_ID);
        verify(service, never()).findById(anyString());
    }

    @Test
    public void testUpdateStatusChangeScheduleInvalidParams() {
        Map<String, String> body = new HashMap<>();
        body.put("statusDokter", "CHANGE_SCHEDULE");
        body.put("date", "2025-05-15");
        body.put("startTime", "invalid");
        body.put("endTime", "14:30");
        body.put("message", "Mohon reschedule, jadwal tabrakan dengan jadwal saya di RS.");

        ResponseEntity<JadwalKonsultasi> response = controller.updateStatus(SCHEDULE_ID, body);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(service, never()).changeJadwal(anyString(), any(), any(), any(), anyString());
    }

    @Test
    public void testMockMvcCreateJadwal() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("date", "2025-05-10");
        requestBody.put("startTime", "09:00");
        requestBody.put("endTime", "09:30");

        when(service.createJadwal(
                DOCTOR_ID,
                LocalDate.of(2025, 5, 10),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30)
        )).thenReturn(jadwal);

        mockMvc.perform(post("/doctors/" + DOCTOR_ID + "/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SCHEDULE_ID));
    }

    @Test
    public void testMockMvcCreateJadwalWithInvalidDate() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("date", "invalid");
        requestBody.put("startTime", "09:00");
        requestBody.put("endTime", "09:30");

        mockMvc.perform(post("/doctors/" + DOCTOR_ID + "/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMockMvcFindById() throws Exception {
        when(service.findById(SCHEDULE_ID)).thenReturn(jadwal);

        mockMvc.perform(get("/doctors/schedules/" + SCHEDULE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SCHEDULE_ID));
    }

    @Test
    public void testMockMvcUpdateStatus() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("statusDokter", "APPROVED");

        when(service.approveJadwal(SCHEDULE_ID)).thenReturn(true);
        when(service.findById(SCHEDULE_ID)).thenReturn(jadwal);

        mockMvc.perform(patch("/doctors/schedules/" + SCHEDULE_ID + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SCHEDULE_ID));
    }

    @Test
    public void testMockMvcGetJadwalByDokter() throws Exception {
        List<JadwalKonsultasi> jadwalList = Arrays.asList(jadwal, jadwal);
        when(service.findByIdDokter(DOCTOR_ID)).thenReturn(jadwalList);

        mockMvc.perform(get("/doctors/" + DOCTOR_ID + "/schedules")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(SCHEDULE_ID))
                .andExpect(jsonPath("$[1].id").value(SCHEDULE_ID));
    }
}