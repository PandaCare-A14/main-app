package com.pandacare.rating.service;

import com.pandacare.rating.model.Rating;
import java.util.List;

public interface RatingService {
    /**
     * Menambahkan rating baru
     * @param rating Rating yang akan ditambahkan
     * @return Rating yang telah disimpan dengan ID yang diperbarui
     */
    Rating addRating(Rating rating);
    
    /**
     * Memperbarui rating yang sudah ada
     * @param rating Rating dengan data yang diperbarui
     * @return Rating yang telah diperbarui
     */
    Rating updateRating(Rating rating);
    
    /**
     * Menghapus rating berdasarkan ID Pacillian dan ID Dokter
     * @param idPacillian ID dari Pacillian
     * @param idDokter ID dari Dokter
     * @return Rating yang telah dihapus
     */
    Rating deleteRating(String idPacillian, String idDokter);
    
    /**
     * Mendapatkan semua rating berdasarkan ID pemilik (Pacillian)
     * @param idPacillian ID dari Pacillian
     * @return Daftar rating
     */
    List<Rating> getRatingsByOwnerId(String idPacillian);
    
    /**
     * Mendapatkan semua rating berdasarkan ID Dokter
     * @param idDokter ID dari Dokter
     * @return Daftar rating
     */
    List<Rating> getRatingsByIdDokter(String idDokter);
}