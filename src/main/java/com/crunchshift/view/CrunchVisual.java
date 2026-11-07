package com.crunchshift.view;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * Satu bahasa visual untuk setiap menu Admin Pusat, Admin Cabang, Supervisor, dan Staff.
 * Hanya mengatur tampilan; tidak menyentuh controller, data, hak akses, atau action listener.
 */
final class CrunchVisual {
    private static final Color NAVY = new Color(27,42,92);
    private static final Color ORANGE = new Color(224,123,16);
    private static final Color PALE = new Color(238,245,255);
    private static final Color BLUE_HEADER = new Color(227,237,251);
    private static final Color BG = new Color(248,250,252);
    private static final Color BORDER = new Color(214,225,239);
    private static final Color MUTED = new Color(91,107,134);
    private CrunchVisual() {}

    static void terapkan(String key, JPanel page, HalamanModul modul, String role) {
        if (!(page.getLayout() instanceof BorderLayout)) return;
        BorderLayout pageLayout=(BorderLayout)page.getLayout();
        page.setBackground(BG);
        page.setBorder(BorderFactory.createEmptyBorder(11,13,10,13));
        Component center=pageLayout.getLayoutComponent(BorderLayout.CENTER);
        if (!(center instanceof JPanel)) return;
        JPanel body=(JPanel)center;
        if (!(body.getLayout() instanceof BorderLayout)) return;
        BorderLayout bodyLayout=(BorderLayout)body.getLayout();
        body.setBackground(BG);
        bodyLayout.setHgap(13);
        Component leftC=bodyLayout.getLayoutComponent(BorderLayout.WEST);
        Component rightC=bodyLayout.getLayoutComponent(BorderLayout.CENTER);
        if (!(leftC instanceof JPanel) || !(rightC instanceof JPanel)) return;
        JPanel left=(JPanel)leftC;
        JPanel right=(JPanel)rightC;
        int leftWidth = "Poin".equals(key) && role != null && role.equalsIgnoreCase("Staff") ? 370 : 355;
        left.setPreferredSize(new Dimension(Math.min(leftWidth,322),450));
        boolean informative = isInformative(left);
        left.setBackground(informative ? PALE : Color.WHITE);
        left.setBorder(cardBorder());
        right.setBackground(Color.WHITE);
        right.setBorder(cardBorder());
        colorTitle(left, ORANGE);
        colorTitle(right, ORANGE);

        // Apply the same card language to the left and right sections for EVERY role.
        if (informative) fillNested(left, PALE);
        if (role != null && role.equalsIgnoreCase("Staff") && "Poin".equals(key)) {
            configurePointsSummary(left);
        }
        setButtonStyle(left);
        setButtonStyle(right);
        styleInputs(left);
        styleInputs(right);
        beautifyTable(modul.tabel());
        styleToolbar(page, modul);

        // Pertahankan hanya panel bantuan/panduan yang memang sudah ada
        // di desain asli masing-masing halaman; jangan menyisipkan panduan baru.
        body.revalidate();
        body.repaint();
    }

