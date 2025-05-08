package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import com.pandacare.mainapp.doctor_profile.strategy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DoctorProfileServiceImpl implements DoctorProfileService {
    private final DoctorProfileRepository doctorProfileRepository;
    private final Map<String, SearchStrategy> strategies;

    @Autowired
    public DoctorProfileServiceImpl(DoctorProfileRepository doctorProfileRepository,
                                    Map<String, SearchStrategy> strategies) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.strategies = strategies;
    }

    @Override
    public DoctorProfile createProfile(DoctorProfile doctorProfile) {
        if (doctorProfile == null) {
            return null;
        }
        if (doctorProfile.getId() == null) {
            return null;
        }
        if (doctorProfileRepository.findById(doctorProfile.getId()) == null) {
            doctorProfileRepository.save(doctorProfile);
            return doctorProfile;
        }
        return null;
    }

    @Override
    public DoctorProfile updateProfile(DoctorProfile newDoctorProfile) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(newDoctorProfile.getId());
        if (doctorProfile != null) {
            doctorProfileRepository.save(newDoctorProfile);
            return newDoctorProfile;
        } else {
            return null;
        }
    }

    @Override
    public DoctorProfile deleteProfile(DoctorProfile doctorProfile) {
        return doctorProfileRepository.delete(doctorProfile);
    }

    @Override
    public List<DoctorProfile> findAll() {
        return doctorProfileRepository.findAll();
    }

    @Override
    public DoctorProfile findById(String id) {
        return doctorProfileRepository.findById(id);
    }

    @Override
    public List<DoctorProfile> searchDoctorProfile(String searchType, String keyword) {
        SearchStrategy strategy = strategies.get(searchType.toLowerCase() + "SearchStrategy");
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid search type");
        }
        DoctorSearchContext context = new DoctorSearchContext();
        context.setStrategy(strategy);
        return context.executeSearch(keyword);
    }
}
