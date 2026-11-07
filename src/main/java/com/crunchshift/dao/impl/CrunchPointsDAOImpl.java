package com.crunchshift.dao.impl;

import com.crunchshift.config.DatabaseConnection;
import com.crunchshift.dao.CrunchPointsDAO;
import com.crunchshift.model.CrunchPoints;
import com.crunchshift.util.DateUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi DAO untuk Gamifikasi Crunch Points.
 * Selaras dengan tabel crunch_points (id_poin, deskripsi_kontribusi, jumlah_poin,
 * status_verifikasi, tanggal_pengajuan, staff_id_karyawan, supervisor_id_karyawan, kategori_poin_id_kategori).
 */
public class CrunchPointsDAOImpl implements CrunchPointsDAO {

    private static final String BASE_SELECT =
            "SELECT cp.id_poin, cp.staff_id_karyawan AS id_staff, cp.supervisor_id_karyawan AS id_supervisor, "
                    + "cp.kategori_poin_id_kategori AS id_kategori, cp.jumlah_poin AS nilai_poin, "
                    + "cp.deskripsi_kontribusi AS catatan_apresiasi, cp.tanggal_pengajuan AS tanggal_pemberian, cp.status_verifikasi, "
                    + "IF(cp.jumlah_poin = 0, 'Evaluasi', 'Apresiasi') AS jenis_transaksi, "
                    + "COALESCE(kStaff.nama_lengkap, '-') AS nama_staff, "
                    + "COALESCE(kSpv.nama_lengkap, '-') AS nama_supervisor, "
                    + "COALESCE(kp.nama_kategori, '-') AS nama_kategori "
                    + "FROM crunch_points cp "
                    + "LEFT JOIN karyawan kStaff ON cp.staff_id_karyawan = kStaff.id_karyawan "
                    + "LEFT JOIN karyawan kSpv ON cp.supervisor_id_karyawan = kSpv.id_karyawan "
                    + "LEFT JOIN kategori_poin kp ON cp.kategori_poin_id_kategori = kp.id_kategori ";

    @Override
    public List<CrunchPoints> findAll() {
        List<CrunchPoints> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY cp.tanggal_pengajuan DESC, cp.id_poin DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractPoints(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua crunch points: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<CrunchPoints> findByStaff(int idStaff) {
        List<CrunchPoints> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE cp.staff_id_karyawan = ? ORDER BY cp.tanggal_pengajuan DESC, cp.id_poin DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idStaff);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPoints(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari crunch points by staff: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<CrunchPoints> findByKaryawan(int idKaryawan) {
        return cariPoin(idKaryawan);
    }

    @Override
    public int getTotalPoinByStaff(int idStaff) {
        String sql = "SELECT COALESCE(SUM(jumlah_poin), 0) FROM crunch_points WHERE staff_id_karyawan = ? AND status_verifikasi = 'Disetujui'";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idStaff);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil saldo poin staff: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int hitungTotalPoinNasional() {
        String sql = "SELECT COALESCE(SUM(jumlah_poin), 0) FROM crunch_points WHERE status_verifikasi = 'Disetujui'";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Gagal menghitung total poin nasional: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public boolean insertApresiasi(CrunchPoints poin) {
        String sqlInsert = "INSERT INTO crunch_points (staff_id_karyawan, supervisor_id_karyawan, kategori_poin_id_kategori, "
                + "jumlah_poin, deskripsi_kontribusi, tanggal_pengajuan, status_verifikasi) "
                + "VALUES (?, ?, ?, ?, ?, CURDATE(), 'Disetujui')";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, poin.getIdStaff());
            ps.setInt(2, poin.getIdSupervisor() > 0 ? poin.getIdSupervisor() : 4);
            ps.setInt(3, poin.getIdKategori() > 0 ? poin.getIdKategori() : 1);
            ps.setInt(4, poin.getNilaiPoin());
            ps.setString(5, poin.getCatatanApresiasi() != null ? poin.getCatatanApresiasi() : "Apresiasi Kinerja");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        poin.setIdPoin(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Gagal insert apresiasi poin: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean insertEvaluasi(CrunchPoints poin) {
        String sqlInsert = "INSERT INTO crunch_points (staff_id_karyawan, supervisor_id_karyawan, kategori_poin_id_kategori, "
                + "jumlah_poin, deskripsi_kontribusi, tanggal_pengajuan, status_verifikasi) "
                + "VALUES (?, ?, ?, 0, ?, CURDATE(), 'Disetujui')";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, poin.getIdStaff());
            ps.setInt(2, poin.getIdSupervisor() > 0 ? poin.getIdSupervisor() : 4);
            ps.setInt(3, poin.getIdKategori() > 0 ? poin.getIdKategori() : 1);
            ps.setString(4, poin.getCatatanApresiasi() != null ? poin.getCatatanApresiasi() : "Catatan Evaluasi");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        poin.setIdPoin(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Gagal insert evaluasi poin (poin 0): " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<CrunchPoints> cariPoin(int idKaryawan) {
        List<CrunchPoints> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE cp.staff_id_karyawan = ? ORDER BY cp.tanggal_pengajuan DESC, cp.id_poin DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPoints(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal cari poin karyawan: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<CrunchPoints> cariPoin(int idKaryawan, String jenisTransaksi) {
        List<CrunchPoints> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE cp.staff_id_karyawan = ? "
                + ("Evaluasi".equalsIgnoreCase(jenisTransaksi) ? "AND cp.jumlah_poin = 0 " : "AND cp.jumlah_poin > 0 ")
                + "ORDER BY cp.tanggal_pengajuan DESC, cp.id_poin DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPoints(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal cari poin karyawan filter jenis: " + e.getMessage());
        }
        return list;
    }


    /** Pengajuan staf: tanpa memberikan saldo sebelum disetujui Supervisor. */
    public boolean ajukanPoin(int idStaff, int idSupervisor, int idKategori, String kontribusi, int nilaiAcuan) {
        if (idStaff <= 0 || idSupervisor <= 0 || idKategori <= 0 || kontribusi == null || kontribusi.trim().isEmpty()) return false;
        String sql = "INSERT INTO crunch_points (staff_id_karyawan, supervisor_id_karyawan, kategori_poin_id_kategori, jumlah_poin, deskripsi_kontribusi, tanggal_pengajuan, status_verifikasi) VALUES (?,?,?,?,?,CURDATE(),'Menunggu')";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1,idStaff); ps.setInt(2,idSupervisor); ps.setInt(3,idKategori);
                ps.setInt(4,Math.max(0,nilaiAcuan)); ps.setString(5,kontribusi.trim());
                return ps.executeUpdate()==1;
            }
        } catch(SQLException ex) { throw new IllegalStateException("Pengajuan poin gagal: " + ex.getMessage(),ex); }
    }

    /** Hanya permohonan Menunggu yang dapat diputuskan ulang. */
    public boolean putuskanPoin(int idPoin, int idSupervisor, String keputusan) {
        if (!"Disetujui".equals(keputusan) && !"Ditolak".equals(keputusan)) throw new IllegalArgumentException("Status tidak valid");
        String sql="UPDATE crunch_points SET status_verifikasi=? WHERE id_poin=? AND supervisor_id_karyawan=? AND status_verifikasi='Menunggu'";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn==null) return false;
            try (PreparedStatement ps=conn.prepareStatement(sql)) {
                ps.setString(1,keputusan);ps.setInt(2,idPoin);ps.setInt(3,idSupervisor);
                return ps.executeUpdate()==1;
            }
        } catch(SQLException ex) {throw new IllegalStateException("Verifikasi poin gagal: " + ex.getMessage(),ex);}
    }

