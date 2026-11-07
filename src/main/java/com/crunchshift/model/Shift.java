package com.crunchshift.model;

import java.time.LocalTime;

/**
 * Entitas Master Shift operasional pabrik.
 */
public class Shift {

    private int idShift;
    private String namaShift;
    private LocalTime jamMasuk;
    private LocalTime jamKeluar;

    public Shift() {
    }

    public Shift(int idShift, String namaShift, LocalTime jamMasuk, LocalTime jamKeluar) {
        this.idShift = idShift;
        this.namaShift = namaShift;
        this.jamMasuk = jamMasuk;
        this.jamKeluar = jamKeluar;
    }

    public int getIdShift() {
        return idShift;
    }

    public void setIdShift(int idShift) {
        this.idShift = idShift;
    }

    public String getNamaShift() {
        return namaShift;
    }

    public void setNamaShift(String namaShift) {
        this.namaShift = namaShift;
    }

    public LocalTime getJamMasuk() {
        return jamMasuk;
    }

    public void setJamMasuk(LocalTime jamMasuk) {
        this.jamMasuk = jamMasuk;
    }

    public LocalTime getJamKeluar() {
        return jamKeluar;
    }

    public void setJamKeluar(LocalTime jamKeluar) {
        this.jamKeluar = jamKeluar;
    }

    @Override
    public String toString() {
        return namaShift + " (" + jamMasuk + " - " + jamKeluar + ")";
    }
}
