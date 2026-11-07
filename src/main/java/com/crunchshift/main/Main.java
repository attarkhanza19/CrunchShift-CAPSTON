package com.crunchshift.main;

import com.crunchshift.view.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Titik masuk utama (Entry Point) aplikasi desktop CrunchShift.
 * Memenuhi standar Lab PBO: hanya mengonfigurasi Look & Feel (MetalLookAndFeel)
 * dan membuka jendela utama LoginFrame tanpa business logic.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception e) {
            System.err.println("Gagal menerapkan MetalLookAndFeel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
    }
}
