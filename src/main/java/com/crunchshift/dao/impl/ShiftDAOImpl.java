package com.crunchshift.dao.impl;

import com.crunchshift.config.DatabaseConnection;
import com.crunchshift.dao.ShiftDAO;
import com.crunchshift.model.Shift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi DAO untuk Master Shift operasional pabrik.
 */
public class ShiftDAOImpl implements ShiftDAO {

    @Override
    public List<Shift> findAll() {
        List<Shift> list = new ArrayList<>();
        String sql = "SELECT * FROM shift ORDER BY jam_masuk ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractShift(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua shift: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Shift findById(int idShift) {
        String sql = "SELECT * FROM shift WHERE id_shift = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idShift);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractShift(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari shift by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Shift findByNama(String namaShift) {
        String sql = "SELECT * FROM shift WHERE nama_shift = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaShift);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractShift(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari shift by nama: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insert(Shift shift) {
        String sql = "INSERT INTO shift (nama_shift, jam_masuk, jam_keluar) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, shift.getNamaShift());
            ps.setTime(2, Time.valueOf(shift.getJamMasuk()));
            ps.setTime(3, Time.valueOf(shift.getJamKeluar()));
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        shift.setIdShift(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Gagal insert shift: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Shift shift) {
        String sql = "UPDATE shift SET nama_shift = ?, jam_masuk = ?, jam_keluar = ? WHERE id_shift = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shift.getNamaShift());
            ps.setTime(2, Time.valueOf(shift.getJamMasuk()));
            ps.setTime(3, Time.valueOf(shift.getJamKeluar()));
            ps.setInt(4, shift.getIdShift());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal update shift: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int idShift) {
        String sql = "DELETE FROM shift WHERE id_shift = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idShift);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal delete shift: " + e.getMessage());
            return false;
        }
    }

    private Shift extractShift(ResultSet rs) throws SQLException {
        Shift s = new Shift();
        s.setIdShift(rs.getInt("id_shift"));
        s.setNamaShift(rs.getString("nama_shift"));
        Time tmMasuk = rs.getTime("jam_masuk");
        Time tmKeluar = rs.getTime("jam_keluar");
        if (tmMasuk != null) s.setJamMasuk(tmMasuk.toLocalTime());
        if (tmKeluar != null) s.setJamKeluar(tmKeluar.toLocalTime());
        return s;
    }
}
