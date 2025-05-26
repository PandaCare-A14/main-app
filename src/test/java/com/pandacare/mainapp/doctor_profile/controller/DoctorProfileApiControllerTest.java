package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.ErrorResponse;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class DoctorProfileApiControllerTest {

    @Mock
    private DoctorProfileService doctorProfileService;

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
    class SearchDoctorsTests {
        @Test
        void shouldReturnMatchingDoctors() {
            // Arrange
            String name = "Smith";
            String speciality = "Cardio";
            String day = "Monday";
            String startTime = "09:00";
            String endTime = "12:00";

            when(doctorProfileService.searchByCriteria(name, speciality, day, startTime, endTime))
                    .thenReturn(CompletableFuture.completedFuture(doctorList));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctors(name, speciality, day, startTime, endTime);

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertSuccessfulResponse(response, HttpStatus.OK);
            assertEquals(2, response.getBody().getTotalItems());
            verify(doctorProfileService).searchByCriteria(name, speciality, day, startTime, endTime);
        }

        @Test
        void shouldReturnNotFoundWhenNoMatchingDoctors() {
            // Arrange
            String name = "NonExistent";
            when(doctorProfileService.searchByCriteria(name, null, null, null, null))
                    .thenReturn(CompletableFuture.completedFuture(null));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctors(name, null, null, null, null);

            ResponseEntity<DoctorProfileListResponse> response =
                    (ResponseEntity<DoctorProfileListResponse>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(doctorProfileService).searchByCriteria(name, null, null, null, null);
        }

        @Test
        void shouldReturnBadRequestWhenInvalidParameters() {
            // Arrange
            String day = "Monday";
            String startTime = "9:00"; // Invalid format
            String endTime = "12:00";

            when(doctorProfileService.searchByCriteria(null, null, day, startTime, endTime))
                    .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Invalid time format")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctors(null, null, day, startTime, endTime);

            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof ErrorResponse);
            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertTrue(errorResponse.message().contains("Invalid time format"));
            verify(doctorProfileService).searchByCriteria(null, null, day, startTime, endTime);
        }

        @Test
        void shouldReturnInternalServerErrorWhenUnexpectedException() {
            // Arrange
            String name = "Smith";
            when(doctorProfileService.searchByCriteria(name, null, null, null, null))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

            // Act
            DeferredResult<ResponseEntity<?>> deferredResult =
                    doctorProfileApiController.searchDoctors(name, null, null, null, null);

            ResponseEntity<?> response = (ResponseEntity<?>) deferredResult.getResult();

            // Assert
            assert response != null;
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            verify(doctorProfileService).searchByCriteria(name, null, null, null, null);
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