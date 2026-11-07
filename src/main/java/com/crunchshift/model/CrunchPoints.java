package com.crunchshift.model;

import java.time.ZonedDateTime;

/**
 * Entitas Gamifikasi Crunch Points.
 * Mendukung transaksi 'Apresiasi' (poin bertambah) dan 'Evaluasi' (poin = 0 sebagai catatan evaluasi).
 */
public class CrunchPoints {

    private int idPoin;
    private int idStaff;
    private int idSupervisor;
    private int idKategori;
    private int nilaiPoin;
    private String catatanApresiasi;
    private ZonedDateTime tanggalPemberian;
    private String statusVerifikasi = "Menunggu";
    private String jenisTransaksi; // 'Apresiasi' atau 'Evaluasi'

    // Transient helper fields untuk tampilan tabel/GUI
    private String namaStaff;
    private String namaSupervisor;
    private String namaKategori;

    public CrunchPoints() {
        this.nilaiPoin = 0;
        this.jenisTransaksi = "Apresiasi";
    }

    public CrunchPoints(int idPoin, int idStaff, int idSupervisor, int idKategori,
                        int nilaiPoin, String catatanApresiasi,
                        ZonedDateTime tanggalPemberian, String jenisTransaksi) {
        this.idPoin = idPoin;
        this.idStaff = idStaff;
        this.idSupervisor = idSupervisor;
        this.idKategori = idKategori;
        this.nilaiPoin = nilaiPoin;
        this.catatanApresiasi = catatanApresiasi;
        this.tanggalPemberian = tanggalPemberian;
        this.jenisTransaksi = jenisTransaksi;
    }

    public int getIdPoin() {
        return idPoin;
    }

    public void setIdPoin(int idPoin) {
        this.idPoin = idPoin;
    }

    public int getIdStaff() {
        return idStaff;
    }

    public void setIdStaff(int idStaff) {
        this.idStaff = idStaff;
    }

    public int getIdSupervisor() {
        return idSupervisor;
    }

    public void setIdSupervisor(int idSupervisor) {
        this.idSupervisor = idSupervisor;
    }

    public int getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(int idKategori) {
        this.idKategori = idKategori;
    }

    public int getNilaiPoin() {
        return nilaiPoin;
    }

    public void setNilaiPoin(int nilaiPoin) {
        this.nilaiPoin = nilaiPoin;
    }

    public String getCatatanApresiasi() {
        return catatanApresiasi;
    }

    public void setCatatanApresiasi(String catatanApresiasi) {
        this.catatanApresiasi = catatanApresiasi;
    }

    public ZonedDateTime getTanggalPemberian() {
        return tanggalPemberian;
    }

    public void setTanggalPemberian(ZonedDateTime tanggalPemberian) {
        this.tanggalPemberian = tanggalPemberian;
    }

    public String getStatusVerifikasi() { return statusVerifikasi; }
    public void setStatusVerifikasi(String s) { statusVerifikasi = s; }

    public String getJenisTransaksi() {
        return jenisTransaksi;
    }

    public void setJenisTransaksi(String jenisTransaksi) {
        this.jenisTransaksi = jenisTransaksi;
    }

    public String getNamaStaff() {
        return namaStaff;
    }

    public void setNamaStaff(String namaStaff) {
        this.namaStaff = namaStaff;
    }

    public String getNamaSupervisor() {
        return namaSupervisor;
    }

    public void setNamaSupervisor(String namaSupervisor) {
        this.namaSupervisor = namaSupervisor;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }
}
