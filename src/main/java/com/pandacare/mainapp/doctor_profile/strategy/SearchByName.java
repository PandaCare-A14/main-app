package com.pandacare.mainapp.doctor_profile.strategy;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("nameSearchStrategy")
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

