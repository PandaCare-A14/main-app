package com.pandacare.mainapp.doctor_profile.dto.response;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String workAddress;
    private List<CaregiverSchedule> workSchedule;
    private String speciality;
    private Double averageRating;
    private int totalRatings;
}