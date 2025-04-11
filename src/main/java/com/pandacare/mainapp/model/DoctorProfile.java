package com.pandacare.mainapp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class DoctorProfile {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String workAddress;
    private Map<String, String> workSchedule;
    private String speciality;
    private double rating;

    public DoctorProfile(String name, String email, String phoneNumber, String workAddress, Map<String, String> workSchedule, String speciality, double rating) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.workAddress = workAddress;
        this.workSchedule = workSchedule;
        this.speciality = speciality;
        this.rating = rating;
    }
}