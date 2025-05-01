package com.pandacare.mainapp.rating.repository;

public interface UserRepository {
    /**
     * Memeriksa apakah pengguna dengan ID tertentu ada
     * @param id ID pengguna
     * @return true jika pengguna ada, false jika tidak
     */
    boolean existsById(String id);
}