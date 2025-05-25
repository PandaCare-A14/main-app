package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorProfileServiceImpl implements DoctorProfileService {
    private final DoctorProfileRepository doctorProfileRepository;
    private final RatingService ratingService;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    public DoctorProfileServiceImpl(DoctorProfileRepository doctorProfileRepository, RatingService ratingService) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.ratingService = ratingService;
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
            return mapToDoctorProfileResponse(caregiver, ratingResponse);
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
        if (workSchedule == null || workSchedule.trim().isEmpty()) {
            throw new IllegalArgumentException("Work schedule parameter cannot be empty");
        }

        try {
            String[] parts = workSchedule.split(" ");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid work schedule format. Expected format: 'Day HH:mm-HH:mm'");
            }

            DayOfWeek day = DayOfWeek.valueOf(parts[0].toUpperCase());
            String[] timeRange = parts[1].split("-");
            if (timeRange.length != 2) {
                throw new IllegalArgumentException("Invalid time range format. Expected format: 'HH:mm-HH:mm'");
            }

            LocalTime searchStart = LocalTime.parse(timeRange[0], timeFormatter);
            LocalTime searchEnd = LocalTime.parse(timeRange[1], timeFormatter);

            if (searchStart.isAfter(searchEnd)) {
                throw new IllegalArgumentException("Start time cannot be after end time");
            }

            List<Caregiver> caregivers = doctorProfileRepository.findByWorkingSchedulesAvailable(day, searchStart, searchEnd);
            return CompletableFuture.completedFuture(getDoctorProfileListResponse(caregivers));

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Use HH:mm format for times");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid day of week. Use full day names (e.g., Monday)");
        }
    }

    // Mapper method for single doctor profile
    private DoctorProfileResponse mapToDoctorProfileResponse(Caregiver caregiver, RatingListResponse ratingResponse) {
        DoctorProfileResponse response = new DoctorProfileResponse();
        response.setCaregiverId(caregiver.getId());
        response.setName(caregiver.getName());
        response.setEmail(caregiver.getEmail());
        response.setPhoneNumber(caregiver.getPhoneNumber());
        response.setWorkAddress(caregiver.getWorkAddress());
        response.setWorkSchedule(mapSchedulesToDTOs(caregiver.getWorkingSchedules()));
        response.setSpeciality(caregiver.getSpeciality());
        response.setAverageRating(ratingResponse != null ? ratingResponse.getAverageRating() : 0.0);
        response.setTotalRatings(ratingResponse != null ? ratingResponse.getTotalRatings() : 0);
        return response;
    }

    // Mapper method for schedule list
    private List<DoctorProfileResponse.CaregiverScheduleDTO> mapSchedulesToDTOs(List<CaregiverSchedule> schedules) {
        return schedules.stream()
                .map(this::mapScheduleToDTO)
                .collect(Collectors.toList());
    }

    // Mapper method for single schedule
    private DoctorProfileResponse.CaregiverScheduleDTO mapScheduleToDTO(CaregiverSchedule schedule) {
        DoctorProfileResponse.CaregiverScheduleDTO dto = new DoctorProfileResponse.CaregiverScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDay(schedule.getDay());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setStatus(schedule.getStatus());
        return dto;
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
                        return mapToDoctorProfileSummary(caregiver, ratings);
                    } catch (Exception e) {
                        // Handle rating service exception gracefully
                        return mapToDoctorProfileSummary(caregiver, null);
                    }
                })
                .collect(Collectors.toList()));
        response.setTotalItems(caregivers.size());
        return response;
    }

    // Mapper method for profile summary
    private DoctorProfileListResponse.DoctorProfileSummary mapToDoctorProfileSummary(
            Caregiver caregiver, RatingListResponse ratings) {
        DoctorProfileListResponse.DoctorProfileSummary summary = new DoctorProfileListResponse.DoctorProfileSummary();
        summary.setCaregiverId(caregiver.getId());
        summary.setName(caregiver.getName());
        summary.setSpeciality(caregiver.getSpeciality());
        summary.setAverageRating(ratings != null ? ratings.getAverageRating() : 0.0);
        summary.setTotalRatings(ratings != null ? ratings.getTotalRatings() : 0);
        return summary;
    }
}