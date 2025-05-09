package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorProfileApiControllerTest {

    @Mock
    private DoctorProfileService doctorProfileService;

    @InjectMocks
    private DoctorProfileApiController doctorProfileApiController;

    private DoctorProfile doctor1;
    private DoctorProfile doctor2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doctor1 = new DoctorProfile();
        doctor1.setId("doc1");
        doctor1.setName("Dr. Smith");

        doctor2 = new DoctorProfile();
        doctor2.setId("doc2");
        doctor2.setName("Dr. Johnson");
    }

    @Test
    void getAllDoctors_ShouldReturnListOfDoctors() {
        // Arrange
        List<DoctorProfile> doctors = Arrays.asList(doctor1, doctor2);
        when(doctorProfileService.findAll()).thenReturn(doctors);

        // Act
        List<DoctorProfile> result = doctorProfileApiController.getAllDoctors();

        // Assert
        assertEquals(2, result.size());
        verify(doctorProfileService, times(1)).findAll();
    }

    @Test
    void getDoctorById_WithExistingId_ShouldReturnDoctor() {
        // Arrange
        when(doctorProfileService.findById("doc1")).thenReturn(doctor1);

        // Act
        ResponseEntity<DoctorProfile> response = doctorProfileApiController.getDoctorById("doc1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(doctor1, response.getBody());
        verify(doctorProfileService, times(1)).findById("doc1");
    }

    @Test
    void getDoctorById_WithNonExistingId_ShouldReturnNotFound() {
        // Arrange
        when(doctorProfileService.findById("nonexistent")).thenReturn(null);

        // Act
        ResponseEntity<DoctorProfile> response = doctorProfileApiController.getDoctorById("nonexistent");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(doctorProfileService, times(1)).findById("nonexistent");
    }

    @Test
    void createDoctor_WithValidData_ShouldReturnCreatedDoctor() {
        // Arrange
        when(doctorProfileService.createProfile(doctor1)).thenReturn(doctor1);

        // Act
        ResponseEntity<DoctorProfile> response = doctorProfileApiController.createDoctor(doctor1);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(doctor1, response.getBody());
        verify(doctorProfileService, times(1)).createProfile(doctor1);
    }

    @Test
    void createDoctor_WithInvalidData_ShouldReturnBadRequest() {
        // Arrange
        when(doctorProfileService.createProfile(doctor1)).thenReturn(null);

        // Act
        ResponseEntity<DoctorProfile> response = doctorProfileApiController.createDoctor(doctor1);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(doctorProfileService, times(1)).createProfile(doctor1);
    }

    @Test
    void updateDoctor_WithValidDataAndMatchingId_ShouldReturnUpdatedDoctor() {
        // Arrange
        when(doctorProfileService.updateProfile(doctor1)).thenReturn(doctor1);

        // Act
        ResponseEntity<DoctorProfile> response = doctorProfileApiController.updateDoctor("doc1", doctor1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(doctor1, response.getBody());
        verify(doctorProfileService, times(1)).updateProfile(doctor1);
    }

    @Test
    void updateDoctor_WithMismatchedId_ShouldReturnBadRequest() {
        // Arrange
        DoctorProfile doctorWithMismatchedId = new DoctorProfile();
        doctorWithMismatchedId.setId("doc2");

        // Act
        ResponseEntity<DoctorProfile> response = doctorProfileApiController.updateDoctor("doc1", doctorWithMismatchedId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(doctorProfileService, never()).updateProfile(any());
    }

    @Test
    void updateDoctor_WithNonExistingId_ShouldReturnNotFound() {
        // Arrange
        when(doctorProfileService.updateProfile(doctor1)).thenReturn(null);

        // Act
        ResponseEntity<DoctorProfile> response = doctorProfileApiController.updateDoctor("doc1", doctor1);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(doctorProfileService, times(1)).updateProfile(doctor1);
    }

    @Test
    void deleteDoctor_WithExistingId_ShouldReturnNoContent() {
        // Arrange
        when(doctorProfileService.findById("doc1")).thenReturn(doctor1);

        // Act
        ResponseEntity<Void> response = doctorProfileApiController.deleteDoctor("doc1");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(doctorProfileService, times(1)).findById("doc1");
        verify(doctorProfileService, times(1)).deleteProfile(doctor1);
    }

    @Test
    void deleteDoctor_WithNonExistingId_ShouldReturnNotFound() {
        // Arrange
        when(doctorProfileService.findById("nonexistent")).thenReturn(null);

        // Act
        ResponseEntity<Void> response = doctorProfileApiController.deleteDoctor("nonexistent");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(doctorProfileService, times(1)).findById("nonexistent");
        verify(doctorProfileService, never()).deleteProfile(any());
    }
}