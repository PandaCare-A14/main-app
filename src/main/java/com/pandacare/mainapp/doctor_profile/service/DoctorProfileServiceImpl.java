package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import com.pandacare.mainapp.doctor_profile.service.factory.DoctorProfileMapper;
import com.pandacare.mainapp.doctor_profile.service.strategy.ParsedWorkSchedule;
import com.pandacare.mainapp.doctor_profile.service.strategy.WorkScheduleParser;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorProfileServiceImpl implements DoctorProfileService {
    private final DoctorProfileRepository doctorProfileRepository;
    private final RatingService ratingService;
    private final WorkScheduleParser workScheduleParser;

    @Autowired
    public DoctorProfileServiceImpl(
            DoctorProfileRepository doctorProfileRepository,
            RatingService ratingService,
            WorkScheduleParser workScheduleParser) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.ratingService = ratingService;
        this.workScheduleParser = workScheduleParser;
    }

    @Override
    @Async
    public CompletableFuture<DoctorProfileListResponse> findAll() {
        List<Caregiver> caregivers = doctorProfileRepository.findAll();
        return CompletableFuture.completedFuture(getDoctorProfileListResponse(caregivers));
    }

    @Override
    @Async
    public CompletableFuture<DoctorProfileResponse> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Doctor ID cannot be null");
        }

        return CompletableFuture.supplyAsync(() -> {
            Caregiver caregiver = doctorProfileRepository.findById(id).orElse(null);
            if (caregiver == null) {
                return null;
            }

            RatingListResponse ratingResponse = ratingService.getRatingsByDokter(id);
            return DoctorProfileMapper.mapToDoctorProfileResponse(caregiver, ratingResponse);
        });
    }

    @Override
    @Async
    public CompletableFuture<DoctorProfileListResponse> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name parameter cannot be empty");
        }
        List<Caregiver> caregivers = doctorProfileRepository.findByNameContainingIgnoreCase(name);
        return CompletableFuture.completedFuture(getDoctorProfileListResponse(caregivers));
    }

    @Override
    @Async
    public CompletableFuture<DoctorProfileListResponse> findBySpeciality(String speciality) {
        if (speciality == null || speciality.trim().isEmpty()) {
            throw new IllegalArgumentException("Speciality parameter cannot be empty");
        }
        List<Caregiver> caregivers = doctorProfileRepository.findBySpecialityContainingIgnoreCase(speciality);
        return CompletableFuture.completedFuture(getDoctorProfileListResponse(caregivers));
    }

    @Override
    @Async
    public CompletableFuture<DoctorProfileListResponse> findByWorkSchedule(String workSchedule) {
        ParsedWorkSchedule parsed = workScheduleParser.parse(workSchedule);
        List<Caregiver> caregivers = doctorProfileRepository.findByWorkingSchedulesAvailable(
                parsed.day(), parsed.startTime(), parsed.endTime()
        );
        return CompletableFuture.completedFuture(getDoctorProfileListResponse(caregivers));
    }

    // Update getDoctorProfileListResponse to use internal mapping
    @NotNull
    public DoctorProfileListResponse getDoctorProfileListResponse(List<Caregiver> caregivers) {
        DoctorProfileListResponse response = new DoctorProfileListResponse();
        if (caregivers == null || caregivers.isEmpty()) {
            response.setDoctorProfiles(List.of());
            response.setTotalItems(0);
            return response;
        }

        response.setDoctorProfiles(caregivers.stream()
                .map(caregiver -> {
                    try {
                        RatingListResponse ratings = ratingService.getRatingsByDokter(caregiver.getId());
                        return DoctorProfileMapper.mapToDoctorProfileSummary(caregiver, ratings);
                    } catch (Exception e) {
                        // Handle rating service exception gracefully
                        return DoctorProfileMapper.mapToDoctorProfileSummary(caregiver, null);
                    }
                })
                .collect(Collectors.toList()));
        response.setTotalItems(caregivers.size());
        return response;
    }
}