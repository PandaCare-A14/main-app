package com.pandacare.mainapp.doctor_profile.strategy;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;

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

