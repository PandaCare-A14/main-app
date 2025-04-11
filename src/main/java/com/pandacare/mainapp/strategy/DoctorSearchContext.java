package com.pandacare.mainapp.strategy;

import com.pandacare.mainapp.model.DoctorProfile;
import lombok.Setter;

import java.util.List;

@Setter
public class DoctorSearchContext {
    private SearchStrategy strategy;

    public List<DoctorProfile> executeSearch(String keyword) {
        return strategy.search(keyword);
    }
}

