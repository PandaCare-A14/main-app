package com.pandacare.mainapp.konsultasi_dokter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandacare.mainapp.konsultasi_dokter.dto.CreateScheduleDTO;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.konsultasi_dokter.service.CaregiverScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CaregiverScheduleControllerTest {
    private MockMvc mockMvc;
    @Mock
    private CaregiverScheduleService service;
    @InjectMocks
    private CaregiverScheduleController controller;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper.findAndRegisterModules();
    }

    @Test
    public void testCreateSchedule() throws Exception {
        UUID idCaregiver = UUID.randomUUID();
        UUID idSchedule = UUID.randomUUID();
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setDay("MONDAY");
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(idSchedule);
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));

        when(service.createSchedule(any(UUID.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(schedule);

        mockMvc.perform(post("/api/doctors/{idCaregiver}/schedules", idCaregiver)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(idSchedule.toString()))
                .andExpect(jsonPath("$.idCaregiver").value(idCaregiver.toString()));

        verify(service).createSchedule(eq(idCaregiver), eq(DayOfWeek.MONDAY),
                eq(LocalTime.of(9, 0)), eq(LocalTime.of(10, 0)));
    }

    @Test
    public void testCreateScheduleWithWeeks() throws Exception {
        UUID idCaregiver = UUID.randomUUID();
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setDay("MONDAY");
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");
        dto.setWeeks(4);

        List<CaregiverSchedule> schedules = new ArrayList<>();
        List<UUID> scheduleIds = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            UUID scheduleId = UUID.randomUUID();
            scheduleIds.add(scheduleId);

            CaregiverSchedule schedule = new CaregiverSchedule();
            schedule.setId(scheduleId);
            schedule.setIdCaregiver(idCaregiver);
            schedule.setDay(DayOfWeek.MONDAY);
            schedule.setStartTime(LocalTime.of(9, 0));
            schedule.setEndTime(LocalTime.of(10, 0));
            schedule.setDate(LocalDate.now().plusWeeks(i + 1));
            schedules.add(schedule);
        }

        when(service.createRepeatedSchedules(any(UUID.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class), anyInt()))
                .thenReturn(schedules);

        mockMvc.perform(post("/api/doctors/{idCaregiver}/schedules", idCaregiver)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(scheduleIds.get(0).toString()))
                .andExpect(jsonPath("$[1].id").value(scheduleIds.get(1).toString()))
                .andExpect(jsonPath("$[2].id").value(scheduleIds.get(2).toString()))
                .andExpect(jsonPath("$[3].id").value(scheduleIds.get(3).toString()))
                .andExpect(jsonPath("$[0].idCaregiver").value(idCaregiver.toString()));
    }

    @Test
    public void testCreateScheduleInterval() throws Exception {
        UUID idCaregiver = UUID.randomUUID();
        UUID scheduleId1 = UUID.randomUUID();
        UUID scheduleId2 = UUID.randomUUID();
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setDay("MONDAY");
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");

        List<CaregiverSchedule> schedules = new ArrayList<>();
        CaregiverSchedule schedule1 = new CaregiverSchedule();
        schedule1.setId(scheduleId1);
        schedule1.setIdCaregiver(idCaregiver);
        schedule1.setDay(DayOfWeek.MONDAY);
        schedule1.setStartTime(LocalTime.of(9, 0));
        schedule1.setEndTime(LocalTime.of(9, 30));

        CaregiverSchedule schedule2 = new CaregiverSchedule();
        schedule2.setId(scheduleId2);
        schedule2.setIdCaregiver(idCaregiver);
        schedule2.setDay(DayOfWeek.MONDAY);
        schedule2.setStartTime(LocalTime.of(9, 30));
        schedule2.setEndTime(LocalTime.of(10, 0));

        schedules.add(schedule1);
        schedules.add(schedule2);

        when(service.createMultipleSchedules(any(UUID.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(schedules);

        mockMvc.perform(post("/api/doctors/{idCaregiver}/schedules/interval", idCaregiver)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(scheduleId1.toString()))
                .andExpect(jsonPath("$[1].id").value(scheduleId2.toString()))
                .andExpect(jsonPath("$[0].idCaregiver").value(idCaregiver.toString()))
                .andExpect(jsonPath("$[1].idCaregiver").value(idCaregiver.toString()));

        verify(service).createMultipleSchedules(eq(idCaregiver), eq(DayOfWeek.MONDAY),
                eq(LocalTime.of(9, 0)), eq(LocalTime.of(10, 0)));
    }

    @Test
    public void testCreateScheduleIntervalWithWeeks() throws Exception {
        UUID idCaregiver = UUID.randomUUID();
        List<UUID> scheduleIds = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            scheduleIds.add(UUID.randomUUID());
        }

        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setDay("MONDAY");
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");
        dto.setWeeks(2);

        List<CaregiverSchedule> schedules = new ArrayList<>();
        CaregiverSchedule schedule1 = new CaregiverSchedule();
        schedule1.setId(scheduleIds.get(0));
        schedule1.setIdCaregiver(idCaregiver);
        schedule1.setDay(DayOfWeek.MONDAY);
        schedule1.setStartTime(LocalTime.of(9, 0));
        schedule1.setEndTime(LocalTime.of(9, 30));

        CaregiverSchedule schedule2 = new CaregiverSchedule();
        schedule2.setId(scheduleIds.get(1));
        schedule2.setIdCaregiver(idCaregiver);
        schedule2.setDay(DayOfWeek.MONDAY);
        schedule2.setStartTime(LocalTime.of(9, 30));
        schedule2.setEndTime(LocalTime.of(10, 0));

        CaregiverSchedule schedule3 = new CaregiverSchedule();
        schedule3.setId(scheduleIds.get(2));
        schedule3.setIdCaregiver(idCaregiver);
        schedule3.setDay(DayOfWeek.MONDAY);
        schedule3.setStartTime(LocalTime.of(9, 0));
        schedule3.setEndTime(LocalTime.of(9, 30));

        CaregiverSchedule schedule4 = new CaregiverSchedule();
        schedule4.setId(scheduleIds.get(3));
        schedule4.setIdCaregiver(idCaregiver);
        schedule4.setDay(DayOfWeek.MONDAY);
        schedule4.setStartTime(LocalTime.of(9, 30));
        schedule4.setEndTime(LocalTime.of(10, 0));

        schedules.add(schedule1);
        schedules.add(schedule2);
        schedules.add(schedule3);
        schedules.add(schedule4);

        when(service.createRepeatedMultipleSchedules(any(UUID.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class), anyInt()))
                .thenReturn(schedules);

        mockMvc.perform(post("/api/doctors/{idCaregiver}/schedules/interval", idCaregiver)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(scheduleIds.get(0).toString()))
                .andExpect(jsonPath("$[1].id").value(scheduleIds.get(1).toString()))
                .andExpect(jsonPath("$[2].id").value(scheduleIds.get(2).toString()))
                .andExpect(jsonPath("$[3].id").value(scheduleIds.get(3).toString()))
                .andExpect(jsonPath("$[0].idCaregiver").value(idCaregiver.toString()))
                .andExpect(jsonPath("$[3].idCaregiver").value(idCaregiver.toString()));
    }

    @Test
    public void testGetScheduleByCaregiver() throws Exception {
        UUID idCaregiver = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();

        List<CaregiverSchedule> schedules = new ArrayList<>();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedules.add(schedule);

        when(service.getSchedulesByCaregiver(idCaregiver)).thenReturn(schedules);

        mockMvc.perform(get("/api/doctors/{idCaregiver}/schedules", idCaregiver))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(scheduleId.toString()))
                .andExpect(jsonPath("$[0].idCaregiver").value(idCaregiver.toString()));
    }

    @Test
    public void testGetScheduleByCaregiverAndStatus() throws Exception {
        UUID idCaregiver = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        String statusStr = "AVAILABLE";
        ScheduleStatus status = ScheduleStatus.AVAILABLE;

        List<CaregiverSchedule> schedules = new ArrayList<>();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setIdCaregiver(idCaregiver);
        schedules.add(schedule);

        when(service.getSchedulesByCaregiverAndStatus(idCaregiver, status)).thenReturn(schedules);

        mockMvc.perform(get("/api/doctors/{idCaregiver}/schedules", idCaregiver)
                        .param("status", statusStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(scheduleId.toString()));
    }

    @Test
    public void testGetScheduleByCaregiverAndDay() throws Exception {
        UUID idCaregiver = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        String day = "MONDAY";

        List<CaregiverSchedule> schedules = new ArrayList<>();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(scheduleId);
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDay(DayOfWeek.MONDAY);
        schedules.add(schedule);

        when(service.getSchedulesByCaregiverAndDay(any(UUID.class), any(DayOfWeek.class))).thenReturn(schedules);

        mockMvc.perform(get("/api/doctors/{idCaregiver}/schedules", idCaregiver)
                        .param("day", day))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(scheduleId.toString()));
    }

    @Test
    public void testGetScheduleByCaregiverAndIdSchedule() throws Exception {
        UUID idCaregiver = UUID.randomUUID();
        UUID idSchedule = UUID.randomUUID();

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(idSchedule);
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDay(DayOfWeek.MONDAY);

        when(service.getSchedulesByCaregiverAndIdSchedule(idCaregiver, idSchedule)).thenReturn(schedule);

        mockMvc.perform(get("/api/doctors/{idCaregiver}/schedules", idCaregiver)
                        .param("idSchedule", idSchedule.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(idSchedule.toString()));
    }
}