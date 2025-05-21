package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.ErrorResponse;
import com.pandacare.mainapp.doctor_profile.facade.DoctorFacade;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorProfileApiController {

    private final DoctorProfileService doctorProfileService;
    private final DoctorFacade doctorFacade;

    public DoctorProfileApiController(DoctorProfileService doctorProfileService, DoctorFacade doctorFacade) {
        this.doctorProfileService = doctorProfileService;
        this.doctorFacade = doctorFacade;
    }

    @GetMapping("/{doctorId}/actions")
    public ResponseEntity<DoctorProfileResponse> getDoctorWithActions(
            @PathVariable String doctorId,
            @RequestParam String patientId
    ) {
        try {
            DoctorProfileResponse response = doctorFacade.getDoctorProfileWithActions(doctorId, patientId);
            return response != null ?
                    ResponseEntity.ok(response) :
                    ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<DoctorProfileListResponse> getAllDoctorProfiles() {
        try {
            DoctorProfileListResponse response = doctorProfileService.findAll();
            return response != null ?
                    ResponseEntity.ok(response) :
                    ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileResponse> getDoctorProfile(@PathVariable String id) {
        try {
            DoctorProfileResponse response = doctorProfileService.findById(id);
            return response != null ?
                    ResponseEntity.ok(response) :
                    ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/by-name")
    public ResponseEntity<DoctorProfileListResponse> searchDoctorsByName(
            @RequestParam String name) {
        try {
            DoctorProfileListResponse response = doctorProfileService.findByName(name);
            return response != null ?
                    ResponseEntity.ok(response) :
                    ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/by-speciality")
    public ResponseEntity<DoctorProfileListResponse> searchDoctorsBySpeciality(
            @RequestParam String speciality) {
        try {
            DoctorProfileListResponse response = doctorProfileService.findBySpeciality(speciality);
            return response != null ?
                    ResponseEntity.ok(response) :
                    ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/by-schedule")
    public ResponseEntity<?> searchDoctorsBySchedule(
            @RequestParam String day,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        try {
            // Combine parameters into the expected format "Day HH:mm-HH:mm"
            String workSchedule = String.format("%s %s-%s", day, startTime, endTime);
            DoctorProfileListResponse response = doctorProfileService.findByWorkSchedule(workSchedule);
            return response != null ?
                    ResponseEntity.ok(response) :
                    ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid schedule format: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}