package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DoctorProfileService {
    CompletableFuture<DoctorProfileListResponse> findAll();
    CompletableFuture<DoctorProfileResponse> findById(UUID id);
    CompletableFuture<DoctorProfileListResponse> findByName(String name);
    CompletableFuture<DoctorProfileListResponse> findBySpeciality(String speciality);
    CompletableFuture<DoctorProfileListResponse> findByWorkSchedule(String workSchedule);
}
