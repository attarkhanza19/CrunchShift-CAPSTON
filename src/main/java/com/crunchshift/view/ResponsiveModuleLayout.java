package com.crunchshift.view;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;

/**
 * Penataan ulang komponen layout NetBeans yang dahulu berukuran absolut.
 * Komponen asli (beserta listener dan model datanya) tidak diganti/dihapus.
 * Semua fitur di semua portal memakai aturan ukuran dan keterbacaan yang sama.
 */
final class ResponsiveModuleLayout {
    private static final Color NAVY = new Color(27,42,92);
    private static final Color ORANGE = new Color(224,123,16);
    private ResponsiveModuleLayout() { }

    static void apply(JPanel page, HalamanModul modul) {
        if (!(page.getLayout() instanceof BorderLayout)) return;
        page.setPreferredSize(new Dimension(900, 585));
        page.setMinimumSize(new Dimension(680, 480));
        page.setBorder(BorderFactory.createEmptyBorder(12,14,11,14));
        Component bodyComponent = ((BorderLayout)page.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (!(bodyComponent instanceof JPanel)) return;
        JPanel body = (JPanel)bodyComponent;
        if (!(body.getLayout() instanceof BorderLayout)) return;
        BorderLayout bodyLayout = (BorderLayout)body.getLayout();
        Component leftComponent = bodyLayout.getLayoutComponent(BorderLayout.WEST);
        Component rightComponent = bodyLayout.getLayoutComponent(BorderLayout.CENTER);
        if (!(leftComponent instanceof JPanel) || !(rightComponent instanceof JPanel)) return;
        JPanel left = (JPanel)leftComponent;
        JPanel right = (JPanel)rightComponent;
        bodyLayout.setHgap(13);
        left.setPreferredSize(new Dimension(322, 490));
        left.setMinimumSize(new Dimension(280, 400));
        // CrunchVisual is responsible for the card borders and palette.
        // Do not compete with the original .form styling here.
        if (!(left.getLayout() instanceof BorderLayout)) return;
        Component formComp = ((BorderLayout)left.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (formComp instanceof JPanel) {
            JPanel form = (JPanel)formComp;
            if (form.getLayout() instanceof BorderLayout) {
                BorderLayout bl = (BorderLayout)form.getLayout();
                Component fields = bl.getLayoutComponent(BorderLayout.NORTH);
                Component actions = bl.getLayoutComponent(BorderLayout.CENTER);
                if (fields instanceof JPanel) formatFields((JPanel)fields);
                if (actions instanceof JPanel) formatActions((JPanel)actions);
            }
        }
        formatTable(modul.tabel());
        styleButtons(left);
        page.revalidate();
    }

    private static void styleButtons(Container parent) {
        for (Component c:parent.getComponents()) {
            if(c instanceof JButton) {
                JButton b=(JButton)c;
                String name=b.getText()==null?"":b.getText().trim().toLowerCase(java.util.Locale.ROOT);
                boolean primary=name.startsWith("simpan") || name.startsWith("tambah")
                        || name.startsWith("kirim") || name.startsWith("berikan")
                        || name.startsWith("ajukan") || name.startsWith("koreksi")
                        || name.startsWith("perbarui") || name.startsWith("check-in")
                        || name.startsWith("setujui") || name.startsWith("ubah password")
                        || name.startsWith("ubah evaluasi") || name.startsWith("ubah jadwal");
                b.setFont(new Font("Segoe UI",Font.BOLD,12));
                b.setFocusPainted(false);
                b.setContentAreaFilled(false);
                b.setOpaque(true);
                b.setBackground(primary?ORANGE:Color.WHITE);
                b.setForeground(primary?Color.WHITE:NAVY);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primary?ORANGE:new Color(214,225,239),1,true),
                        BorderFactory.createEmptyBorder(6,8,6,8)));
            } else if(c instanceof Container) styleButtons((Container)c);
        }
    }

    private static int preferredHeight(Component c) {
        if (c instanceof JLabel) return 19;
        if (c instanceof JCheckBox) return 23;
        if (c instanceof JScrollPane) return 60;
        if (c instanceof JButton) return 33;
        if (c instanceof JTextArea) return 55;
        return 29;
    }

    private static void formatFields(JPanel fields) {
        Component[] comps = Arrays.stream(fields.getComponents())
                .filter(c -> !(c instanceof Box.Filler)).toArray(Component[]::new);
        if (comps.length == 0) { fields.setPreferredSize(new Dimension(0, 0)); return; }
        fields.removeAll();
        fields.setOpaque(false);
        fields.setLayout(new GridBagLayout());
        boolean notesOnly = Arrays.stream(comps).allMatch(c -> c instanceof JLabel);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0;g.gridy=0;g.weightx=1;g.fill=GridBagConstraints.HORIZONTAL;
        g.anchor=GridBagConstraints.NORTHWEST;
        int total=0;
        for (Component c : comps) {
            int h = notesOnly ? 30 : preferredHeight(c);
            boolean label = c instanceof JLabel;
            if (c instanceof JLabel) {
                JLabel l = (JLabel)c;
                l.setFont(new Font("Segoe UI", notesOnly?Font.PLAIN:Font.BOLD, notesOnly?13:12));
                l.setForeground(notesOnly?new Color(76,96,130):NAVY);
                if(l.getText()!=null && l.getText().length()>29)
                    l.setToolTipText(l.getText());
            }
            if (c instanceof JComponent) {
                JComponent jc = (JComponent)c;
                jc.setPreferredSize(new Dimension(245, h));
                if (c instanceof JTextComponent) jc.setMinimumSize(new Dimension(80,h));
                if (c instanceof JComboBox) ((JComboBox<?>)c).setMaximumRowCount(10);
            }
            g.insets = notesOnly ? new Insets(4,5,5,3) : new Insets(label?2:0,0,label?1:3,0);
            fields.add(c,g); g.gridy++;
            total += h + (notesOnly?9:3);
        }
        // No rigid width: fields are always as wide as the left card.
        g.weighty=1; g.fill=GridBagConstraints.BOTH;g.insets=new Insets(0,0,0,0);
        fields.add(Box.createVerticalGlue(),g);
        fields.setPreferredSize(new Dimension(245,total+2));
        fields.revalidate();
    }

    private static void formatActions(JPanel actions) {
        Component[] comps = Arrays.stream(actions.getComponents())
                .filter(c -> !(c instanceof Box.Filler)).toArray(Component[]::new);
        if (comps.length == 0) return;
        actions.removeAll();
        actions.setOpaque(false);
        actions.setLayout(new GridBagLayout());
        int row=0;
        int i=0;
        while(i<comps.length) {
            Component first=comps[i];
            Component second=(i+1<comps.length?comps[i+1]:null);
            boolean pair=first instanceof JButton && second instanceof JButton
                    && fitsHalf((JButton)first) && fitsHalf((JButton)second);
            int count=pair?2:1;
            for(int index=0;index<count;index++){
                Component c=comps[i+index];
                int col=index;
                if(c instanceof JButton) {
                    JButton b=(JButton)c;
                    b.setPreferredSize(new Dimension(pair?128:270,32));
                    b.setMinimumSize(new Dimension(pair?100:140,32));
                    b.setToolTipText(b.getText());
                    b.setFont(new Font("Segoe UI",Font.BOLD,12));
                    b.setFocusPainted(false);
                    b.setHorizontalAlignment(SwingConstants.CENTER);
                } else if(c instanceof JLabel) {
                    ((JLabel)c).setToolTipText(((JLabel)c).getText());
                    ((JLabel)c).setPreferredSize(new Dimension(270,26));
                }
                GridBagConstraints g=new GridBagConstraints();
                g.gridx=col;g.gridy=row;g.gridwidth=pair?1:2;
                g.weightx=pair?0.5:1;g.fill=GridBagConstraints.HORIZONTAL;
                g.anchor=GridBagConstraints.NORTHWEST;
                g.insets=new Insets(2,col==0?0:3,2,col==0?(pair?3:0):0);
                actions.add(c,g);
            }
            i+=count;row++;
        }
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx=0;filler.gridy=row;filler.gridwidth=2;
        filler.weightx=1;filler.weighty=1;filler.fill=GridBagConstraints.BOTH;
        actions.add(Box.createVerticalGlue(),filler);
        actions.revalidate();
    }
    private static boolean fitsHalf(JButton button){
        FontMetrics fm=button.getFontMetrics(new Font("Segoe UI",Font.BOLD,12));
        return fm.stringWidth(button.getText())+28<=128;
    }

    static void formatTable(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (int ci=0;ci<table.getColumnCount();ci++) {
            TableColumn column=table.getColumnModel().getColumn(ci);
            String title=table.getColumnName(ci).trim().toLowerCase(java.util.Locale.ROOT);
            if(title.equals("id") || title.startsWith("id ")){
                column.setMinWidth(40);column.setMaxWidth(90);column.setPreferredWidth(65);
            } else if(title.equals("tanggal") || title.equals("jam masuk") || title.equals("jam keluar")
                    || title.equals("masuk") || title.equals("keluar")) {
                column.setMinWidth(55);column.setMaxWidth(135);column.setPreferredWidth(110);
            } else if(title.equals("poin") || title.equals("nilai acuan") || title.equals("jumlah poin")) {
                column.setMinWidth(52);column.setMaxWidth(130);column.setPreferredWidth(96);
            } else {
                column.setMinWidth(55);
                column.setMaxWidth(Integer.MAX_VALUE);
            }
        }
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setRowHeight(38);
        table.setIntercellSpacing(new Dimension(2,1));
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));
        table.getTableHeader().setPreferredSize(new Dimension(1,44));
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object value,boolean selected,
                     boolean focused,int row,int col){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,value,selected,focused,row,col);
                String full=value==null?"":value.toString();
                l.setToolTipText(full.isEmpty()?null:full);
                l.setForeground(NAVY);
                if(!selected)l.setBackground(row%2==0?Color.WHITE:new Color(249,251,255));
                // Keep one readable row per record; the full value is available on hover.
                l.setText(full);
                l.setBorder(BorderFactory.createEmptyBorder(3,6,3,5));
                return l;
            }
        });
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean selected,
                    boolean focus,int row,int column) {
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,selected,focus,row,column);
                String text=v==null?"":v.toString();
                int usable=Math.max(30,t.getColumnModel().getColumn(column).getWidth()-10);
                String display=shortHeader(text,usable,l.getFontMetrics(l.getFont()));
                String[] lines=wrapWords(display,l.getFontMetrics(l.getFont()),usable);
                StringBuilder html=new StringBuilder("<html><center>");
                for(int idx=0;idx<lines.length;idx++){
                    if(idx>0) html.append("<br>");
                    html.append(escape(lines[idx]));
                }
                html.append("</center></html>");
                l.setText(html.toString());
                l.setToolTipText(text);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setForeground(NAVY);l.setBackground(new Color(235,241,249));
                l.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
                return l;
            }
        });
        if (table.getParent() instanceof JViewport) {
            JViewport viewport=(JViewport)table.getParent();
            if (viewport.getParent() instanceof JScrollPane) {
                JScrollPane sc=(JScrollPane)viewport.getParent();
                sc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                sc.setBorder(BorderFactory.createLineBorder(new Color(218,228,242)));
            }
        }
    }
    /** Only shorten table headings if a narrow viewport cannot accommodate their full text. */
    private static String shortHeader(String full,int pixels,FontMetrics fm) {
        if(fm.stringWidth(full)<=pixels)return full;
        String s=full.trim().toLowerCase(java.util.Locale.ROOT);
        if(s.equals("karyawan"))return "Nama";
        if(s.equals("tanggal"))return "Tgl";
        if(s.equals("kehadiran"))return "Hadir";
        if(s.equals("verifikasi"))return "Verif.";
        if(s.equals("bukti foto"))return "Foto";
        if(s.equals("deskripsi kontribusi"))return "Kontribusi";
        if(s.equals("keterangan"))return "Info";
        if(s.equals("ringkasan"))return "Kegiatan";
        if(s.equals("id log"))return "ID";
        if(s.equals("catatan serah terima"))return "Serah Terima";
        return full;
    }

    /** Calculate hard line breaks using the actual font; Swing HTML CSS width is unreliable. */
    private static String[] wrapWords(String text,FontMetrics font,int maxWidth) {
        java.util.List<String> lines=new ArrayList<>();
        StringBuilder line=new StringBuilder();
        for(String word:text.replace('\n',' ').split("\\s+")) {
            if(word.isEmpty())continue;
            // No wide token can run under the next column.
            java.util.List<String> pieces=new ArrayList<>();
            if(font.stringWidth(word)>maxWidth){
                StringBuilder piece=new StringBuilder();
                for(char ch:word.toCharArray()){
                    if(piece.length()>0 && font.stringWidth(piece.toString()+ch)>maxWidth){
                        pieces.add(piece.toString());piece.setLength(0);
                    }
                    piece.append(ch);
                }
                if(piece.length()>0)pieces.add(piece.toString());
            }else pieces.add(word);
            for(String part:pieces){
                String candidate=line.length()==0?part:line+" "+part;
                if(font.stringWidth(candidate)<=maxWidth){line.setLength(0);line.append(candidate);}
                else {
                    if(line.length()!=0)lines.add(line.toString());
                    line.setLength(0);line.append(part);
                }
            }
        }
        if(line.length()!=0)lines.add(line.toString());
        return lines.toArray(new String[0]);
    }

    private static String escape(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
