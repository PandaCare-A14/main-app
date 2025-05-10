package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.service.CaregiverScheduleService;
import com.pandacare.mainapp.konsultasi_dokter.dto.CreateScheduleDTO;
import com.pandacare.mainapp.konsultasi_dokter.dto.UpdateScheduleStatusDTO;
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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CaregiverScheduleControllerTest {
    private MockMvc mockMvc;
    @Mock
    private CaregiverScheduleService service;
    @InjectMocks
    private CaregiverScheduleController controller;

    private final String DOCTOR_ID = "DOC12345";
    private final String SCHEDULE_ID = "SCHED12345";
    private final String PATIENT_ID = "PAT12345";

    private CaregiverSchedule schedule;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        schedule = new CaregiverSchedule();
        schedule.setId(SCHEDULE_ID);
        schedule.setIdCaregiver(DOCTOR_ID);
        schedule.setIdPacilian(PATIENT_ID);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(9, 30));

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testCreateSchedule() {
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setDay("MONDAY");
        dto.setStartTime("09:00");
        dto.setEndTime("09:30");

        when(service.createSchedule(DOCTOR_ID,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(9, 30))).thenReturn(schedule);

        ResponseEntity<CaregiverSchedule> response = controller.createSchedule(DOCTOR_ID, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(SCHEDULE_ID, response.getBody().getId());
        verify(service).createSchedule(DOCTOR_ID,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(9, 30));
    }

    @Test
    public void testCreateScheduleWithInvalidInput() {
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setDay("invalid");
        dto.setStartTime("09:00");
        dto.setEndTime("09:30");

        ResponseEntity<CaregiverSchedule> response = controller.createSchedule(DOCTOR_ID, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(service, never()).createSchedule(anyString(), any(), any(), any());
    }

    @Test
    public void testCreateScheduleIntervalWithDefaultDuration() {
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setDay("MONDAY");
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");

        List<CaregiverSchedule> scheduleList = Arrays.asList(schedule, schedule);
        when(service.createScheduleInterval(DOCTOR_ID,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0))).thenReturn(scheduleList);

        ResponseEntity<List<CaregiverSchedule>> response = controller.createScheduleInterval(DOCTOR_ID, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(service).createScheduleInterval(DOCTOR_ID,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0));
    }

    @Test
    public void testCreateScheduleIntervalWithInvalidInput() {
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setDay("MONDAY");
        dto.setStartTime("invalid");
        dto.setEndTime("10:00");

        ResponseEntity<List<CaregiverSchedule>> response = controller.createScheduleInterval(DOCTOR_ID, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(service, never()).createScheduleInterval(anyString(), any(), any(), any());
    }

    @Test
    public void testGetScheduleByCaregiverWithoutStatus() {
        List<CaregiverSchedule> scheduleList = Arrays.asList(schedule, schedule);
        when(service.findByIdCaregiver(DOCTOR_ID)).thenReturn(scheduleList);

        ResponseEntity<List<CaregiverSchedule>> response = controller.getScheduleByCaregiver(DOCTOR_ID, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(service).findByIdCaregiver(DOCTOR_ID);
        verify(service, never()).findByIdCaregiverAndStatus(anyString(), anyString());
    }

    @Test
    public void testGetScheduleByCaregiverWithValidStatus() {
        List<CaregiverSchedule> scheduleList = Arrays.asList(schedule, schedule);
        when(service.findByIdCaregiverAndStatus(DOCTOR_ID, "AVAILABLE")).thenReturn(scheduleList);

        ResponseEntity<List<CaregiverSchedule>> response = controller.getScheduleByCaregiver(DOCTOR_ID, "available");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(service).findByIdCaregiverAndStatus(DOCTOR_ID, "AVAILABLE");
        verify(service, never()).findByIdCaregiver(anyString());
    }

    @Test
    public void testFindById() {
        when(service.findById(SCHEDULE_ID)).thenReturn(schedule);

        ResponseEntity<CaregiverSchedule> response = controller.findByScheduleId(SCHEDULE_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SCHEDULE_ID, response.getBody().getId());
        verify(service).findById(SCHEDULE_ID);
    }

    @Test
    public void testUpdateStatusApproved() {
        UpdateScheduleStatusDTO dto = new UpdateScheduleStatusDTO();
        dto.setStatusCaregiver("APPROVED");

        when(service.approveSchedule(SCHEDULE_ID)).thenReturn(true);
        when(service.findById(SCHEDULE_ID)).thenReturn(schedule);

        ResponseEntity<CaregiverSchedule> response = controller.updateStatus(SCHEDULE_ID, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SCHEDULE_ID, response.getBody().getId());
        verify(service).approveSchedule(SCHEDULE_ID);
        verify(service).findById(SCHEDULE_ID);
    }

    @Test
    public void testUpdateStatusRejected() {
        UpdateScheduleStatusDTO dto = new UpdateScheduleStatusDTO();
        dto.setStatusCaregiver("REJECTED");

        when(service.rejectSchedule(SCHEDULE_ID)).thenReturn(true);
        when(service.findById(SCHEDULE_ID)).thenReturn(schedule);

        ResponseEntity<CaregiverSchedule> response = controller.updateStatus(SCHEDULE_ID, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SCHEDULE_ID, response.getBody().getId());
        verify(service).rejectSchedule(SCHEDULE_ID);
        verify(service).findById(SCHEDULE_ID);
    }

    @Test
    public void testUpdateStatusChangeSchedule() {
        UpdateScheduleStatusDTO dto = new UpdateScheduleStatusDTO();
        dto.setStatusCaregiver("CHANGE_SCHEDULE");
        dto.setDay("TUESDAY");
        dto.setStartTime("14:00");
        dto.setEndTime("14:30");
        dto.setMessage(null);

        when(service.changeSchedule(
                SCHEDULE_ID,
                DayOfWeek.TUESDAY,
                LocalTime.of(14, 0),
                LocalTime.of(14, 30),
                null)).thenReturn(true);
        when(service.findById(SCHEDULE_ID)).thenReturn(schedule);

        ResponseEntity<CaregiverSchedule> response = controller.updateStatus(SCHEDULE_ID, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SCHEDULE_ID, response.getBody().getId());
        verify(service).changeSchedule(
                SCHEDULE_ID,
                DayOfWeek.TUESDAY,
                LocalTime.of(14, 0),
                LocalTime.of(14, 30),
                null);
        verify(service).findById(SCHEDULE_ID);
    }

    @Test
    public void testUpdateStatusInvalid() {
        UpdateScheduleStatusDTO dto = new UpdateScheduleStatusDTO();
        dto.setStatusCaregiver("INVALID");

        ResponseEntity<CaregiverSchedule> response = controller.updateStatus(SCHEDULE_ID, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(service, never()).approveSchedule(anyString());
        verify(service, never()).rejectSchedule(anyString());
        verify(service, never()).changeSchedule(anyString(), any(), any(), any(), anyString());
        verify(service, never()).findById(anyString());
    }

    @Test
    public void testUpdateStatusChangeScheduleInvalidParams() {
        UpdateScheduleStatusDTO dto = new UpdateScheduleStatusDTO();
        dto.setStatusCaregiver("CHANGE_SCHEDULE");
        dto.setDay("TUESDAY");
        dto.setStartTime("invalid");
        dto.setEndTime("14:30");
        dto.setMessage("Mohon reschedule, jadwal tabrakan dengan jadwal saya di RS.");

        ResponseEntity<CaregiverSchedule> response = controller.updateStatus(SCHEDULE_ID, dto);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(service, never()).changeSchedule(anyString(), any(), any(), any(), anyString());
    }

    @Test
    public void testMockMvcCreateSchedule() throws Exception {
        CreateScheduleDTO requestBody = new CreateScheduleDTO();
        requestBody.setDay("WEDNESDAY");
        requestBody.setStartTime("09:00");
        requestBody.setEndTime("09:30");

        when(service.createSchedule(
                DOCTOR_ID,
                DayOfWeek.WEDNESDAY,
                LocalTime.of(9, 0),
                LocalTime.of(9, 30)
        )).thenReturn(schedule);

        mockMvc.perform(post("/doctors/" + DOCTOR_ID + "/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SCHEDULE_ID));
    }

    @Test
    public void testMockMvcCreateScheduleWithInvalidDate() throws Exception {
        CreateScheduleDTO requestBody = new CreateScheduleDTO();
        requestBody.setDay("invalid");
        requestBody.setStartTime("09:00");
        requestBody.setEndTime("09:30");

        mockMvc.perform(post("/doctors/" + DOCTOR_ID + "/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMockMvcUpdateStatus() throws Exception {
        UpdateScheduleStatusDTO requestBody = new UpdateScheduleStatusDTO();
        requestBody.setStatusCaregiver("APPROVED");

        when(service.approveSchedule(SCHEDULE_ID)).thenReturn(true);
        when(service.findById(SCHEDULE_ID)).thenReturn(schedule);

        mockMvc.perform(patch("/doctors/schedules/" + SCHEDULE_ID + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SCHEDULE_ID));
    }
}