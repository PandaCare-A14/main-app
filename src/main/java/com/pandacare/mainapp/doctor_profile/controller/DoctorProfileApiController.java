package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.dto.request.CreateDoctorRequest;
import com.pandacare.mainapp.doctor_profile.dto.request.UpdateDoctorRequest;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.ErrorResponse;
import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
public class DoctorProfileApiController {

    private final DoctorProfileService doctorProfileService;

    public DoctorProfileApiController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @GetMapping
    public ResponseEntity<DoctorListResponse> getAllDoctors() {
        List<DoctorProfile> doctors = doctorProfileService.findAll();

        DoctorListResponse response = new DoctorListResponse();
        response.setDoctors(doctors.stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList()));
        response.setTotalItems(doctors.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable String id) {
        DoctorProfile doctor = doctorProfileService.findById(id);
        return doctor != null ?
                ResponseEntity.ok(convertToDto(doctor)) :
                ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        try {
            DoctorProfile doctor = convertToEntity(request);
            DoctorProfile created = doctorProfileService.createProfile(doctor);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(convertToDto(created));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Bad Request",
                            e.getMessage(),
                            "/api/doctors"
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDoctor(
            @PathVariable String id,
            @Valid @RequestBody UpdateDoctorRequest request) {
        if (!id.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            DoctorProfile doctor = convertToEntity(request);
            DoctorProfile updated = doctorProfileService.updateProfile(doctor);
            return updated != null ?
                    ResponseEntity.ok(convertToDto(updated)) :
                    ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Bad Request",
                            e.getMessage(),
                            "/api/doctors/" + id
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        if (doctorProfileService.deleteProfile(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Conversion methods
    private DoctorProfile convertToEntity(CreateDoctorRequest request) {
        DoctorProfile doctor = new DoctorProfile();
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPhoneNumber(request.getPhoneNumber());
        doctor.setWorkAddress(request.getWorkAddress());
        doctor.setWorkSchedule(request.getWorkSchedule());
        doctor.setSpeciality(request.getSpeciality());
        return doctor;
    }

    private DoctorProfile convertToEntity(UpdateDoctorRequest request) {
        DoctorProfile doctor = new DoctorProfile();
        doctor.setId(request.getId());
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPhoneNumber(request.getPhoneNumber());
        doctor.setWorkAddress(request.getWorkAddress());
        doctor.setWorkSchedule(request.getWorkSchedule());
        doctor.setSpeciality(request.getSpeciality());
        return doctor;
    }

    private DoctorResponse convertToDto(DoctorProfile entity) {
        DoctorResponse response = new DoctorResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setWorkAddress(entity.getWorkAddress());
        response.setWorkSchedule(entity.getWorkSchedule());
        response.setSpeciality(entity.getSpeciality());
        response.setRating(entity.getRating());
        return response;
    }

    private DoctorListResponse.DoctorSummary convertToSummaryDto(DoctorProfile entity) {
        DoctorListResponse.DoctorSummary summary = new DoctorListResponse.DoctorSummary();
        summary.setId(entity.getId());
        summary.setName(entity.getName());
        summary.setSpeciality(entity.getSpeciality());
        summary.setRating(entity.getRating());
        return summary;
    }
}