package com.crunchshift.controller;

import com.crunchshift.dao.AbsensiDAO;
import com.crunchshift.dao.CrunchPointsDAO;
import com.crunchshift.dao.JadwalShiftDAO;
import com.crunchshift.dao.KaryawanDAO;
import com.crunchshift.dao.KategoriPoinDAO;
import com.crunchshift.dao.LogAktivitasDAO;
import com.crunchshift.dao.ShiftDAO;
import com.crunchshift.dao.impl.AbsensiDAOImpl;
import com.crunchshift.dao.impl.CrunchPointsDAOImpl;
import com.crunchshift.dao.impl.JadwalShiftDAOImpl;
import com.crunchshift.dao.impl.KaryawanDAOImpl;
import com.crunchshift.dao.impl.KategoriPoinDAOImpl;
import com.crunchshift.dao.impl.LogAktivitasDAOImpl;
import com.crunchshift.dao.impl.ShiftDAOImpl;
import com.crunchshift.model.Absensi;
import com.crunchshift.model.CrunchPoints;
import com.crunchshift.model.Karyawan;
import com.crunchshift.model.KategoriPoin;
import com.crunchshift.model.LogAktivitas;
import com.crunchshift.model.Shift;

import java.util.List;

/**
 * Controller untuk modul Admin Pusat: manajemen data master, analitik global, dan monitoring.
 */
public class AdminPusatController {

    private final KaryawanDAO karyawanDAO;
    private final ShiftDAO shiftDAO;
    private final JadwalShiftDAO jadwalShiftDAO;
    private final AbsensiDAO absensiDAO;
    private final LogAktivitasDAO logAktivitasDAO;
    private final KategoriPoinDAO kategoriPoinDAO;
    private final CrunchPointsDAO crunchPointsDAO;

    public static final String CARD_DASHBOARD = "CARD_DASHBOARD";
    public static final String CARD_KARYAWAN = "CARD_KARYAWAN";
    public static final String CARD_SHIFT_POIN = "CARD_SHIFT_POIN";
    public static final String CARD_MONITORING = "CARD_MONITORING";

    public void switchCard(java.awt.Container container, String cardName) {
        if (container != null && container.getLayout() instanceof java.awt.CardLayout) {
            ((java.awt.CardLayout) container.getLayout()).show(container, cardName);
        }
    }

    public AdminPusatController() {
        this.karyawanDAO = new KaryawanDAOImpl();
        this.shiftDAO = new ShiftDAOImpl();
        this.jadwalShiftDAO = new JadwalShiftDAOImpl();
        this.absensiDAO = new AbsensiDAOImpl();
        this.logAktivitasDAO = new LogAktivitasDAOImpl();
        this.kategoriPoinDAO = new KategoriPoinDAOImpl();
        this.crunchPointsDAO = new CrunchPointsDAOImpl();
    }

    public List<Karyawan> getAllKaryawan() {
        return karyawanDAO.findAll();
    }

    public List<Karyawan> searchKaryawan(String keyword) {
        return karyawanDAO.search(keyword);
    }

    public List<Karyawan> searchKaryawan(String keyword, String peran) {
        return karyawanDAO.search(keyword, peran);
    }

    public boolean tambahKaryawan(Karyawan karyawan) {
        if (karyawan == null || karyawan.getEmail() == null || karyawan.getEmail().trim().isEmpty()) {
            return false;
        }
        return karyawanDAO.insert(karyawan);
    }

    public boolean ubahKaryawan(Karyawan karyawan) {
        if (karyawan == null || karyawan.getIdKaryawan() <= 0) {
            return false;
        }
        return karyawanDAO.update(karyawan);
    }

    public boolean hapusKaryawan(int idKaryawan) {
        return karyawanDAO.delete(idKaryawan);
    }

    public Karyawan getKaryawanById(int idKaryawan) {
        return karyawanDAO.findById(idKaryawan);
    }

    public boolean toggleStatusKaryawan(int idKaryawan, boolean currentStatus) {
        return karyawanDAO.updateStatus(idKaryawan, !currentStatus);
    }

    public List<Shift> getAllShift() {
        return shiftDAO.findAll();
    }

    public boolean simpanShift(Shift shift) {
        if (shift.getIdShift() > 0) {
            return shiftDAO.update(shift);
        }
        return shiftDAO.insert(shift);
    }

    public boolean hapusShift(int idShift) {
        return shiftDAO.delete(idShift);
    }

    public List<KategoriPoin> getAllKategoriPoin() {
        return kategoriPoinDAO.findAll();
    }

    public boolean simpanKategoriPoin(KategoriPoin kp) {
        if (kp.getIdKategori() > 0) {
            return kategoriPoinDAO.update(kp);
        }
        return kategoriPoinDAO.insert(kp);
    }

    public boolean hapusKategoriPoin(int idKategori) {
        return kategoriPoinDAO.delete(idKategori);
    }

    public List<Absensi> getMonitoringAbsensi() {
        return absensiDAO.findAll();
    }

    public List<LogAktivitas> getMonitoringLog() {
        return logAktivitasDAO.findAll();
    }

    public List<CrunchPoints> getMonitoringPoin() {
        return crunchPointsDAO.findAll();
    }

    public int getTotalKaryawanAktif() {
        return karyawanDAO.countAktif();
    }

    public double getPersentaseKehadiranHariIni() {
        return absensiDAO.hitungPersentaseKehadiranHariIni();
    }

    public int getTotalPoinNasional() {
        return crunchPointsDAO.hitungTotalPoinNasional();
    }
}
