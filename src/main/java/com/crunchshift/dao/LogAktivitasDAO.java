package com.crunchshift.dao;

import com.crunchshift.model.LogAktivitas;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface DAO untuk Log Aktivitas dan Handover Shift.
 */
public interface LogAktivitasDAO {

    List<LogAktivitas> findAll();

    List<LogAktivitas> findByKaryawan(int idKaryawan);

    List<LogAktivitas> findPendingVerifikasi();

    boolean insert(LogAktivitas log);

    boolean updateVerifikasi(int idLog, String statusVerifikasi, String catatanSupervisor);

    // Overloaded search methods (Lab PBO Compliance)
    List<LogAktivitas> cariLog(int idKaryawan);

    List<LogAktivitas> cariLog(int idKaryawan, LocalDate tanggal);
}
