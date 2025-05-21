package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.service.RatingService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
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
    @Transactional(readOnly = true)
    public DoctorProfileListResponse findAll() {
        List<Caregiver> caregivers = doctorProfileRepository.findAll();
        return getDoctorProfileListResponse(caregivers);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorProfileResponse findById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            Caregiver caregiver = doctorProfileRepository.findById(uuid).orElse(null);
            if (caregiver == null) {
                return null;
            }

            RatingListResponse ratingResponse = ratingService.getRatingsByDokter(id);
            return new DoctorProfileResponse(
                    id,
                    caregiver.getName(),
                    caregiver.getEmail(),
                    caregiver.getPhoneNumber(),
                    caregiver.getWorkAddress(),
                    caregiver.getWorkingSchedules(),
                    caregiver.getSpeciality(),
                    ratingResponse != null ? ratingResponse.getAverageRating() : 0.0,
                    ratingResponse != null ? ratingResponse.getTotalRatings() : 0
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid doctor ID format");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorProfileListResponse findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name parameter cannot be empty");
        }
        List<Caregiver> caregivers = doctorProfileRepository.findByNameContainingIgnoreCase(name);
        return getDoctorProfileListResponse(caregivers);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorProfileListResponse findBySpeciality(String speciality) {
        if (speciality == null || speciality.trim().isEmpty()) {
            throw new IllegalArgumentException("Speciality parameter cannot be empty");
        }
        List<Caregiver> caregivers = doctorProfileRepository.findBySpecialityContainingIgnoreCase(speciality);
        return getDoctorProfileListResponse(caregivers);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorProfileListResponse findByWorkSchedule(String workSchedule) {
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

            List<Caregiver> caregivers = doctorProfileRepository.findByWorkScheduleAvailable(day, searchStart, searchEnd);
            return getDoctorProfileListResponse(caregivers);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Use HH:mm format for times");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid day of week. Use full day names (e.g., Monday)");
        }
    }

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
                    RatingListResponse ratings = ratingService.getRatingsByDokter(caregiver.getId().toString());
                    return new DoctorProfileListResponse.DoctorProfileSummary(
                            caregiver.getId().toString(),
                            caregiver.getName(),
                            caregiver.getSpeciality(),
                            ratings != null ? ratings.getAverageRating() : 0.0,
                            ratings != null ? ratings.getTotalRatings() : 0
                    );
                })
                .collect(Collectors.toList()));
        response.setTotalItems(caregivers.size());

        return response;
    }
}