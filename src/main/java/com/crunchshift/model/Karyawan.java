package com.crunchshift.model;

/**
 * Superclass abstrak untuk semua entitas karyawan dalam sistem CrunchShift.
 * Memenuhi prinsip OOP Abstraction, Encapsulation, dan Inheritance.
 */
public abstract class Karyawan {

    private int idKaryawan;
    private String nama;
    private String email;
    private String password;
    private String noTelepon;
    private String peran;
    private boolean statusAktif;

    public Karyawan() {
        this.statusAktif = true;
    }

    public Karyawan(int idKaryawan, String nama, String email, String password,
                    String noTelepon, String peran, boolean statusAktif) {
        this.idKaryawan = idKaryawan;
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.noTelepon = noTelepon;
        this.peran = peran;
        this.statusAktif = statusAktif;
    }

    /**
     * Metode abstrak polimorfis untuk menampilkan deskripsi peran spesifik.
     * Harus diimplementasikan oleh setiap subclass (Admin, Supervisor, Staff).
     */
    public abstract String getDeskripsiPeran();

    public int getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(int idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    public String getPeran() {
        return peran;
    }

    public void setPeran(String peran) {
        this.peran = peran;
    }

    public boolean isStatusAktif() {
        return statusAktif;
    }

    public void setStatusAktif(boolean statusAktif) {
        this.statusAktif = statusAktif;
    }

    @Override
    public String toString() {
        return nama + " (" + peran + ")";
    }
}
