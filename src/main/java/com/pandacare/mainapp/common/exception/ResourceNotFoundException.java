package com.pandacare.mainapp.common.exception;

/**
 * Exception for resource not found errors
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, 404);
    }

    public static ResourceNotFoundException forEntity(String entity, String id) {
        return new ResourceNotFoundException(entity + " dengan ID " + id + " tidak ditemukan");
    }
}