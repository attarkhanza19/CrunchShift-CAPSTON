package com.crunchshift.dao;

import com.crunchshift.model.CrunchPoints;
import java.util.List;

/**
 * Interface DAO untuk Gamifikasi Crunch Points.
 * Menangani pemberian poin bertingkat (0, 25, 50, 75, 100) dan transaksi multi-tabel saldo staff.
 */
public interface CrunchPointsDAO {

    List<CrunchPoints> findAll();

    List<CrunchPoints> findByStaff(int idStaff);

    List<CrunchPoints> findByKaryawan(int idKaryawan);

    int getTotalPoinByStaff(int idStaff);

    int hitungTotalPoinNasional();

    boolean insertApresiasi(CrunchPoints poin);

    boolean insertEvaluasi(CrunchPoints poin);

    // Overloaded search methods (Lab PBO Compliance)
    List<CrunchPoints> cariPoin(int idKaryawan);

    List<CrunchPoints> cariPoin(int idKaryawan, String jenisTransaksi);
}
