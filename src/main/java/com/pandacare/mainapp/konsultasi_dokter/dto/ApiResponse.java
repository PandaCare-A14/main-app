package com.pandacare.mainapp.konsultasi_dokter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> of(int status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return of(HttpStatus.OK.value(), "Success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return of(HttpStatus.OK.value(), message, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return of(HttpStatus.CREATED.value(), "Created successfully", data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return of(HttpStatus.CREATED.value(), message, data);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return of(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return of(HttpStatus.NOT_FOUND.value(), message, null);
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return of(HttpStatus.CONFLICT.value(), message, null);
    }

    public static <T> ApiResponse<T> internalError(String message) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    }

    public static <T> ApiResponse<T> withStatus(HttpStatus status, String message, T data) {
        return of(status.value(), message, data);
    }
}