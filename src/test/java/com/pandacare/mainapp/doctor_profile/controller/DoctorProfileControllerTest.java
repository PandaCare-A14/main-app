package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorProfileControllerRefactoredTest {

    @Mock
    private DoctorProfileService doctorProfileService;

    @Mock
    private Model model;

    @InjectMocks
    private DoctorProfileController doctorProfileController;

    private DoctorProfile testDoctor;
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
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

        redirectAttributes = new RedirectAttributesModelMap();
    }

    @Nested
    @DisplayName("View Operations")
    class ViewOperationsTests {
        @Test
        @DisplayName("Show all doctors should return search view with doctors list")
        void showAllDoctors_shouldReturnSearchViewWithDoctors() {
            List<DoctorProfile> doctors = List.of(testDoctor);
            when(doctorProfileService.findAll()).thenReturn(doctors);

            String viewName = doctorProfileController.showAllDoctors(model);

            assertEquals("doctors/search", viewName);
            verify(model).addAttribute("doctors", doctors);
            verify(model).addAttribute("searchType", "name");
        }

        @Test
        @DisplayName("Search doctors with valid criteria should return filtered results")
        void searchDoctors_withValidCriteria_shouldReturnResults() {
            List<DoctorProfile> doctors = List.of(testDoctor);
            when(doctorProfileService.searchDoctorProfile("name", "Smith")).thenReturn(doctors);

            String viewName = doctorProfileController.searchDoctors("name", "Smith", model);

            assertEquals("doctors/search", viewName);
            verify(model).addAttribute("doctors", doctors);
            verify(model).addAttribute("searchType", "name");
            verify(model).addAttribute("keyword", "Smith");
        }

        @Test
        @DisplayName("Search doctors with invalid criteria should show error")
        void searchDoctors_withInvalidCriteria_shouldShowError() {
            when(doctorProfileService.searchDoctorProfile("invalid", "Smith"))
                    .thenThrow(new IllegalArgumentException());

            List<DoctorProfile> allDoctors = List.of(testDoctor);
            when(doctorProfileService.findAll()).thenReturn(allDoctors);

            String viewName = doctorProfileController.searchDoctors("invalid", "Smith", model);

            assertEquals("doctors/search", viewName);
            verify(model).addAttribute("error", "Invalid search criteria");
            verify(model).addAttribute("doctors", allDoctors);
        }

        @Test
        @DisplayName("Show doctor details for existing doctor should return detail view")
        void showDoctorDetails_forExistingDoctor_shouldReturnDetailView() {
            when(doctorProfileService.findById("eb558e9f-1c39-460e-8860-71af6af63ds2")).thenReturn(testDoctor);

            String viewName = doctorProfileController.showDoctorDetails("eb558e9f-1c39-460e-8860-71af6af63ds2", model);

            assertEquals("doctors/detail", viewName);
            verify(model).addAttribute("doctor", testDoctor);
        }

        @Test
        @DisplayName("Show doctor details for non-existent doctor should redirect")
        void showDoctorDetails_forNonExistentDoctor_shouldRedirect() {
            when(doctorProfileService.findById("invalid")).thenReturn(null);

            String viewName = doctorProfileController.showDoctorDetails("invalid", model);

            assertEquals("redirect:/doctors", viewName);
        }
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperationsTests {
        @Test
        @DisplayName("Show add doctor form should return form with new doctor")
        void showAddDoctorForm_shouldReturnFormWithNewDoctor() {
            String viewName = doctorProfileController.showAddDoctorForm(model);

            assertEquals("doctors/form", viewName);
            verify(model).addAttribute(eq("doctor"), any(DoctorProfile.class));
        }

        @Test
        @DisplayName("Save new doctor should redirect to detail page")
        void saveDoctor_withNewDoctor_shouldRedirectToDetail() {
            when(doctorProfileService.createProfile(testDoctor)).thenReturn(testDoctor);

            String viewName = doctorProfileController.saveDoctor(testDoctor);

            assertEquals("redirect:/doctors/eb558e9f-1c39-460e-8860-71af6af63ds2", viewName);
        }

        @Test
        @DisplayName("Show edit form for existing doctor should return form")
        void showEditDoctorForm_forExistingDoctor_shouldReturnForm() {
            when(doctorProfileService.findById("eb558e9f-1c39-460e-8860-71af6af63ds2")).thenReturn(testDoctor);

            String viewName = doctorProfileController.showEditDoctorForm("eb558e9f-1c39-460e-8860-71af6af63ds2", model);

            assertEquals("doctors/form", viewName);
            verify(model).addAttribute("doctor", testDoctor);
        }

        @Test
        @DisplayName("Update existing doctor should redirect to detail page")
        void updateDoctor_forExistingDoctor_shouldRedirectToDetail() {
            when(doctorProfileService.updateProfile(testDoctor)).thenReturn(testDoctor);

            String viewName = doctorProfileController.updateDoctor("eb558e9f-1c39-460e-8860-71af6af63ds2", testDoctor);

            assertEquals("redirect:/doctors/eb558e9f-1c39-460e-8860-71af6af63ds2", viewName);
        }

        @Test
        @DisplayName("Delete existing doctor should redirect to list")
        void deleteDoctor_forExistingDoctor_shouldRedirectToList() {
            when(doctorProfileService.findById("eb558e9f-1c39-460e-8860-71af6af63ds2")).thenReturn(testDoctor);

            String viewName = doctorProfileController.deleteDoctor("eb558e9f-1c39-460e-8860-71af6af63ds2");

            assertEquals("redirect:/doctors", viewName);
            verify(doctorProfileService).deleteProfile(testDoctor);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {
        @Test
        @DisplayName("Save with duplicate ID should redirect with error")
        void saveDoctor_withDuplicateId_shouldRedirectWithError() {
            when(doctorProfileService.createProfile(testDoctor)).thenReturn(null);

            String viewName = doctorProfileController.saveDoctor(testDoctor);

            assertEquals("redirect:/doctors/new?error", viewName);
        }

        @Test
        @DisplayName("Update non-existent doctor should redirect with error")
        void updateDoctor_forNonExistentDoctor_shouldRedirectWithError() {
            when(doctorProfileService.updateProfile(testDoctor)).thenReturn(null);

            String viewName = doctorProfileController.updateDoctor("eb558e9f-1c39-460e-8860-71af6af63ds2", testDoctor);

            assertEquals("redirect:/doctors/eb558e9f-1c39-460e-8860-71af6af63ds2/edit?error", viewName);
        }
    }
}