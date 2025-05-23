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
import java.util.UUID;
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
    private UUID testUuid1;
    private UUID testUuid2;

    @BeforeEach
    void setUp() {
        testUuid1 = UUID.randomUUID();
        testUuid2 = UUID.randomUUID();

        doctorProfile1 = createDoctorProfileResponse(testUuid1, "Dr. Smith", "Cardiology", 4.5, 10);
        doctorProfile2 = createDoctorProfileResponse(testUuid2, "Dr. Johnson", "Neurology", 4.8, 15);

        List<DoctorProfileResponse> doctorProfiles = new ArrayList<>();
        doctorProfiles.add(doctorProfile1);
        doctorProfiles.add(doctorProfile2);

        DoctorProfileListResponse.DoctorProfileSummary doctorProfileSummary1 = new DoctorProfileListResponse.DoctorProfileSummary(testUuid1, "Dr. Smith", "Cardiology", 4.8, 10);
        DoctorProfileListResponse.DoctorProfileSummary doctorProfileSummary2 = new DoctorProfileListResponse.DoctorProfileSummary(testUuid2, "Dr. Johnson", "Neurology", 4.7, 15);

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
            UUID caregiverId = UUID.randomUUID();
            UUID patientId = UUID.randomUUID();
            when(doctorFacade.getDoctorProfileWithActions(eq(caregiverId), eq(patientId)))
                    .thenReturn(CompletableFuture.completedFuture(doctorProfile1));

            // Act
            DeferredResult<ResponseEntity<DoctorProfileResponse>> deferredResult =
                    doctorProfileApiController.getDoctorWithActions(caregiverId, patientId);

            // Wait for async completion
            ResponseEntity<DoctorProfileResponse> response = (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(testUuid1, response.getBody().getCaregiverId());
            assertEquals("Dr. Smith", response.getBody().getName());
            verify(doctorFacade).getDoctorProfileWithActions(caregiverId, patientId);
        }

        @Test
        void shouldReturnNotFoundWhenDoctorDoesNotExist() {
            // Arrange
            UUID caregiverId = UUID.randomUUID();
            UUID patientId = UUID.randomUUID();
            when(doctorFacade.getDoctorProfileWithActions(eq(caregiverId), eq(patientId)))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<DoctorProfileResponse>> deferredResult =
                    doctorProfileApiController.getDoctorWithActions(caregiverId, patientId);

            // Wait for async completion
            ResponseEntity<DoctorProfileResponse> response = (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorFacade).getDoctorProfileWithActions(caregiverId, patientId);
        }

        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() {
            // Arrange
            UUID caregiverId = UUID.randomUUID();
            UUID patientId = UUID.randomUUID();
            when(doctorFacade.getDoctorProfileWithActions(eq(caregiverId), eq(patientId)))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Something went wrong")));

            // Act
            DeferredResult<ResponseEntity<DoctorProfileResponse>> deferredResult =
                    doctorProfileApiController.getDoctorWithActions(caregiverId, patientId);

            // Wait for async completion
            ResponseEntity<DoctorProfileResponse> response = (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorFacade).getDoctorProfileWithActions(caregiverId, patientId);
        }

        @Test
        void shouldReturnTimeoutWhenOperationTakesTooLong() {
            // Arrange
            UUID caregiverId = UUID.randomUUID();
            UUID patientId = UUID.randomUUID();
            when(doctorFacade.getDoctorProfileWithActions(eq(caregiverId), eq(patientId)))
                    .thenReturn(new CompletableFuture<>()); // Never completes

            // Act
            DeferredResult<ResponseEntity<DoctorProfileResponse>> deferredResult =
                    doctorProfileApiController.getDoctorWithActions(caregiverId, patientId);

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
        void shouldReturnListOfDoctorsWhenDoctorsExist() {
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
        void shouldReturnNotFoundWhenNoDoctorsExist() {
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
        void shouldReturnInternalServerErrorWhenExceptionThrown() {
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
        void shouldReturnDoctorWhenIdExists() {
            // Arrange
            UUID doctorId = testUuid1;
            when(doctorProfileService.findById(doctorId))
                    .thenReturn(CompletableFuture.completedFuture(doctorProfile1));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.getDoctorProfile(doctorId);

            ResponseEntity<DoctorProfileResponse> response =
                    (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(testUuid1, response.getBody().getCaregiverId());
            assertEquals("Dr. Smith", response.getBody().getName());
            verify(doctorProfileService).findById(doctorId);
        }

        @Test
        void shouldReturnNotFoundWhenIdDoesNotExist() {
            // Arrange
            UUID doctorId = UUID.randomUUID();
            when(doctorProfileService.findById(doctorId))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.getDoctorProfile(doctorId);

            ResponseEntity<DoctorProfileResponse> response =
                    (ResponseEntity<DoctorProfileResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findById(doctorId);
        }

        @Test
        void shouldReturnBadRequestWhenInvalidIdFormat() {
            // Arrange
            UUID doctorId = null;
            when(doctorProfileService.findById(doctorId))
                    .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Invalid ID format")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.getDoctorProfile(doctorId);

            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof ErrorResponse);
            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertTrue(errorResponse.message().contains("Invalid ID format"));
            verify(doctorProfileService).findById(doctorId);
        }

        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() {
            // Arrange
            UUID doctorId = testUuid2;
            when(doctorProfileService.findById(doctorId))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.getDoctorProfile(doctorId);

            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findById(doctorId);
        }
    }

    @Nested
    class SearchDoctorsByNameTests {
        @Test
        void shouldReturnMatchingDoctors() {
            // Arrange
            String name = "Smith";
            when(doctorProfileService.findByName(name))
                    .thenReturn(CompletableFuture.completedFuture(doctorList));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsByName(name);

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            verify(doctorProfileService).findByName(name);
        }

        @Test
        void shouldReturnNotFoundWhenNoMatchingDoctors() {
            // Arrange
            String name = "NonExistent";
            when(doctorProfileService.findByName(name))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsByName(name);

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findByName(name);
        }

        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() {
            // Arrange
            String name = "Smith";
            when(doctorProfileService.findByName(name))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Search error")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsByName(name);

            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findByName(name);
        }
    }

    @Nested
    class SearchDoctorsBySpecialityTests {
        @Test
        void shouldReturnMatchingDoctors() {
            // Arrange
            String speciality = "Cardiology";
            when(doctorProfileService.findBySpeciality(speciality))
                    .thenReturn(CompletableFuture.completedFuture(doctorList));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySpeciality(speciality);

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            verify(doctorProfileService).findBySpeciality(speciality);
        }

        @Test
        void shouldReturnNotFoundWhenNoMatchingDoctors() {
            // Arrange
            String speciality = "NonExistent";
            when(doctorProfileService.findBySpeciality(speciality))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySpeciality(speciality);

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findBySpeciality(speciality);
        }

        @Test
        void shouldReturnInternalServerErrorWhenExceptionThrown() {
            // Arrange
            String speciality = "Cardiology";
            when(doctorProfileService.findBySpeciality(speciality))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Search error")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySpeciality(speciality);

            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findBySpeciality(speciality);
        }
    }

    @Nested
    class SearchDoctorsByScheduleTests {
        @Test
        void shouldReturnMatchingDoctors() {
            // Arrange
            String day = "Monday";
            String start = "09:00";
            String end = "12:00";
            String schedule = day + " " + start + "-" + end;

            when(doctorProfileService.findByWorkSchedule(schedule))
                    .thenReturn(CompletableFuture.completedFuture(doctorList));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySchedule(day, start, end);

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            verify(doctorProfileService).findByWorkSchedule(schedule);
        }

        @Test
        void shouldReturnNotFoundWhenNoMatchingDoctors() {
            // Arrange
            String day = "Tuesday";
            String start = "14:00";
            String end = "16:00";
            String schedule = day + " " + start + "-" + end;

            when(doctorProfileService.findByWorkSchedule(schedule))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySchedule(day, start, end);

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).findByWorkSchedule(schedule);
        }

        @Test
        void shouldReturnBadRequestWhenInvalidScheduleFormat() {
            // Arrange
            String day = "Monday";
            String start = "9:00";
            String end = "12:00";
            String schedule = day + " " + start + "-" + end;

            when(doctorProfileService.findByWorkSchedule(schedule))
                    .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Invalid time format")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySchedule(day, start, end);

            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof ErrorResponse);
            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertTrue(errorResponse.message().contains("Invalid time format"));
            verify(doctorProfileService).findByWorkSchedule(schedule);
        }

        @Test
        void shouldReturnInternalServerErrorWhenUnexpectedExceptionThrown() {
            // Arrange
            String day = "Monday";
            String start = "09:00";
            String end = "12:00";
            String schedule = day + " " + start + "-" + end;

            when(doctorProfileService.findByWorkSchedule(schedule))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctorsBySchedule(day, start, end);

            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).findByWorkSchedule(schedule);
        }
    }

    // Helper methods
    private DoctorProfileResponse createDoctorProfileResponse(UUID caregiverId, String name, String speciality,
                                                              double averageRating, int totalRatings) {
        DoctorProfileResponse response = new DoctorProfileResponse();
        response.setCaregiverId(caregiverId);
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