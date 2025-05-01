package com.pandacare.mainapp.doctor_profile.strategy;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;

import java.util.List;

public interface SearchStrategy {
    List<DoctorProfile> search(String keyword);
}