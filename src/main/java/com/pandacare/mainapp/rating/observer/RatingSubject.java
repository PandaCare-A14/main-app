package com.pandacare.mainapp.rating.observer;

import com.pandacare.mainapp.rating.model.Rating;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject class for Rating events in Observer pattern
 */
public class RatingSubject {

    private static RatingSubject instance;
    private final List<RatingObserver> observers = new ArrayList<>();

    private RatingSubject() {
        // Private constructor for Singleton pattern
    }

    public static synchronized RatingSubject getInstance() {
        if (instance == null) {
            instance = new RatingSubject();
        }
        return instance;
    }

    public void attach(RatingObserver observer) {
        observers.add(observer);
    }

    public void detach(RatingObserver observer) {
        observers.remove(observer);
    }

    public void notifyRatingCreated(Rating rating) {
        for (RatingObserver observer : observers) {
            observer.onRatingCreated(rating);
        }
    }

    public void notifyRatingUpdated(Rating rating) {
        for (RatingObserver observer : observers) {
            observer.onRatingUpdated(rating);
        }
    }

    public void notifyRatingDeleted(Rating rating) {
        for (RatingObserver observer : observers) {
            observer.onRatingDeleted(rating);
        }
    }
}