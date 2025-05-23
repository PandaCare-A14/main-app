package com.pandacare.mainapp.authentication.controller;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.authentication.model.Pacilian;
import com.pandacare.mainapp.authentication.repository.CaregiverRepository;
import com.pandacare.mainapp.authentication.repository.PacilianRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.pandacare.mainapp.authentication.dto.CaregiverProfileDto;
import com.pandacare.mainapp.authentication.dto.PacilianProfileDto;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final CaregiverRepository caregiverRepository;
    private final PacilianRepository pacilianRepository;

    public ProfileController(CaregiverRepository caregiverRepository, PacilianRepository pacilianRepository) {
        this.caregiverRepository = caregiverRepository;
        this.pacilianRepository = pacilianRepository;
    }

    @GetMapping
    public ResponseEntity<Object> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String id = jwt.getClaimAsString("user_id");
        String role = jwt.getClaimAsString("role");

        if (id == null || role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing user_id or role in JWT");
        }

        UUID userId = UUID.fromString(id);

        switch (role.toLowerCase()) {
            case "caregiver": {
                Optional<Caregiver> caregiver = caregiverRepository.findById(userId);
                if (caregiver.isPresent()) {
                    Caregiver c = caregiver.get();
                    CaregiverProfileDto dto = new CaregiverProfileDto(
                            c.getId(), c.getName(), c.getPhoneNumber(),
                            c.getWorkAddress(), c.getSpeciality()
                    );
                    return ResponseEntity.ok(dto);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Caregiver not found");
                }
            }
            case "pacilian": {
                Optional<Pacilian> pacilian = pacilianRepository.findById(userId);
                if (pacilian.isPresent()) {
                    Pacilian p = pacilian.get();
                    PacilianProfileDto dto = new PacilianProfileDto(
                            p.getId(), p.getName(), p.getPhoneNumber(),
                            p.getAddress(), p.getMedicalHistory()
                    );
                    return ResponseEntity.ok(dto);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pacilian not found");
                }
            }
            default: {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Invalid role in token: " + role);
            }
        }
    }
}