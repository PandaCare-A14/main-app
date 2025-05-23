package com.pandacare.mainapp.doctor_profile.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ErrorResponseTest {

    @Test
    void testConstructorAndGetters() {
        String message = "Invalid input data";
        ErrorResponse response = new ErrorResponse(message);

        assertEquals(message, response.message());
    }

    @Test
    void testEqualsAndHashCode() {
        ErrorResponse response1 = new ErrorResponse("Error 1");
        ErrorResponse response2 = new ErrorResponse("Error 1");
        ErrorResponse response3 = new ErrorResponse("Error 2");

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
    }
}