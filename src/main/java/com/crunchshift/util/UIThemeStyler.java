package com.crunchshift.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

/**
 * Helper utilitas untuk styling komponen UI CrunchShift.
 * Menyediakan palet warna resmi, font standar, dan metode styling antarmuka Swing Classic.
 */
public class UIThemeStyler {

    // ====== COLOR PALETTE ======
    public static final Color BLEU_OCEAN = new Color(0x1b, 0x2a, 0x5c);       // #0a1680
    public static final Color BLEU_CIEL = new Color(0xe2, 0xe8, 0xf0);        // #93b2f8
    public static final Color BUTTER_YELLOW = new Color(0xff, 0xf6, 0xe8);    // #fbedb0
    public static final Color PASTEL_ORANGE = new Color(0xe0, 0x7b, 0x10);    // #f1b94c
    public static final Color BLANC_CASSE = new Color(0xf8, 0xfa, 0xfc);      // #fcfdff

    public static final int DEFAULT_BUFFER_MINUTES = 15;

    // ====== FONT ======
    public static final Font FONT_DEFAULT = new Font("Tahoma", Font.PLAIN, 12);
    public static final Font FONT_BOLD_12 = new Font("Tahoma", Font.BOLD, 12);
    public static final Font FONT_BOLD_14 = new Font("Tahoma", Font.BOLD, 14);
    public static final Font FONT_BOLD_24 = new Font("Tahoma", Font.BOLD, 24);
    public static final Font FONT_BOLD_32 = new Font("Tahoma", Font.BOLD, 32);
    public static final Font FONT_11 = new Font("Tahoma", Font.PLAIN, 11);

    private UIThemeStyler() {
    }

    public static void styleTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(BLEU_OCEAN);
        header.setForeground(Color.WHITE);
        header.setFont(FONT_BOLD_12);
        header.setOpaque(true);

        table.setSelectionBackground(BUTTER_YELLOW);
        table.setSelectionForeground(BLEU_OCEAN);
        table.setFont(FONT_11);
        table.setRowHeight(26);
        table.setGridColor(BLEU_CIEL);
        table.setShowGrid(true);
    }

    public static void styleTable(JTable table) {
        styleTableHeader(table);
    }

    public static void stylePrimaryButton(JButton button) {
        button.setBackground(PASTEL_ORANGE);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BOLD_12);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BLEU_OCEAN, 1));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setBackground(BUTTER_YELLOW);
        button.setForeground(BLEU_OCEAN);
        button.setFont(FONT_BOLD_12);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BLEU_OCEAN, 1));
    }

    public static void styleWarningButton(JButton button) {
        button.setBackground(PASTEL_ORANGE);
        button.setForeground(BLEU_OCEAN);
        button.setFont(FONT_BOLD_12);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BLEU_OCEAN, 1));
    }

    public static void styleClearButton(JButton button) {
        button.setBackground(BLANC_CASSE);
        button.setForeground(BLEU_OCEAN);
        button.setFont(FONT_BOLD_12);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BLEU_CIEL, 1));
    }

    public static TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BLEU_CIEL, 1), title);
        border.setTitleFont(FONT_BOLD_12);
        border.setTitleColor(BLEU_OCEAN);
        return border;
    }

    public static TitledBorder createTitledBorder(String title, Color borderColor) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderColor, 1), title);
        border.setTitleFont(FONT_BOLD_12);
        border.setTitleColor(BLEU_OCEAN);
        return border;
    }

    public static void styleHeaderPanel(JPanel panel, Color bgColor) {
        panel.setBackground(bgColor);
        panel.setOpaque(true);
    }

    public static void styleMainPanel(JPanel panel) {
        panel.setBackground(BLANC_CASSE);
        panel.setOpaque(true);
    }

    public static void styleHeaderLabel(JLabel label) {
        label.setForeground(BLEU_OCEAN);
        label.setFont(FONT_BOLD_14);
    }

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BLANC_CASSE);
    }

    public static JPanel createMetricCard(String title, String value, Color bgColor, Color borderColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_11);
        lblTitle.setForeground(BLEU_OCEAN);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(FONT_BOLD_24);
        lblValue.setForeground(BLEU_OCEAN);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(5));
        card.add(lblValue);

        return card;
    }
}
