package com.pandacare.mainapp.rating.observer;

import com.pandacare.mainapp.rating.model.Rating;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Concrete observer for logging rating events
 */
@Component
@Slf4j
public class RatingLogObserver implements RatingObserver {

    public RatingLogObserver() {
        RatingSubject.getInstance().attach(this);
    }

    @Override
    public void onRatingCreated(Rating rating) {
        log.info("Rating created: ID={}, DokterID={}, PasienID={}, Score={}",
                rating.getId(), rating.getIdDokter(), rating.getIdPasien(), rating.getRatingScore());
    }

    @Override
    public void onRatingUpdated(Rating rating) {
        log.info("Rating updated: ID={}, DokterID={}, PasienID={}, Score={}",
                rating.getId(), rating.getIdDokter(), rating.getIdPasien(), rating.getRatingScore());
    }

    @Override
    public void onRatingDeleted(Rating rating) {
        log.info("Rating deleted: ID={}, DokterID={}, PasienID={}",
                rating.getId(), rating.getIdDokter(), rating.getIdPasien());
    }
}