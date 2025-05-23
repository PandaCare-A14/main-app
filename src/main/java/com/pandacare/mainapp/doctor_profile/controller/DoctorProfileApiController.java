package com.pandacare.mainapp.doctor_profile.controller;

import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.DoctorProfileListResponse;
import com.pandacare.mainapp.doctor_profile.dto.response.ErrorResponse;
import com.pandacare.mainapp.doctor_profile.facade.DoctorFacade;
import com.pandacare.mainapp.doctor_profile.service.DoctorProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/doctors")
@EnableAsync
public class DoctorProfileApiController {

    private final DoctorProfileService doctorProfileService;
    private final DoctorFacade doctorFacade;
    private static final long ASYNC_TIMEOUT = 5000; // 5 seconds timeout

    public DoctorProfileApiController(DoctorProfileService doctorProfileService, DoctorFacade doctorFacade) {
        this.doctorProfileService = doctorProfileService;
        this.doctorFacade = doctorFacade;
    }

@GetMapping("/{doctorId}/actions")
public DeferredResult<ResponseEntity<DoctorProfileResponse>> getDoctorWithActions(
        @PathVariable UUID doctorId,
        @RequestParam UUID patientId) {

    DeferredResult<ResponseEntity<DoctorProfileResponse>> deferredResult = new DeferredResult<>(ASYNC_TIMEOUT);

    doctorFacade.getDoctorProfileWithActions(doctorId, patientId)
            .<ResponseEntity<DoctorProfileResponse>>thenApply(response ->
                    response != null ?
                            ResponseEntity.ok(response) :
                            ResponseEntity.<DoctorProfileResponse>notFound().build())
            .exceptionally(ex ->
                    ResponseEntity.<DoctorProfileResponse>status(HttpStatus.INTERNAL_SERVER_ERROR).build())
            .thenAccept(deferredResult::setResult);

    deferredResult.onTimeout(() ->
            deferredResult.setErrorResult(
                    ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                            .body(new ErrorResponse("Request timeout occurred"))));

    return deferredResult;
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
    public DeferredResult<ResponseEntity<?>> getDoctorProfile(@PathVariable UUID id) {
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

    @GetMapping("/search/by-name")
    public DeferredResult<ResponseEntity<?>> searchDoctorsByName(
            @RequestParam String name) {

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(ASYNC_TIMEOUT);

        doctorProfileService.findByName(name)
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

    @GetMapping("/search/by-speciality")
    public DeferredResult<ResponseEntity<?>> searchDoctorsBySpeciality(
            @RequestParam String speciality) {

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(ASYNC_TIMEOUT);

        doctorProfileService.findBySpeciality(speciality)
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

    @GetMapping("/search/by-schedule")
    public DeferredResult<ResponseEntity<?>> searchDoctorsBySchedule(
            @RequestParam String day,
            @RequestParam String startTime,
            @RequestParam String endTime) {

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(ASYNC_TIMEOUT);

        String workSchedule = String.format("%s %s-%s", day, startTime, endTime);

        doctorProfileService.findByWorkSchedule(workSchedule)
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