package com.pandacare.mainapp.repository;

import com.pandacare.mainapp.model.DoctorProfile;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
public class DoctorProfileRepository {
    private final List<DoctorProfile> doctorProfiles = new ArrayList<>();

    public DoctorProfile save(DoctorProfile doctorProfile) {

    }

    public DoctorProfile delete(DoctorProfile doctorProfile) {

    }


    public List<DoctorProfile> findAll() {

    }

    public DoctorProfile findById(String id) {

    }

    public List<DoctorProfile> findByName(String name) {

    }

    public List<DoctorProfile> findBySpeciality(String speciality) {

    }

    public List<DoctorProfile> findByWorkSchedule(String day, String workHour) {

    }
}
