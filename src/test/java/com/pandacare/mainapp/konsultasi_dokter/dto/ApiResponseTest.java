package com.pandacare.mainapp.konsultasi_dokter.dto;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ApiResponseTest {

    @Test
    void testNoArgsConstructor() {
        ApiResponse<String> response = new ApiResponse<>();

        assertEquals(0, response.getStatus());
        assertNull(response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testAllArgsConstructor() {
        String testData = "test data";
        ApiResponse<String> response = new ApiResponse<>(200, "Success", testData);

        assertEquals(200, response.getStatus());
        assertEquals("Success", response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    void testSettersAndGetters() {
        ApiResponse<String> response = new ApiResponse<>();

        response.setStatus(201);
        response.setMessage("Created");
        response.setData("new data");

        assertEquals(201, response.getStatus());
        assertEquals("Created", response.getMessage());
        assertEquals("new data", response.getData());
    }

    @Test
    void testOfMethod() {
        String testData = "test";
        ApiResponse<String> response = ApiResponse.of(404, "Not Found", testData);

        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    void testSuccessWithData() {
        String testData = "success data";
        ApiResponse<String> response = ApiResponse.success(testData);

        assertEquals(200, response.getStatus());
        assertEquals("Success", response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    void testSuccessWithMessageAndData() {
        String testData = "custom data";
        ApiResponse<String> response = ApiResponse.success("Custom success", testData);

        assertEquals(200, response.getStatus());
        assertEquals("Custom success", response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    void testCreatedWithData() {
        String testData = "created data";
        ApiResponse<String> response = ApiResponse.created(testData);

        assertEquals(201, response.getStatus());
        assertEquals("Created successfully", response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    void testCreatedWithMessageAndData() {
        String testData = "custom created data";
        ApiResponse<String> response = ApiResponse.created("Custom created", testData);

        assertEquals(201, response.getStatus());
        assertEquals("Custom created", response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    void testBadRequest() {
        ApiResponse<String> response = ApiResponse.badRequest("Invalid input");

        assertEquals(400, response.getStatus());
        assertEquals("Invalid input", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testNotFound() {
        ApiResponse<String> response = ApiResponse.notFound("Resource not found");

        assertEquals(404, response.getStatus());
        assertEquals("Resource not found", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testConflict() {
        ApiResponse<String> response = ApiResponse.conflict("Resource already exists");

        assertEquals(409, response.getStatus());
        assertEquals("Resource already exists", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testInternalError() {
        ApiResponse<String> response = ApiResponse.internalError("Server error");

        assertEquals(500, response.getStatus());
        assertEquals("Server error", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testWithStatus() {
        String testData = "status data";
        ApiResponse<String> response = ApiResponse.withStatus(HttpStatus.ACCEPTED, "Accepted", testData);

        assertEquals(202, response.getStatus());
        assertEquals("Accepted", response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    void testWithStatusNoData() {
        ApiResponse<String> response = ApiResponse.withStatus(HttpStatus.UNAUTHORIZED, "Unauthorized", null);

        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testGenericTypes() {
        Integer intData = 42;
        ApiResponse<Integer> intResponse = ApiResponse.success(intData);

        assertEquals(200, intResponse.getStatus());
        assertEquals("Success", intResponse.getMessage());
        assertEquals(intData, intResponse.getData());

        Boolean boolData = true;
        ApiResponse<Boolean> boolResponse = ApiResponse.created(boolData);

        assertEquals(201, boolResponse.getStatus());
        assertEquals("Created successfully", boolResponse.getMessage());
        assertEquals(boolData, boolResponse.getData());
    }

    @Test
    void testNullData() {
        ApiResponse<String> response = ApiResponse.success((String) null);

        assertEquals(200, response.getStatus());
        assertEquals("Success", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testEmptyMessage() {
        ApiResponse<String> response = ApiResponse.of(200, "", "data");

        assertEquals(200, response.getStatus());
        assertEquals("", response.getMessage());
        assertEquals("data", response.getData());
    }

    @Test
    void testNullMessage() {
        ApiResponse<String> response = ApiResponse.of(200, null, "data");

        assertEquals(200, response.getStatus());
        assertNull(response.getMessage());
        assertEquals("data", response.getData());
    }

    @Test
    void testAllHttpStatusCodes() {
        ApiResponse<String> okResponse = ApiResponse.withStatus(HttpStatus.OK, "OK", "data");
        assertEquals(200, okResponse.getStatus());

        ApiResponse<String> createdResponse = ApiResponse.withStatus(HttpStatus.CREATED, "Created", "data");
        assertEquals(201, createdResponse.getStatus());

        ApiResponse<String> badRequestResponse = ApiResponse.withStatus(HttpStatus.BAD_REQUEST, "Bad Request", null);
        assertEquals(400, badRequestResponse.getStatus());

        ApiResponse<String> unauthorizedResponse = ApiResponse.withStatus(HttpStatus.UNAUTHORIZED, "Unauthorized", null);
        assertEquals(401, unauthorizedResponse.getStatus());

        ApiResponse<String> forbiddenResponse = ApiResponse.withStatus(HttpStatus.FORBIDDEN, "Forbidden", null);
        assertEquals(403, forbiddenResponse.getStatus());

        ApiResponse<String> notFoundResponse = ApiResponse.withStatus(HttpStatus.NOT_FOUND, "Not Found", null);
        assertEquals(404, notFoundResponse.getStatus());

        ApiResponse<String> conflictResponse = ApiResponse.withStatus(HttpStatus.CONFLICT, "Conflict", null);
        assertEquals(409, conflictResponse.getStatus());

        ApiResponse<String> serverErrorResponse = ApiResponse.withStatus(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null);
        assertEquals(500, serverErrorResponse.getStatus());
    }

    @Test
    void testEquals() {
        ApiResponse<String> response1 = new ApiResponse<>(200, "Success", "data");
        ApiResponse<String> response2 = new ApiResponse<>(200, "Success", "data");

        assertEquals(response1, response2);
    }

    @Test
    void testHashCode() {
        ApiResponse<String> response1 = new ApiResponse<>(200, "Success", "data");
        ApiResponse<String> response2 = new ApiResponse<>(200, "Success", "data");

        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        ApiResponse<String> response = new ApiResponse<>(200, "Success", "test data");
        String result = response.toString();

        assertNotNull(result);
        assertTrue(result.contains("200"));
        assertTrue(result.contains("Success"));
        assertTrue(result.contains("test data"));
    }

    @Test
    void testCanEqual() {
        ApiResponse<String> response1 = new ApiResponse<>(200, "Success", "data");
        ApiResponse<String> response2 = new ApiResponse<>(200, "Success", "data");
        Object other = new Object();

        assertTrue(response1.canEqual(response2));
        assertFalse(response1.canEqual(other));
    }

    @Test
    void testComplexObject() {
        TestObject testObj = new TestObject("test", 123);
        ApiResponse<TestObject> response = ApiResponse.success("Success with object", testObj);

        assertEquals(200, response.getStatus());
        assertEquals("Success with object", response.getMessage());
        assertEquals(testObj, response.getData());
        assertEquals("test", response.getData().getName());
        assertEquals(123, response.getData().getValue());
    }

    private static class TestObject {
        private String name;
        private int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public int getValue() { return value; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestObject that = (TestObject) obj;
            return value == that.value && name.equals(that.name);
        }
    }
}