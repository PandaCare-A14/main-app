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
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        void shouldReturnDoctorWithActionsWhenDoctorExists() throws Exception {
            // Arrange
            String doctorId = "doc1";
            String patientId = "patient123";
            when(doctorFacade.getDoctorProfileWithActions(doctorId, patientId))
                    .thenReturn(CompletableFuture.completedFuture(doctorProfile1));

            // Act
            DeferredResult<ResponseEntity<DoctorProfileResponse>> deferredResult =
                    doctorProfileApiController.getDoctorWithActions(doctorId, patientId);

            // Wait for async completion
            ResponseEntity<DoctorProfileResponse> response = (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals("doc1", response.getBody().getId());
            assertEquals("Dr. Smith", response.getBody().getName());
            verify(doctorFacade).getDoctorProfileWithActions(doctorId, patientId);
        }

        @Test
        void shouldReturnNotFoundWhenDoctorDoesNotExist() throws Exception {
            // Arrange
            String doctorId = "nonexistent";
            String patientId = "patient123";
            when(doctorFacade.getDoctorProfileWithActions(doctorId, patientId))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<DoctorProfileResponse>> deferredResult =
                    doctorProfileApiController.getDoctorWithActions(doctorId, patientId);

            // Wait for async completion
            ResponseEntity<DoctorProfileResponse> response = (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorFacade).getDoctorProfileWithActions(doctorId, patientId);
        }

        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() throws Exception {
            // Arrange
            String doctorId = "doc1";
            String patientId = "patient123";
            when(doctorFacade.getDoctorProfileWithActions(doctorId, patientId))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Something went wrong")));

            // Act
            DeferredResult<ResponseEntity<DoctorProfileResponse>> deferredResult =
                    doctorProfileApiController.getDoctorWithActions(doctorId, patientId);

            // Wait for async completion
            ResponseEntity<DoctorProfileResponse> response = (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorFacade).getDoctorProfileWithActions(doctorId, patientId);
        }

        @Test
        void shouldReturnTimeoutWhenOperationTakesTooLong() throws Exception {
            // Arrange
            String doctorId = "doc1";
            String patientId = "patient123";
            when(doctorFacade.getDoctorProfileWithActions(doctorId, patientId))
                    .thenReturn(new CompletableFuture<>()); // Never completes

            // Act
            DeferredResult<ResponseEntity<DoctorProfileResponse>> deferredResult =
                    doctorProfileApiController.getDoctorWithActions(doctorId, patientId);

            // Simulate timeout
            deferredResult.setErrorResult(
                    ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                            .body(new ErrorResponse("Request timeout occurred")));

            // Assert
            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();
            assertEquals(HttpStatus.REQUEST_TIMEOUT, response.getStatusCode());
            assertTrue(response.getBody() instanceof ErrorResponse);
            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertEquals("Request timeout occurred", errorResponse.message());
        }
    }

    @Nested
    class GetAllDoctorsTests {
        @Test
        void shouldReturnListOfDoctorsWhenDoctorsExist() throws Exception {
            // Arrange
            when(doctorProfileService.findAll())
                    .thenReturn(CompletableFuture.completedFuture(doctorList));

            // Act
            DeferredResult<ResponseEntity<DoctorProfileListResponse>> deferredResult =
                    doctorProfileApiController.getAllDoctorProfiles();

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            assertEquals(2, response.getBody().getDoctorProfiles().size());
            verify(doctorProfileService).findAll();
        }

        @Test
        void shouldReturnNotFoundWhenNoDoctorsExist() throws Exception {
            // Arrange
            when(doctorProfileService.findAll())
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<DoctorProfileListResponse>> deferredResult =
                    doctorProfileApiController.getAllDoctorProfiles();

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findAll();
        }

        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() throws Exception {
            // Arrange
            when(doctorProfileService.findAll())
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

            // Act
            DeferredResult<ResponseEntity<DoctorProfileListResponse>> deferredResult =
                    doctorProfileApiController.getAllDoctorProfiles();

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findAll();
        }
    }

    @Nested
    class GetDoctorByIdTests {
        @Test
        void shouldReturnDoctorWhenIdExists() throws Exception {
            // Arrange
            when(doctorProfileService.findById("doc1"))
                    .thenReturn(CompletableFuture.completedFuture(doctorProfile1));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.getDoctorProfile("doc1");

            ResponseEntity<DoctorProfileResponse> response =
                    (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals("doc1", response.getBody().getId());
            assertEquals("Dr. Smith", response.getBody().getName());
            verify(doctorProfileService).findById("doc1");
        }

        @Test
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            // Arrange
            when(doctorProfileService.findById("nonexistent"))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.getDoctorProfile("nonexistent");

            ResponseEntity<DoctorProfileResponse> response =
                    (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findById("nonexistent");
        }

        @Test
        void shouldReturnBadRequestWhenInvalidIdFormat() throws Exception {
            // Arrange
            when(doctorProfileService.findById("invalid"))
                    .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Invalid ID format")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.getDoctorProfile("invalid");

            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof ErrorResponse);
            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertTrue(errorResponse.message().contains("Invalid ID format"));
            verify(doctorProfileService).findById("invalid");
        }

        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() throws Exception {
            // Arrange
            when(doctorProfileService.findById(anyString()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.getDoctorProfile("doc1");

            ResponseEntity<DoctorProfileResponse> response =
                    (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findById("doc1");
        }
    }

    @Nested
    class SearchDoctorsByNameTests {
        @Test
        void shouldReturnMatchingDoctors() throws Exception {
            // Arrange
            when(doctorProfileService.findByName("Smith"))
                    .thenReturn(CompletableFuture.completedFuture(doctorList));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsByName("Smith");

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            verify(doctorProfileService).findByName("Smith");
        }

        @Test
        void shouldReturnNotFoundWhenNoMatchingDoctors() throws Exception {
            // Arrange
            when(doctorProfileService.findByName(anyString()))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsByName("NonExistent");

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findByName("NonExistent");
        }

        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() throws Exception {
            // Arrange
            when(doctorProfileService.findByName(anyString()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Search error")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsByName("Smith");

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findByName("Smith");
        }
    }

    @Nested
    class SearchDoctorsBySpecialityTests {
        @Test
        void shouldReturnMatchingDoctors() throws Exception {
            // Arrange
            when(doctorProfileService.findBySpeciality("Cardiology"))
                    .thenReturn(CompletableFuture.completedFuture(doctorList));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySpeciality("Cardiology");

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            verify(doctorProfileService).findBySpeciality("Cardiology");
        }

        @Test
        void shouldReturnNotFoundWhenNoMatchingDoctors() throws Exception {
            // Arrange
            when(doctorProfileService.findBySpeciality(anyString()))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySpeciality("NonExistent");

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findBySpeciality("NonExistent");
        }

        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() throws Exception {
            // Arrange
            when(doctorProfileService.findBySpeciality(anyString()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Search error")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySpeciality("Cardiology");

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findBySpeciality("Cardiology");
        }
    }

    @Nested
    class SearchDoctorsByScheduleTests {
        @Test
        void shouldReturnMatchingDoctors() throws Exception {
            // Arrange
            when(doctorProfileService.findByWorkSchedule("Monday 09:00-12:00"))
                    .thenReturn(CompletableFuture.completedFuture(doctorList));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySchedule("Monday", "09:00", "12:00");

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            verify(doctorProfileService).findByWorkSchedule("Monday 09:00-12:00");
        }

        @Test
        void shouldReturnNotFoundWhenNoMatchingDoctors() throws Exception {
            // Arrange
            when(doctorProfileService.findByWorkSchedule(anyString()))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySchedule("Tuesday", "14:00", "16:00");

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findByWorkSchedule("Tuesday 14:00-16:00");
        }

        @Test
        void shouldReturnBadRequestWhenInvalidScheduleFormat() throws Exception {
            // Arrange
            when(doctorProfileService.findByWorkSchedule(anyString()))
                    .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Invalid time format")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySchedule("Monday", "9:00", "12:00");

            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof ErrorResponse);
            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertTrue(errorResponse.message().contains("Invalid time format"));
            verify(doctorProfileService).findByWorkSchedule("Monday 9:00-12:00");
        }

        @Test
        void shouldReturnInternalServerErrorWhenUnexpectedExceptionThrown() throws Exception {
            // Arrange
            when(doctorProfileService.findByWorkSchedule(anyString()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySchedule("Monday", "09:00", "12:00");

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

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