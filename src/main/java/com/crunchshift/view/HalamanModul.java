package com.crunchshift.view;
/** Kontrak panel fitur agar GUI Builder dan controller aplikasi tetap terhubung. */
public interface HalamanModul {
    javax.swing.JTable tabel();
    javax.swing.JTextField pencarian();
    javax.swing.JButton tombol(int index);
    javax.swing.JButton tombolCari();
    javax.swing.JButton tombolSegarkan();
    javax.swing.JComponent masukan(int index);
    void pesan(String pesan);
    default void tampilkanForm(boolean tampil) {}
    default void tampilkanJamInput(boolean tampil) {}
}
