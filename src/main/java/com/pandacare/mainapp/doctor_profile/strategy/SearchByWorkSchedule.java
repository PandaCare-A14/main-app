package com.pandacare.mainapp.doctor_profile.strategy;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;

import java.util.List;

public class SearchByWorkSchedule implements SearchStrategy {
    private final DoctorProfileRepository doctorProfileRepository;

    public SearchByWorkSchedule(DoctorProfileRepository doctorProfileRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Override
    public List<DoctorProfile> search(String workSchedule) {
        return doctorProfileRepository.findByWorkSchedule(workSchedule);
    }
}

