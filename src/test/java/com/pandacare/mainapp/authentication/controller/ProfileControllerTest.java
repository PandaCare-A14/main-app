package com.pandacare.mainapp.authentication.controller;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.authentication.model.Pacillian;
import com.pandacare.mainapp.authentication.repository.CaregiverRepository;
import com.pandacare.mainapp.authentication.repository.PacillianRepository;
import com.pandacare.mainapp.authentication.dto.CaregiverProfileDto;
import com.pandacare.mainapp.authentication.dto.PacillianProfileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private CaregiverRepository caregiverRepository;

    @Mock
    private PacillianRepository pacillianRepository;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private ProfileController profileController;

    private UUID testUserId;
    private Caregiver testCaregiver;
    private Pacillian testPacillian;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testCaregiver = new Caregiver();
        testCaregiver.setId(testUserId);
        testCaregiver.setName("Dr. John Doe");
        testCaregiver.setPhoneNumber("081234567890");
        testCaregiver.setWorkAddress("Hospital A");
        testCaregiver.setSpeciality("Cardiology");

        testPacillian = new Pacillian();
        testPacillian.setId(testUserId);
        testPacillian.setName("Jane Smith");
        testPacillian.setPhoneNumber("081987654321");
        testPacillian.setAddress("Jakarta");
        testPacillian.setMedicalHistory("Diabetes");
    }

    @Test
    void testGetProfileCaregiverSuccess() {
        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("caregiver");
        when(caregiverRepository.findById(testUserId)).thenReturn(Optional.of(testCaregiver));

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(CaregiverProfileDto.class, response.getBody());
        CaregiverProfileDto dto = (CaregiverProfileDto) response.getBody();
        assertEquals(testUserId, dto.id());
        assertEquals("Dr. John Doe", dto.name());
        assertEquals("081234567890", dto.phoneNumber());
        assertEquals("Hospital A", dto.workAddress());
        assertEquals("Cardiology", dto.speciality());
    }

    @Test
    void testGetProfilePacillianSuccess() {
        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("pacilian");
        when(pacillianRepository.findById(testUserId)).thenReturn(Optional.of(testPacillian));

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(PacillianProfileDto.class, response.getBody());
        PacillianProfileDto dto = (PacillianProfileDto) response.getBody();
        assertEquals(testUserId, dto.id());
        assertEquals("Jane Smith", dto.name());
        assertEquals("081987654321", dto.phoneNumber());
        assertEquals("Jakarta", dto.address());
        assertEquals("Diabetes", dto.medicalHistory());
    }

    @Test
    void testGetProfileCaregiverNotFound() {
        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("caregiver");
        when(caregiverRepository.findById(testUserId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Caregiver not found", body.get("error"));
    }

    @Test
    void testGetProfilePacillianNotFound() {
        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("pacilian");
        when(pacillianRepository.findById(testUserId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Pacillian not found", body.get("error"));
    }

    @Test
    void testGetProfileMissingUserId() {
        when(jwt.getClaimAsString("user_id")).thenReturn(null);
        when(jwt.getClaimAsString("role")).thenReturn("caregiver");

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Missing user_id or role in JWT", body.get("error"));
    }

    @Test
    void testGetProfileMissingRole() {
        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn(null);
        when(jwt.getClaimAsStringList("roles")).thenReturn(null);

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Missing user_id or role in JWT", body.get("error"));
    }

    @Test
    void testGetProfileInvalidRole() {
        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("admin");

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Invalid role: admin", body.get("error"));
    }

    @Test
    void testGetProfileRoleFromRolesList() {
        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn(null);
        when(jwt.getClaimAsStringList("roles")).thenReturn(Arrays.asList("caregiver", "admin"));
        when(caregiverRepository.findById(testUserId)).thenReturn(Optional.of(testCaregiver));

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(CaregiverProfileDto.class, response.getBody());
    }

    @Test
    void testGetProfileEmptyRolesList() {
        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn(null);
        when(jwt.getClaimAsStringList("roles")).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testCreatePacillianProfileSuccess() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Patient");
        data.put("nik", "1234567890123456");
        data.put("email", "john@example.com");
        data.put("phone_number", "081234567890");
        data.put("address", "Jakarta");
        data.put("medical_history", "No allergies");

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("pacilian");
        when(pacillianRepository.existsById(testUserId)).thenReturn(false);
        when(pacillianRepository.save(any(Pacillian.class))).thenReturn(testPacillian);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(PacillianProfileDto.class, response.getBody());
        verify(pacillianRepository).save(any(Pacillian.class));
    }

    @Test
    void testCreateCaregiverProfileSuccess() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Dr. Jane");
        data.put("nik", "1234567890123456");
        data.put("email", "jane@example.com");
        data.put("phone_number", "081234567890");
        data.put("work_address", "Hospital B");
        data.put("speciality", "Neurology");

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("caregiver");
        when(caregiverRepository.existsById(testUserId)).thenReturn(false);
        when(caregiverRepository.save(any(Caregiver.class))).thenReturn(testCaregiver);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(CaregiverProfileDto.class, response.getBody());
        verify(caregiverRepository).save(any(Caregiver.class));
    }

    @Test
    void testCreateProfilePacillianAlreadyExists() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Patient");

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("pacilian");
        when(pacillianRepository.existsById(testUserId)).thenReturn(true);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Profile already exists", body.get("error"));
        verify(pacillianRepository, never()).save(any());
    }

    @Test
    void testCreateProfileCaregiverAlreadyExists() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Dr. Jane");

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("caregiver");
        when(caregiverRepository.existsById(testUserId)).thenReturn(true);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Profile already exists", body.get("error"));
        verify(caregiverRepository, never()).save(any());
    }

    @Test
    void testCreateProfileMissingUserId() {
        Map<String, Object> data = new HashMap<>();

        when(jwt.getClaimAsString("user_id")).thenReturn(null);
        when(jwt.getClaimAsString("role")).thenReturn("pacilian");

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Missing user_id or role in JWT", body.get("error"));
    }

    @Test
    void testCreateProfileMissingRole() {
        Map<String, Object> data = new HashMap<>();

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn(null);
        when(jwt.getClaimAsStringList("roles")).thenReturn(null);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Missing user_id or role in JWT", body.get("error"));
    }

    @Test
    void testCreateProfileInvalidRole() {
        Map<String, Object> data = new HashMap<>();

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("admin");

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Invalid role: admin", body.get("error"));
    }

    @Test
    void testCreateProfileWithNullValues() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", null);
        data.put("phone_number", null);

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("pacilian");
        when(pacillianRepository.existsById(testUserId)).thenReturn(false);
        when(pacillianRepository.save(any(Pacillian.class))).thenReturn(testPacillian);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(pacillianRepository).save(any(Pacillian.class));
    }

    @Test
    void testCreateProfileWithEmptyMap() {
        Map<String, Object> data = new HashMap<>();

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("pacilian");
        when(pacillianRepository.existsById(testUserId)).thenReturn(false);
        when(pacillianRepository.save(any(Pacillian.class))).thenReturn(testPacillian);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(pacillianRepository).save(any(Pacillian.class));
    }

    @Test
    void testCreateProfileWithNonStringValues() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", 123);
        data.put("phone_number", true);
        data.put("address", 45.67);

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("pacilian");
        when(pacillianRepository.existsById(testUserId)).thenReturn(false);
        when(pacillianRepository.save(any(Pacillian.class))).thenReturn(testPacillian);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(pacillianRepository).save(any(Pacillian.class));
    }

    @Test
    void testGetRoleFromRolesListSingleRole() {
        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn(null);
        when(jwt.getClaimAsStringList("roles")).thenReturn(Arrays.asList("pacilian"));
        when(pacillianRepository.findById(testUserId)).thenReturn(Optional.of(testPacillian));

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof PacillianProfileDto);
    }

    @Test
    void testCaseInsensitiveRoles() {
        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("CAREGIVER");
        when(caregiverRepository.findById(testUserId)).thenReturn(Optional.of(testCaregiver));

        ResponseEntity<?> response = profileController.getProfile(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CaregiverProfileDto);
    }

    @Test
    void testCaseInsensitiveRolesForCreate() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Dr. Test");

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("CAREGIVER");
        when(caregiverRepository.existsById(testUserId)).thenReturn(false);
        when(caregiverRepository.save(any(Caregiver.class))).thenReturn(testCaregiver);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof CaregiverProfileDto);
    }

    @Test
    void testCreateProfileWithSpecialCharacters() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Dr. José María");
        data.put("speciality", "Cardiología");
        data.put("work_address", "Hospital São Paulo");

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("caregiver");
        when(caregiverRepository.existsById(testUserId)).thenReturn(false);
        when(caregiverRepository.save(any(Caregiver.class))).thenReturn(testCaregiver);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(caregiverRepository).save(any(Caregiver.class));
    }

    @Test
    void testCreateProfilePacillianWithAllFields() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Complete Patient");
        data.put("nik", "1234567890123456");
        data.put("email", "complete@example.com");
        data.put("phone_number", "081234567890");
        data.put("address", "Complete Address Jakarta");
        data.put("medical_history", "Complete medical history with allergies");

        Pacillian savedPacillian = new Pacillian();
        savedPacillian.setId(testUserId);
        savedPacillian.setName("Complete Patient");
        savedPacillian.setPhoneNumber("081234567890");
        savedPacillian.setAddress("Complete Address Jakarta");
        savedPacillian.setMedicalHistory("Complete medical history with allergies");

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("pacilian");
        when(pacillianRepository.existsById(testUserId)).thenReturn(false);
        when(pacillianRepository.save(any(Pacillian.class))).thenReturn(savedPacillian);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof PacillianProfileDto);

        PacillianProfileDto dto = (PacillianProfileDto) response.getBody();
        assertEquals("Complete Patient", dto.name());
        assertEquals("081234567890", dto.phoneNumber());
        assertEquals("Complete Address Jakarta", dto.address());
        assertEquals("Complete medical history with allergies", dto.medicalHistory());
    }

    @Test
    void testCreateProfileCaregiverWithAllFields() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Dr. Complete Doctor");
        data.put("nik", "1234567890123456");
        data.put("email", "doctor@example.com");
        data.put("phone_number", "081234567890");
        data.put("work_address", "Complete Hospital Address");
        data.put("speciality", "Complete Specialty");

        Caregiver savedCaregiver = new Caregiver();
        savedCaregiver.setId(testUserId);
        savedCaregiver.setName("Dr. Complete Doctor");
        savedCaregiver.setPhoneNumber("081234567890");
        savedCaregiver.setWorkAddress("Complete Hospital Address");
        savedCaregiver.setSpeciality("Complete Specialty");

        when(jwt.getClaimAsString("user_id")).thenReturn(testUserId.toString());
        when(jwt.getClaimAsString("role")).thenReturn("caregiver");
        when(caregiverRepository.existsById(testUserId)).thenReturn(false);
        when(caregiverRepository.save(any(Caregiver.class))).thenReturn(savedCaregiver);

        ResponseEntity<?> response = profileController.createProfile(jwt, data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof CaregiverProfileDto);

        CaregiverProfileDto dto = (CaregiverProfileDto) response.getBody();
        assertEquals("Dr. Complete Doctor", dto.name());
        assertEquals("081234567890", dto.phoneNumber());
        assertEquals("Complete Hospital Address", dto.workAddress());
        assertEquals("Complete Specialty", dto.speciality());
    }
}