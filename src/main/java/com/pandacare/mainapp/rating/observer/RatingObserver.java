package com.pandacare.mainapp.rating.observer;

import com.pandacare.mainapp.rating.model.Rating;

/**
 * Observer interface for Rating events
 */
public interface RatingObserver {
    void onRatingCreated(Rating rating);
    void onRatingUpdated(Rating rating);
    void onRatingDeleted(Rating rating);
}