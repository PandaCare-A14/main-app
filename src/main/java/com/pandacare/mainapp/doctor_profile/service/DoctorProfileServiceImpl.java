package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import com.pandacare.mainapp.doctor_profile.strategy.DoctorSearchContext;
import com.pandacare.mainapp.doctor_profile.strategy.SearchByName;
import com.pandacare.mainapp.doctor_profile.strategy.SearchBySpeciality;
import com.pandacare.mainapp.doctor_profile.strategy.SearchByWorkSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorProfileServiceImpl implements DoctorProfileService {

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Override
    public DoctorProfile createProfile(DoctorProfile doctorProfile) {
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
        DoctorSearchContext context = new DoctorSearchContext();

        switch (searchType) {
            case "NAME":
                context.setStrategy(new SearchByName(doctorProfileRepository));
                break;
            case "SPECIALITY":
                context.setStrategy(new SearchBySpeciality(doctorProfileRepository));
                break;
            case "WORK_SCHEDULE":
                context.setStrategy(new SearchByWorkSchedule(doctorProfileRepository));
                break;
            default:
                throw new IllegalArgumentException("Invalid search type");
        }

        return context.executeSearch(keyword);
    }
}
