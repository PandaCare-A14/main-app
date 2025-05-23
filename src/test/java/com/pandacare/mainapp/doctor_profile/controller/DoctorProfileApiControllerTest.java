package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.ErrorResponse;
import com.pandacare.mainapp.doctor_profile.facade.DoctorFacade;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class DoctorProfileApiControllerTest {

    @Mock
    private DoctorProfileService doctorProfileService;

    @Mock
    private DoctorFacade doctorFacade;

    @InjectMocks
    private DoctorProfileApiController doctorProfileApiController;

    private DoctorProfileResponse doctorProfile1;
    private DoctorProfileResponse doctorProfile2;
    private DoctorProfileListResponse doctorList;

    @BeforeEach
    void setUp() {
        doctorProfileApiController = new DoctorProfileApiController(doctorProfileService, doctorFacade);

        doctorProfile1 = createDoctorProfileResponse("doc1", "Dr. Smith", "Cardiology", 4.5, 10);
        doctorProfile2 = createDoctorProfileResponse("doc2", "Dr. Johnson", "Neurology", 4.8, 15);
        
        List<DoctorProfileResponse> doctorProfiles = new ArrayList<>();
        doctorProfiles.add(doctorProfile1);
        doctorProfiles.add(doctorProfile2);

        DoctorProfileListResponse.DoctorProfileSummary doctorProfileSummary1 = new DoctorProfileListResponse.DoctorProfileSummary("doc1", "Dr. Smith", "Cardiology", 4.8, 10);
        DoctorProfileListResponse.DoctorProfileSummary doctorProfileSummary2 = new DoctorProfileListResponse.DoctorProfileSummary("doc2", "Dr. Johnson", "Neurology", 4.7, 15);

        List<DoctorProfileListResponse.DoctorProfileSummary> doctorProfileSummaries = new ArrayList<>();
        doctorProfileSummaries.add(doctorProfileSummary1);
        doctorProfileSummaries.add(doctorProfileSummary2);

        doctorList = new DoctorProfileListResponse();
        doctorList.setDoctorProfiles(doctorProfileSummaries);
        doctorList.setTotalItems(doctorProfileSummaries.size());
    }

    @Nested
    class GetDoctorWithActionsTests {
        @Test
        void shouldReturnDoctorWithActionsWhenDoctorExists() {
            // Arrange
            String doctorId = "doc1";
            String patientId = "patient123";
            when(doctorFacade.getDoctorProfileWithActions(doctorId, patientId)).thenReturn(doctorProfile1);

            // Act
            ResponseEntity<DoctorProfileResponse> response = doctorProfileApiController.getDoctorWithActions(doctorId, patientId);

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals("doc1", response.getBody().getId());
            assertEquals("Dr. Smith", response.getBody().getName());
            verify(doctorFacade).getDoctorProfileWithActions(doctorId, patientId);
        }

        @Test
        void shouldReturnNotFoundWhenDoctorDoesNotExist() {
            // Arrange
            String doctorId = "nonexistent";
            String patientId = "patient123";
            when(doctorFacade.getDoctorProfileWithActions(doctorId, patientId)).thenReturn(null);

            // Act
            ResponseEntity<DoctorProfileResponse> response = doctorProfileApiController.getDoctorWithActions(doctorId, patientId);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorFacade).getDoctorProfileWithActions(doctorId, patientId);
        }
        
        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() {
            // Arrange
            String doctorId = "doc1";
            String patientId = "patient123";
            when(doctorFacade.getDoctorProfileWithActions(doctorId, patientId))
                .thenThrow(new RuntimeException("Something went wrong"));

            // Act
            ResponseEntity<DoctorProfileResponse> response = doctorProfileApiController.getDoctorWithActions(doctorId, patientId);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorFacade).getDoctorProfileWithActions(doctorId, patientId);
        }
    }

    @Nested
    class GetAllDoctorsTests {
        @Test
        void shouldReturnListOfDoctorsWhenDoctorsExist() {
            // Arrange
            when(doctorProfileService.findAll()).thenReturn(doctorList);

            // Act
            ResponseEntity<DoctorProfileListResponse> response = doctorProfileApiController.getAllDoctorProfiles();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            assertEquals(2, response.getBody().getDoctorProfiles().size());
            verify(doctorProfileService).findAll();
        }

        @Test
        void shouldReturnNotFoundWhenNoDoctorsExist() {
            // Arrange
            DoctorProfileListResponse emptyList = new DoctorProfileListResponse();
            emptyList.setDoctorProfiles(Collections.emptyList());
            emptyList.setTotalItems(0);
            
            when(doctorProfileService.findAll()).thenReturn(null);

            // Act
            ResponseEntity<DoctorProfileListResponse> response = doctorProfileApiController.getAllDoctorProfiles();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findAll();
        }
        
        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() {
            // Arrange
            when(doctorProfileService.findAll()).thenThrow(new RuntimeException("Database error"));

            // Act
            ResponseEntity<DoctorProfileListResponse> response = doctorProfileApiController.getAllDoctorProfiles();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findAll();
        }
    }

    @Nested
    class GetDoctorByIdTests {
        @Test
        void shouldReturnDoctorWhenIdExists() {
            // Arrange
            when(doctorProfileService.findById("doc1")).thenReturn(doctorProfile1);

            // Act
            ResponseEntity<DoctorProfileResponse> response = doctorProfileApiController.getDoctorProfile("doc1");

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
            ResponseEntity<DoctorProfileResponse> response = doctorProfileApiController.getDoctorProfile("nonexistent");

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findById("nonexistent");
        }
        
        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() {
            // Arrange
            when(doctorProfileService.findById(anyString())).thenThrow(new RuntimeException("Database error"));

            // Act
            ResponseEntity<DoctorProfileResponse> response = doctorProfileApiController.getDoctorProfile("doc1");

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findById("doc1");
        }
    }

    @Nested
    class SearchDoctorsByNameTests {
        @Test
        void shouldReturnMatchingDoctors() {
            // Arrange
            when(doctorProfileService.findByName("Smith")).thenReturn(doctorList);

            // Act
            ResponseEntity<DoctorProfileListResponse> response = doctorProfileApiController.searchDoctorsByName("Smith");

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            verify(doctorProfileService).findByName("Smith");
        }
        
        @Test
        void shouldReturnNotFoundWhenNoMatchingDoctors() {
            // Arrange
            when(doctorProfileService.findByName(anyString())).thenReturn(null);

            // Act
            ResponseEntity<DoctorProfileListResponse> response = doctorProfileApiController.searchDoctorsByName("NonExistent");

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findByName("NonExistent");
        }
        
        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() {
            // Arrange
            when(doctorProfileService.findByName(anyString())).thenThrow(new RuntimeException("Search error"));

            // Act
            ResponseEntity<DoctorProfileListResponse> response = doctorProfileApiController.searchDoctorsByName("Smith");

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findByName("Smith");
        }
    }

    @Nested
    class SearchDoctorsBySpecialityTests {
        @Test
        void shouldReturnMatchingDoctors() {
            // Arrange
            when(doctorProfileService.findBySpeciality("Cardiology")).thenReturn(doctorList);

            // Act
            ResponseEntity<DoctorProfileListResponse> response = doctorProfileApiController.searchDoctorsBySpeciality("Cardiology");

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            verify(doctorProfileService).findBySpeciality("Cardiology");
        }
        
        @Test
        void shouldReturnNotFoundWhenNoMatchingDoctors() {
            // Arrange
            when(doctorProfileService.findBySpeciality(anyString())).thenReturn(null);

            // Act
            ResponseEntity<DoctorProfileListResponse> response = doctorProfileApiController.searchDoctorsBySpeciality("NonExistent");

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findBySpeciality("NonExistent");
        }
        
        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() {
            // Arrange
            when(doctorProfileService.findBySpeciality(anyString())).thenThrow(new RuntimeException("Search error"));

            // Act
            ResponseEntity<DoctorProfileListResponse> response = doctorProfileApiController.searchDoctorsBySpeciality("Cardiology");

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findBySpeciality("Cardiology");
        }
    }

    @Nested
    class SearchDoctorsByScheduleTests {
        @Test
        void shouldReturnMatchingDoctors() {
            // Arrange
            when(doctorProfileService.findByWorkSchedule("Monday 09:00-12:00")).thenReturn(doctorList);

            // Act
            ResponseEntity<?> response = doctorProfileApiController.searchDoctorsBySchedule("Monday", "09:00", "12:00");

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, ((DoctorProfileListResponse)response.getBody()).getTotalItems());
            verify(doctorProfileService).findByWorkSchedule("Monday 09:00-12:00");
        }
        
        @Test
        void shouldReturnNotFoundWhenNoMatchingDoctors() {
            // Arrange
            when(doctorProfileService.findByWorkSchedule(anyString())).thenReturn(null);

            // Act
            ResponseEntity<?> response = doctorProfileApiController.searchDoctorsBySchedule("Tuesday", "14:00", "16:00");

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findByWorkSchedule("Tuesday 14:00-16:00");
        }
        
        @Test
        void shouldReturnBadRequestWhenInvalidScheduleFormat() {
            // Arrange
            when(doctorProfileService.findByWorkSchedule(anyString())).thenThrow(
                new IllegalArgumentException("Invalid time format"));

            // Act
            ResponseEntity<?> response = doctorProfileApiController.searchDoctorsBySchedule("Monday", "9:00", "12:00");

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof ErrorResponse);
            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertTrue(errorResponse.message().contains("Invalid schedule format"));
            verify(doctorProfileService).findByWorkSchedule("Monday 9:00-12:00");
        }
        
        @Test
        void shouldReturnInternalServerErrorWhenUnexpectedExceptionThrown() {
            // Arrange
            when(doctorProfileService.findByWorkSchedule(anyString())).thenThrow(new RuntimeException("Database error"));

            // Act
            ResponseEntity<?> response = doctorProfileApiController.searchDoctorsBySchedule("Monday", "09:00", "12:00");

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findByWorkSchedule("Monday 09:00-12:00");
        }
    }

    // Helper methods
    private DoctorProfileResponse createDoctorProfileResponse(String id, String name, String speciality, 
                                                     double averageRating, int totalRatings) {
        DoctorProfileResponse response = new DoctorProfileResponse();
        response.setId(id);
        response.setName(name);
        response.setSpeciality(speciality);
        response.setAverageRating(averageRating);
        response.setTotalRatings(totalRatings);
        return response;
    }

    private <T> void assertSuccessfulResponse(ResponseEntity<T> response, HttpStatus expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
