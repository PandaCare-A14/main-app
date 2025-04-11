package com.pandacare.mainapp.strategy;

import com.pandacare.mainapp.model.DoctorProfile;

import java.util.List;

public interface SearchStrategy {
    List<DoctorProfile> search(String keyword);
}