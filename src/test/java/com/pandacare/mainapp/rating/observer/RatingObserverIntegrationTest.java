package com.pandacare.mainapp.rating.observer;

import com.pandacare.mainapp.rating.model.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests for Observer pattern in Rating module
 */
@ActiveProfiles("test")
class RatingObserverIntegrationTest {

    private RatingSubject ratingSubject;
    private TestRatingObserver testObserver;
    private Rating testRating;

    @BeforeEach
    void setUp() {
        // Use actual RatingSubject singleton for integration test
        ratingSubject = RatingSubject.getInstance();

        // Create test observer
        testObserver = new TestRatingObserver();

        // Create test rating
        testRating = new Rating();
        testRating.setId("test-rating");
        testRating.setIdDokter("dr001");
        testRating.setIdPasien("p001");
        testRating.setIdJadwalKonsultasi("cons001");
        testRating.setRatingScore(5);
        testRating.setUlasan("Test review");
        testRating.setCreatedAt(LocalDateTime.now());
        testRating.setUpdatedAt(LocalDateTime.now());

        // Attach observer
        ratingSubject.attach(testObserver);
    }

    @Test
    @DisplayName("Should notify observer when rating is created")
    void shouldNotifyObserver_WhenRatingIsCreated() {
        // Act
        ratingSubject.notifyRatingCreated(testRating);

        // Assert
        assertTrue(testObserver.wasRatingCreatedCalled());
        assertEquals(testRating, testObserver.getLastCreatedRating());
    }

    @Test
    @DisplayName("Should notify observer when rating is updated")
    void shouldNotifyObserver_WhenRatingIsUpdated() {
        // Act
        ratingSubject.notifyRatingUpdated(testRating);

        // Assert
        assertTrue(testObserver.wasRatingUpdatedCalled());
        assertEquals(testRating, testObserver.getLastUpdatedRating());
    }

    @Test
    @DisplayName("Should notify observer when rating is deleted")
    void shouldNotifyObserver_WhenRatingIsDeleted() {
        // Act
        ratingSubject.notifyRatingDeleted(testRating);

        // Assert
        assertTrue(testObserver.wasRatingDeletedCalled());
        assertEquals(testRating, testObserver.getLastDeletedRating());
    }

    @Test
    @DisplayName("Should notify multiple observers")
    void shouldNotifyMultipleObservers() {
        // Arrange
        TestRatingObserver secondObserver = new TestRatingObserver();
        ratingSubject.attach(secondObserver);

        // Act
        ratingSubject.notifyRatingCreated(testRating);

        // Assert
        assertTrue(testObserver.wasRatingCreatedCalled());
        assertTrue(secondObserver.wasRatingCreatedCalled());

        // Cleanup
        ratingSubject.detach(secondObserver);
    }

    @Test
    @DisplayName("Should detach observer correctly")
    void shouldDetachObserver_Correctly() {
        // Arrange
        ratingSubject.detach(testObserver);

        // Act
        ratingSubject.notifyRatingCreated(testRating);

        // Assert
        assertFalse(testObserver.wasRatingCreatedCalled());
    }

    /**
     * Test implementation of RatingObserver for testing purposes
     */
    private static class TestRatingObserver implements RatingObserver {
        private final AtomicBoolean ratingCreatedCalled = new AtomicBoolean(false);
        private final AtomicBoolean ratingUpdatedCalled = new AtomicBoolean(false);
        private final AtomicBoolean ratingDeletedCalled = new AtomicBoolean(false);

        private Rating lastCreatedRating;
        private Rating lastUpdatedRating;
        private Rating lastDeletedRating;

        @Override
        public void onRatingCreated(Rating rating) {
            ratingCreatedCalled.set(true);
            lastCreatedRating = rating;
        }

        @Override
        public void onRatingUpdated(Rating rating) {
            ratingUpdatedCalled.set(true);
            lastUpdatedRating = rating;
        }

        @Override
        public void onRatingDeleted(Rating rating) {
            ratingDeletedCalled.set(true);
            lastDeletedRating = rating;
        }

        public boolean wasRatingCreatedCalled() {
            return ratingCreatedCalled.get();
        }

        public boolean wasRatingUpdatedCalled() {
            return ratingUpdatedCalled.get();
        }

        public boolean wasRatingDeletedCalled() {
            return ratingDeletedCalled.get();
        }

        public Rating getLastCreatedRating() {
            return lastCreatedRating;
        }

        public Rating getLastUpdatedRating() {
            return lastUpdatedRating;
        }

        public Rating getLastDeletedRating() {
            return lastDeletedRating;
        }
    }
}