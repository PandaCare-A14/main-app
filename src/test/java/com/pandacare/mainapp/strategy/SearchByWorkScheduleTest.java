package com.pandacare.mainapp.strategy;

import com.pandacare.mainapp.model.DoctorProfile;
import com.pandacare.mainapp.repository.DoctorProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchByWorkScheduleTest {

    SearchByWorkSchedule searchByWorkSchedule;
    DoctorProfileRepository doctorProfileRepository;
    List<DoctorProfile> doctorProfileList;

    @BeforeEach
    void setUp() {
        doctorProfileRepository = mock(DoctorProfileRepository.class);
        searchByWorkSchedule = new SearchByWorkSchedule(doctorProfileRepository);
        doctorProfileList = new ArrayList<>();

        Map<String, String> workSchedule1 = new HashMap<>();
        workSchedule1.put("Senin", "09:00-12:00");
        workSchedule1.put("Rabu", "10:00-13:00");

        DoctorProfile doctorProfile1 = new DoctorProfile("Dr. Hafiz", "hafiz@pandacare.com", "08123456789", "RS Pandacare",
                workSchedule1, "Cardiologist", 4.9);
        doctorProfile1.setId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        doctorProfileList.add(doctorProfile1);

        Map<String, String> workSchedule2 = new HashMap<>();
        workSchedule2.put("Selasa", "14:00-18:00");
        workSchedule2.put("Kamis", "09:00-12:00");

        DoctorProfile doctorProfile2 = new DoctorProfile("Dr. Jonah", "jonah@pandacare.com", "08192836789", "RS Pondok Indah",
                workSchedule2, "Orthopedic", 4.8);
        doctorProfile2.setId("eb558e9f-1c39-460e-8860-71af6af63ds2");
        doctorProfileList.add(doctorProfile2);
    }

    @Test
    void testSearchByWorkSchedule() {
        DoctorProfile doctorProfile = doctorProfileList.get(1);
        List<DoctorProfile> expected = new ArrayList<>();
        expected.add(doctorProfile);
        String workSchedule = "Kamis 10:00-11:30";
        doReturn(expected).when(doctorProfileRepository).findByWorkSchedule(workSchedule);

        List<DoctorProfile> result = searchByWorkSchedule.search(workSchedule);

        assertEquals(expected, result);
        verify(doctorProfileRepository, times(1)).findByWorkSchedule(workSchedule);
    }
}
