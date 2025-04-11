package com.pandacare.mainapp.strategy;

import com.pandacare.mainapp.model.DoctorProfile;
import com.pandacare.mainapp.repository.DoctorProfileRepository;

import java.util.List;

public class SearchByName implements SearchStrategy {
    private DoctorProfileRepository doctorProfileRepository;

    public SearchByName(DoctorProfileRepository doctorProfileRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Override
    public List<DoctorProfile> search(String name) {
        return doctorProfileRepository.findByName(name);
    }
}

