package com.pandacare.mainapp.doctor_profile.service.factory;

import com.pandacare.mainapp.authentication.model.Caregiver;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;

import java.util.List;
import java.util.stream.Collectors;

public class DoctorProfileMapper {

    // Mapper for detailed doctor profile response
    public static DoctorProfileResponse mapToDoctorProfileResponse(
            Caregiver caregiver,
            RatingListResponse ratingResponse) {

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

    // Mapper for profile summary (used in lists)
    public static DoctorProfileListResponse.DoctorProfileSummary mapToDoctorProfileSummary(
            Caregiver caregiver,
            RatingListResponse ratings) {

        DoctorProfileListResponse.DoctorProfileSummary summary =
                new DoctorProfileListResponse.DoctorProfileSummary();
        summary.setCaregiverId(caregiver.getId());
        summary.setName(caregiver.getName());
        summary.setSpeciality(caregiver.getSpeciality());
        summary.setAverageRating(ratings != null ? ratings.getAverageRating() : 0.0);
        summary.setTotalRatings(ratings != null ? ratings.getTotalRatings() : 0);
        return summary;
    }

    // Helper method for schedule mapping
    private static List<DoctorProfileResponse.CaregiverScheduleDTO> mapSchedulesToDTOs(
            List<CaregiverSchedule> schedules) {

        return schedules.stream()
                .map(DoctorProfileMapper::mapScheduleToDTO)
                .collect(Collectors.toList());
    }

    // Helper method for single schedule mapping
    private static DoctorProfileResponse.CaregiverScheduleDTO mapScheduleToDTO(
            CaregiverSchedule schedule) {

        DoctorProfileResponse.CaregiverScheduleDTO dto =
                new DoctorProfileResponse.CaregiverScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDay(schedule.getDay());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setStatus(schedule.getStatus());
        return dto;
    }
}