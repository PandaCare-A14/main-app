package com.pandacare.mainapp.doctor_profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileListResponse {
    private List<DoctorProfileSummary> doctorProfiles;
    private long totalItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorProfileSummary {
        private UUID caregiverId;
        private String name;
        private String speciality;
        private Double averageRating;
        private Integer totalRatings;
    }
}