package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;

import java.util.List;

public interface DoctorProfileService {
    public DoctorProfile createProfile(DoctorProfile profile);
    public DoctorProfile updateProfile(DoctorProfile profile);
    public DoctorProfile deleteProfile(DoctorProfile profile);
    public List<DoctorProfile> findAll();
    public DoctorProfile findById(String id);
    public List<DoctorProfile> searchDoctorProfile(String searchType, String keyword);
}
