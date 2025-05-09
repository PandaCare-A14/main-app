package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.dto.request.CreateDoctorRequest;
import com.pandacare.mainapp.doctor_profile.dto.request.UpdateDoctorRequest;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.ErrorResponse;
import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        doctor1 = createDoctorProfile("doc1", "Dr. Smith", "dr.smith@example.com", "Cardiology", 4.5);
        doctor2 = createDoctorProfile("doc2", "Dr. Johnson", "dr.johnson@example.com", "Neurology", 4.8);

        createRequest = createDoctorRequest("Dr. New", "dr.new@example.com", "Pediatrics");
        updateRequest = updateDoctorRequest("doc1", "Dr. Smith Updated", "dr.smith.updated@example.com", "Cardiology Updated");
    }

    @Nested
    class GetAllDoctorsTests {
        @Test
        void shouldReturnListOfDoctorsWhenDoctorsExist() {
            // Arrange
            when(doctorProfileService.findAll()).thenReturn(List.of(doctor1, doctor2));

            // Act
            ResponseEntity<DoctorListResponse> response = doctorProfileApiController.getAllDoctors();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            assertEquals(2, response.getBody().getDoctors().size());
            verify(doctorProfileService).findAll();
        }

        @Test
        void shouldReturnEmptyListWhenNoDoctorsExist() {
            // Arrange
            when(doctorProfileService.findAll()).thenReturn(List.of());

            // Act
            ResponseEntity<DoctorListResponse> response = doctorProfileApiController.getAllDoctors();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(0, response.getBody().getTotalItems());
            assertTrue(response.getBody().getDoctors().isEmpty());
            verify(doctorProfileService).findAll();
        }
    }

    @Nested
    class GetDoctorByIdTests {
        @Test
        void shouldReturnDoctorWhenIdExists() {
            // Arrange
            when(doctorProfileService.findById("doc1")).thenReturn(doctor1);

            // Act
            ResponseEntity<DoctorResponse> response = doctorProfileApiController.getDoctorById("doc1");

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals("doc1", response.getBody().getId());
            assertEquals("Dr. Smith", response.getBody().getName());
            verify(doctorProfileService).findById("doc1");
        }

        @Test
        void shouldReturnNotFoundWhenIdDoesNotExist() {
            // Arrange
            when(doctorProfileService.findById("nonexistent")).thenReturn(null);

            // Act
            ResponseEntity<DoctorResponse> response = doctorProfileApiController.getDoctorById("nonexistent");

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
            verify(doctorProfileService).findById("nonexistent");
        }
    }

    @Nested
    class CreateDoctorTests {
        @Test
        void shouldCreateDoctorWhenRequestIsValid() {
            // Arrange
            DoctorProfile newDoctor = createDoctorProfile("new-doc", createRequest.getName(),
                    createRequest.getEmail(), createRequest.getSpeciality(), 0.0); // Default rating

            when(doctorProfileService.createProfile(any(DoctorProfile.class))).thenReturn(newDoctor);

            // Act
            ResponseEntity<?> response = doctorProfileApiController.createDoctor(createRequest);

            // Assert
            assertSuccessfulResponse(response, HttpStatus.CREATED);
            assertDoctorResponseEqualsRequest((DoctorResponse) response.getBody(), createRequest);
            verify(doctorProfileService).createProfile(any(DoctorProfile.class));
        }

        @Test
        void shouldReturnBadRequestWhenServiceThrowsException() {
            // Arrange
            when(doctorProfileService.createProfile(any(DoctorProfile.class)))
                    .thenThrow(new IllegalArgumentException("Invalid data"));

            // Act
            ResponseEntity<?> response = doctorProfileApiController.createDoctor(createRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof ErrorResponse);
            verify(doctorProfileService).createProfile(any(DoctorProfile.class));
        }
    }

    @Nested
    class UpdateDoctorTests {
        @Test
        void shouldUpdateDoctorWhenIdMatchesAndDataIsValid() {
            // Arrange
            DoctorProfile updatedDoctor = createDoctorProfile(
                    updateRequest.getId(),
                    updateRequest.getName(),
                    updateRequest.getEmail(),
                    updateRequest.getSpeciality(),
                    4.5
            );

            when(doctorProfileService.updateProfile(any(DoctorProfile.class))).thenReturn(updatedDoctor);

            // Act
            ResponseEntity<?> response = doctorProfileApiController.updateDoctor("doc1", updateRequest);

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertDoctorResponseEqualsRequest((DoctorResponse) response.getBody(), updateRequest);
            verify(doctorProfileService).updateProfile(any(DoctorProfile.class));
        }

        @Test
        void shouldReturnBadRequestWhenIdsDoNotMatch() {
            // Arrange
            UpdateDoctorRequest mismatchedRequest = updateDoctorRequest(
                    "doc2",
                    "Dr. Mismatch",
                    "mismatch@example.com",
                    "Mismatch"
            );

            // Act
            ResponseEntity<?> response = doctorProfileApiController.updateDoctor("doc1", mismatchedRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            verify(doctorProfileService, never()).updateProfile(any());
        }

        @Test
        void shouldReturnNotFoundWhenDoctorDoesNotExist() {
            // Arrange
            when(doctorProfileService.updateProfile(any(DoctorProfile.class))).thenReturn(null);

            // Act
            ResponseEntity<?> response = doctorProfileApiController.updateDoctor("doc1", updateRequest);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).updateProfile(any(DoctorProfile.class));
        }

        @Test
        void shouldReturnErrorResponseWhenUpdateFails() {
            // Arrange
            when(doctorProfileService.updateProfile(any(DoctorProfile.class)))
                    .thenThrow(new RuntimeException("Update failed"));

            // Act
            ResponseEntity<?> response = doctorProfileApiController.updateDoctor("doc1", updateRequest);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof ErrorResponse);
            verify(doctorProfileService).updateProfile(any(DoctorProfile.class));
        }
    }

    @Nested
    class DeleteDoctorTests {
        @Test
        void shouldDeleteDoctorWhenIdExists() {
            // Arrange
            when(doctorProfileService.findById("doc1")).thenReturn(doctor1);

            // Act
            ResponseEntity<Void> response = doctorProfileApiController.deleteDoctor("doc1");

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(doctorProfileService).findById("doc1");
            verify(doctorProfileService).deleteProfile(doctor1);
        }

        @Test
        void shouldReturnNotFoundWhenIdDoesNotExist() {
            // Arrange
            when(doctorProfileService.findById("nonexistent")).thenReturn(null);

            // Act
            ResponseEntity<Void> response = doctorProfileApiController.deleteDoctor("nonexistent");

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findById("nonexistent");
            verify(doctorProfileService, never()).deleteProfile(any());
        }
    }

    // Helper methods
    private DoctorProfile createDoctorProfile(String id, String name, String email, String speciality, Double rating) {
        DoctorProfile doctor = new DoctorProfile();
        doctor.setId(id);
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setSpeciality(speciality);
        if (rating != null) {
            doctor.setRating(rating);
        } else {
            doctor.setRating(0.0); // Default rating if null is passed
        }
        return doctor;
    }

    private CreateDoctorRequest createDoctorRequest(String name, String email, String speciality) {
        CreateDoctorRequest request = new CreateDoctorRequest();
        request.setName(name);
        request.setEmail(email);
        request.setSpeciality(speciality);
        return request;
    }

    private UpdateDoctorRequest updateDoctorRequest(String id, String name, String email, String speciality) {
        UpdateDoctorRequest request = new UpdateDoctorRequest();
        request.setId(id);
        request.setName(name);
        request.setEmail(email);
        request.setSpeciality(speciality);
        return request;
    }

    private <T> void assertSuccessfulResponse(ResponseEntity<T> response, HttpStatus expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private void assertDoctorResponseEqualsRequest(DoctorResponse response, CreateDoctorRequest request) {
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getSpeciality(), response.getSpeciality());
    }

    private void assertDoctorResponseEqualsRequest(DoctorResponse response, UpdateDoctorRequest request) {
        assertEquals(request.getId(), response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getSpeciality(), response.getSpeciality());
    }
}