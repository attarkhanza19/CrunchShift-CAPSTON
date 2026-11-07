package com.crunchshift.dao;

import com.crunchshift.model.Absensi;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Interface DAO untuk Presensi Absensi Karyawan.
 */
public interface AbsensiDAO {

    List<Absensi> findAll();

    List<Absensi> findByKaryawan(int idKaryawan);

    List<Absensi> findByTanggal(LocalDate tanggal);

    List<Absensi> findPendingVerifikasi();

    Absensi findHariIni(int idKaryawan, LocalDate tanggal);

    boolean insertCheckIn(Absensi absensi);

    boolean updateCheckOut(int idAbsensi, ZonedDateTime waktuKeluar, String fotoKeluar);

    boolean updateVerifikasi(int idAbsensi, String statusVerifikasi, String catatan);

    double hitungPersentaseKehadiranHariIni();

    // Overloaded search methods (Lab PBO Compliance)
    List<Absensi> cariAbsensi(int idKaryawan);

    List<Absensi> cariAbsensi(int idKaryawan, LocalDate tanggal);
}
