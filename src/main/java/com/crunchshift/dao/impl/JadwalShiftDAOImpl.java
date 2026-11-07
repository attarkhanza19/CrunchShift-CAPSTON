package com.crunchshift.dao.impl;

import com.crunchshift.config.DatabaseConnection;
import com.crunchshift.dao.JadwalShiftDAO;
import com.crunchshift.model.JadwalShift;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi DAO untuk Penjadwalan Shift Karyawan.
 * Selaras dengan tabel jadwal_shift (id_jadwal, tanggal_jadwal, karyawan_id_karyawan, shift_id_shift).
 */
public class JadwalShiftDAOImpl implements JadwalShiftDAO {

    private static final String BASE_SELECT =
            "SELECT j.id_jadwal, j.karyawan_id_karyawan AS id_karyawan, j.shift_id_shift AS id_shift, "
                    + "j.tanggal_jadwal AS tanggal, 'Terjadwal' AS status_plot, "
                    + "k.nama_lengkap AS nama_karyawan, s.nama_shift, s.jam_masuk, s.jam_keluar "
                    + "FROM jadwal_shift j "
                    + "JOIN karyawan k ON j.karyawan_id_karyawan = k.id_karyawan "
                    + "JOIN shift s ON j.shift_id_shift = s.id_shift ";

    @Override
    public List<JadwalShift> findAll() {
        List<JadwalShift> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY j.tanggal_jadwal DESC, s.jam_masuk ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractJadwal(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua jadwal: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<JadwalShift> findByKaryawan(int idKaryawan) {
        return cariJadwal(idKaryawan);
    }

    @Override
    public List<JadwalShift> findByTanggal(LocalDate tanggal) {
        List<JadwalShift> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE j.tanggal_jadwal = ? ORDER BY s.jam_masuk ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractJadwal(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari jadwal by tanggal: " + e.getMessage());
        }
        return list;
    }

    @Override
    public JadwalShift findJadwalHariIni(int idKaryawan, LocalDate tanggal) {
        String sql = BASE_SELECT + "WHERE j.karyawan_id_karyawan = ? AND j.tanggal_jadwal = ? LIMIT 1";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            ps.setDate(2, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractJadwal(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari jadwal hari ini: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean existsJadwal(int idKaryawan, LocalDate tanggal) {
        String sql = "SELECT COUNT(*) FROM jadwal_shift WHERE karyawan_id_karyawan = ? AND tanggal_jadwal = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            ps.setDate(2, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal cek duplikasi jadwal: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean insert(JadwalShift jadwal) {
        String sql = "INSERT INTO jadwal_shift (karyawan_id_karyawan, shift_id_shift, tanggal_jadwal) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, jadwal.getIdKaryawan());
            ps.setInt(2, jadwal.getIdShift());
            ps.setDate(3, Date.valueOf(jadwal.getTanggal()));
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        jadwal.setIdJadwal(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Gagal insert jadwal: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(JadwalShift jadwal) {
        // Cek bentrok tanggal milik karyawan, tidak menghitung baris yang sedang diubah.
        String sql = "UPDATE jadwal_shift j "
                + "LEFT JOIN jadwal_shift konflik ON konflik.karyawan_id_karyawan = j.karyawan_id_karyawan "
                + "AND konflik.tanggal_jadwal = ? AND konflik.id_jadwal <> j.id_jadwal "
                + "SET j.shift_id_shift = ?, j.tanggal_jadwal = ? "
                + "WHERE j.id_jadwal = ? AND j.karyawan_id_karyawan = ? "
                + "AND konflik.id_jadwal IS NULL";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(jadwal.getTanggal()));
            ps.setInt(2, jadwal.getIdShift());
            ps.setDate(3, Date.valueOf(jadwal.getTanggal()));
            ps.setInt(4, jadwal.getIdJadwal());
            ps.setInt(5, jadwal.getIdKaryawan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal update jadwal: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int idJadwal) {
        String sql = "DELETE FROM jadwal_shift WHERE id_jadwal = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJadwal);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal delete jadwal: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<JadwalShift> cariJadwal(int idKaryawan) {
        List<JadwalShift> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE j.karyawan_id_karyawan = ? ORDER BY j.tanggal_jadwal DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractJadwal(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal cari jadwal karyawan: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<JadwalShift> cariJadwal(int idKaryawan, LocalDate tanggal) {
        List<JadwalShift> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE j.karyawan_id_karyawan = ? AND j.tanggal_jadwal = ? ORDER BY s.jam_masuk ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            ps.setDate(2, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractJadwal(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal cari jadwal karyawan filter tanggal: " + e.getMessage());
        }
        return list;
    }

    private JadwalShift extractJadwal(ResultSet rs) throws SQLException {
        JadwalShift j = new JadwalShift();
        j.setIdJadwal(rs.getInt("id_jadwal"));
        j.setIdKaryawan(rs.getInt("id_karyawan"));
        j.setIdShift(rs.getInt("id_shift"));
        Date dt = rs.getDate("tanggal");
        if (dt != null) j.setTanggal(dt.toLocalDate());
        j.setStatusPlot(rs.getString("status_plot"));
        j.setNamaKaryawan(rs.getString("nama_karyawan"));
        j.setNamaShift(rs.getString("nama_shift"));
        j.setJamShift(rs.getTime("jam_masuk") + " - " + rs.getTime("jam_keluar"));
        return j;
    }
}
