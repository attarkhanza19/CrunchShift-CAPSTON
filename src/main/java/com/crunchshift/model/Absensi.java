package com.crunchshift.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Entitas Presensi Absensi Karyawan.
 * Mendukung pencatatan waktu nyata Asia/Makassar (WITA), verifikasi supervisor, dan anti-fraud foto.
 */
public class Absensi {

    private int idAbsensi;
    private int idKaryawan;
    private int idShift;
    private LocalDate tanggal;
    private ZonedDateTime waktuMasuk;
    private ZonedDateTime waktuKeluar;
    private String statusKehadiran; // 'Hadir', 'Terlambat', 'Izin', 'Alpa'
    private String statusVerifikasi; // 'Menunggu', 'Disetujui', 'Ditolak'
    private String fotoMasuk;
    private String fotoKeluar;
    private String hashFoto;
    private String catatan;

    // Transient helper fields untuk tampilan tabel/GUI
    private String namaKaryawan;
    private String namaShift;

    public Absensi() {
        this.statusKehadiran = "Hadir";
        this.statusVerifikasi = "Menunggu";
    }

    public Absensi(int idAbsensi, int idKaryawan, int idShift, LocalDate tanggal,
                   ZonedDateTime waktuMasuk, ZonedDateTime waktuKeluar,
                   String statusKehadiran, String statusVerifikasi,
                   String fotoMasuk, String fotoKeluar, String hashFoto, String catatan) {
        this.idAbsensi = idAbsensi;
        this.idKaryawan = idKaryawan;
        this.idShift = idShift;
        this.tanggal = tanggal;
        this.waktuMasuk = waktuMasuk;
        this.waktuKeluar = waktuKeluar;
        this.statusKehadiran = statusKehadiran;
        this.statusVerifikasi = statusVerifikasi;
        this.fotoMasuk = fotoMasuk;
        this.fotoKeluar = fotoKeluar;
        this.hashFoto = hashFoto;
        this.catatan = catatan;
    }

    public int getIdAbsensi() {
        return idAbsensi;
    }

    public void setIdAbsensi(int idAbsensi) {
        this.idAbsensi = idAbsensi;
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

    public ZonedDateTime getWaktuMasuk() {
        return waktuMasuk;
    }

    public void setWaktuMasuk(ZonedDateTime waktuMasuk) {
        this.waktuMasuk = waktuMasuk;
    }

    public ZonedDateTime getWaktuKeluar() {
        return waktuKeluar;
    }

    public void setWaktuKeluar(ZonedDateTime waktuKeluar) {
        this.waktuKeluar = waktuKeluar;
    }

    public String getStatusKehadiran() {
        return statusKehadiran;
    }

    public void setStatusKehadiran(String statusKehadiran) {
        this.statusKehadiran = statusKehadiran;
    }

    public String getStatusVerifikasi() {
        return statusVerifikasi;
    }

    public void setStatusVerifikasi(String statusVerifikasi) {
        this.statusVerifikasi = statusVerifikasi;
    }

    public String getFotoMasuk() {
        return fotoMasuk;
    }

    public void setFotoMasuk(String fotoMasuk) {
        this.fotoMasuk = fotoMasuk;
    }

    public String getFotoKeluar() {
        return fotoKeluar;
    }

    public void setFotoKeluar(String fotoKeluar) {
        this.fotoKeluar = fotoKeluar;
    }

    public String getHashFoto() {
        return hashFoto;
    }

    public void setHashFoto(String hashFoto) {
        this.hashFoto = hashFoto;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
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
}
