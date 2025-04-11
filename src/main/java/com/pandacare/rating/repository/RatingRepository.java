// src/main/java/com/pandacare/rating/repository/RatingRepository.java
package com.pandacare.rating.repository;

import com.pandacare.rating.model.Rating;
import java.util.List;
import java.util.Optional;

public interface RatingRepository {
    /**
     * Mencari rating berdasarkan ID pemilik (Pacillian)
     * @param idPacillian ID dari Pacillian
     * @return Optional berisi daftar rating atau kosong jika tidak ditemukan
     */
    Optional<List<Rating>> findByOwnerId(String idPacillian);
    
    /**
     * Menghapus rating berdasarkan ID Pacillian dan ID Dokter
     * @param idPacillian ID dari Pacillian
     * @param idDokter ID dari Dokter
     * @return Optional berisi rating yang dihapus atau kosong jika tidak ditemukan
     */
    Optional<Rating> deleteById(String idPacillian, String idDokter);
    
    /**
     * Menyimpan atau memperbarui rating
     * @param rating Rating yang akan disimpan
     * @return Rating yang telah disimpan dengan ID yang diperbarui jika baru
     */
    Rating save(Rating rating);
}