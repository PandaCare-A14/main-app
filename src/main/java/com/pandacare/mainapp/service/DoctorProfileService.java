package com.pandacare.mainapp.service;

import com.pandacare.mainapp.model.DoctorProfile;

import java.util.List;

public interface DoctorProfileService {
    public DoctorProfile createProfile(DoctorProfile profile);
    public DoctorProfile updateProfile(DoctorProfile profile);
    public DoctorProfile deleteProfile(DoctorProfile profile);
    public List<DoctorProfile> findAll();
    public DoctorProfile findById(String id);
    public List<DoctorProfile> findByName(String name);
    public List<DoctorProfile> findBySpeciality(String speciality);
    public List<DoctorProfile> findByWorkSchedule(String workSchedule);
}
