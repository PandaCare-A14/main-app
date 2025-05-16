package com.pandacare.mainapp.doctor_profile.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter @Setter
public class DoctorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String workAddress;
    private Map<String, String> workSchedule;
    private String speciality;
    private double rating;

    public DoctorProfile() {
        this.id = UUID.randomUUID().toString();
        this.workSchedule = new HashMap<>();
    }

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