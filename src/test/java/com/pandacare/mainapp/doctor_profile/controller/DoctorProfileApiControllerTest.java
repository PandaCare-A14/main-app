package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.dto.request.CreateDoctorRequest;
import com.pandacare.mainapp.doctor_profile.dto.request.UpdateDoctorRequest;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.ErrorResponse;
import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
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
    private CreateDoctorRequest createRequest;
    private UpdateDoctorRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doctor1 = new DoctorProfile();
        doctor1.setId("doc1");
        doctor1.setName("Dr. Smith");
        doctor1.setEmail("dr.smith@example.com");
        doctor1.setSpeciality("Cardiology");
        doctor1.setRating(4.5);

        doctor2 = new DoctorProfile();
        doctor2.setId("doc2");
        doctor2.setName("Dr. Johnson");
        doctor2.setEmail("dr.johnson@example.com");
        doctor2.setSpeciality("Neurology");
        doctor2.setRating(4.8);

        createRequest = new CreateDoctorRequest();
        createRequest.setName("Dr. New");
        createRequest.setEmail("dr.new@example.com");
        createRequest.setSpeciality("Pediatrics");

        updateRequest = new UpdateDoctorRequest();
        updateRequest.setId("doc1");
        updateRequest.setName("Dr. Smith Updated");
        updateRequest.setEmail("dr.smith.updated@example.com");
        updateRequest.setSpeciality("Cardiology Updated");
    }

    @Test
    void getAllDoctors_ShouldReturnListOfDoctors() {
        // Arrange
        List<DoctorProfile> doctors = Arrays.asList(doctor1, doctor2);
        when(doctorProfileService.findAll()).thenReturn(doctors);

        // Act
        ResponseEntity<DoctorListResponse> response = doctorProfileApiController.getAllDoctors();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalItems());
        assertEquals(2, response.getBody().getDoctors().size());
        verify(doctorProfileService, times(1)).findAll();
    }

    @Test
    void getDoctorById_WithExistingId_ShouldReturnDoctor() {
        // Arrange
        when(doctorProfileService.findById("doc1")).thenReturn(doctor1);

        // Act
        ResponseEntity<DoctorResponse> response = doctorProfileApiController.getDoctorById("doc1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("doc1", response.getBody().getId());
        assertEquals("Dr. Smith", response.getBody().getName());
        verify(doctorProfileService, times(1)).findById("doc1");
    }

    @Test
    void getDoctorById_WithNonExistingId_ShouldReturnNotFound() {
        // Arrange
        when(doctorProfileService.findById("nonexistent")).thenReturn(null);

        // Act
        ResponseEntity<DoctorResponse> response = doctorProfileApiController.getDoctorById("nonexistent");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(doctorProfileService, times(1)).findById("nonexistent");
    }

    @Test
    void createDoctor_WithValidData_ShouldReturnCreatedDoctor() {
        // Arrange
        DoctorProfile newDoctor = new DoctorProfile();
        newDoctor.setName(createRequest.getName());
        newDoctor.setEmail(createRequest.getEmail());
        newDoctor.setSpeciality(createRequest.getSpeciality());

        when(doctorProfileService.createProfile(any(DoctorProfile.class))).thenReturn(newDoctor);

        // Act
        ResponseEntity<?> response = doctorProfileApiController.createDoctor(createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof DoctorResponse);
        DoctorResponse responseBody = (DoctorResponse) response.getBody();
        assertEquals("Dr. New", responseBody.getName());
        verify(doctorProfileService, times(1)).createProfile(any(DoctorProfile.class));
    }

    @Test
    void createDoctor_WithInvalidData_ShouldReturnBadRequest() {
        // Arrange
        when(doctorProfileService.createProfile(any(DoctorProfile.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act
        ResponseEntity<?> response = doctorProfileApiController.createDoctor(createRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        verify(doctorProfileService, times(1)).createProfile(any(DoctorProfile.class));
    }

    @Test
    void updateDoctor_WithValidDataAndMatchingId_ShouldReturnUpdatedDoctor() {
        // Arrange
        DoctorProfile updatedDoctor = new DoctorProfile();
        updatedDoctor.setId(updateRequest.getId());
        updatedDoctor.setName(updateRequest.getName());
        updatedDoctor.setEmail(updateRequest.getEmail());
        updatedDoctor.setSpeciality(updateRequest.getSpeciality());

        when(doctorProfileService.updateProfile(any(DoctorProfile.class))).thenReturn(updatedDoctor);

        // Act
        ResponseEntity<?> response = doctorProfileApiController.updateDoctor("doc1", updateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof DoctorResponse);
        DoctorResponse responseBody = (DoctorResponse) response.getBody();
        assertEquals("Dr. Smith Updated", responseBody.getName());
        verify(doctorProfileService, times(1)).updateProfile(any(DoctorProfile.class));
    }

    @Test
    void updateDoctor_WithMismatchedId_ShouldReturnBadRequest() {
        // Arrange
        UpdateDoctorRequest mismatchedRequest = new UpdateDoctorRequest();
        mismatchedRequest.setId("doc2"); // Different from path ID

        // Act
        ResponseEntity<?> response = doctorProfileApiController.updateDoctor("doc1", mismatchedRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(doctorProfileService, never()).updateProfile(any());
    }

    @Test
    void updateDoctor_WithNonExistingId_ShouldReturnNotFound() {
        // Arrange
        when(doctorProfileService.updateProfile(any(DoctorProfile.class))).thenReturn(null);

        // Act
        ResponseEntity<?> response = doctorProfileApiController.updateDoctor("doc1", updateRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(doctorProfileService, times(1)).updateProfile(any(DoctorProfile.class));
    }

    @Test
    void updateDoctor_WithException_ShouldReturnErrorResponse() {
        // Arrange
        when(doctorProfileService.updateProfile(any(DoctorProfile.class)))
                .thenThrow(new RuntimeException("Update failed"));

        // Act
        ResponseEntity<?> response = doctorProfileApiController.updateDoctor("doc1", updateRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        verify(doctorProfileService, times(1)).updateProfile(any(DoctorProfile.class));
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