package com.pandacare.mainapp.doctor_profile.strategy;

import com.pandacare.mainapp.doctor_profile.model.DoctorProfile;
import com.pandacare.mainapp.doctor_profile.strategy.DoctorSearchContext;
import com.pandacare.mainapp.doctor_profile.strategy.SearchStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DoctorSearchContextTest {

    private DoctorSearchContext searchContext;
    private SearchStrategy mockStrategy;
    List<DoctorProfile> doctorProfileList;

    @BeforeEach
    void setUp() {
        searchContext = new DoctorSearchContext();
        mockStrategy = mock(SearchStrategy.class);
        searchContext.setStrategy(mockStrategy);
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
    void testExecuteSearchUsesCorrectStrategyAndReturnsResults() {
        DoctorProfile doctorProfile = doctorProfileList.getFirst();
        List<DoctorProfile> expected = new ArrayList<>();
        expected.add(doctorProfile);
        doReturn(expected).when(mockStrategy).search(doctorProfile.getName());

        List<DoctorProfile> result = searchContext.executeSearch(doctorProfile.getName());

        assertEquals(expected, result);
        verify(mockStrategy, times(1)).search(doctorProfile.getName());
    }
}
