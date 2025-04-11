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

    }

    @Override
    public DoctorProfile updateProfile(DoctorProfile newDoctorProfile) {

    }

    @Override
    public DoctorProfile deleteProfile(DoctorProfile doctorProfile) {

    }

    @Override
    public List<DoctorProfile> findAll() {

    }

    @Override
    public DoctorProfile findById(String id) {

    }

    @Override
    public List<DoctorProfile> findByName(String name) {

    }

    @Override
    public List<DoctorProfile> findBySpeciality(String speciality) {

    }

    @Override
    public List<DoctorProfile> findByWorkSchedule(String day, String workHour) {

    }
}
