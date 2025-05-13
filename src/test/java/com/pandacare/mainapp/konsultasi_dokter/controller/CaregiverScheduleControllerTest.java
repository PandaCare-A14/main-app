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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
        String idCaregiver = "DOC12345";
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setDay("MONDAY");
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId("SCHED12345");
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));

        when(service.createSchedule(anyString(), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(schedule);

        mockMvc.perform(post("/api/doctors/{idCaregiver}/schedules", idCaregiver)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("SCHED12345"))
                .andExpect(jsonPath("$.idCaregiver").value(idCaregiver));
    }

    @Test
    public void testCreateScheduleInterval() throws Exception {
        String idCaregiver = "DOC12345";
        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setDay("MONDAY");
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");

        List<CaregiverSchedule> schedules = new ArrayList<>();
        CaregiverSchedule schedule1 = new CaregiverSchedule();
        schedule1.setId("SCHED12345");
        schedule1.setIdCaregiver(idCaregiver);
        schedule1.setDay(DayOfWeek.MONDAY);
        schedule1.setStartTime(LocalTime.of(9, 0));
        schedule1.setEndTime(LocalTime.of(9, 30));

        CaregiverSchedule schedule2 = new CaregiverSchedule();
        schedule2.setId("SCHED12346");
        schedule2.setIdCaregiver(idCaregiver);
        schedule2.setDay(DayOfWeek.MONDAY);
        schedule2.setStartTime(LocalTime.of(9, 30));
        schedule2.setEndTime(LocalTime.of(10, 0));

        schedules.add(schedule1);
        schedules.add(schedule2);

        when(service.createMultipleSchedules(anyString(), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(schedules);

        mockMvc.perform(post("/api/doctors/{idCaregiver}/schedules/interval", idCaregiver)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value("SCHED12345"))
                .andExpect(jsonPath("$[1].id").value("SCHED12346"))
                .andExpect(jsonPath("$[0].idCaregiver").value(idCaregiver))
                .andExpect(jsonPath("$[1].idCaregiver").value(idCaregiver));
    }

    @Test
    public void testGetScheduleByCaregiver() throws Exception {
        String idCaregiver = "DOC4567";

        List<CaregiverSchedule> schedules = new ArrayList<>();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId("SCHED4567");
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDay(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedules.add(schedule);

        when(service.getSchedulesByCaregiver(idCaregiver)).thenReturn(schedules);

        mockMvc.perform(get("/api/doctors/{idCaregiver}/schedules", idCaregiver))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("SCHED4567"))
                .andExpect(jsonPath("$[0].idCaregiver").value(idCaregiver));
    }

    @Test
    public void testGetScheduleByCaregiverAndStatus() throws Exception {
        String idCaregiver = "DOC4567";
        String statusStr = "AVAILABLE";
        ScheduleStatus status = ScheduleStatus.AVAILABLE;

        List<CaregiverSchedule> schedules = new ArrayList<>();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId("SCHED4567");
        schedule.setIdCaregiver(idCaregiver);
        schedules.add(schedule);

        when(service.getSchedulesByCaregiverAndStatus(idCaregiver, status)).thenReturn(schedules);

        mockMvc.perform(get("/api/doctors/{idCaregiver}/schedules", idCaregiver)
                        .param("status", statusStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("SCHED4567"));
    }

    @Test
    public void testGetScheduleByCaregiverAndDay() throws Exception {
        String idCaregiver = "DOC1111";
        String day = "MONDAY";

        List<CaregiverSchedule> schedules = new ArrayList<>();
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId("SCHED1111");
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDay(DayOfWeek.MONDAY);
        schedules.add(schedule);

        when(service.getSchedulesByCaregiverAndDay(anyString(), any(DayOfWeek.class))).thenReturn(schedules);

        mockMvc.perform(get("/api/doctors/{idCaregiver}/schedules", idCaregiver)
                        .param("day", day))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("SCHED1111"));
    }

    @Test
    public void testGetScheduleByCaregiverAndIdSchedule() throws Exception {
        String idCaregiver = "DOC1111";
        String idSchedule = "SCHED1111";

        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setId(idSchedule);
        schedule.setIdCaregiver(idCaregiver);
        schedule.setDay(DayOfWeek.MONDAY);

        when(service.getSchedulesByCaregiverAndIdSchedule(idCaregiver, idSchedule)).thenReturn(schedule);

        mockMvc.perform(get("/api/doctors/{idCaregiver}/schedules", idCaregiver)
                        .param("idSchedule", idSchedule))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(idSchedule));
    }
}