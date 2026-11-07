package com.crunchshift.dao;

import com.crunchshift.model.JadwalShift;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface DAO untuk Penjadwalan Shift Karyawan.
 */
public interface JadwalShiftDAO {

    List<JadwalShift> findAll();

    List<JadwalShift> findByKaryawan(int idKaryawan);

    List<JadwalShift> findByTanggal(LocalDate tanggal);

    JadwalShift findJadwalHariIni(int idKaryawan, LocalDate tanggal);

    boolean existsJadwal(int idKaryawan, LocalDate tanggal);

    boolean insert(JadwalShift jadwal);

    boolean update(JadwalShift jadwal);

    boolean delete(int idJadwal);

    // Overloaded search methods (Lab PBO Compliance)
    List<JadwalShift> cariJadwal(int idKaryawan);

    List<JadwalShift> cariJadwal(int idKaryawan, LocalDate tanggal);
}
