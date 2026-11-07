package com.crunchshift.controller;

import com.crunchshift.dao.AbsensiDAO;
import com.crunchshift.dao.CrunchPointsDAO;
import com.crunchshift.dao.JadwalShiftDAO;
import com.crunchshift.dao.KaryawanDAO;
import com.crunchshift.dao.KategoriPoinDAO;
import com.crunchshift.dao.LogAktivitasDAO;
import com.crunchshift.dao.impl.AbsensiDAOImpl;
import com.crunchshift.dao.impl.CrunchPointsDAOImpl;
import com.crunchshift.dao.impl.JadwalShiftDAOImpl;
import com.crunchshift.dao.impl.KaryawanDAOImpl;
import com.crunchshift.dao.impl.KategoriPoinDAOImpl;
import com.crunchshift.dao.impl.LogAktivitasDAOImpl;
import com.crunchshift.model.Absensi;
import com.crunchshift.model.CrunchPoints;
import com.crunchshift.model.JadwalShift;
import com.crunchshift.model.Karyawan;
import com.crunchshift.model.KategoriPoin;
import com.crunchshift.model.LogAktivitas;
import com.crunchshift.model.Staff;
import com.crunchshift.util.DateUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller untuk modul Supervisor: Verifikasi absensi & foto, verifikasi log serah terima,
 * serta evaluasi/apresiasi Crunch Points.
 */
public class SupervisorController {

    private final JadwalShiftDAO jadwalShiftDAO;
    private final AbsensiDAO absensiDAO;
    private final LogAktivitasDAO logAktivitasDAO;
    private final CrunchPointsDAO crunchPointsDAO;
    private final KategoriPoinDAO kategoriPoinDAO;
    private final KaryawanDAO karyawanDAO;

    public static final String CARD_DASHBOARD = "CARD_DASHBOARD";
    public static final String CARD_MONITORING_SHIFT = "CARD_MONITORING_SHIFT";
    public static final String CARD_VERIFIKASI_PRESENSI = "CARD_VERIFIKASI_PRESENSI";
    public static final String CARD_VERIFIKASI_LOG = "CARD_VERIFIKASI_LOG";
    public static final String CARD_CRUNCH_POINTS = "CARD_CRUNCH_POINTS";

    public void switchCard(java.awt.Container container, String cardName) {
        if (container != null && container.getLayout() instanceof java.awt.CardLayout) {
            ((java.awt.CardLayout) container.getLayout()).show(container, cardName);
        }
    }

    public SupervisorController() {
        this.jadwalShiftDAO = new JadwalShiftDAOImpl();
        this.absensiDAO = new AbsensiDAOImpl();
        this.logAktivitasDAO = new LogAktivitasDAOImpl();
        this.crunchPointsDAO = new CrunchPointsDAOImpl();
        this.kategoriPoinDAO = new KategoriPoinDAOImpl();
        this.karyawanDAO = new KaryawanDAOImpl();
    }

    public boolean ubahJadwalShift(int idJadwal, int idKaryawan, int idShift, LocalDate tanggal) {
        if (tanggal == null || idJadwal <= 0 || idKaryawan <= 0 || idShift <= 0) return false;
        JadwalShift jadwal = new JadwalShift();
        jadwal.setIdJadwal(idJadwal); jadwal.setIdKaryawan(idKaryawan);
        jadwal.setIdShift(idShift); jadwal.setTanggal(tanggal);
        return jadwalShiftDAO.update(jadwal);
    }


    public List<JadwalShift> getMonitoringShiftHariIni() {
        return jadwalShiftDAO.findByTanggal(LocalDate.now());
    }

    public List<Absensi> getPendingAbsensi() {
        return absensiDAO.findPendingVerifikasi();
    }

    public boolean setujuiAbsensi(int idAbsensi, String catatan) {
        return absensiDAO.updateVerifikasi(idAbsensi, "Disetujui", catatan);
    }

    public boolean tolakAbsensi(int idAbsensi, String alasanPenolakan) {
        if (alasanPenolakan == null || alasanPenolakan.trim().isEmpty()) {
            return false;
        }
        return ((AbsensiDAOImpl)absensiDAO).tolakIzin(idAbsensi, alasanPenolakan.trim());
    }

    public boolean koreksiKehadiran(int idAbsensi, String status, String alasan) {
        return ((AbsensiDAOImpl) absensiDAO).koreksiKehadiran(idAbsensi, status, alasan);
    }


    public List<LogAktivitas> getSemuaLog() { return logAktivitasDAO.findAll(); }
    public List<Absensi> getSemuaAbsensi() { return absensiDAO.findAll(); }
    public boolean ubahEvaluasiPoin(int idPoin, int idSupervisor, String deskripsi, int poin, String status) {
        return ((CrunchPointsDAOImpl)crunchPointsDAO).ubahEvaluasi(idPoin,idSupervisor,deskripsi,poin,status);
    }


    public boolean putuskanCrunchPoints(int idPoin,int supervisorId,String keputusan) {
        return ((CrunchPointsDAOImpl)crunchPointsDAO).putuskanPoin(idPoin,supervisorId,keputusan);
    }
    public List<LogAktivitas> getPendingLog() {
        return logAktivitasDAO.findPendingVerifikasi();
    }

    public boolean verifikasiLog(int idLog, String catatan) {
        return logAktivitasDAO.updateVerifikasi(idLog, "Terverifikasi", catatan);
    }

    public boolean mintaRevisiLog(int idLog, String catatanRevisi) {
        if (catatanRevisi == null || catatanRevisi.trim().isEmpty()) {
            return false;
        }
        return logAktivitasDAO.updateVerifikasi(idLog, "Revisi", catatanRevisi.trim());
    }

    public List<KategoriPoin> getAllKategoriPoin() {
        return kategoriPoinDAO.findAll();
    }

    public List<Karyawan> getAllStaff() {
        return karyawanDAO.findByPeran("Staff");
    }

    public List<CrunchPoints> getAllPointsHistory() {
        return crunchPointsDAO.findAll();
    }

    /**
     * Memberikan apresiasi atau evaluasi Crunch Points sesuai aturan skala 0, 25, 50, 75, 100.
     * @param poin Objek CrunchPoints yang diinput
     * @return true jika berhasil disimpan
     */
    public boolean prosesPemberianPoin(CrunchPoints poin) {
        if (poin == null || poin.getIdStaff() <= 0 || poin.getIdSupervisor() <= 0) {
            return false;
        }

        poin.setTanggalPemberian(DateUtil.sekarangWITA());

        // Decision Check PRD 2.0: Apakah Nilai Poin = 0?
        if (poin.getNilaiPoin() == 0) {
            poin.setJenisTransaksi("Evaluasi");
            return crunchPointsDAO.insertEvaluasi(poin);
        } else {
            poin.setJenisTransaksi("Apresiasi");
            return crunchPointsDAO.insertApresiasi(poin);
        }
    }

    public int getJumlahStaffBertugasHariIni() {
        return getMonitoringShiftHariIni().size();
    }

    public int getJumlahPresensiPending() {
        return getPendingAbsensi().size();
    }

    public int getJumlahLogPending() {
        return getPendingLog().size();
    }

    public int getTotalPoinDiberikan() {
        return getAllPointsHistory().size();
    }
}
