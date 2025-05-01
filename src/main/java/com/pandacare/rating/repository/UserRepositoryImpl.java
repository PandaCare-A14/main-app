package com.pandacare.rating.repository;

import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementasi sementara dari UserRepository untuk testing
 * Dalam implementasi nyata, ini akan terhubung ke database atau service lain
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    // Contoh data dummy untuk testing
    private final Set<String> userIds = new HashSet<>();

    public UserRepositoryImpl() {
        // Menyiapkan beberapa data dummy
        userIds.add("DOK001");
        userIds.add("DOK002");
        userIds.add("DOK003");
        userIds.add("PAC001");
        userIds.add("PAC002");
        userIds.add("PAC003");
    }

    @Override
    public boolean existsById(String id) {
        return userIds.contains(id);
    }
}