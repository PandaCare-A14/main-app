package com.pandacare.mainapp.doctor_profile.strategy;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import lombok.Setter;

import java.util.List;

@Setter
public class DoctorSearchContext {
    private SearchStrategy strategy;

    public List<DoctorProfile> executeSearch(String keyword) {
        return strategy.search(keyword);
    }
}

