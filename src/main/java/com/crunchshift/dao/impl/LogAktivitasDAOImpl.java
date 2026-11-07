package com.crunchshift.dao.impl;

import com.crunchshift.config.DatabaseConnection;
import com.crunchshift.dao.LogAktivitasDAO;
import com.crunchshift.model.LogAktivitas;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi DAO untuk Log Aktivitas dan Handover Shift Karyawan.
 * Selaras dengan tabel log_aktivitas (id_log, tanggal_log, ringkasan_kegiatan, catatan_serah_terima,
 * dokumentasi_kegiatan, status_verifikasi, karyawan_id_karyawan).
 */
public class LogAktivitasDAOImpl implements LogAktivitasDAO {

    private static final String BASE_SELECT =
            "SELECT l.id_log, l.karyawan_id_karyawan AS id_karyawan, l.tanggal_log AS tanggal, "
                    + "'08:00:00' AS jam_mulai, '16:00:00' AS jam_selesai, "
                    + "l.ringkasan_kegiatan AS deskripsi_aktivitas, l.status_verifikasi, "
                    + "l.catatan_serah_terima AS catatan_supervisor, %REVISION% AS catatan_revisi, l.dokumentasi_kegiatan, "
                    + "k.nama_lengkap AS nama_karyawan "
                    + "FROM log_aktivitas l "
                    + "JOIN karyawan k ON l.karyawan_id_karyawan = k.id_karyawan ";

    /** Database lama belum tentu sudah menjalankan migrasi catatan_revisi. */
    private boolean adaKolomRevisi(Connection conn) {
        try (ResultSet cols=conn.getMetaData().getColumns(conn.getCatalog(),null,"log_aktivitas","catatan_revisi")) {
            return cols.next();
        } catch (SQLException ex) { return false; }
    }
    private String sqlDasar(Connection conn) {
        return BASE_SELECT.replace("%REVISION%", adaKolomRevisi(conn)?"l.catatan_revisi":"NULL");
    }
    private static Connection koneksiWajib(){
        Connection conn=DatabaseConnection.getConnection();
        if(conn==null) throw new IllegalStateException(
            "Database tidak terhubung. Jalankan MySQL dan periksa koneksi pada halaman Login.");
        return conn;
    }

