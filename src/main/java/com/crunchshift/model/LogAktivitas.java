package com.crunchshift.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entitas Log Aktivitas & Handover Shift operasional pabrik.
 */
public class LogAktivitas {

    private int idLog;
    private int idKaryawan;
    private LocalDate tanggal;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private String deskripsiAktivitas;
    private String statusVerifikasi; // 'Menunggu', 'Terverifikasi', 'Revisi'
    private String catatanSupervisor;
    private String catatanRevisi;
    private String dokumentasiKegiatan;

    // Transient helper fields untuk tampilan tabel/GUI
    private String namaKaryawan;

    public LogAktivitas() {
        this.statusVerifikasi = "Menunggu";
    }

    public LogAktivitas(int idLog, int idKaryawan, LocalDate tanggal,
                        LocalTime jamMulai, LocalTime jamSelesai,
                        String deskripsiAktivitas, String statusVerifikasi,
                        String catatanSupervisor) {
        this.idLog = idLog;
        this.idKaryawan = idKaryawan;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.deskripsiAktivitas = deskripsiAktivitas;
        this.statusVerifikasi = statusVerifikasi;
        this.catatanSupervisor = catatanSupervisor;
    }

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public int getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(int idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(LocalTime jamMulai) {
        this.jamMulai = jamMulai;
    }

    public LocalTime getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(LocalTime jamSelesai) {
        this.jamSelesai = jamSelesai;
    }

    public String getDeskripsiAktivitas() {
        return deskripsiAktivitas;
    }

    public void setDeskripsiAktivitas(String deskripsiAktivitas) {
        this.deskripsiAktivitas = deskripsiAktivitas;
    }

    public String getStatusVerifikasi() {
        return statusVerifikasi;
    }

    public void setStatusVerifikasi(String statusVerifikasi) {
        this.statusVerifikasi = statusVerifikasi;
    }

    public String getCatatanRevisi() { return catatanRevisi; }
    public void setCatatanRevisi(String catatanRevisi) { this.catatanRevisi = catatanRevisi; }


    public String getCatatanSupervisor() {
        return catatanSupervisor;
    }

    public void setCatatanSupervisor(String catatanSupervisor) {
        this.catatanSupervisor = catatanSupervisor;
    }

    public String getDokumentasiKegiatan() { return dokumentasiKegiatan; }
    public void setDokumentasiKegiatan(String path) { dokumentasiKegiatan=path; }
    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }
}
