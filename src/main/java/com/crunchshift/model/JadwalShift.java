package com.crunchshift.model;

import java.time.LocalDate;

/**
 * Entitas Penjadwalan Shift Karyawan.
 */
public class JadwalShift {

    private int idJadwal;
    private int idKaryawan;
    private int idShift;
    private LocalDate tanggal;
    private String statusPlot; // 'Terjadwal', 'Selesai', 'Batal'

    // Transient helper fields untuk tampilan tabel/GUI
    private String namaKaryawan;
    private String namaShift;
    private String jamShift;

    public JadwalShift() {
        this.statusPlot = "Terjadwal";
    }

    public JadwalShift(int idJadwal, int idKaryawan, int idShift, LocalDate tanggal, String statusPlot) {
        this.idJadwal = idJadwal;
        this.idKaryawan = idKaryawan;
        this.idShift = idShift;
        this.tanggal = tanggal;
        this.statusPlot = statusPlot;
    }

    public int getIdJadwal() {
        return idJadwal;
    }

    public void setIdJadwal(int idJadwal) {
        this.idJadwal = idJadwal;
    }

    public int getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(int idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public int getIdShift() {
        return idShift;
    }

    public void setIdShift(int idShift) {
        this.idShift = idShift;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public String getStatusPlot() {
        return statusPlot;
    }

    public void setStatusPlot(String statusPlot) {
        this.statusPlot = statusPlot;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }

    public String getNamaShift() {
        return namaShift;
    }

    public void setNamaShift(String namaShift) {
        this.namaShift = namaShift;
    }

    public String getJamShift() {
        return jamShift;
    }

    public void setJamShift(String jamShift) {
        this.jamShift = jamShift;
    }
}
