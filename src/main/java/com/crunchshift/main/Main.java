package com.crunchshift.main;

import com.crunchshift.view.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

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
