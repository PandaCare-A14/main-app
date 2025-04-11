package com.pandacare.mainapp.strategy;

import com.pandacare.mainapp.model.DoctorProfile;
import com.pandacare.mainapp.repository.DoctorProfileRepository;

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

