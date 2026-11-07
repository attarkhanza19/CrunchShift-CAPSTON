package com.crunchshift.controller;

import com.crunchshift.config.SessionManager;
import com.crunchshift.dao.KaryawanDAO;
import com.crunchshift.dao.impl.KaryawanDAOImpl;
import com.crunchshift.model.Admin;
import com.crunchshift.model.Karyawan;

/**
 * Controller untuk menangani proses autentikasi, validasi login, dan routing frame awal.
 */
public class AuthController {

    private final KaryawanDAO karyawanDAO;

    public AuthController() {
        this.karyawanDAO = new KaryawanDAOImpl();
    }

    public AuthController(KaryawanDAO karyawanDAO) {
        this.karyawanDAO = karyawanDAO;
    }

    public Karyawan authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }

        Karyawan karyawan = karyawanDAO.authenticate(email.trim(), password.trim());
        if (karyawan != null) {
            SessionManager.setCurrentUser(karyawan);
        }
        return karyawan;
    }

    public String determineFrame(Karyawan karyawan) {
        if (karyawan == null) return null;

        String peran = karyawan.getPeran();
        if ("Admin".equalsIgnoreCase(peran)) {
            if (karyawan instanceof Admin) {
                Admin admin = (Admin) karyawan;
                if ("Cabang".equalsIgnoreCase(admin.getTingkatanAkses())) {
                    return "AdminCabang";
                }
            }
            return "AdminPusat";
        } else if ("Supervisor".equalsIgnoreCase(peran)) {
            return "Supervisor";
        } else if ("Staff".equalsIgnoreCase(peran)) {
            return "Staff";
        }
        return null;
    }
}
