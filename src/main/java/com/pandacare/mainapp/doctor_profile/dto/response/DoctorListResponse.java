package com.pandacare.mainapp.doctor_profile.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class DoctorListResponse {
    private List<DoctorSummary> doctors;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    @Data
    public static class DoctorSummary {
        private String id;
        private String name;
        private String speciality;
        private double rating;
    }
}