    /** Ubah evaluasi yang dibuat atau ditangani Supervisor ini. */
    public boolean ubahEvaluasi(int idPoin, int supervisorId, String deskripsi, int poin, String status) {
        if (idPoin <= 0 || supervisorId <= 0 || deskripsi == null || deskripsi.isBlank()
                || deskripsi.length() > 500 || !java.util.Arrays.asList(0, 25, 50, 75, 100).contains(poin)
                || !java.util.Arrays.asList("Menunggu", "Disetujui", "Ditolak").contains(status)) return false;
        String sql = "UPDATE crunch_points SET deskripsi_kontribusi = ?, jumlah_poin = ?, status_verifikasi = ? "
                + "WHERE id_poin = ? AND supervisor_id_karyawan = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,deskripsi.trim()); ps.setInt(2,poin); ps.setString(3,status);
            ps.setInt(4,idPoin); ps.setInt(5,supervisorId);
            return ps.executeUpdate() == 1;
        } catch(SQLException e) {throw new IllegalStateException("Perubahan evaluasi gagal: " + e.getMessage(), e);}
    }


    private CrunchPoints extractPoints(ResultSet rs) throws SQLException {
        CrunchPoints cp = new CrunchPoints();
        cp.setIdPoin(rs.getInt("id_poin"));
        cp.setIdStaff(rs.getInt("id_staff"));
        cp.setIdSupervisor(rs.getInt("id_supervisor"));
        cp.setIdKategori(rs.getInt("id_kategori"));
        cp.setNilaiPoin(rs.getInt("nilai_poin"));
        cp.setCatatanApresiasi(rs.getString("catatan_apresiasi"));
        Date dt = rs.getDate("tanggal_pemberian");
        if (dt != null) {
            cp.setTanggalPemberian(DateUtil.fromLocalDate(dt.toLocalDate()));
        } else {
            cp.setTanggalPemberian(DateUtil.sekarangWITA());
        }
        cp.setJenisTransaksi(rs.getString("jenis_transaksi"));
        cp.setStatusVerifikasi(rs.getString("status_verifikasi"));
        cp.setNamaStaff(rs.getString("nama_staff"));
        cp.setNamaSupervisor(rs.getString("nama_supervisor"));
        cp.setNamaKategori(rs.getString("nama_kategori"));
        return cp;
    }
}
