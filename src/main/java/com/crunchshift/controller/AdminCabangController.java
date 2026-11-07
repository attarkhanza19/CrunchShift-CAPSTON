package com.crunchshift.controller;

import com.crunchshift.dao.AbsensiDAO;
import com.crunchshift.dao.JadwalShiftDAO;
import com.crunchshift.dao.KaryawanDAO;
import com.crunchshift.dao.ShiftDAO;
import com.crunchshift.dao.impl.AbsensiDAOImpl;
import com.crunchshift.dao.impl.JadwalShiftDAOImpl;
import com.crunchshift.dao.impl.KaryawanDAOImpl;
import com.crunchshift.dao.impl.ShiftDAOImpl;
import com.crunchshift.model.Absensi;
import com.crunchshift.model.JadwalShift;
import com.crunchshift.model.Karyawan;
import com.crunchshift.model.Shift;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller untuk modul Admin Cabang: operasional cabang, penjadwalan bebas konflik, dan rekapitulasi.
 */
public class AdminCabangController {

    private final KaryawanDAO karyawanDAO;
    private final JadwalShiftDAO jadwalShiftDAO;
    private final ShiftDAO shiftDAO;
    private final AbsensiDAO absensiDAO;

    public static final String CARD_DASHBOARD = "CARD_DASHBOARD";
    public static final String CARD_KARYAWAN_CABANG = "CARD_KARYAWAN_CABANG";
    public static final String CARD_PENJADWALAN = "CARD_PENJADWALAN";
    public static final String CARD_REKAP_PRESENSI = "CARD_REKAP_PRESENSI";

    public void switchCard(java.awt.Container container, String cardName) {
        if (container != null && container.getLayout() instanceof java.awt.CardLayout) {
            ((java.awt.CardLayout) container.getLayout()).show(container, cardName);
        }
    }

    public AdminCabangController() {
        this.karyawanDAO = new KaryawanDAOImpl();
        this.jadwalShiftDAO = new JadwalShiftDAOImpl();
        this.shiftDAO = new ShiftDAOImpl();
        this.absensiDAO = new AbsensiDAOImpl();
    }

    public List<Karyawan> getKaryawanCabang() {
        List<Karyawan> result = new ArrayList<>();
        result.addAll(karyawanDAO.findByPeran("Supervisor"));
        result.addAll(karyawanDAO.findByPeran("Staff"));
        return result;
    }

    public List<Shift> getAllShift() {
        return shiftDAO.findAll();
    }

    public List<JadwalShift> getAllJadwal() {
        return jadwalShiftDAO.findAll();
    }

    public boolean jadwalkanShift(int idKaryawan, int idShift, LocalDate tanggal) {
        if (idKaryawan <= 0 || idShift <= 0 || tanggal == null) {
            return false;
        }

        // Conflict check: cegah double booking karyawan pada tanggal yang sama
        if (jadwalShiftDAO.existsJadwal(idKaryawan, tanggal)) {
            return false;
        }

        JadwalShift j = new JadwalShift();
        j.setIdKaryawan(idKaryawan);
        j.setIdShift(idShift);
        j.setTanggal(tanggal);
        j.setStatusPlot("Terjadwal");
        return jadwalShiftDAO.insert(j);
    }

    public boolean batalkanJadwal(int idJadwal) {
        return jadwalShiftDAO.delete(idJadwal);
    }

    public List<Absensi> getRekapAbsensi() {
        return absensiDAO.findAll();
    }

    public List<Absensi> filterRekapByTanggal(LocalDate tanggal) {
        return absensiDAO.findByTanggal(tanggal);
    }

    public boolean eksporRekapCSV(File targetFile, List<Absensi> listData) {
        if (targetFile == null || listData == null) return false;

        try (FileWriter writer = new FileWriter(targetFile)) {
            writer.write("ID,Nama Karyawan,Shift,Tanggal,Waktu Masuk,Waktu Keluar,Status Kehadiran,Status Verifikasi,Catatan\n");
            for (Absensi a : listData) {
                String nama = a.getNamaKaryawan() != null ? a.getNamaKaryawan().replace(",", " ") : "-";
                String shift = a.getNamaShift() != null ? a.getNamaShift().replace(",", " ") : "-";
                String tgl = a.getTanggal() != null ? a.getTanggal().toString() : "-";
                String masuk = a.getWaktuMasuk() != null ? a.getWaktuMasuk().toString() : "-";
                String keluar = a.getWaktuKeluar() != null ? a.getWaktuKeluar().toString() : "-";
                String hadir = a.getStatusKehadiran() != null ? a.getStatusKehadiran() : "-";
                String verif = a.getStatusVerifikasi() != null ? a.getStatusVerifikasi() : "-";
                String cat = a.getCatatan() != null ? a.getCatatan().replace(",", " ").replace("\n", " ") : "-";

                writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        a.getIdAbsensi(), nama, shift, tgl, masuk, keluar, hadir, verif, cat));
            }
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Gagal ekspor CSV: " + e.getMessage());
            return false;
        }
    }

    public int getTotalPegawaiCabang() {
        return getKaryawanCabang().size();
    }

    public int getJumlahShiftHariIni() {
        return jadwalShiftDAO.findByTanggal(LocalDate.now()).size();
    }

    public int getJumlahKehadiranHariIni() {
        return absensiDAO.findByTanggal(LocalDate.now()).size();
    }
}
