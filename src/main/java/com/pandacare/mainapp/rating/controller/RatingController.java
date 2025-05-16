package com.pandacare.mainapp.rating.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "/doctors/{idDokter}/ratings", method = RequestMethod.GET)
    public ResponseEntity getRatingsByDokter(@PathVariable String idDokter) {
        ResponseEntity responseEntity = null;
        try {
            RatingListResponse ratings = ratingService.getRatingsByDokter(idDokter);
            responseEntity = ResponseEntity.ok(ratings);
        } catch (Exception e) {
            System.out.println("Error getting ratings for doctor: " + e.getMessage());
            responseEntity = ResponseEntity.badRequest().body(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    /**
     * Get all ratings given by a patient
     * @param idPasien patient ID
     * @return list of ratings
     */
    @RequestMapping(value = "/patients/{idPasien}/ratings", method = RequestMethod.GET)
    public ResponseEntity getRatingsByPasien(@PathVariable String idPasien) {
        ResponseEntity responseEntity = null;
        try {
            List<RatingResponse> ratings = ratingService.getRatingsByPasien(idPasien);
            responseEntity = ResponseEntity.ok(ratings);
        } catch (Exception e) {
            System.out.println("Error getting ratings by patient: " + e.getMessage());
            responseEntity = ResponseEntity.badRequest().body(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    /**
     * Get a specific rating given by a patient to a doctor
     * @param idPasien patient ID
     * @param idDokter doctor ID
     * @return rating response
     */
    @RequestMapping(value = "/patients/{idPasien}/doctors/{idDokter}/ratings", method = RequestMethod.GET)
    public ResponseEntity getRatingByPasienAndDokter(@PathVariable String idPasien, @PathVariable String idDokter) {
        ResponseEntity responseEntity = null;
        try {
            RatingResponse rating = ratingService.getRatingByPasienAndDokter(idPasien, idDokter);
            responseEntity = ResponseEntity.ok(rating);
        } catch (Exception e) {
            System.out.println("Error getting specific rating: " + e.getMessage());
            responseEntity = ResponseEntity.badRequest().body(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    /**
     * Add a new rating
     * @param idDokter doctor ID
     * @param userId user ID from header
     * @param request rating data
     * @return created rating
     */
    @RequestMapping(value = "/doctors/{idDokter}/ratings", method = RequestMethod.POST)
    public ResponseEntity addRating(@PathVariable String idDokter,
                                    @RequestHeader("X-User-ID") String userId,
                                    @RequestBody RatingRequest request) {
        ResponseEntity responseEntity = null;
        try {
            // Manual validation
            request.validate();

            RatingResponse rating = ratingService.addRating(userId, idDokter, request);
            responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(rating);
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
            responseEntity = ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error adding rating: " + e.getMessage());
            responseEntity = ResponseEntity.badRequest().body(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    /**
     * Update an existing rating
     * @param idDokter doctor ID
     * @param userId user ID from header
     * @param request rating data
     * @return updated rating
     */
    @RequestMapping(value = "/doctors/{idDokter}/ratings", method = RequestMethod.PUT)
    public ResponseEntity updateRating(@PathVariable String idDokter,
                                       @RequestHeader("X-User-ID") String userId,
                                       @RequestBody RatingRequest request) {
        ResponseEntity responseEntity = null;
        try {
            // Manual validation
            request.validate();

            RatingResponse rating = ratingService.updateRating(userId, idDokter, request);
            responseEntity = ResponseEntity.ok(rating);
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
            responseEntity = ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error updating rating: " + e.getMessage());
            responseEntity = ResponseEntity.badRequest().body(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    /**
     * Delete a rating
     * @param idDokter doctor ID
     * @param userId user ID from header
     * @return success message
     */
    @RequestMapping(value = "/doctors/{idDokter}/ratings", method = RequestMethod.DELETE)
    public ResponseEntity deleteRating(@PathVariable String idDokter,
                                       @RequestHeader("X-User-ID") String userId) {
        ResponseEntity responseEntity = null;
        try {
            ratingService.deleteRating(userId, idDokter);
            responseEntity = ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error deleting rating: " + e.getMessage());
            responseEntity = ResponseEntity.badRequest().body(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}