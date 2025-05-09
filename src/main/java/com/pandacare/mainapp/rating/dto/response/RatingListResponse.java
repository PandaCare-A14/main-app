package com.pandacare.mainapp.rating.dto.response;

import java.util.List;

/**
 * DTO for sending multiple ratings data to client
 */
public class RatingListResponse {
    private Double averageRating;
    private Integer totalRatings;
    private List<RatingResponse> ratings;

    // Default constructor
    public RatingListResponse() {}

    // Constructor with fields
    public RatingListResponse(Double averageRating, Integer totalRatings, List<RatingResponse> ratings) {
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
        this.ratings = ratings;
    }

    // Getters and Setters
    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public List<RatingResponse> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingResponse> ratings) {
        this.ratings = ratings;
    }
}