    private static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER,1,true),
            BorderFactory.createEmptyBorder(13,14,13,14));
    }
    private static void colorTitle(JPanel panel, Color accent) {
        if (!(panel.getLayout() instanceof BorderLayout)) return;
        Component head=((BorderLayout)panel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        if (head instanceof JLabel) {
            JLabel label=(JLabel)head;
            label.setFont(new Font("Segoe UI",Font.BOLD,16));
            label.setForeground(NAVY);
            label.setOpaque(true);
            label.setBackground(isInformative(panel) ? BLUE_HEADER : Color.WHITE);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0,5,0,0,accent),
                    BorderFactory.createEmptyBorder(7,11,7,3)));
        }
    }
    private static boolean isInformative(JPanel left) {
        if (!(left.getLayout() instanceof BorderLayout)) return false;
        Component heading=((BorderLayout)left.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        if (!(heading instanceof JLabel)) return false;
        String t=((JLabel)heading).getText().toLowerCase(Locale.ROOT);
        return t.contains("panduan") || t.contains("petunjuk") || t.contains("informasi")
          || t.contains("ringkasan") || t.contains("cara ") || t.contains("catatan verifikasi");
    }
    private static void fillNested(JPanel panel, Color c) {
        panel.setBackground(c);
        for(Component ch:panel.getComponents()) {
            if (ch instanceof JPanel) fillNested((JPanel)ch,c);
            else if(ch instanceof JLabel && !((JLabel)ch).getText().isBlank()) {
                JLabel l=(JLabel)ch;
                if(l.getForeground().equals(new Color(99,115,137)))l.setForeground(MUTED);
            }
        }
    }
    private static void configurePointsSummary(JPanel left) {
        left.setBackground(PALE);
        fillNested(left,PALE);
    }
    private static void setButtonStyle(Container c) {
        for(Component child:c.getComponents()) {
            if(child instanceof JButton) {
                JButton b=(JButton)child;
                boolean primary = isPrimary(b.getText());
                b.setFont(new Font("Segoe UI",Font.BOLD,12));
                b.setFocusPainted(false);
                b.setContentAreaFilled(false);
                b.setOpaque(true);
                b.setBackground(primary?ORANGE:Color.WHITE);
                b.setForeground(primary?Color.WHITE:NAVY);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primary?ORANGE:BORDER,1,true),
                        BorderFactory.createEmptyBorder(8,11,8,11)));
            } else if(child instanceof Container) setButtonStyle((Container)child);
        }
    }
    /**
     * Satu aksi utama oranye pada kartu form sebelah kiri.
     * Aksi lain putih agar tiga tombol tidak tampak sama-sama dominan.
     * Dipanggil sesudah pemasangan action tambahan setiap role.
     */
    static void rapikanWarnaAksi(JPanel page) {
        if (!(page.getLayout() instanceof BorderLayout)) return;
        Component bodyComp=((BorderLayout)page.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (!(bodyComp instanceof JPanel) || !(((JPanel)bodyComp).getLayout() instanceof BorderLayout)) return;
        Component leftComp=((BorderLayout)((JPanel)bodyComp).getLayout()).getLayoutComponent(BorderLayout.WEST);
        if (!(leftComp instanceof JPanel) || !leftComp.isVisible()) return;
        java.util.List<JButton> actions=new ArrayList<>();
        kumpulkanAksi((Container)leftComp, actions);
        JButton chosen=null;
        for(JButton b:actions){
            if(!b.isVisible() || b.getText()==null) continue;
            String text=b.getText().toLowerCase(Locale.ROOT).trim();
            // Pilih foto, Cari, dan tombol utilitas tidak dihitung sebagai aksi primer.
            if(text.contains("pilih foto") || text.contains("lihat foto") || text.contains("bersihkan")
                    || text.equals("cari") || text.equals("segarkan")) continue;
            if(chosen==null) chosen=b;
            if(isPrimary(b.getText()) || text.startsWith("ubah") || text.startsWith("verifikasi")) {
                chosen=b; break;
            }
        }
        // Keep exactly one primary orange action per group. For groups with three
        // substantive actions, the last secondary uses soft blue so their hierarchy
        // is consistent across every role (not just Staff Presensi).
        java.util.List<JButton> group=new ArrayList<>();
        for(JButton b:actions){
            if(!b.isVisible() || b.getText()==null)continue;
            String text=b.getText().toLowerCase(Locale.ROOT).trim();
            if(text.contains("pilih foto")||text.contains("lihat foto")
                    ||text.equals("cari")||text.equals("segarkan"))continue;
            group.add(b);
        }
        java.util.List<JButton> alternatives=new ArrayList<>(group);
        alternatives.remove(chosen);
        JButton softButton=(group.size()>=3 && alternatives.size()>=2)
                ? alternatives.get(alternatives.size()-1) : null;
        for(JButton b:actions){
            boolean main=b==chosen;
            Color fill=main?ORANGE:(b==softButton?new Color(236,243,253):Color.WHITE);
            b.setBackground(fill);
            b.setForeground(main?Color.WHITE:NAVY);
            b.setOpaque(true);
            b.setContentAreaFilled(false);
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(main?ORANGE:BORDER,1,true),
                    BorderFactory.createEmptyBorder(7,10,7,10)));
        }
        ((JPanel)leftComp).revalidate();
        ((JPanel)leftComp).repaint();
    }
    private static void kumpulkanAksi(Container parent, java.util.List<JButton> list){
        for(Component c: parent.getComponents()) {
            if(c instanceof JButton) list.add((JButton)c);
            else if(c instanceof Container) kumpulkanAksi((Container)c,list);
        }
    }

    private static void styleInputs(Container container) {
        for(Component child:container.getComponents()) {
            if(child instanceof JTextField || child instanceof JTextArea || child instanceof JPasswordField) {
                JComponent input=(JComponent)child;
                input.setFont(new Font("Segoe UI",Font.PLAIN,13));
                input.setBackground(Color.WHITE);
                input.setForeground(NAVY);
                input.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER,1,true),
                    BorderFactory.createEmptyBorder(4,7,4,7)));
            } else if(child instanceof Container && !(child instanceof JTable)) styleInputs((Container)child);
        }
    }
    private static boolean isPrimary(String text) {
        if(text==null)return false;
        String t=text.toLowerCase(Locale.ROOT);
        return t.contains("simpan")||t.contains("kirim")||t.contains("ajukan")||t.contains("tambah")
                ||t.contains("berikan")||t.contains("check-in")||t.contains("setujui");
    }
    private static void styleToolbar(JPanel page, HalamanModul mod) {
        mod.pencarian().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160,179,204)),
                BorderFactory.createEmptyBorder(4,7,4,7)));
        mod.pencarian().setFont(new Font("Segoe UI",Font.PLAIN,13));
        for(JButton b:new JButton[]{mod.tombolCari(),mod.tombolSegarkan()}){
            b.setFont(new Font("Segoe UI",Font.BOLD,12));
            b.setBackground(Color.WHITE);b.setForeground(NAVY);
            b.setFocusPainted(false);b.setContentAreaFilled(false);b.setOpaque(true);
            b.setBorder(BorderFactory.createLineBorder(new Color(174,190,214),1,true));
        }
    }
    private static void beautifyTable(JTable table) {
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));
        table.setForeground(NAVY);
        table.setRowHeight(37);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229,235,244));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(PALE);
        table.setSelectionForeground(NAVY);
        JTableHeader head=table.getTableHeader();
        head.setBackground(new Color(236,241,248));
        head.setForeground(NAVY);
        head.setFont(new Font("Segoe UI",Font.BOLD,12));
        head.setOpaque(true);
        head.setPreferredSize(new Dimension(0,42));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t,Object obj,boolean sel,
                                boolean focused,int row,int col) {
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,obj,sel,focused,row,col);
                String text=obj==null?"":String.valueOf(obj);
                l.setToolTipText(text);
                if(!sel)l.setBackground(row%2==0?Color.WHITE:new Color(250,252,255));
                l.setBorder(BorderFactory.createEmptyBorder(3,7,3,7));
                l.setVerticalAlignment(JLabel.CENTER);
                return l;
            }
        });
        if(table.getParent() instanceof JViewport) {
            JViewport viewport=(JViewport)table.getParent();
            if(viewport.getParent() instanceof JScrollPane) {
                JScrollPane sc=(JScrollPane)viewport.getParent();
                sc.setBorder(BorderFactory.createLineBorder(BORDER));
                sc.getViewport().setBackground(Color.WHITE);
            }
        }
    }
}
