package com.pandacare.mainapp.doctor_profile.strategy;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("scheduleSearchStrategy")
public class SearchBySchedule implements SearchStrategy {
    private DoctorProfileRepository doctorProfileRepository;

    public SearchBySchedule(DoctorProfileRepository doctorProfileRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Override
    public List<DoctorProfile> search(String workSchedule) {
        return doctorProfileRepository.findByWorkSchedule(workSchedule);
    }
}

