package com.pandacare.mainapp.rating.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pandacare.mainapp.common.dto.ApiResponse;
import com.pandacare.mainapp.rating.dto.response.RatingListResponse;
import com.pandacare.mainapp.rating.dto.RatingRequest;
import com.pandacare.mainapp.rating.dto.response.RatingResponse;
import com.pandacare.mainapp.rating.service.RatingService;

/**
 * Controller for managing doctor ratings
 *
 * This controller implements REST API with unary RPC pattern for rating operations
 */
@RestController
@RequestMapping("/api/v1")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    /**
     * Get all ratings for a doctor
     * @param idDokter doctor ID
     * @return list of ratings with average and total count
     */
    @GetMapping("/doctors/{idDokter}/ratings")
    public ResponseEntity<ApiResponse<RatingListResponse>> getRatingsByDokter(@PathVariable String idDokter) {
        RatingListResponse ratings = ratingService.getRatingsByDokter(idDokter);
        return ResponseEntity.ok(ApiResponse.success(ratings));
    }

    /**
     * Get all ratings given by a patient
     * @param idPasien patient ID
     * @return list of ratings
     */
    @GetMapping("/patients/{idPasien}/ratings")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> getRatingsByPasien(@PathVariable String idPasien) {
        List<RatingResponse> ratings = ratingService.getRatingsByPasien(idPasien);
        return ResponseEntity.ok(ApiResponse.success(ratings));
    }

    /**
     * Get a specific rating given by a patient to a doctor
     * @param idPasien patient ID
     * @param idDokter doctor ID
     * @return rating response
     */
    @GetMapping("/patients/{idPasien}/doctors/{idDokter}/ratings")
    public ResponseEntity<ApiResponse<RatingResponse>> getRatingByPasienAndDokter(
            @PathVariable String idPasien, @PathVariable String idDokter) {
        RatingResponse rating = ratingService.getRatingByPasienAndDokter(idPasien, idDokter);
        return ResponseEntity.ok(ApiResponse.success(rating));
    }

    /**
     * Add a new rating
     * @param idDokter doctor ID
     * @param userId user ID from header
     * @param request rating data
     * @return created rating
     */
    @PostMapping("/doctors/{idDokter}/ratings")
    public ResponseEntity<ApiResponse<RatingResponse>> addRating(
            @PathVariable String idDokter,
            @RequestHeader("X-User-ID") String userId,
            @RequestBody RatingRequest request) {
        // Manual validation
        try {
            request.validate();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }

        RatingResponse rating = ratingService.addRating(userId, idDokter, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(rating));
    }

    /**
     * Update an existing rating
     * @param idDokter doctor ID
     * @param userId user ID from header
     * @param request rating data
     * @return updated rating
     */
    @PutMapping("/doctors/{idDokter}/ratings")
    public ResponseEntity<ApiResponse<RatingResponse>> updateRating(
            @PathVariable String idDokter,
            @RequestHeader("X-User-ID") String userId,
            @RequestBody RatingRequest request) {
        // Manual validation
        try {
            request.validate();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }

        RatingResponse rating = ratingService.updateRating(userId, idDokter, request);
        return ResponseEntity.ok(ApiResponse.success(rating));
    }

    /**
     * Delete a rating
     * @param idDokter doctor ID
     * @param userId user ID from header
     * @return success message
     */
    @DeleteMapping("/doctors/{idDokter}/ratings")
    public ResponseEntity<ApiResponse<Object>> deleteRating(
            @PathVariable String idDokter,
            @RequestHeader("X-User-ID") String userId) {
        ratingService.deleteRating(userId, idDokter);
        return ResponseEntity.ok(ApiResponse.success("Rating berhasil dihapus"));
    }
}