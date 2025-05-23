package com.pandacare.mainapp.authentication.model;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "caregivers")
@Setter
@Getter
public class Caregiver extends User {
    private String workAddress;
    private String speciality;

    @OneToMany(mappedBy = "idCaregiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaregiverSchedule> workingSchedules = new ArrayList<>();

    public Caregiver() {}

    public Caregiver(String name, String nik, String phoneNumber,
                     String workAddress, String speciality) {
        super(name, nik, phoneNumber);
        this.workAddress = workAddress;
        this.speciality = speciality;
        this.workingSchedules = new ArrayList<>();
    }

    public void addWorkingSchedule(CaregiverSchedule schedule) {
        workingSchedules.add(schedule);
        schedule.setIdCaregiver(this.getId());
    }

    public void removeWorkingSchedule(CaregiverSchedule schedule) {
        workingSchedules.remove(schedule);
        schedule.setIdCaregiver(null);
    }
}