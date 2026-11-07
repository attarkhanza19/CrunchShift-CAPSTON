package com.crunchshift.dao.impl;

import com.crunchshift.config.DatabaseConnection;
import com.crunchshift.dao.KaryawanDAO;
import com.crunchshift.model.Admin;
import com.crunchshift.model.Karyawan;
import com.crunchshift.model.Staff;
import com.crunchshift.model.Supervisor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi DAO untuk entitas Karyawan dengan pemetaan polimorfisme ke Admin, Supervisor, dan Staff.
 * Selaras dengan skema database crunchshift_db:
 * - karyawan: id_karyawan, nama_lengkap, email, password, jenis_karyawan, status_akun
 * - admin: id_karyawan, departemen, tingkat_akses
 * - supervisor: id_karyawan, area_supervisi, shift_supervisi
 * - staff: id_karyawan, divisi, bagian_kerja
 */
public class KaryawanDAOImpl implements KaryawanDAO {

    @Override
    public Karyawan authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM karyawan WHERE LOWER(email) = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase(java.util.Locale.ROOT));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && password.equals(rs.getString("password"))) {
                    Karyawan user = buildKaryawanSubtype(conn, rs);
                    if (user != null && !user.isStatusAktif())
                        throw new IllegalStateException("Akun Anda telah dinonaktifkan");
                    return user;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Gagal autentikasi: " + ex.getMessage());
        }
        return null;
    }

    /** Ganti kata sandi sendiri: sandi lama wajib sesuai dan akun harus aktif. */
    public boolean ubahPasswordSendiri(int idKaryawan, String lama, String baru) {
        if (idKaryawan <= 0 || lama == null || baru == null || baru.length() < 5) return false;
        String sql = "UPDATE karyawan SET password = ? WHERE id_karyawan = ? "
                + "AND status_akun = 'Aktif' AND password = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, baru); ps.setInt(2, idKaryawan); ps.setString(3, lama);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { throw new IllegalStateException("Gagal mengubah kata sandi: " + e.getMessage(), e); }
    }


    /** Dipertahankan untuk kompatibilitas pemanggil lama. */
    public boolean ubahPasswordStaff(int idKaryawan, String lama, String baru) {
        return ubahPasswordSendiri(idKaryawan, lama, baru);
    }

    @Override
    public List<Karyawan> findAll() {
        List<Karyawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan ORDER BY id_karyawan ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Karyawan k = buildKaryawanSubtype(conn, rs);
                if (k != null) list.add(k);
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua karyawan: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Karyawan> findByPeran(String peran) {
        List<Karyawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan WHERE jenis_karyawan = ? ORDER BY nama_lengkap ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, peran);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Karyawan k = buildKaryawanSubtype(conn, rs);
                    if (k != null) list.add(k);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari karyawan berdasarkan peran: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Karyawan findById(int idKaryawan) {
        String sql = "SELECT * FROM karyawan WHERE id_karyawan = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildKaryawanSubtype(conn, rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari karyawan by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Karyawan findByEmail(String email) {
        String sql = "SELECT * FROM karyawan WHERE email = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildKaryawanSubtype(conn, rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari karyawan by email: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insert(Karyawan karyawan) {
        String sqlKaryawan = "INSERT INTO karyawan (nama_lengkap, email, password, jenis_karyawan, status_akun) "
                + "VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);
            int generatedId = -1;

            try (PreparedStatement ps = conn.prepareStatement(sqlKaryawan, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, karyawan.getNama());
                ps.setString(2, karyawan.getEmail());
                ps.setString(3, karyawan.getPassword());
                ps.setString(4, karyawan.getPeran());
                ps.setString(5, karyawan.isStatusAktif() ? "Aktif" : "Nonaktif");
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        karyawan.setIdKaryawan(generatedId);
                    }
                }
            }

            if (generatedId <= 0) {
                conn.rollback();
                return false;
            }

            insertSubtype(conn, karyawan, generatedId);
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback gagal: " + ex.getMessage());
            }
            System.err.println("Gagal insert karyawan: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Gagal reset auto-commit: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean update(Karyawan karyawan) {
        String sql = "UPDATE karyawan SET nama_lengkap = ?, email = ?, password = ?, jenis_karyawan = ?, "
                + "status_akun = ? WHERE id_karyawan = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);

            String peranSebelum = null;
            try (PreparedStatement cek = conn.prepareStatement(
                    "SELECT jenis_karyawan FROM karyawan WHERE id_karyawan = ? FOR UPDATE")) {
                cek.setInt(1, karyawan.getIdKaryawan());
                try (ResultSet hasil = cek.executeQuery()) {
                    if (hasil.next()) peranSebelum = hasil.getString(1);
                }
            }
            if (peranSebelum == null) throw new SQLException("Karyawan tidak ditemukan.");

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, karyawan.getNama());
                ps.setString(2, karyawan.getEmail());
                ps.setString(3, karyawan.getPassword());
                ps.setString(4, karyawan.getPeran());
                ps.setString(5, karyawan.isStatusAktif() ? "Aktif" : "Nonaktif");
                ps.setInt(6, karyawan.getIdKaryawan());
                ps.executeUpdate();
            }

            // Tidak menghapus subtype yang masih mempunyai referensi transaksi.
            if (!peranSebelum.equalsIgnoreCase(karyawan.getPeran())) {
                String tabelLama = "Admin".equals(peranSebelum) ? "admin" :
                        "Supervisor".equals(peranSebelum) ? "supervisor" :
                        "Staff".equals(peranSebelum) ? "staff" : null;
                if (tabelLama == null) throw new SQLException("Peran lama tidak dikenal.");
                try (PreparedStatement hapus = conn.prepareStatement(
                        "DELETE FROM " + tabelLama + " WHERE id_karyawan = ?")) {
                    hapus.setInt(1, karyawan.getIdKaryawan());
                    hapus.executeUpdate();
                }
                insertSubtype(conn, karyawan, karyawan.getIdKaryawan());
            } else {
                updateSubtype(conn, karyawan);
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback gagal: " + ex.getMessage());
            }
            System.err.println("Gagal update karyawan: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Gagal reset auto-commit: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean delete(int idKaryawan) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM admin WHERE id_karyawan = ?")) {
                ps1.setInt(1, idKaryawan);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM supervisor WHERE id_karyawan = ?")) {
                ps2.setInt(1, idKaryawan);
                ps2.executeUpdate();
            }
            try (PreparedStatement ps3 = conn.prepareStatement("DELETE FROM staff WHERE id_karyawan = ?")) {
                ps3.setInt(1, idKaryawan);
                ps3.executeUpdate();
            }
            try (PreparedStatement ps4 = conn.prepareStatement("DELETE FROM karyawan WHERE id_karyawan = ?")) {
                ps4.setInt(1, idKaryawan);
                int aff = ps4.executeUpdate();
                conn.commit();
                return aff > 0;
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // Ignore rollback ex
            }
            System.err.println("Gagal menghapus karyawan: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    @Override
    public boolean updateStatus(int idKaryawan, boolean statusAktif) {
        String sql = "UPDATE karyawan SET status_akun = ? WHERE id_karyawan = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusAktif ? "Aktif" : "Nonaktif");
            ps.setInt(2, idKaryawan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal ubah status aktif karyawan: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int countAktif() {
        String sql = "SELECT COUNT(*) FROM karyawan WHERE status_akun = 'Aktif'";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Gagal menghitung karyawan aktif: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public List<Karyawan> search(String keyword) {
        List<Karyawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan WHERE nama_lengkap LIKE ? OR email LIKE ? ORDER BY nama_lengkap ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Karyawan k = buildKaryawanSubtype(conn, rs);
                    if (k != null) list.add(k);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari karyawan: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Karyawan> search(String keyword, String peran) {
        List<Karyawan> list = new ArrayList<>();
        String sql = "SELECT * FROM karyawan WHERE (nama_lengkap LIKE ? OR email LIKE ?) AND jenis_karyawan = ? ORDER BY nama_lengkap ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, peran);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Karyawan k = buildKaryawanSubtype(conn, rs);
                    if (k != null) list.add(k);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari karyawan terfilter: " + e.getMessage());
        }
        return list;
    }

    private Karyawan buildKaryawanSubtype(Connection conn, ResultSet rs) throws SQLException {
        int id = rs.getInt("id_karyawan");
        String nama = rs.getString("nama_lengkap");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String peran = rs.getString("jenis_karyawan");
        String statusAkun = rs.getString("status_akun");
        boolean statusAktif = "Aktif".equalsIgnoreCase(statusAkun);

        switch (peran) {
            case "Admin": {
                Admin admin = new Admin();
                admin.setIdKaryawan(id);
                admin.setIdAdmin(id);
                admin.setNama(nama);
                admin.setEmail(email);
                admin.setPassword(password);
                admin.setPeran("Admin");
                admin.setStatusAktif(statusAktif);

                String sqlAdmin = "SELECT departemen, tingkat_akses FROM admin WHERE id_karyawan = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlAdmin)) {
                    ps.setInt(1, id);
                    try (ResultSet rsa = ps.executeQuery()) {
                        if (rsa.next()) {
                            admin.setDepartemen(rsa.getString("departemen"));
                            admin.setTingkatAkses(rsa.getString("tingkat_akses"));
                        }
                    }
                }
                return admin;
            }
            case "Supervisor": {
                Supervisor spv = new Supervisor();
                spv.setIdKaryawan(id);
                spv.setIdSupervisor(id);
                spv.setNama(nama);
                spv.setEmail(email);
                spv.setPassword(password);
                spv.setPeran("Supervisor");
                spv.setStatusAktif(statusAktif);

                String sqlSpv = "SELECT area_supervisi, shift_supervisi FROM supervisor WHERE id_karyawan = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlSpv)) {
                    ps.setInt(1, id);
                    try (ResultSet rss = ps.executeQuery()) {
                        if (rss.next()) {
                            spv.setAreaSupervisi(rss.getString("area_supervisi"));
                            spv.setShiftSupervisi(rss.getString("shift_supervisi"));
                        }
                    }
                }
                return spv;
            }
            case "Staff": {
                Staff staff = new Staff();
                staff.setIdKaryawan(id);
                staff.setIdStaff(id);
                staff.setNama(nama);
                staff.setEmail(email);
                staff.setPassword(password);
                staff.setPeran("Staff");
                staff.setStatusAktif(statusAktif);

                String sqlStaff = "SELECT divisi, bagian_kerja FROM staff WHERE id_karyawan = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlStaff)) {
                    ps.setInt(1, id);
                    try (ResultSet rst = ps.executeQuery()) {
                        if (rst.next()) {
                            staff.setDivisi(rst.getString("divisi"));
                            staff.setBagianKerja(rst.getString("bagian_kerja"));
                        }
                    }
                }

                // Hitung saldo poin dinamis dari crunch_points
                String sqlPoin = "SELECT COALESCE(SUM(jumlah_poin), 0) FROM crunch_points WHERE staff_id_karyawan = ? AND status_verifikasi = 'Disetujui'";
                try (PreparedStatement psPoin = conn.prepareStatement(sqlPoin)) {
                    psPoin.setInt(1, id);
                    try (ResultSet rsp = psPoin.executeQuery()) {
                        if (rsp.next()) {
                            staff.setSaldoPoin(rsp.getInt(1));
                        }
                    }
                }

                return staff;
            }
            default:
                return null;
        }
    }

    private void insertSubtype(Connection conn, Karyawan k, int idKaryawan) throws SQLException {
        if (k instanceof Admin) {
            Admin a = (Admin) k;
            String sql = "INSERT INTO admin (id_karyawan, departemen, tingkat_akses) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE departemen = VALUES(departemen), tingkat_akses = VALUES(tingkat_akses)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idKaryawan);
                ps.setString(2, a.getDepartemen() != null ? a.getDepartemen() : "administrasi");
                ps.setString(3, a.getTingkatanAkses() != null ? a.getTingkatanAkses() : "Pusat");
                ps.executeUpdate();
            }
        } else if (k instanceof Supervisor) {
            Supervisor s = (Supervisor) k;
            String sql = "INSERT INTO supervisor (id_karyawan, area_supervisi, shift_supervisi) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE area_supervisi = VALUES(area_supervisi), shift_supervisi = VALUES(shift_supervisi)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idKaryawan);
                ps.setString(2, s.getAreaSupervisi() != null ? s.getAreaSupervisi() : "Lini Produksi");
                ps.setString(3, s.getShiftSupervisi() != null ? s.getShiftSupervisi() : "Pagi");
                ps.executeUpdate();
            }
        } else if (k instanceof Staff) {
            Staff s = (Staff) k;
            String sql = "INSERT INTO staff (id_karyawan, divisi, bagian_kerja) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE divisi = VALUES(divisi), bagian_kerja = VALUES(bagian_kerja)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idKaryawan);
                ps.setString(2, s.getDivisi() != null ? s.getDivisi() : "Produksi");
                ps.setString(3, s.getBagianKerja() != null ? s.getBagianKerja() : "Operator");
                ps.executeUpdate();
            }
        }
    }

    private void updateSubtype(Connection conn, Karyawan k) throws SQLException {
        if (k instanceof Admin) {
            Admin a = (Admin) k;
            String sql = "UPDATE admin SET departemen = ?, tingkat_akses = ? WHERE id_karyawan = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, a.getDepartemen() != null ? a.getDepartemen() : "administrasi");
                ps.setString(2, a.getTingkatanAkses() != null ? a.getTingkatanAkses() : "Pusat");
                ps.setInt(3, a.getIdKaryawan());
                ps.executeUpdate();
            }
        } else if (k instanceof Supervisor) {
            Supervisor s = (Supervisor) k;
            String sql = "UPDATE supervisor SET area_supervisi = ?, shift_supervisi = ? WHERE id_karyawan = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, s.getAreaSupervisi() != null ? s.getAreaSupervisi() : "Lini Produksi");
                ps.setString(2, s.getShiftSupervisi() != null ? s.getShiftSupervisi() : "Pagi");
                ps.setInt(3, s.getIdKaryawan());
                ps.executeUpdate();
            }
        } else if (k instanceof Staff) {
            Staff s = (Staff) k;
            String sql = "UPDATE staff SET divisi = ?, bagian_kerja = ? WHERE id_karyawan = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, s.getDivisi() != null ? s.getDivisi() : "Produksi");
                ps.setString(2, s.getBagianKerja() != null ? s.getBagianKerja() : "Operator");
                ps.setInt(3, s.getIdKaryawan());
                ps.executeUpdate();
            }
        }
    }
}
