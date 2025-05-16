package com.pandacare.mainapp.doctor_profile.service;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.repository.DoctorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DoctorProfileServiceImpl implements DoctorProfileService {
    private final DoctorProfileRepository doctorProfileRepository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    public DoctorProfileServiceImpl(DoctorProfileRepository doctorProfileRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Override
    public DoctorProfile createProfile(DoctorProfile doctorProfile) {
        if (doctorProfile == null || doctorProfile.getId() == null) {
            return null;
        }

        if (!doctorProfileRepository.existsById(doctorProfile.getId())) {
            return doctorProfileRepository.save(doctorProfile);
        }
        return null;
    }

    @Override
    public DoctorProfile updateProfile(DoctorProfile newDoctorProfile) {
        if (newDoctorProfile.getId() == null) {
            return null;
        }

        return doctorProfileRepository.findById(newDoctorProfile.getId())
                .map(existingProfile -> doctorProfileRepository.save(newDoctorProfile))
                .orElse(null);
    }

    @Override
    public boolean deleteProfile(String id) {
        if (id == null) {
            return false;
        }

        if (doctorProfileRepository.existsById(id)) {
            doctorProfileRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorProfile> findAll() {
        return doctorProfileRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorProfile findById(String id) {
        return doctorProfileRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorProfile> findByName(String name) {
        return doctorProfileRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorProfile> findBySpeciality(String speciality) {
        return doctorProfileRepository.findBySpecialityContainingIgnoreCase(speciality);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorProfile> findByWorkSchedule(String workSchedule) {
        String[] parts = workSchedule.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid work schedule format. Expected format: 'Day HH:mm-HH:mm'");
        }

        String day = parts[0];
        String[] timeRange = parts[1].split("-");
        if (timeRange.length != 2) {
            throw new IllegalArgumentException("Invalid time range format. Expected format: 'HH:mm-HH:mm'");
        }

        LocalTime searchStart = LocalTime.parse(timeRange[0], timeFormatter);
        LocalTime searchEnd = LocalTime.parse(timeRange[1], timeFormatter);

        return doctorProfileRepository.findByWorkScheduleAvailable(day, searchStart, searchEnd);
    }
}