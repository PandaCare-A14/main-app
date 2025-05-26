package com.pandacare.mainapp.doctor_profile.dto.response;

import com.pandacare.mainapp.konsultasi_dokter.enums.ScheduleStatus;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileResponse {
    private UUID caregiverId;
    private String name;
    private String email;
    private String phoneNumber;
    private String workAddress;
    private List<CaregiverScheduleDTO> workSchedule;
    private String speciality;
    private Double averageRating;
    private List<RatingResponse> ratings;
    private int totalRatings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CaregiverScheduleDTO {
        private UUID id;
        private DayOfWeek day;
        private LocalTime startTime;
        private LocalTime endTime;
        private ScheduleStatus status;
    }
}