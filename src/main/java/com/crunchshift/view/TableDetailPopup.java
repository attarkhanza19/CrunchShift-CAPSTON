package com.crunchshift.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * A read-only, full-text details dialog for every CrunchShift table.
 * Does not alter data, sorting, column widths or existing row selection actions.
 */
public final class TableDetailPopup {
    private static final Color NAVY = new Color(27, 42, 92);
    private static final Color ORANGE = new Color(224, 123, 16);
    private static final Color PALE = new Color(239, 245, 253);

    private TableDetailPopup() { }

    public static void install(final JTable table, final String menuName) {
        if (table == null || Boolean.TRUE.equals(table.getClientProperty("crunchshift.detail.installed"))) return;
        table.putClientProperty("crunchshift.detail.installed", Boolean.TRUE);
        table.setToolTipText("Klik dua kali sel untuk melihat detail lengkap, atau klik kanan pada baris.");
        if (table.getTableHeader() != null) {
            table.getTableHeader().setToolTipText("Klik dua kali baris untuk membuka detail lengkap.");
        }

        JMenuItem openDetail = new JMenuItem("Lihat Detail Lengkap");
        JPopupMenu popup = new JPopupMenu();
        popup.add(openDetail);
        openDetail.addActionListener(e -> showSelected(table, menuName));
        table.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { maybePopup(e); }
            @Override public void mouseReleased(MouseEvent e) { maybePopup(e); }
            private void maybePopup(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                table.setRowSelectionInterval(row, row);
                popup.show(table, e.getX(), e.getY());
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e) || e.getClickCount() != 2) return;
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) return;
                // A single click on the photo column already opens the existing photo viewer.
                if ("Bukti Foto".equalsIgnoreCase(table.getColumnName(col))) return;
                table.setRowSelectionInterval(row, row);
                showDetail(table, row, menuName);
            }
        });
        table.getInputMap(JTable.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl ENTER"), "crunchshift.detail");
        table.getActionMap().put("crunchshift.detail", new javax.swing.AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                showSelected(table, menuName);
            }
        });
    }

    private static void showSelected(JTable table, String menuName) {
        int row = table.getSelectedRow();
        if (row >= 0) showDetail(table, row, menuName);
    }

    public static void showDetail(JTable table, int viewRow, String menuName) {
        if (viewRow < 0 || viewRow >= table.getRowCount()) return;
        Window owner = SwingUtilities.getWindowAncestor(table);
        JDialog dialog = new JDialog(owner, "Detail " + menuName, JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));

        JPanel title = new JPanel(new GridLayout(0, 1, 0, 5));
        title.setOpaque(false);
        JLabel heading = new JLabel("Detail " + menuName);
        heading.setForeground(NAVY);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 19));
        title.add(heading);
        JLabel description = new JLabel("Semua isi pada baris ini ditampilkan lengkap (hanya baca).");
        description.setForeground(new Color(102, 116, 139));
        description.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        title.add(description);
        root.add(title, BorderLayout.NORTH);

        JPanel entries = new DetailRows();
        entries.setLayout(new BoxLayout(entries, BoxLayout.Y_AXIS));
        entries.setBackground(Color.WHITE);
        entries.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 8));
        for (int col = 0; col < table.getColumnCount(); col++) {
            String name = table.getColumnName(col);
            Object value = table.getValueAt(viewRow, col); // view coordinates: correct after sorting/filtering
            String fullText = value == null ? "-" : value.toString();
            if (fullText.isBlank()) fullText = "-";

            JPanel entry = new JPanel(new BorderLayout(0, 5));
            entry.setAlignmentX(Component.LEFT_ALIGNMENT);
            entry.setBackground(col % 2 == 0 ? PALE : Color.WHITE);
            entry.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, col % 2 == 0 ? ORANGE : new Color(216, 225, 238)),
                    BorderFactory.createEmptyBorder(9, 12, 9, 12)));
            JLabel label = new JLabel(name);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(NAVY);
            entry.add(label, BorderLayout.NORTH);
            JTextArea valueArea = new JTextArea(fullText);
            valueArea.setEditable(false);
            valueArea.setLineWrap(true);
            valueArea.setWrapStyleWord(true);
            valueArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            valueArea.setForeground(new Color(38, 54, 84));
            valueArea.setBackground(entry.getBackground());
            valueArea.setBorder(null);
            valueArea.setMargin(new Insets(0, 0, 0, 0));
            valueArea.setFocusable(true); // user can select and copy full contents
            int lines = Math.max(1, Math.min(14, (fullText.length() + 57) / 58));
            valueArea.setRows(lines);
            entry.add(valueArea, BorderLayout.CENTER);
            entries.add(entry);
            entries.add(Box.createVerticalStrut(5));
        }
        JScrollPane scroll = new JScrollPane(entries,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(219, 228, 239)));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JLabel hint = new JLabel("Teks dapat dipilih dan disalin.");
        hint.setForeground(new Color(102, 116, 139));
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.add(hint, BorderLayout.WEST);
        JButton close = new JButton("Tutup");
        close.setFont(new Font("Segoe UI", Font.BOLD, 12));
        close.setForeground(Color.WHITE);
        close.setBackground(ORANGE);
        close.setPreferredSize(new Dimension(105, 34));
        close.addActionListener(e -> dialog.dispose());
        footer.add(close, BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setSize(610, Math.min(610, Math.max(340, 180 + table.getColumnCount() * 70)));
        dialog.setMinimumSize(new Dimension(420, 320));
        dialog.setLocationRelativeTo(owner);
        dialog.getRootPane().setDefaultButton(close);
        dialog.setVisible(true);
    }

    /** Track the viewport width so long text wraps instead of being cut on the right. */
    private static final class DetailRows extends JPanel implements javax.swing.Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 20; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) { return 140; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
