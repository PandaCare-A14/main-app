package com.pandacare.mainapp.doctor_profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
        private String id;
        private String name;
        private String speciality;
        private Double averageRating;
        private Integer totalRatings;
    }
}