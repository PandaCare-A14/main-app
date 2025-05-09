package com.pandacare.mainapp.doctor_profile.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(LocalDateTime now, int value, String badRequest, String message, String path) {
        this.timestamp = now;
        this.status = value;
        this.error = badRequest;
        this.message = message;
        this.path = path;
    }
}