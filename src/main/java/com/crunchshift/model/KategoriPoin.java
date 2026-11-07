package com.crunchshift.model;

/**
 * Entitas Kategori Crunch Points gamifikasi.
 */
public class KategoriPoin {

    private int idKategori;
    private String namaKategori;
    private int batasMaksimalPoin;
    private String deskripsi;

    public KategoriPoin() {
    }

    public KategoriPoin(int idKategori, String namaKategori, int batasMaksimalPoin, String deskripsi) {
        this.idKategori = idKategori;
        this.namaKategori = namaKategori;
        this.batasMaksimalPoin = batasMaksimalPoin;
        this.deskripsi = deskripsi;
    }

    public int getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(int idKategori) {
        this.idKategori = idKategori;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public int getBatasMaksimalPoin() {
        return batasMaksimalPoin;
    }

    public void setBatasMaksimalPoin(int batasMaksimalPoin) {
        this.batasMaksimalPoin = batasMaksimalPoin;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    @Override
    public String toString() {
        return namaKategori + " (Maks " + batasMaksimalPoin + " Poin)";
    }
}