    @Override
    public List<LogAktivitas> findAll() {
        List<LogAktivitas> list = new ArrayList<>();
        Connection conn = koneksiWajib();
        String sql = sqlDasar(conn) + "ORDER BY l.tanggal_log DESC, l.id_log DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractLog(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mengambil semua log aktivitas: " + e.getMessage(),e);
        }
        return list;
    }

    @Override
    public List<LogAktivitas> findByKaryawan(int idKaryawan) {
        return cariLog(idKaryawan);
    }

    @Override
    public List<LogAktivitas> findPendingVerifikasi() {
        List<LogAktivitas> list = new ArrayList<>();
        Connection conn = koneksiWajib();
        String sql = sqlDasar(conn) + "WHERE l.status_verifikasi = 'Menunggu' ORDER BY l.tanggal_log DESC, l.id_log DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractLog(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mencari log aktivitas pending: " + e.getMessage(),e);
        }
        return list;
    }

    @Override
    public boolean insert(LogAktivitas log) {
        String sql = "INSERT INTO log_aktivitas (karyawan_id_karyawan, tanggal_log, ringkasan_kegiatan, "
                + "catatan_serah_terima, dokumentasi_kegiatan, status_verifikasi) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = koneksiWajib();

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, log.getIdKaryawan());
            ps.setDate(2, Date.valueOf(log.getTanggal() != null ? log.getTanggal() : LocalDate.now()));
            ps.setString(3, log.getDeskripsiAktivitas() != null ? log.getDeskripsiAktivitas() : "-");
            ps.setString(4, log.getCatatanSupervisor() != null ? log.getCatatanSupervisor() : "-");
            ps.setString(5, log.getDokumentasiKegiatan());
            ps.setString(6, log.getStatusVerifikasi() != null ? log.getStatusVerifikasi() : "Menunggu");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        log.setIdLog(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menyimpan log aktivitas ke MySQL: " + e.getMessage(),e);
        }
        return false;
    }

    @Override
    public boolean updateVerifikasi(int idLog, String statusVerifikasi, String catatanSupervisor) {
        Connection conn = koneksiWajib();
        boolean migrated=adaKolomRevisi(conn);
        if(!migrated && "Revisi".equals(statusVerifikasi)) throw new IllegalStateException(
           "Fitur Minta Revisi memerlukan migrasi database/migrasi_catatan_revisi.sql. Log harian tetap dapat digunakan.");
        String sql = migrated ? "UPDATE log_aktivitas SET status_verifikasi = ?, "
                + "catatan_revisi = CASE WHEN ? = 'Revisi' THEN ? ELSE catatan_revisi END "
                + "WHERE id_log = ? AND status_verifikasi IN ('Menunggu','Revisi')"
            : "UPDATE log_aktivitas SET status_verifikasi = ? "
                + "WHERE id_log = ? AND status_verifikasi IN ('Menunggu','Revisi')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusVerifikasi);
            if (migrated) {
                ps.setString(2, statusVerifikasi);
                ps.setString(3, catatanSupervisor);
                ps.setInt(4, idLog);
            } else ps.setInt(2, idLog);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal update verifikasi log aktivitas: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<LogAktivitas> cariLog(int idKaryawan) {
        List<LogAktivitas> list = new ArrayList<>();
        Connection conn = koneksiWajib();
        String sql = sqlDasar(conn) + "WHERE l.karyawan_id_karyawan = ? ORDER BY l.tanggal_log DESC, l.id_log DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal cari log karyawan: " + e.getMessage(),e);
        }
        return list;
    }

    @Override
    public List<LogAktivitas> cariLog(int idKaryawan, LocalDate tanggal) {
        List<LogAktivitas> list = new ArrayList<>();
        Connection conn = koneksiWajib();
        String sql = sqlDasar(conn) + "WHERE l.karyawan_id_karyawan = ? AND l.tanggal_log = ? ORDER BY l.id_log DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            ps.setDate(2, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractLog(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal cari log karyawan filter tanggal: " + e.getMessage(),e);
        }
        return list;
    }

    /** Staff hanya boleh memperbarui log milik sendiri yang masih Menunggu / Revisi. */
    public boolean perbaruiLogStaff(int idLog, int idKaryawan, String ringkasan,
                                   String serahTerima, String dokumentasi) {
        if (idLog <= 0 || idKaryawan <= 0 || ringkasan == null || ringkasan.isBlank()
                || serahTerima == null || serahTerima.isBlank()
                || ringkasan.length() > 500 || serahTerima.length() > 500) return false;
        Connection conn = koneksiWajib();
        // A staff member can still update their own daily log on an un-migrated database.
        String sql = "UPDATE log_aktivitas SET ringkasan_kegiatan = ?, catatan_serah_terima = ?, "
                + "dokumentasi_kegiatan = COALESCE(?, dokumentasi_kegiatan), "
                + "status_verifikasi = 'Menunggu'" + (adaKolomRevisi(conn)?", catatan_revisi = NULL":"") + " "
                + "WHERE id_log = ? AND karyawan_id_karyawan = ? AND status_verifikasi IN ('Menunggu','Revisi')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,ringkasan.trim()); ps.setString(2,serahTerima.trim());
            ps.setString(3,dokumentasi); ps.setInt(4,idLog); ps.setInt(5,idKaryawan);
            return ps.executeUpdate() == 1;
        } catch(SQLException e) {throw new IllegalStateException("Log gagal diperbarui: " + e.getMessage(),e);}
    }


    private LogAktivitas extractLog(ResultSet rs) throws SQLException {
        LogAktivitas log = new LogAktivitas();
        log.setIdLog(rs.getInt("id_log"));
        log.setIdKaryawan(rs.getInt("id_karyawan"));
        Date dt = rs.getDate("tanggal");
        if (dt != null) log.setTanggal(dt.toLocalDate());
        log.setJamMulai(LocalTime.of(8, 0));
        log.setJamSelesai(LocalTime.of(16, 0));
        log.setDeskripsiAktivitas(rs.getString("deskripsi_aktivitas"));
        log.setStatusVerifikasi(rs.getString("status_verifikasi"));
        log.setCatatanSupervisor(rs.getString("catatan_supervisor"));
        log.setCatatanRevisi(rs.getString("catatan_revisi"));
        log.setDokumentasiKegiatan(rs.getString("dokumentasi_kegiatan"));
        log.setNamaKaryawan(rs.getString("nama_karyawan"));
        return log;
    }
}
