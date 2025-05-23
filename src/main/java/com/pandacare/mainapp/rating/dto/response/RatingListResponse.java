package com.pandacare.mainapp.rating.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for sending multiple ratings data to client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingListResponse {
    private Double averageRating;
    private Integer totalRatings;
    private List<RatingResponse> ratings;
}