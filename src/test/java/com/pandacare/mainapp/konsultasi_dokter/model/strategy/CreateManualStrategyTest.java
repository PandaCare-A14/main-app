package com.pandacare.mainapp.konsultasi_dokter.model.strategy;

import com.pandacare.mainapp.konsultasi_dokter.model.CaregiverSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CreateManualStrategyTest {
    private CreateManualStrategy strategy;
    private final String DOCTOR_ID = "DOC12345";
    private final DayOfWeek TEST_DAY = DayOfWeek.MONDAY;
    private final LocalTime START_TIME = LocalTime.of(9, 0);
    private final LocalTime END_TIME = LocalTime.of(10, 0);

    @BeforeEach
    void setUp() {
        strategy = new CreateManualStrategy();
    }

    @Test
    void testCreateSuccess() {
        CaregiverSchedule schedule = strategy.create(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME);

        assertNotNull(schedule);
        assertEquals(DOCTOR_ID, schedule.getIdCaregiver());
        assertEquals(TEST_DAY, schedule.getDay());
        assertEquals(START_TIME, schedule.getStartTime());
        assertEquals(END_TIME, schedule.getEndTime());
        assertEquals("AVAILABLE", schedule.getStatusCaregiver());
        assertFalse(schedule.isChangeSchedule());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidParameters")
    void testCreateWithInvalidParameters(String idCaregiver, DayOfWeek day,
                                          LocalTime startTime, LocalTime endTime,
                                          String expectedErrorMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.create(idCaregiver, day, startTime, endTime));

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    private static Stream<Arguments> provideInvalidParameters() {
        return Stream.of(
                Arguments.of(null, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                        "Field can't be empty."),
                Arguments.of("", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                        "Field can't be empty."),
                Arguments.of("   ", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                        "Field can't be empty."),
                Arguments.of("DOC12345", null, LocalTime.of(9, 0), LocalTime.of(10, 0),
                        "Field can't be empty."),
                Arguments.of("DOC12345", DayOfWeek.MONDAY, null, LocalTime.of(10, 0),
                        "Field can't be empty."),
                Arguments.of("DOC12345", DayOfWeek.MONDAY, LocalTime.of(9, 0), null,
                        "Field can't be empty.")
        );
    }

    @Test
    void testCreateCreatesNewStateInstance() {
        CaregiverSchedule schedule1 = strategy.create(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME);
        CaregiverSchedule schedule2 = strategy.create(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME);
        assertNotSame(schedule1.getCurrentState(), schedule2.getCurrentState());
    }

    @Test
    void testCreateSetsChangeScheduleFalse() {
        CaregiverSchedule schedule = new CaregiverSchedule();
        schedule.setChangeSchedule(true);
        CaregiverSchedule newSchedule = strategy.create(DOCTOR_ID, TEST_DAY, START_TIME, END_TIME);
        assertFalse(newSchedule.isChangeSchedule());
    }
}