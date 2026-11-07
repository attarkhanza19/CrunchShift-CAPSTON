package com.crunchshift.model;

/**
 * Entitas Staff lini kerja/operator, subclass dari Karyawan.
 * Memiliki saldo Crunch Points untuk sistem gamifikasi.
 */
public class Staff extends Karyawan {

    private int idStaff;
    private String divisi;
    private String bagianKerja;
    private int saldoPoin;

    public Staff() {
        super();
        setPeran("Staff");
        this.saldoPoin = 0;
    }

    public Staff(int idKaryawan, String nama, String email, String password,
                 String noTelepon, boolean statusAktif, int idStaff, String divisi, int saldoPoin) {
        super(idKaryawan, nama, email, password, noTelepon, "Staff", statusAktif);
        this.idStaff = (idStaff > 0) ? idStaff : idKaryawan;
        this.divisi = divisi;
        this.saldoPoin = saldoPoin;
    }

    @Override
    public String getDeskripsiPeran() {
        return "Staff " + (divisi != null ? divisi : "-")
                + (bagianKerja != null ? " (" + bagianKerja + ")" : "");
    }

    public int getIdStaff() {
        return idStaff > 0 ? idStaff : getIdKaryawan();
    }

    public void setIdStaff(int idStaff) {
        this.idStaff = idStaff;
    }

    public String getDivisi() {
        return divisi;
    }

    public void setDivisi(String divisi) {
        this.divisi = divisi;
    }

    public String getBagianKerja() {
        return bagianKerja;
    }

    public void setBagianKerja(String bagianKerja) {
        this.bagianKerja = bagianKerja;
    }

    public int getSaldoPoin() {
        return saldoPoin;
    }

    public void setSaldoPoin(int saldoPoin) {
        this.saldoPoin = saldoPoin;
    }
}
