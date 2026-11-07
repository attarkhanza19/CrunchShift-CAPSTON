package com.crunchshift.dao.impl;

import com.crunchshift.config.DatabaseConnection;
import com.crunchshift.dao.KategoriPoinDAO;
import com.crunchshift.model.KategoriPoin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi DAO untuk Kategori Crunch Points.
 * Selaras dengan tabel kategori_poin (id_kategori, nama_kategori, nilai_acuan, keterangan, admin_id_karyawan).
 */
public class KategoriPoinDAOImpl implements KategoriPoinDAO {

    @Override
    public List<KategoriPoin> findAll() {
        List<KategoriPoin> list = new ArrayList<>();
        String sql = "SELECT id_kategori, nama_kategori, nilai_acuan, keterangan FROM kategori_poin ORDER BY id_kategori ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractKategori(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua kategori poin: " + e.getMessage());
        }
        return list;
    }

    @Override
    public KategoriPoin findById(int idKategori) {
        String sql = "SELECT id_kategori, nama_kategori, nilai_acuan, keterangan FROM kategori_poin WHERE id_kategori = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKategori);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractKategori(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari kategori poin by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insert(KategoriPoin kategori) {
        String sql = "INSERT INTO kategori_poin (nama_kategori, nilai_acuan, keterangan, admin_id_karyawan) VALUES (?, ?, ?, 1)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, kategori.getNamaKategori());
            String acuan = String.valueOf(kategori.getBatasMaksimalPoin());
            ps.setString(2, acuan);
            ps.setString(3, kategori.getDeskripsi() != null ? kategori.getDeskripsi() : "-");
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        kategori.setIdKategori(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Gagal insert kategori poin: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(KategoriPoin kategori) {
        String sql = "UPDATE kategori_poin SET nama_kategori = ?, nilai_acuan = ?, keterangan = ? WHERE id_kategori = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kategori.getNamaKategori());
            String acuan = String.valueOf(kategori.getBatasMaksimalPoin());
            ps.setString(2, acuan);
            ps.setString(3, kategori.getDeskripsi() != null ? kategori.getDeskripsi() : "-");
            ps.setInt(4, kategori.getIdKategori());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal update kategori poin: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int idKategori) {
        String sql = "DELETE FROM kategori_poin WHERE id_kategori = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKategori);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal delete kategori poin: " + e.getMessage());
            return false;
        }
    }

    private KategoriPoin extractKategori(ResultSet rs) throws SQLException {
        KategoriPoin kp = new KategoriPoin();
        kp.setIdKategori(rs.getInt("id_kategori"));
        kp.setNamaKategori(rs.getString("nama_kategori"));
        String acuan = rs.getString("nilai_acuan");
        try {
            kp.setBatasMaksimalPoin(Integer.parseInt(acuan));
        } catch (Exception e) {
            kp.setBatasMaksimalPoin(0);
        }
        kp.setDeskripsi(rs.getString("keterangan"));
        return kp;
    }
}
