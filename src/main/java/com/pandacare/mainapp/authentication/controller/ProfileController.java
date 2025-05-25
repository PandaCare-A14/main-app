package com.pandacare.mainapp.authentication.controller;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.authentication.model.Pacillian;
import com.pandacare.mainapp.authentication.repository.CaregiverRepository;
import com.pandacare.mainapp.authentication.repository.PacillianRepository;
import com.pandacare.mainapp.authentication.dto.CaregiverProfileDto;
import com.pandacare.mainapp.authentication.dto.PacillianProfileDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {    private final CaregiverRepository caregiverRepository;
    private final PacillianRepository pacillianRepository;

    public ProfileController(@Qualifier("caregiverRepository") CaregiverRepository caregiverRepository, PacillianRepository pacillianRepository) {
        this.caregiverRepository = caregiverRepository;
        this.pacillianRepository = pacillianRepository;
    }

    @GetMapping
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = getUserId(jwt);
        String role = getRole(jwt);

        if (userId == null || role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing user_id or role in JWT"));
        }

        return switch (role.toLowerCase()) {
            case "caregiver" -> caregiverRepository.findById(userId)
                    .<ResponseEntity<?>>map(c -> ResponseEntity.ok(new CaregiverProfileDto(
                            c.getId(), c.getName(), c.getPhoneNumber(),
                            c.getWorkAddress(), c.getSpeciality())))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Caregiver not found")));
            case "pacilian" -> pacillianRepository.findById(userId)
                    .<ResponseEntity<?>>map(p -> ResponseEntity.ok(new PacillianProfileDto(
                            p.getId(), p.getName(), p.getPhoneNumber(),
                            p.getAddress(), p.getMedicalHistory())))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Pacillian not found")));
            default -> ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid role: " + role));
        };
    }

    @PostMapping
    public ResponseEntity<?> createProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody Map<String, Object> data) {
        UUID userId = getUserId(jwt);
        String role = getRole(jwt);

        if (userId == null || role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing user_id or role in JWT"));
        }

        return switch (role.toLowerCase()) {
            case "pacilian" -> createPacillianProfile(userId, data);
            case "caregiver" -> createCaregiverProfile(userId, data);
            default -> ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid role: " + role));
        };
    }

    private ResponseEntity<?> createPacillianProfile(UUID userId, Map<String, Object> data) {
        if (pacillianRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Profile already exists"));
        }

        Pacillian pacillian = new Pacillian();
        pacillian.setId(userId);
        pacillian.setName(getString(data, "name"));
        pacillian.setNik(getString(data, "nik"));
        pacillian.setEmail(getString(data, "email"));
        pacillian.setPhoneNumber(getString(data, "phone_number"));
        pacillian.setAddress(getString(data, "address"));
        pacillian.setMedicalHistory(getString(data, "medical_history"));

        Pacillian saved = pacillianRepository.save(pacillian);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PacillianProfileDto(saved.getId(), saved.getName(),
                        saved.getPhoneNumber(), saved.getAddress(), saved.getMedicalHistory()));
    }

    private ResponseEntity<?> createCaregiverProfile(UUID userId, Map<String, Object> data) {
        if (caregiverRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Profile already exists"));
        }

        Caregiver caregiver = new Caregiver();
        caregiver.setId(userId);
        caregiver.setName(getString(data, "name"));
        caregiver.setNik(getString(data, "nik"));
        caregiver.setEmail(getString(data, "email"));
        caregiver.setPhoneNumber(getString(data, "phone_number"));
        caregiver.setWorkAddress(getString(data, "work_address"));
        caregiver.setSpeciality(getString(data, "speciality"));

        Caregiver saved = caregiverRepository.save(caregiver);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CaregiverProfileDto(saved.getId(), saved.getName(),
                        saved.getPhoneNumber(), saved.getWorkAddress(), saved.getSpeciality()));
    }

    private UUID getUserId(Jwt jwt) {
        String id = jwt.getClaimAsString("user_id");
        return id != null ? UUID.fromString(id) : null;
    }

    private String getRole(Jwt jwt) {
        String role = jwt.getClaimAsString("role");
        if (role != null) return role;
        List<String> roles = jwt.getClaimAsStringList("roles");
        return (roles != null && !roles.isEmpty()) ? roles.get(0) : null;
    }

    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }
}