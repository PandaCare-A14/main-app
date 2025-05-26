package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.ErrorResponse;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@EnableAsync
public class DoctorProfileApiController {

    private final DoctorProfileService doctorProfileService;
    private static final long ASYNC_TIMEOUT = 5000; // 5 seconds timeout

    public DoctorProfileApiController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @GetMapping
    public DeferredResult<ResponseEntity<DoctorProfileListResponse>> getAllDoctorProfiles() {
        DeferredResult<ResponseEntity<DoctorProfileListResponse>> deferredResult = new DeferredResult<>(ASYNC_TIMEOUT);

        doctorProfileService.findAll()
                .<ResponseEntity<DoctorProfileListResponse>>thenApply(response ->
                        response != null ?
                                ResponseEntity.ok(response) :
                                ResponseEntity.<DoctorProfileListResponse>notFound().build())
                .exceptionally(ex ->
                        ResponseEntity.<DoctorProfileListResponse>status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .thenAccept(deferredResult::setResult);

        deferredResult.onTimeout(() ->
                deferredResult.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body(new ErrorResponse("Request timeout occurred"))));

        return deferredResult;
    }

    @GetMapping("/{id}")
    public DeferredResult<ResponseEntity<?>> getDoctorProfile(@PathVariable("id") UUID id) {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(ASYNC_TIMEOUT);

        doctorProfileService.findById(id)
                .<ResponseEntity<?>>thenApply(response ->
                        response != null ?
                                ResponseEntity.ok(response) :
                                ResponseEntity.notFound().build())
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.badRequest()
                                .body(new ErrorResponse(ex.getCause().getMessage()));
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                })
                .thenAccept(deferredResult::setResult);

        deferredResult.onTimeout(() ->
                deferredResult.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body(new ErrorResponse("Request timeout occurred"))));

        return deferredResult;
    }

    @GetMapping("/search")
    public DeferredResult<ResponseEntity<?>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String speciality,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(ASYNC_TIMEOUT);

        doctorProfileService.searchByCriteria(name, speciality, day, startTime, endTime)
                .<ResponseEntity<?>>thenApply(response ->
                        response != null ?
                                ResponseEntity.ok(response) :
                                ResponseEntity.notFound().build())
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.badRequest()
                                .body(new ErrorResponse(ex.getCause().getMessage()));
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                })
                .thenAccept(deferredResult::setResult);

        deferredResult.onTimeout(() ->
                deferredResult.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body(new ErrorResponse("Request timeout occurred"))));

        return deferredResult;
    }
}