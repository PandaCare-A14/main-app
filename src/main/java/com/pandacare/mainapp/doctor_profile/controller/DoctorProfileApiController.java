package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorProfileApiController {

    private final DoctorProfileService doctorProfileService;

    public DoctorProfileApiController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @GetMapping
    public List<DoctorProfile> getAllDoctors() {
        return doctorProfileService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfile> getDoctorById(@PathVariable String id) {
        DoctorProfile doctor = doctorProfileService.findById(id);
        return doctor != null ? ResponseEntity.ok(doctor) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<DoctorProfile> createDoctor(@RequestBody DoctorProfile doctorProfile) {
        DoctorProfile created = doctorProfileService.createProfile(doctorProfile);
        return created != null ?
                ResponseEntity.status(HttpStatus.CREATED).body(created) :
                ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorProfile> updateDoctor(
            @PathVariable String id,
            @RequestBody DoctorProfile doctorProfile) {
        if (!id.equals(doctorProfile.getId())) {
            return ResponseEntity.badRequest().build();
        }
        DoctorProfile updated = doctorProfileService.updateProfile(doctorProfile);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        DoctorProfile doctor = doctorProfileService.findById(id);
        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }
        doctorProfileService.deleteProfile(doctor);
        return ResponseEntity.noContent().build();
    }
}