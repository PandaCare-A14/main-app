package com.pandacare.mainapp.service;

import com.pandacare.mainapp.model.DoctorProfile;
import com.pandacare.mainapp.repository.DoctorProfileRepository;
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
    public List<DoctorProfile> findByName(String name) {
        return doctorProfileRepository.findByName(name);
    }

    @Override
    public List<DoctorProfile> findBySpeciality(String speciality) {
        return doctorProfileRepository.findBySpeciality(speciality);
    }

    @Override
    public List<DoctorProfile> findByWorkSchedule(String workSchedule) {
        return doctorProfileRepository.findByWorkSchedule(workSchedule);
    }
}
