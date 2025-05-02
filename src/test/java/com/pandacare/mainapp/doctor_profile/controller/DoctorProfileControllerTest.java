package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorProfileControllerTest {

    @Mock
    private DoctorProfileService doctorProfileService;

    @Mock
    private Model model;

    @InjectMocks
    private DoctorProfileController doctorProfileController;

    private DoctorProfile testDoctor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Map<String, String> workSchedule = new HashMap<>();
        workSchedule.put("Monday", "09:00-17:00");

        testDoctor = new DoctorProfile(
                "Dr. Smith",
                "smith@example.com",
                "1234567890",
                "123 Main St",
                workSchedule,
                "Cardiology",
                4.8
        );
        testDoctor.setId("eb558e9f-1c39-460e-8860-71af6af63ds2");
    }

    @Test
    void showAllDoctors_shouldReturnSearchView() {
        List<DoctorProfile> doctors = new ArrayList<>();
        doctors.add(testDoctor);

        when(doctorProfileService.findAll()).thenReturn(doctors);

        String viewName = doctorProfileController.showAllDoctors(model);

        assertEquals("doctors/search", viewName);
        verify(model).addAttribute("doctors", doctors);
        verify(model).addAttribute("searchType", "name");
    }

    @Test
    void searchDoctors_shouldReturnSearchViewWithResults() {
        List<DoctorProfile> doctors = new ArrayList<>();
        doctors.add(testDoctor);

        when(doctorProfileService.searchDoctorProfile("name", "Smith")).thenReturn(doctors);

        String viewName = doctorProfileController.searchDoctors("name", "Smith", model);

        assertEquals("doctors/search", viewName);
        verify(model).addAttribute("doctors", doctors);
        verify(model).addAttribute("searchType", "name");
        verify(model).addAttribute("keyword", "Smith");
    }

    @Test
    void showDoctorDetails_shouldReturnDetailView() {
        when(doctorProfileService.findById("eb558e9f-1c39-460e-8860-71af6af63ds2")).thenReturn(testDoctor);

        String viewName = doctorProfileController.showDoctorDetails("eb558e9f-1c39-460e-8860-71af6af63ds2", model);

        assertEquals("doctors/detail", viewName);
        verify(model).addAttribute("doctor", testDoctor);
    }

    @Test
    void showDoctorDetails_shouldRedirectWhenNotFound() {
        when(doctorProfileService.findById("invalid")).thenReturn(null);

        String viewName = doctorProfileController.showDoctorDetails("invalid", model);

        assertEquals("redirect:/doctors", viewName);
    }

    @Test
    void showAddDoctorForm_shouldReturnFormView() {
        String viewName = doctorProfileController.showAddDoctorForm(model);

        assertEquals("doctors/form", viewName);
        verify(model).addAttribute(eq("doctor"), any(DoctorProfile.class));
    }

    @Test
    void saveDoctor_shouldRedirectToDetailPage() {
        when(doctorProfileService.createProfile(testDoctor)).thenReturn(testDoctor);

        String viewName = doctorProfileController.saveDoctor(testDoctor);

        assertEquals("redirect:/doctors/eb558e9f-1c39-460e-8860-71af6af63ds2", viewName);
    }

    @Test
    void showEditDoctorForm_shouldReturnFormView() {
        when(doctorProfileService.findById("eb558e9f-1c39-460e-8860-71af6af63ds2")).thenReturn(testDoctor);

        String viewName = doctorProfileController.showEditDoctorForm("eb558e9f-1c39-460e-8860-71af6af63ds2", model);

        assertEquals("doctors/form", viewName);
        verify(model).addAttribute("doctor", testDoctor);
    }

    @Test
    void deleteDoctor_shouldRedirectToList() {
        when(doctorProfileService.findById("eb558e9f-1c39-460e-8860-71af6af63ds2")).thenReturn(testDoctor);

        String viewName = doctorProfileController.deleteDoctor("eb558e9f-1c39-460e-8860-71af6af63ds2");

        assertEquals("redirect:/doctors", viewName);
        verify(doctorProfileService).deleteProfile(testDoctor);
    }
}