package com.pandacare.mainapp.rating.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pandacare.mainapp.rating.model.Rating;
import com.pandacare.mainapp.rating.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<Rating> addRating(@RequestBody Rating rating) {
        Rating savedRating = ratingService.addRating(rating);
        return new ResponseEntity<>(savedRating, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Rating> updateRating(@RequestBody Rating rating) {
        Rating updatedRating = ratingService.updateRating(rating);
        return ResponseEntity.ok(updatedRating);
    }

    @DeleteMapping("/{idPacillian}/{idDokter}")
    public ResponseEntity<Rating> deleteRating(@PathVariable String idPacillian, @PathVariable String idDokter) {
        Rating deletedRating = ratingService.deleteRating(idPacillian, idDokter);
        return ResponseEntity.ok(deletedRating);
    }

    @GetMapping("/pacillian/{idPacillian}")
    public ResponseEntity<List<Rating>> getRatingsByOwnerId(@PathVariable String idPacillian) {
        List<Rating> ratings = ratingService.getRatingsByOwnerId(idPacillian);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/dokter/{idDokter}")
    public ResponseEntity<List<Rating>> getRatingsByIdDokter(@PathVariable String idDokter) {
        List<Rating> ratings = ratingService.getRatingsByIdDokter(idDokter);
        return ResponseEntity.ok(ratings);
    }
}