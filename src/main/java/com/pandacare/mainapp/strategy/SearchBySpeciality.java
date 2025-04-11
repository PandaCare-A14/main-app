package com.pandacare.mainapp.strategy;

import com.pandacare.mainapp.model.DoctorProfile;
import com.pandacare.mainapp.repository.DoctorProfileRepository;

import java.util.List;

public class SearchBySpeciality implements SearchStrategy {
    private DoctorProfileRepository doctorProfileRepository;

    public SearchBySpeciality(DoctorProfileRepository doctorProfileRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Override
    public List<DoctorProfile> search(String speciality) {
        return doctorProfileRepository.findBySpeciality(speciality);
    }
}

