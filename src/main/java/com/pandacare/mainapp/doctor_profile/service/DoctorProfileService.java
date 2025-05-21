package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;

public interface DoctorProfileService {
    DoctorProfileListResponse findAll();
    DoctorProfileResponse findById(String id);
    DoctorProfileListResponse findByName(String name);
    DoctorProfileListResponse findBySpeciality(String speciality);
    DoctorProfileListResponse findByWorkSchedule(String workSchedule);
}
