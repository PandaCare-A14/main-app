package com.pandacare.mainapp.rating.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RatingListResponseTest {

    private UUID testId1;
    private UUID testId2;
    private UUID testId3;
    private UUID testId4;
    private LocalDateTime testDateTime;
    private RatingResponse testRating1;
    private RatingResponse testRating2;
    private RatingResponse testRating3;
    private List<RatingResponse> testRatings;

    @BeforeEach
    void setUp() {
        testId1 = UUID.randomUUID();
        testId2 = UUID.randomUUID();
        testId3 = UUID.randomUUID();
        testId4 = UUID.randomUUID();
        testDateTime = LocalDateTime.now();

        testRating1 = new RatingResponse(testId1, testId2, testId3, testId4, 5, "Great", testDateTime, testDateTime);
        testRating2 = new RatingResponse(testId2, testId3, testId4, testId1, 4, "Good", testDateTime, testDateTime);
        testRating3 = new RatingResponse(testId3, testId4, testId1, testId2, 3, "Average", testDateTime, testDateTime);
        testRatings = Arrays.asList(testRating1, testRating2, testRating3);
    }

    @Nested
    class ConstructorTests {

        @Test
        void testNoArgsConstructor() {
            RatingListResponse response = new RatingListResponse();

            assertNull(response.getAverageRating());
            assertNull(response.getTotalRatings());
            assertNull(response.getRatings());
        }

        @Test
        void testAllArgsConstructor() {
            RatingListResponse response = new RatingListResponse(4.5, 10, testRatings);

            assertEquals(4.5, response.getAverageRating());
            assertEquals(10, response.getTotalRatings());
            assertEquals(testRatings, response.getRatings());
            assertEquals(3, response.getRatings().size());
        }

        @Test
        void testAllArgsConstructorWithNullValues() {
            RatingListResponse response = new RatingListResponse(null, null, null);

            assertNull(response.getAverageRating());
            assertNull(response.getTotalRatings());
            assertNull(response.getRatings());
        }

        @Test
        void testAllArgsConstructorWithEmptyList() {
            List<RatingResponse> emptyList = Collections.emptyList();
            RatingListResponse response = new RatingListResponse(0.0, 0, emptyList);

            assertEquals(0.0, response.getAverageRating());
            assertEquals(0, response.getTotalRatings());
            assertEquals(emptyList, response.getRatings());
            assertEquals(0, response.getRatings().size());
        }

        @Test
        void testAllArgsConstructorWithSingleRating() {
            List<RatingResponse> singleRating = Arrays.asList(testRating1);
            RatingListResponse response = new RatingListResponse(5.0, 1, singleRating);

            assertEquals(5.0, response.getAverageRating());
            assertEquals(1, response.getTotalRatings());
            assertEquals(singleRating, response.getRatings());
            assertEquals(1, response.getRatings().size());
        }
    }

    @Nested
    class GetterSetterTests {

        @Test
        void testSetAndGetAverageRating() {
            RatingListResponse response = new RatingListResponse();

            response.setAverageRating(4.2);
            assertEquals(4.2, response.getAverageRating());

            response.setAverageRating(0.0);
            assertEquals(0.0, response.getAverageRating());

            response.setAverageRating(5.0);
            assertEquals(5.0, response.getAverageRating());
        }

        @Test
        void testSetAndGetTotalRatings() {
            RatingListResponse response = new RatingListResponse();

            response.setTotalRatings(25);
            assertEquals(25, response.getTotalRatings());

            response.setTotalRatings(0);
            assertEquals(0, response.getTotalRatings());

            response.setTotalRatings(1000);
            assertEquals(1000, response.getTotalRatings());
        }

        @Test
        void testSetAndGetRatings() {
            RatingListResponse response = new RatingListResponse();

            response.setRatings(testRatings);
            assertEquals(testRatings, response.getRatings());
            assertEquals(3, response.getRatings().size());

            List<RatingResponse> emptyList = Collections.emptyList();
            response.setRatings(emptyList);
            assertEquals(emptyList, response.getRatings());
            assertEquals(0, response.getRatings().size());
        }

        @Test
        void testSettersWithNullValues() {
            RatingListResponse response = new RatingListResponse();

            response.setAverageRating(null);
            response.setTotalRatings(null);
            response.setRatings(null);

            assertNull(response.getAverageRating());
            assertNull(response.getTotalRatings());
            assertNull(response.getRatings());
        }

        @Test
        void testGettersWithNullValues() {
            RatingListResponse response = new RatingListResponse(null, null, null);

            assertNull(response.getAverageRating());
            assertNull(response.getTotalRatings());
            assertNull(response.getRatings());
        }

        @Test
        void testSettersChaining() {
            RatingListResponse response = new RatingListResponse();

            response.setAverageRating(4.0);
            response.setTotalRatings(8);
            response.setRatings(testRatings);

            assertEquals(4.0, response.getAverageRating());
            assertEquals(8, response.getTotalRatings());
            assertEquals(testRatings, response.getRatings());
        }
    }

    @Nested
    class EqualsAndHashCodeTests {

        @Test
        void testEquals() {
            RatingListResponse response1 = new RatingListResponse(4.5, 10, testRatings);
            RatingListResponse response2 = new RatingListResponse(4.5, 10, testRatings);
            RatingListResponse response3 = new RatingListResponse(3.5, 5, testRatings);

            assertEquals(response1, response2);
            assertNotEquals(response1, response3);
            assertNotEquals(response1, null);
            assertNotEquals(response1, new Object());
        }

        @Test
        void testEqualsWithSameReference() {
            RatingListResponse response = new RatingListResponse(4.5, 10, testRatings);
            assertEquals(response, response);
        }

        @Test
        void testEqualsWithDifferentAverageRating() {
            RatingListResponse response1 = new RatingListResponse(4.0, 8, testRatings);
            RatingListResponse response2 = new RatingListResponse(3.0, 8, testRatings);

            assertNotEquals(response1, response2);
        }

        @Test
        void testEqualsWithDifferentTotalRatings() {
            RatingListResponse response1 = new RatingListResponse(4.0, 8, testRatings);
            RatingListResponse response2 = new RatingListResponse(4.0, 10, testRatings);

            assertNotEquals(response1, response2);
        }

        @Test
        void testEqualsWithDifferentRatings() {
            List<RatingResponse> differentRatings = Arrays.asList(testRating1);
            RatingListResponse response1 = new RatingListResponse(4.0, 8, testRatings);
            RatingListResponse response2 = new RatingListResponse(4.0, 8, differentRatings);

            assertNotEquals(response1, response2);
        }

        @Test
        void testEqualsWithNullFields() {
            RatingListResponse response1 = new RatingListResponse(null, null, null);
            RatingListResponse response2 = new RatingListResponse(null, null, null);
            RatingListResponse response3 = new RatingListResponse(4.0, 8, testRatings);

            assertEquals(response1, response2);
            assertNotEquals(response1, response3);
        }

        @Test
        void testHashCode() {
            RatingListResponse response1 = new RatingListResponse(4.0, 8, testRatings);
            RatingListResponse response2 = new RatingListResponse(4.0, 8, testRatings);

            assertEquals(response1.hashCode(), response2.hashCode());
        }

        @Test
        void testHashCodeConsistency() {
            RatingListResponse response = new RatingListResponse(4.0, 8, testRatings);
            int hashCode1 = response.hashCode();
            int hashCode2 = response.hashCode();

            assertEquals(hashCode1, hashCode2);
        }

        @Test
        void testHashCodeWithNullFields() {
            RatingListResponse response1 = new RatingListResponse(null, null, null);
            RatingListResponse response2 = new RatingListResponse(null, null, null);

            assertEquals(response1.hashCode(), response2.hashCode());
        }
    }

    @Nested
    class CanEqualTests {

        @Test
        void testCanEqual() {
            RatingListResponse response1 = new RatingListResponse();
            RatingListResponse response2 = new RatingListResponse();
            Object other = new Object();

            assertTrue(response1.canEqual(response2));
            assertFalse(response1.canEqual(other));
            assertFalse(response1.canEqual(null));
        }

        @Test
        void testCanEqualWithSameInstance() {
            RatingListResponse response = new RatingListResponse();
            assertTrue(response.canEqual(response));
        }

        @Test
        void testCanEqualWithDifferentTypes() {
            RatingListResponse response = new RatingListResponse();
            String string = "test";
            Integer number = 123;

            assertFalse(response.canEqual(string));
            assertFalse(response.canEqual(number));
        }
    }

    @Nested
    class ToStringTests {

        @Test
        void testToString() {
            RatingListResponse response = new RatingListResponse(3.8, 15, testRatings);
            String result = response.toString();

            assertTrue(result.contains("RatingListResponse"));
            assertTrue(result.contains("averageRating=3.8"));
            assertTrue(result.contains("totalRatings=15"));
            assertTrue(result.contains("ratings="));
        }

        @Test
        void testToStringWithNullValues() {
            RatingListResponse response = new RatingListResponse(null, null, null);
            String result = response.toString();

            assertTrue(result.contains("RatingListResponse"));
            assertTrue(result.contains("averageRating=null"));
            assertTrue(result.contains("totalRatings=null"));
            assertTrue(result.contains("ratings=null"));
        }

        @Test
        void testToStringWithEmptyRatings() {
            List<RatingResponse> emptyRatings = Collections.emptyList();
            RatingListResponse response = new RatingListResponse(0.0, 0, emptyRatings);
            String result = response.toString();

            assertTrue(result.contains("RatingListResponse"));
            assertTrue(result.contains("averageRating=0.0"));
            assertTrue(result.contains("totalRatings=0"));
            assertTrue(result.contains("ratings=[]"));
        }

        @Test
        void testToStringNotNull() {
            RatingListResponse response = new RatingListResponse();
            String result = response.toString();

            assertNotNull(result);
            assertTrue(result.length() > 0);
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void testWithExtremeValues() {
            RatingListResponse response = new RatingListResponse();

            response.setAverageRating(Double.MAX_VALUE);
            response.setTotalRatings(Integer.MAX_VALUE);

            assertEquals(Double.MAX_VALUE, response.getAverageRating());
            assertEquals(Integer.MAX_VALUE, response.getTotalRatings());
        }

        @Test
        void testWithMinimumValues() {
            RatingListResponse response = new RatingListResponse();

            response.setAverageRating(Double.MIN_VALUE);
            response.setTotalRatings(Integer.MIN_VALUE);

            assertEquals(Double.MIN_VALUE, response.getAverageRating());
            assertEquals(Integer.MIN_VALUE, response.getTotalRatings());
        }

        @Test
        void testWithZeroValues() {
            RatingListResponse response = new RatingListResponse(0.0, 0, Collections.emptyList());

            assertEquals(0.0, response.getAverageRating());
            assertEquals(0, response.getTotalRatings());
            assertEquals(Collections.emptyList(), response.getRatings());
        }

        @Test
        void testWithNegativeValues() {
            RatingListResponse response = new RatingListResponse();

            response.setAverageRating(-1.0);
            response.setTotalRatings(-5);

            assertEquals(-1.0, response.getAverageRating());
            assertEquals(-5, response.getTotalRatings());
        }

        @Test
        void testWithLargeRatingsList() {
            List<RatingResponse> largeList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                largeList.add(new RatingResponse(
                        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        (i % 5) + 1, "Rating " + i, testDateTime, testDateTime
                ));
            }

            RatingListResponse response = new RatingListResponse(3.5, 1000, largeList);

            assertEquals(3.5, response.getAverageRating());
            assertEquals(1000, response.getTotalRatings());
            assertEquals(1000, response.getRatings().size());
        }

        @Test
        void testMutableRatingsList() {
            List<RatingResponse> mutableList = new ArrayList<>(testRatings);
            RatingListResponse response = new RatingListResponse(4.0, 3, mutableList);

            assertEquals(3, response.getRatings().size());

            mutableList.add(new RatingResponse(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                    2, "New Rating", testDateTime, testDateTime
            ));

            assertEquals(4, response.getRatings().size());
        }

        @Test
        void testImmutableOperations() {
            RatingListResponse response = new RatingListResponse(4.0, 3, testRatings);

            assertDoesNotThrow(() -> {
                response.getAverageRating();
                response.getTotalRatings();
                response.getRatings();
            });
        }
    }

    @Nested
    class ValidationTests {

        @Test
        void testAverageRatingPrecision() {
            RatingListResponse response = new RatingListResponse();

            response.setAverageRating(4.123456789);
            assertEquals(4.123456789, response.getAverageRating());
        }

        @Test
        void testSpecialDoubleValues() {
            RatingListResponse response = new RatingListResponse();

            response.setAverageRating(Double.NaN);
            assertTrue(Double.isNaN(response.getAverageRating()));

            response.setAverageRating(Double.POSITIVE_INFINITY);
            assertEquals(Double.POSITIVE_INFINITY, response.getAverageRating());

            response.setAverageRating(Double.NEGATIVE_INFINITY);
            assertEquals(Double.NEGATIVE_INFINITY, response.getAverageRating());
        }

        @Test
        void testFieldIndependence() {
            RatingListResponse response = new RatingListResponse();

            response.setAverageRating(4.5);
            assertNull(response.getTotalRatings());
            assertNull(response.getRatings());

            response.setTotalRatings(10);
            assertEquals(4.5, response.getAverageRating());
            assertNull(response.getRatings());

            response.setRatings(testRatings);
            assertEquals(4.5, response.getAverageRating());
            assertEquals(10, response.getTotalRatings());
        }

        @Test
        void testOverwritingValues() {
            RatingListResponse response = new RatingListResponse(1.0, 1, Collections.singletonList(testRating1));

            response.setAverageRating(5.0);
            response.setTotalRatings(100);
            response.setRatings(testRatings);

            assertEquals(5.0, response.getAverageRating());
            assertEquals(100, response.getTotalRatings());
            assertEquals(testRatings, response.getRatings());
            assertEquals(3, response.getRatings().size());
        }
    }
}