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
        int i = 0;
        for (DoctorProfile savedDoctorProfile : doctorProfiles) {
            if (savedDoctorProfile.getId().equals(doctorProfile.getId())) {
                doctorProfiles.remove(i);
                doctorProfiles.add(i, doctorProfile);
                return doctorProfile;
            }
            i += 1;
        }
        doctorProfiles.add(doctorProfile);
        return doctorProfile;
    }

    public DoctorProfile delete(DoctorProfile doctorProfile) {
        System.out.println(doctorProfile.getId());
        Iterator<DoctorProfile> iterator = doctorProfiles.iterator();
        while (iterator.hasNext()) {
            DoctorProfile d = iterator.next();
            if (d.getId().equals(doctorProfile.getId())) {
                iterator.remove();
                return doctorProfile;
            }
        }
        return null;
    }


    public List<DoctorProfile> findAll() {
        return doctorProfiles;
    }

    public DoctorProfile findById(String id) {
        for (DoctorProfile savedDoctorProfile : doctorProfiles) {
            if (savedDoctorProfile.getId().equals(id)) {
                return savedDoctorProfile;
            }
        }
        return null;
    }

    public List<DoctorProfile> findByName(String name) {
        List<DoctorProfile> matchedDoctors = new ArrayList<>();

        for (DoctorProfile savedDoctorProfile : doctorProfiles) {
            if (savedDoctorProfile.getName().toLowerCase().contains(name.toLowerCase())) {
                matchedDoctors.add(savedDoctorProfile);
            }
        }

        return matchedDoctors;
    }

    public List<DoctorProfile> findBySpeciality(String speciality) {
        List<DoctorProfile> matchedDoctors = new ArrayList<>();

        for (DoctorProfile savedDoctorProfile : doctorProfiles) {
            if (savedDoctorProfile.getSpeciality().toLowerCase().contains(speciality.toLowerCase())) {
                matchedDoctors.add(savedDoctorProfile);
            }
        }
        return matchedDoctors;
    }

    public List<DoctorProfile> findByWorkSchedule(String day, String workHour) {
        List<DoctorProfile> matchedDoctors = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String[] timeRange = workHour.split("-");
        LocalTime searchStart = LocalTime.parse(timeRange[0], formatter);
        LocalTime searchEnd = LocalTime.parse(timeRange[1], formatter);

        for (DoctorProfile savedDoctorProfile : doctorProfiles) {
            Map<String, String> doctorSchedule = savedDoctorProfile.getWorkSchedule();
            if (doctorSchedule.containsKey(day)) {
                String doctorWorkHour = doctorSchedule.get(day);

                String[] doctorTimeRange = doctorWorkHour.split("-");
                LocalTime doctorStart = LocalTime.parse(doctorTimeRange[0].trim(), formatter);
                LocalTime doctorEnd = LocalTime.parse(doctorTimeRange[1].trim(), formatter);

                if (searchStart.isAfter(doctorStart) && searchStart.isBefore(doctorEnd)) {
                    if (Duration.between(searchStart, doctorEnd).toMinutes() >= 30) {
                        matchedDoctors.add(savedDoctorProfile);
                        continue;
                    }
                }
                if (searchEnd.isAfter(doctorStart) && searchEnd.isBefore(doctorEnd)) {
                    if (Duration.between(doctorStart, searchEnd).toMinutes() >= 30) {
                        matchedDoctors.add(savedDoctorProfile);
                    }
                }
            }
        }

        return matchedDoctors;
    }
}