package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import com.pandacare.mainapp.doctor_profile.service.factory.DoctorProfileMapper;
import com.pandacare.mainapp.doctor_profile.service.strategy.ParsedWorkSchedule;
import com.pandacare.mainapp.doctor_profile.service.strategy.WorkScheduleParser;
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
    public CompletableFuture<DoctorProfileListResponse> searchByCriteria(
            String name,
            String speciality,
            String day,
            String startTime,
            String endTime) {

        return CompletableFuture.supplyAsync(() -> {
            List<Caregiver> caregivers;

            if (day != null && startTime != null && endTime != null) {
                // Case 1: Search by schedule plus optional name/speciality
                ParsedWorkSchedule parsed = workScheduleParser.parse(
                        String.format("%s %s-%s", day, startTime, endTime));
                caregivers = doctorProfileRepository.findByWorkingSchedulesAvailable(
                        parsed.day(), parsed.startTime(), parsed.endTime());

                // Apply additional filters if present
                if (name != null && !name.trim().isEmpty()) {
                    caregivers = caregivers.stream()
                            .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                            .collect(Collectors.toList());
                }
                if (speciality != null && !speciality.trim().isEmpty()) {
                    caregivers = caregivers.stream()
                            .filter(c -> c.getSpeciality().toLowerCase().contains(speciality.toLowerCase()))
                            .collect(Collectors.toList());
                }
            } else {
                // Case 2: Search by name and/or speciality only
                boolean hasName = name != null && !name.trim().isEmpty();
                boolean hasSpeciality = speciality != null && !speciality.trim().isEmpty();

                if (hasName && hasSpeciality) {
                    caregivers = doctorProfileRepository.findByNameContainingIgnoreCaseAndSpecialityContainingIgnoreCase(
                            name, speciality);
                } else if (hasName) {
                    caregivers = doctorProfileRepository.findByNameContainingIgnoreCase(name);
                } else if (hasSpeciality) {
                    caregivers = doctorProfileRepository.findBySpecialityContainingIgnoreCase(speciality);
                } else {
                    throw new IllegalArgumentException("At least one search parameter must be provided");
                }
            }

            return getDoctorProfileListResponse(caregivers);
        });
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