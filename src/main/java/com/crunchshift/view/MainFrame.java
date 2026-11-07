package com.crunchshift.view;

import com.crunchshift.config.SessionManager;
import com.crunchshift.controller.AdminCabangController;
import com.crunchshift.controller.AdminPusatController;
import com.crunchshift.controller.StaffController;
import com.crunchshift.controller.SupervisorController;
import com.crunchshift.dao.impl.JadwalShiftDAOImpl;
import com.crunchshift.model.*;

import java.awt.*;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

/**
 * JFrame utama CrunchShift. Menu disusun di atas menggunakan JButton.
 * Semua halaman adalah JPanel Form TERPISAH yang dapat dibuka di NetBeans Design.
 * Controller/DAO/model asli tidak diubah; semua operasi dialirkan melalui controller lama.
 */
public class MainFrame extends javax.swing.JFrame {
    private final Map<String,HalamanModul> halaman = new LinkedHashMap<>();
    private String jenisPortal;
    private final AdminPusatController pusat = new AdminPusatController();
    private final AdminCabangController cabang = new AdminCabangController();
    private final StaffController staf = new StaffController();
    private final SupervisorController supervisor = new SupervisorController();

    /** Konstruktor kosong untuk pratinjau GUI Builder (tanpa koneksi basis data). */
    public MainFrame() {
        initComponents();
        pasangLogoCrunchShift();
        rapikanHeader();
    }
    public MainFrame(String jenisPortal) {
        this();
        this.jenisPortal=jenisPortal;
        setTitle("CrunchShift | " + jenisPortal);
        Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1360, Math.max(960, screen.width-90)), Math.min(790, Math.max(640, screen.height-90)));
        setMinimumSize(new Dimension(960,620));
        setLocationRelativeTo(null);
        Karyawan user=SessionManager.getCurrentUser();
        lblUser.setText(user==null?"Mode pratinjau":user.getNama()+"  |  "+user.getDeskripsiPeran());
        siapkanHalaman();
        siapkanNavigasi();
        tampilkan("Dashboard");
    }
    /** Logo transparan dan varian kontras untuk area biru navy, tanpa kotak putih. */
    private void pasangLogoCrunchShift() {
        java.net.URL url = getClass().getResource("/com/crunchshift/view/assets/icon_brand_header.png");
        if (url != null) {
            ImageIcon original = new ImageIcon(url);
            Image image = original.getImage();
            int tinggi = 47;
            int lebar = Math.max(1, original.getIconWidth() * tinggi / Math.max(1,original.getIconHeight()));
            lblBrand.setIcon(new ImageIcon(image.getScaledInstance(lebar, tinggi, Image.SCALE_SMOOTH)));
        }
        lblBrand.setText("");
        lblBrand.setOpaque(false);
        lblBrand.setBackground(new Color(27,42,92));
        lblBrand.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        JLabel deskripsi = new JLabel("Manajemen Shift, Aktivitas dan CrunchPoints");
        deskripsi.setForeground(Color.WHITE);
        deskripsi.setFont(new Font("Segoe UI",Font.BOLD,13));
        pnlBrand.add(deskripsi);
        pnlBrand.setLayout(new FlowLayout(FlowLayout.LEFT, 7, 8));
        pnlBrand.setOpaque(false);
    }

    /** Letak navbar: semua menu terlihat rapat di atas, Profil dan Keluar di bawah. */
    private void rapikanTampilanGlobal() {
        // Hanya tombol yang benar-benar tersedia bagi role aktif yang dimasukkan.
        // Tombol tersembunyi TIDAK boleh meninggalkan spacer kosong.
        pnlNav.removeAll();
        pnlNav.setLayout(new java.awt.BorderLayout());
        javax.swing.JPanel menuUtama = new javax.swing.JPanel();
        menuUtama.setOpaque(false);
        menuUtama.setLayout(new javax.swing.BoxLayout(menuUtama, javax.swing.BoxLayout.Y_AXIS));
        javax.swing.JPanel menuAkun = new javax.swing.JPanel();
        menuAkun.setOpaque(false);
        menuAkun.setLayout(new javax.swing.BoxLayout(menuAkun, javax.swing.BoxLayout.Y_AXIS));
        javax.swing.JButton[] urutan = {btnDashboard, btnKaryawan, btnShift, btnKategori,
            btnJadwal, btnPresensi, btnLog, btnPoin, btnMonitoring, btnLaporan,
            btnProfil, btnKeluar};
        for (javax.swing.JButton tombol : urutan) {
            if (!tombol.isVisible()) continue;
            tombol.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            tombol.setPreferredSize(new java.awt.Dimension(175, 41));
            tombol.setMinimumSize(new java.awt.Dimension(175, 41));
            tombol.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 41));
            javax.swing.JPanel tujuan = (tombol == btnProfil || tombol == btnKeluar)
                    ? menuAkun : menuUtama;
            if (tujuan.getComponentCount() > 0) tujuan.add(javax.swing.Box.createVerticalStrut(4));
            tujuan.add(tombol);
        }
        pnlNav.add(menuUtama, java.awt.BorderLayout.NORTH);
        pnlNav.add(menuAkun, java.awt.BorderLayout.SOUTH);
        pnlNav.revalidate();
        pnlNav.repaint();
    }

    /** Posisi identitas user pada ujung kanan header semua role. */
    private void rapikanHeader() {
        pnlBrand.remove(lblUser);
        lblUser.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        lblUser.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUser.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 16));
        pnlTop.add(lblUser, java.awt.BorderLayout.EAST);
        pnlTop.revalidate();
    }

    /** Semua portal memakai bahasa visual yang sama tanpa menambah menu panduan. */
    private void seragamkanModul(String key, JPanel panel, HalamanModul modul) {
        CrunchVisual.terapkan(key, panel, modul, jenisPortal);
    }

    /** Adaptasi lebar halaman ke jendela, tanpa menumpuk label dan tabel. */
    private static final class HalamanGulir extends JPanel implements javax.swing.Scrollable {
        HalamanGulir(JPanel content) {
            super(new BorderLayout());
            setBackground(new Color(248,250,252));
            add(content,BorderLayout.CENTER);
        }
        @Override public Dimension getPreferredScrollableViewportSize(){return getPreferredSize();}
        @Override public int getScrollableUnitIncrement(Rectangle r,int orientation,int direction){return 18;}
        @Override public int getScrollableBlockIncrement(Rectangle r,int orientation,int direction){return 140;}
        @Override public boolean getScrollableTracksViewportWidth(){
            Container p=getParent();
            return true;
        }
        @Override public boolean getScrollableTracksViewportHeight(){
            return getParent()!=null && getParent().getHeight() >= Math.min(550, getPreferredSize().height);
        }
    }

    /** Menampilkan isi ringkasan yang dihitung dari data dashboard terkini. */
    private void tampilkanRingkasanDashboard() {
        HalamanModul dashboard = v("Dashboard");
        if (dashboard == null) return;
        javax.swing.table.TableModel data = dashboard.tabel().getModel();
        java.util.Map<String, Integer> peran = new java.util.LinkedHashMap<>();
        java.util.Map<String, Integer> status = new java.util.LinkedHashMap<>();
        for (int i = 0; i < data.getRowCount(); i++) {
            String r = String.valueOf(data.getValueAt(i, 2));
            String st = String.valueOf(data.getValueAt(i, 3));
            peran.merge(r, 1, Integer::sum);
            status.merge(st, 1, Integer::sum);
        }
        StringBuilder info = new StringBuilder("Total karyawan pada dashboard: " + data.getRowCount() + "\n\nPeran:\n");
        peran.forEach((k, n) -> info.append("• ").append(k).append(": ").append(n).append("\n"));
        info.append("\nStatus:\n");
        status.forEach((k, n) -> info.append("• ").append(k).append(": ").append(n).append("\n"));
        javax.swing.JOptionPane.showMessageDialog(this, info.toString(),
                "Ringkasan Dashboard " + jenisPortal, javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    /** Rekap yang benar-benar menggunakan hasil laporan terkini, tanpa data contoh/hardcode. */
    private void tampilkanRekapLaporan() {
        HalamanModul halamanLaporan = v("Laporan");
        if (halamanLaporan == null) return;
        JTable asal = halamanLaporan.tabel();
        if (asal.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Belum ada data rekap yang dapat ditampilkan. Gunakan Segarkan untuk memuat data.",
                "Rekap Laporan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        for (int col = 0; col < asal.getColumnCount(); col++) model.addColumn(asal.getColumnName(col));
        for (int row = 0; row < asal.getRowCount(); row++) {
            Object[] nilai = new Object[asal.getColumnCount()];
            for (int col = 0; col < nilai.length; col++) nilai[col] = asal.getValueAt(row, col);
            model.addRow(nilai);
        }
        JTable rekap = new JTable(model);
        TableDetailPopup.install(rekap, "Rekap Laporan");
        rekap.setRowHeight(32);
        rekap.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rekap.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        rekap.setAutoCreateRowSorter(true);
        JScrollPane gulir = new JScrollPane(rekap);
        gulir.setPreferredSize(new Dimension(680, Math.min(400, 60 + rekap.getRowCount() * 34)));
        JPanel isi = new JPanel(new BorderLayout(0, 12));
        isi.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        isi.add(new JLabel("Rekap aktual " + (jenisPortal == null ? "CrunchShift" : jenisPortal)
            + " • " + rekap.getRowCount() + " baris rekap"), BorderLayout.NORTH);
        isi.add(gulir, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, isi, "Rekap Laporan CrunchShift",
                JOptionPane.PLAIN_MESSAGE);
    }

    /** Detail dari baris monitoring yang dipilih, mengikuti hasil filter dan sorting. */
    private void tampilkanDetailMonitoring() {
        HalamanModul halamanMonitoring = v("Monitoring");
        if (halamanMonitoring == null) return;
        JTable tabel = halamanMonitoring.tabel();
        int pilihan = tabel.getSelectedRow();
        if (pilihan < 0) {
            JOptionPane.showMessageDialog(this, "Pilih satu baris monitoring terlebih dahulu.",
                "Lihat Detail", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder detail = new StringBuilder();
        for (int kol = 0; kol < tabel.getColumnCount(); kol++) {
            detail.append(tabel.getColumnName(kol)).append(": ")
                  .append(str(tabel.getValueAt(pilihan, kol))).append("\n");
        }
        JTextArea text = new JTextArea(detail.toString(), 12, 48);
        text.setEditable(false);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        text.setBackground(new Color(248, 250, 252));
        JOptionPane.showMessageDialog(this, new JScrollPane(text),
                "Detail Monitoring", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Membandingkan angka sebagai angka, tanggal sebagai tanggal, dan teks secara natural. */
    private static int bandingkanData(Object a, Object b) {
        if (a == b) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        if (a instanceof Number && b instanceof Number)
            return new java.math.BigDecimal(a.toString()).compareTo(new java.math.BigDecimal(b.toString()));
        String x = a.toString().trim();
        String y = b.toString().trim();
        if (x.matches("-?\\d+(\\.\\d+)?") && y.matches("-?\\d+(\\.\\d+)?")) {
            try { return new java.math.BigDecimal(x).compareTo(new java.math.BigDecimal(y)); }
            catch (NumberFormatException ignored) { }
        }
        try { return java.time.LocalDateTime.parse(x.replace(' ', 'T')).compareTo(java.time.LocalDateTime.parse(y.replace(' ', 'T'))); }
        catch (java.time.format.DateTimeParseException ignored) { }
        try { return java.time.LocalDate.parse(x).compareTo(java.time.LocalDate.parse(y)); }
        catch (java.time.format.DateTimeParseException ignored) { }
        try { return java.time.LocalTime.parse(x).compareTo(java.time.LocalTime.parse(y)); }
        catch (java.time.format.DateTimeParseException ignored) { }
        java.text.Collator kolator = java.text.Collator.getInstance(new java.util.Locale("id", "ID"));
        kolator.setStrength(java.text.Collator.PRIMARY);
        return kolator.compare(x, y);
    }

    /** Make full explanatory text available on hover at any window size. */
    private void pasangTooltipPenjelasan(java.awt.Container parent) {
        if(parent instanceof javax.swing.JPanel && parent.getLayout() instanceof java.awt.GridLayout) {
            java.awt.GridLayout grid=(java.awt.GridLayout)parent.getLayout();
            // Form input dua kolom memiliki label yang mudah terpotong.
            // Susun label + input dalam satu kolom dengan ruang baris cukup.
            boolean adaInput=false;
            for(Component item:parent.getComponents())if(item instanceof JTextField || item instanceof JComboBox || item instanceof JPasswordField || item instanceof JScrollPane)adaInput=true;
            if(grid.getColumns()==2 && adaInput && parent.getComponentCount()>=2 && parent.getComponentCount()<=10){
                int n=parent.getComponentCount();
                parent.setLayout(new GridLayout(n,1,0,5));
                parent.setPreferredSize(new Dimension(300,n*37));
            }
            if(parent.getComponentCount()>=3 && grid.getColumns()==2) {
                boolean semuanyaLabel=true;
                for(java.awt.Component item:parent.getComponents())if(!(item instanceof javax.swing.JLabel)){semuanyaLabel=false;break;}
                if(semuanyaLabel) {
                    parent.setLayout(new java.awt.GridLayout(parent.getComponentCount(),1,0,8));
                    parent.setPreferredSize(new Dimension(290,Math.max(155,parent.getComponentCount()*53)));
                }
            }
        }
        for (java.awt.Component c : parent.getComponents()) {
            if (c instanceof javax.swing.JLabel) {
                javax.swing.JLabel lbl = (javax.swing.JLabel) c;
                String t = lbl.getText();
                if (t != null && !t.trim().isEmpty()) {
                    String plain=t.replaceAll("<[^>]+>", " ").replace("&amp;", "&").trim();
                    lbl.setToolTipText(plain);
                    if ((lbl.getName()==null || !lbl.getName().equals("no-wrap")) &&
                        lbl.getFont()!=null && lbl.getFont().getSize()<=14 &&
                        plain.length()>75 && !(lbl.getParent() instanceof javax.swing.table.JTableHeader)) {
                        lbl.setText("<html><div style='width:260px'>"+plain.replace("&","&amp;").replace("<","&lt;")+"</div></html>");
                    }
                }
            }
            if (c instanceof java.awt.Container) pasangTooltipPenjelasan((java.awt.Container) c);
        }
    }

    /**
     * Responsif bagi SEMUA role. Panel desain .form tidak ditimpa, hanya tata letak
     * toolbar/status saat aplikasi berjalan. Setiap komponen tetap milik form aslinya.
     */
    private void rapikanModulResponsif(JPanel halamanPanel, HalamanModul modul) {
        if (!(halamanPanel.getLayout() instanceof BorderLayout)) return;
        Component utara = ((BorderLayout) halamanPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        if (utara instanceof JPanel) {
            JPanel header = (JPanel) utara;
            if (header.getLayout() instanceof BorderLayout) {
                Component bar = ((BorderLayout)header.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
                if (bar instanceof JPanel) {
                    JPanel toolbar = (JPanel) bar;
                    java.util.List<Component> items = new ArrayList<>(Arrays.asList(toolbar.getComponents()));
                    toolbar.removeAll();
                    toolbar.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 6));
                    JTextField search = modul.pencarian();
                    search.setColumns(17);
                    search.setPreferredSize(new Dimension(165, 32));
                    modul.tombolCari().setPreferredSize(new Dimension(66, 32));
                    modul.tombolSegarkan().setPreferredSize(new Dimension(98, 32));
                    for (Component item : items) toolbar.add(item);
                    toolbar.setPreferredSize(new Dimension(0, 44));
                }
                Component heading = ((BorderLayout)header.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                if (heading instanceof JPanel) {
                    JPanel judul = (JPanel) heading;
                    judul.setVisible(false); // Acuan desain: cukup judul kartu dan toolbar pencarian
                    java.util.List<Component> labels = new ArrayList<>(Arrays.asList(judul.getComponents()));
                    judul.removeAll();
                    judul.setLayout(new BoxLayout(judul, BoxLayout.Y_AXIS));
                    judul.add(Box.createVerticalStrut(2));
                    for (Component item: labels) {
                        if (item instanceof JLabel) {
                            ((JLabel)item).setHorizontalAlignment(SwingConstants.LEFT);
                            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, item == labels.get(0) ? 49 : 41));
                            item.setPreferredSize(new Dimension(500, item == labels.get(0) ? 49 : 41));
                            ((JComponent)item).setAlignmentX(Component.LEFT_ALIGNMENT);
                        }
                        judul.add(item);
                    }
                }
            }
        }
        Component selatan=((BorderLayout)halamanPanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        if(selatan instanceof JPanel) {
            JPanel status=(JPanel) selatan;
            Component[] children=status.getComponents();
            status.removeAll();status.setLayout(new BorderLayout());
            if(children.length>0) {
                Component lbl=children[0];
                if(lbl instanceof JLabel) {
                    ((JLabel)lbl).setText("Siap.");
                    ((JLabel)lbl).setToolTipText("Status terakhir operasi");
                }
                status.add(lbl,BorderLayout.CENTER);
            }
            status.setPreferredSize(new Dimension(0,27));
        }
        Dimension desain=halamanPanel.getPreferredSize();
        halamanPanel.setPreferredSize(new Dimension(Math.max(650,Math.min(1020,desain.width)),Math.max(505,Math.min(550,desain.height))));
        halamanPanel.setMinimumSize(new Dimension(600,450));
    }

    private void tataAksiResponsif(JButton tombol) {
        if (tombol==null || !(tombol.getParent() instanceof JPanel))return;
        JPanel p=(JPanel)tombol.getParent();
        java.util.List<Component> list=new ArrayList<>(Arrays.asList(p.getComponents()));
        p.removeAll();p.setLayout(new FlowLayout(FlowLayout.LEFT, 9, 6));
        for(Component item:list) {
            if (item instanceof JButton) ((JButton)item).setPreferredSize(new Dimension(Math.max(110, Math.min(155, ((JButton)item).getFontMetrics(((JButton)item).getFont()).stringWidth(((JButton)item).getText())+26)),34));
            p.add(item);
        }
        p.revalidate();p.repaint();
    }

    /** Tooltips display the entire cell content when columns are narrow. */
    private void pasangTooltipTabel(final JTable table) {
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focused, int row, int col) {
                javax.swing.JLabel label = (javax.swing.JLabel) super.getTableCellRendererComponent(
                        t, value, selected, focused, row, col);
                String text = value == null ? "" : value.toString();
                label.setToolTipText(text.isEmpty() ? null : text);
                return label;
            }
        });
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setRowHeight(44);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        table.getTableHeader().setPreferredSize(new Dimension(0,38));
        table.setIntercellSpacing(new Dimension(8,2));
        table.setSelectionBackground(new Color(225,235,248));
        table.setSelectionForeground(new Color(27,42,92));
    }

    private void jalankanAksi(Runnable action){
        try {action.run();}catch(Exception ex){
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Kesalahan CrunchShift",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tambahTombol(java.awt.Container tujuan,String tulisan,Runnable aksi) {
        JButton b=new JButton(tulisan);
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setForeground(new Color(27,42,92));
        b.addActionListener(e->{try{aksi.run();}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"CrunchShift",JOptionPane.ERROR_MESSAGE);}});
        tujuan.add(b);tujuan.revalidate();tujuan.repaint();
    }

    /** Penempatan tambahan sesuai checklist tanpa mengubah komponen form asli. */
    private JButton aksiTambahan(String key, String tulisan, Runnable aksi) {
        HalamanModul modul = v(key);
        if (modul == null) return null;
        JPanel halaman = (JPanel) modul;
        JPanel badan = (JPanel) ((BorderLayout) halaman.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JPanel kiri = (JPanel) ((BorderLayout) badan.getLayout()).getLayoutComponent(BorderLayout.WEST);
        JPanel footer = (JPanel) ((BorderLayout) kiri.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        if (footer == null) {
            footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            footer.setOpaque(false);
            kiri.add(footer, BorderLayout.SOUTH);
        }
        JButton tombol = new JButton(tulisan);
        tombol.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boolean utama = tulisan.startsWith("Ubah") || tulisan.startsWith("Perbarui")
                || tulisan.startsWith("Koreksi") || tulisan.startsWith("Ajukan");
        tombol.setForeground(utama ? Color.WHITE : new Color(27, 42, 92));
        tombol.setBackground(utama ? new Color(224, 123, 16) : Color.WHITE);
        tombol.setFocusPainted(false);
        tombol.setContentAreaFilled(false);
        tombol.setOpaque(true);
        tombol.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215,225,239),1,true),
                BorderFactory.createEmptyBorder(8,10,8,10)));
        tombol.addActionListener(e -> jalankanAksi(aksi));
        // Aksi tambahan Log Staff masuk ke area tombol formulir yang sama.
        // Menghindari tombol "Kirim Aktivitas" tersembunyi saat tinggi jendela mengecil.
        if ("Log".equals(key) && staff() && modul.tombol(0) != null
                && modul.tombol(0).getParent() instanceof JPanel) {
            JPanel actions=(JPanel) modul.tombol(0).getParent();
            if (actions.getLayout() instanceof FlowLayout) {
                tombol.setPreferredSize(new Dimension(144,32));
                actions.add(tombol);
                if (footer.getComponentCount()==0 && footer.getParent()==kiri) kiri.remove(footer);
                actions.revalidate();
                return tombol;
            }
        }
        JPanel form = (JPanel) ((BorderLayout) kiri.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (form!=null && form.getLayout() instanceof BorderLayout) {
            Component actions=((BorderLayout)form.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (actions instanceof JPanel) {
                ((JPanel)actions).add(tombol);
                if(footer.getComponentCount()==0 && footer.getParent()==kiri) kiri.remove(footer);
                ResponsiveModuleLayout.apply(halaman,modul);
                return tombol;
            }
        }
        footer.add(tombol);
        return tombol;
    }

    private void batalPilih(String key) {
        HalamanModul modul = v(key);
        if (modul == null) return;
        modul.tabel().clearSelection();
        for (int i = 0; i < 6; i++) {
            JComponent komponen = modul.masukan(i);
            if (komponen instanceof JTextField) ((JTextField) komponen).setText("");
            else if (komponen instanceof JComboBox) ((JComboBox<?>) komponen).setSelectedIndex(-1);
        }
        modul.pesan("Pilihan dibersihkan. Form siap untuk penambahan data baru.");
    }


    /** Hanya untuk UI Staff: menghapus nominal dari nama kategori lama di database.
     *  Nilai dan ID kategori asli tidak berubah, sehingga evaluasi Supervisor tetap utuh. */
    private static String kategoriTanpaNominal(String nama) {
        if (nama == null) return "";
        // Nama kategori lama di MySQL memuat angka, misalnya "Kontribusi Dasar (Poin 25)".
        // Untuk Staff jangan pernah tampilkan angka tersebut; nilai kategori di DB tidak berubah.
        return nama.replaceAll("(?i)\\s*\\([^)]*\\b(?:poin|points?|nilai)\\b[^)]*\\)", "")
                   .replaceAll("(?i)\\s*[-–:]?\\s*(?:poin|points?|nilai)\\s*[:=]?\\s*\\d+\\s*$", "")
                   .trim();
    }

    private void bukaPengajuanPoin() {
        java.util.List<Karyawan> spvs=staf.daftarSupervisor();
        java.util.List<KategoriPoin> kategori=staf.daftarKategoriPoin();
        if(spvs.isEmpty()||kategori.isEmpty())throw new IllegalStateException("Supervisor atau kategori poin belum tersedia di database.");
        JComboBox<String> daftarSpv=new JComboBox<>();
        JComboBox<String> daftarKategori=new JComboBox<>();
        for(Karyawan u:spvs)daftarSpv.addItem(u.getIdKaryawan()+" - "+u.getNama());
        for (KategoriPoin k : kategori) {
            daftarKategori.addItem(kategoriTanpaNominal(k.getNamaKategori()));
        }
        daftarKategori.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list,
                Object value, int index, boolean selected, boolean focus) {
                return super.getListCellRendererComponent(list,
                    kategoriTanpaNominal(String.valueOf(value)), index, selected, focus);
            }
        });
        JTextArea kontribusi=new JTextArea(5,32);kontribusi.setLineWrap(true);kontribusi.setWrapStyleWord(true);
        JPanel form = new JPanel();
        form.setLayout(new javax.swing.BoxLayout(form, javax.swing.BoxLayout.Y_AXIS));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 14, 8, 14));
        for (JComboBox<String> box : new JComboBox[]{daftarSpv, daftarKategori}) {
            box.setMaximumSize(new java.awt.Dimension(440, 36));
            box.setPreferredSize(new java.awt.Dimension(380, 36));
            box.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        }
        JLabel spvLabel = new JLabel("Supervisor tujuan:");
        JLabel catLabel = new JLabel("Kategori kontribusi:");
        JLabel descLabel = new JLabel("Jelaskan kontribusi yang diajukan:");
        for (JLabel label : new JLabel[]{spvLabel, catLabel, descLabel}) {
            label.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }
        JScrollPane descScroll = new JScrollPane(kontribusi);
        descScroll.setPreferredSize(new java.awt.Dimension(380, 104));
        descScroll.setMaximumSize(new java.awt.Dimension(440, 110));
        descScroll.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(spvLabel); form.add(javax.swing.Box.createVerticalStrut(7)); form.add(daftarSpv);
        form.add(javax.swing.Box.createVerticalStrut(15));
        form.add(catLabel); form.add(javax.swing.Box.createVerticalStrut(7)); form.add(daftarKategori);
        form.add(javax.swing.Box.createVerticalStrut(15));
        form.add(descLabel); form.add(javax.swing.Box.createVerticalStrut(7)); form.add(descScroll);
        if(JOptionPane.showConfirmDialog(this,form,"Ajukan CrunchPoints",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return;
        String uraian=kontribusi.getText().trim();
        if(uraian.isEmpty()||uraian.length()>500)throw new IllegalArgumentException("Kontribusi wajib diisi (maksimal 500 karakter).");
        KategoriPoin pilihan=kategori.get(daftarKategori.getSelectedIndex());
        boolean ok=staf.ajukanCrunchPoints(idUser(),spvs.get(daftarSpv.getSelectedIndex()).getIdKaryawan(),pilihan.getIdKategori(),uraian,pilihan.getBatasMaksimalPoin());
        if(!ok)throw new IllegalStateException("Pengajuan gagal disimpan.");
        muat("Poin");JOptionPane.showMessageDialog(this,"Pengajuan tercatat dengan status Menunggu.");
    }

    /** Izin menggunakan jadwal yang sudah ada; tidak mengirim foto check-in. */
    private void ajukanIzinStaff() {
        List<JadwalShift> daftar = staf.getJadwalSaya(idUser());
        if (daftar.isEmpty()) throw new IllegalArgumentException("Jadwal shift Anda belum tersedia.");
        JComboBox<String> opsi = new JComboBox<>();
        for (JadwalShift j : daftar) opsi.addItem(j.getIdJadwal()+" - "+j.getTanggal()+" / "+j.getNamaShift());
        JTextArea alasan = new JTextArea(3, 25); alasan.setLineWrap(true); alasan.setWrapStyleWord(true);
        JPanel form = new JPanel(new GridLayout(0,1,5,6));
        form.add(new JLabel("Pilih jadwal shift:")); form.add(opsi);
        form.add(new JLabel("Alasan izin:")); form.add(new JScrollPane(alasan));
        if (JOptionPane.showConfirmDialog(this,form,"Ajukan Izin Kehadiran",
                JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION) return;
        String teks = alasan.getText().trim();
        if (teks.isBlank() || teks.length()>200) throw new IllegalArgumentException("Alasan izin wajib diisi, maksimal 200 karakter.");
        JadwalShift jadwal=daftar.get(opsi.getSelectedIndex());
        ok(staf.ajukanIzin(idUser(),jadwal.getIdJadwal(),teks),"Izin gagal: jadwal tidak tersedia atau sudah memiliki presensi.");
        muat("Presensi");
    }

    /** Edit Jadwal Supervisor menggunakan form kiri yang otomatis terisi saat baris dipilih. */
    private void ubahJadwalSupervisor() {
        int id=selectedId("Jadwal");
        JadwalShift dipilih=null;
        for (JadwalShift j:cabang.getAllJadwal()) if(j.getIdJadwal()==id){dipilih=j;break;}
        if(dipilih==null) throw new IllegalArgumentException("Jadwal tidak ditemukan.");
        HalamanModul form=v("Jadwal");
        // DAO mengubah tanggal dan shift saja; cegah pilihan karyawan lain diam-diam diabaikan.
        if(choiceId(form,0)!=dipilih.getIdKaryawan())
            throw new IllegalArgumentException("Karyawan pada jadwal ini tidak dapat diganti. Pilih karyawan semula atau buat jadwal baru.");
        int shiftId=choiceId(form,1);
        LocalDate tanggal;
        try {tanggal=LocalDate.parse(val(form,2));}
        catch(java.time.format.DateTimeParseException ex){
            throw new IllegalArgumentException("Tanggal harus berformat yyyy-MM-dd, contoh 2026-11-07.");
        }
        if(!confirm("Simpan perubahan jadwal untuk "+dipilih.getNamaKaryawan()+" pada "+tanggal+"?"))return;
        ok(supervisor.ubahJadwalShift(id,dipilih.getIdKaryawan(),shiftId,tanggal),
                "Jadwal bentrok atau tidak berhasil diperbarui.");
        muat("Jadwal");
    }

    private Absensi presensiDipilihSupervisor() {
        int id=selectedId("Presensi");
        for(Absensi a:supervisor.getSemuaAbsensi())if(a.getIdAbsensi()==id)return a;
        throw new IllegalArgumentException("Presensi tidak ditemukan.");
    }

    private void koreksiPresensiSupervisor() {
        Absensi a=presensiDipilihSupervisor();
        JComboBox<String> status=new JComboBox<>(new String[]{"Hadir","Terlambat","Izin","Tidak Hadir"});
        status.setSelectedItem(a.getStatusKehadiran());
        JTextField alasan=new JTextField(a.getCatatan()==null?"":a.getCatatan());
        JPanel form=new JPanel(new GridLayout(0,1,4,5));
        form.add(new JLabel("Status kehadiran:"));form.add(status);
        form.add(new JLabel("Keterangan koreksi:"));form.add(alasan);
        if(JOptionPane.showConfirmDialog(this,form,"Koreksi Absensi",JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return;
        ok(supervisor.koreksiKehadiran(a.getIdAbsensi(),(String)status.getSelectedItem(),alasan.getText().trim()),
                "Koreksi gagal. Masukkan keterangan yang valid (maksimal 200 karakter).");
        muat("Presensi");
    }

    /** Revisi log milik Staff; foto lama tetap digunakan jika foto baru tidak dipilih. */
    private void perbaruiLogStaff() {
        int id=selectedId("Log"); LogAktivitas dipilih=null;
        for(LogAktivitas l:staf.getRiwayatLogStaff(idUser()))if(l.getIdLog()==id){dipilih=l;break;}
        if(dipilih==null)throw new IllegalArgumentException("Log bukan milik akun ini.");
        if(!"Revisi".equals(dipilih.getStatusVerifikasi()) && !"Menunggu".equals(dipilih.getStatusVerifikasi()))
            throw new IllegalArgumentException("Log Terverifikasi tidak boleh diubah.");
        JTextArea ringkasan=new JTextArea(dipilih.getDeskripsiAktivitas(),3,25);
        JTextArea handover=new JTextArea(dipilih.getCatatanSupervisor(),3,25);
        JLabel lampiran=new JLabel("Foto lama tetap digunakan");
        final java.io.File[] fotoBaru=new java.io.File[1];
        JButton pilih=new JButton("Ganti Foto Dokumentasi (opsional)");
        pilih.addActionListener(e->{JFileChooser fc=new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPG / PNG","jpg","jpeg","png"));
            if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){fotoBaru[0]=fc.getSelectedFile();lampiran.setText(fotoBaru[0].getName());}});
        JPanel form=new JPanel(new GridLayout(0,1,4,5));
        if(dipilih.getCatatanRevisi()!=null && !dipilih.getCatatanRevisi().isBlank())
            form.add(new JLabel("Catatan revisi Supervisor: "+dipilih.getCatatanRevisi()));
        form.add(new JLabel("Ringkasan Kegiatan:"));form.add(new JScrollPane(ringkasan));
        form.add(new JLabel("Catatan Serah Terima:"));form.add(new JScrollPane(handover));
        form.add(pilih);form.add(lampiran);
        if(JOptionPane.showConfirmDialog(this,form,"Perbarui Log Kerja",JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return;
        String uraian=ringkasan.getText().trim(), serah=handover.getText().trim();
        if(uraian.isBlank() || serah.isBlank() || uraian.length()>500 || serah.length()>500)
            throw new IllegalArgumentException("Ringkasan dan serah terima wajib diisi, maksimal 500 karakter.");
        String baru=null;
        if(fotoBaru[0]!=null){
            if(!fotoBaru[0].isFile()||fotoBaru[0].length()>8L*1024*1024)
                throw new IllegalArgumentException("Foto maksimal 8 MB.");
            if(!previewFoto(fotoBaru[0],"Pratinjau Dokumentasi Baru"))return;
            String nama=fotoBaru[0].getName().toLowerCase(java.util.Locale.ROOT);
            if(!nama.matches(".*\\.(jpg|jpeg|png)$")) throw new IllegalArgumentException("Format harus JPG atau PNG.");
            try {if(javax.imageio.ImageIO.read(fotoBaru[0])==null)throw new java.io.IOException("Gambar tidak valid");
                String path=java.util.UUID.randomUUID()+nama.substring(nama.lastIndexOf('.'));
                java.nio.file.Path file=java.nio.file.Path.of(System.getProperty("user.dir"),"uploads",path);
                java.nio.file.Files.createDirectories(file.getParent());
                java.nio.file.Files.copy(fotoBaru[0].toPath(),file);
                baru="uploads/"+path;
            }catch(IOException ex){throw new IllegalStateException("Gagal menyalin foto: "+ex.getMessage(),ex);}
        }
        ok(staf.perbaruiLogStaff(id,idUser(),uraian,serah,baru),"Log gagal diperbarui.");
        muat("Log");
    }

    private void ubahEvaluasiPoin() {
        int id=selectedId("Poin"); CrunchPoints dipilih=null;
        for(CrunchPoints c:supervisor.getAllPointsHistory())if(c.getIdPoin()==id){dipilih=c;break;}
        if(dipilih==null || dipilih.getIdSupervisor()!=idUser())
            throw new IllegalArgumentException("Hanya evaluasi Supervisor ini yang dapat diubah.");
        JTextArea kontribusi=new JTextArea(dipilih.getCatatanApresiasi(),4,27);
        JComboBox<Integer> nilai=new JComboBox<>(new Integer[]{0,25,50,75,100});
        nilai.setSelectedItem(dipilih.getNilaiPoin());
        JComboBox<String> status=new JComboBox<>(new String[]{"Menunggu","Disetujui","Ditolak"});
        status.setSelectedItem(dipilih.getStatusVerifikasi());
        JPanel form=new JPanel(new GridLayout(0,1,4,5));
        form.add(new JLabel("Deskripsi Kontribusi:"));form.add(new JScrollPane(kontribusi));
        form.add(new JLabel("Jumlah Poin:"));form.add(nilai);
        form.add(new JLabel("Status Verifikasi:"));form.add(status);
        if(JOptionPane.showConfirmDialog(this,form,"Ubah Evaluasi Poin",JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return;
        ok(supervisor.ubahEvaluasiPoin(id,idUser(),kontribusi.getText().trim(),
                (Integer)nilai.getSelectedItem(),(String)status.getSelectedItem()),
                "Evaluasi tidak dapat diperbarui.");
        muat("Poin");
    }

    private void simpanProfilStaff() {
        ProfilPanel form=(ProfilPanel)v("Profil");
        Karyawan sekarang=new com.crunchshift.dao.impl.KaryawanDAOImpl().findById(idUser());
        if(!(sekarang instanceof Staff))throw new IllegalStateException("Hanya Staff dapat mengubah divisi dan bagian kerja.");
        String divisi=form.divisiDipilih(), bagian=form.bagianDipilih();
        if(divisi==null || divisi.isBlank() || bagian.isBlank() || bagian.length()>50)
            throw new IllegalArgumentException("Divisi dan Bagian Kerja wajib diisi (maksimal 50 karakter).");
        ((Staff)sekarang).setDivisi(divisi);((Staff)sekarang).setBagianKerja(bagian);
        ok(new com.crunchshift.dao.impl.KaryawanDAOImpl().update(sekarang),"Gagal menyimpan profil.");
        SessionManager.setCurrentUser(sekarang);
        lblUser.setText(sekarang.getNama()+"  |  "+sekarang.getDeskripsiPeran());
        muat("Profil");
    }

    private void ubahPasswordStaff() {
        ProfilPanel form=(ProfilPanel)v("Profil");
        String lama=form.sandiLama(), baru=form.sandiBaru();
        if(lama.isEmpty()||baru.length()<5)throw new IllegalArgumentException("Isi kata sandi lama dan kata sandi baru minimal 5 karakter.");
        if(lama.equals(baru))throw new IllegalArgumentException("Password baru harus berbeda dari sebelumnya.");
        ok(new com.crunchshift.dao.impl.KaryawanDAOImpl().ubahPasswordSendiri(idUser(),lama,baru),
                "Password lama tidak sesuai atau gagal diubah.");
        form.bersihkanSandi();
        // Sinkronkan session dengan kata sandi terbaru.
        Karyawan terbaru=new com.crunchshift.dao.impl.KaryawanDAOImpl().findById(idUser());
        if(terbaru!=null)SessionManager.setCurrentUser(terbaru);
    }


    /** Ubah kata sandi akun yang sedang login untuk Admin / Supervisor. */
    private void ubahPasswordPribadi() {
        javax.swing.JPasswordField lama = new javax.swing.JPasswordField(20);
        javax.swing.JPasswordField baru = new javax.swing.JPasswordField(20);
        javax.swing.JPanel fields = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 0, 6));
        fields.add(new javax.swing.JLabel("Kata sandi lama:"));
        fields.add(lama);
        fields.add(new javax.swing.JLabel("Kata sandi baru (minimal 5 karakter):"));
        fields.add(baru);
        int pilihan = javax.swing.JOptionPane.showConfirmDialog(this, fields,
                "Ubah Kata Sandi", javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.PLAIN_MESSAGE);
        if (pilihan != javax.swing.JOptionPane.OK_OPTION) return;
        char[] sandiLama = lama.getPassword(), sandiBaru = baru.getPassword();
        try {
            String lamaText = new String(sandiLama), baruText = new String(sandiBaru);
            if (lamaText.isEmpty() || baruText.length() < 5)
                throw new IllegalArgumentException("Isi kata sandi lama dan kata sandi baru minimal 5 karakter.");
            if (lamaText.equals(baruText))
                throw new IllegalArgumentException("Sandi baru harus berbeda dari sandi lama.");
            ok(new com.crunchshift.dao.impl.KaryawanDAOImpl().ubahPasswordSendiri(
                    idUser(), lamaText, baruText), "Kata sandi lama tidak sesuai atau gagal diubah.");
            Karyawan terbaru = new com.crunchshift.dao.impl.KaryawanDAOImpl().findById(idUser());
            if (terbaru != null) SessionManager.setCurrentUser(terbaru);
            muat("Profil");
        } finally {
            java.util.Arrays.fill(sandiLama, '\0');
            java.util.Arrays.fill(sandiBaru, '\0');
            lama.setText("");
            baru.setText("");
        }
    }

    private void putuskanPoin(String keputusan) {
        int id=selectedId("Poin");
        boolean found=false;
        for(CrunchPoints cp:supervisor.getAllPointsHistory())if(cp.getIdPoin()==id) {
            found=true;
            if(!"Menunggu".equals(cp.getStatusVerifikasi()))throw new IllegalArgumentException("Hanya pengajuan Menunggu yang dapat diputuskan.");
            if(cp.getIdSupervisor()!=idUser())throw new IllegalArgumentException("Pengajuan ini ditujukan kepada supervisor lain.");
            break;
        }
        if(!found)throw new IllegalArgumentException("Pengajuan tidak ditemukan.");
        if(!confirm(keputusan+" pengajuan nomor "+id+"?"))return;
        if(!supervisor.putuskanCrunchPoints(id,idUser(),keputusan))throw new IllegalStateException("Status pengajuan gagal diubah.");
        muat("Poin");
    }

    private void lihatDokumentasiLog() {
        int id=selectedId("Log");
        LogAktivitas selected=null;
        for(LogAktivitas l:(staff()?staf.getRiwayatLogStaff(idUser()):supervisor.getSemuaLog()))if(l.getIdLog()==id){selected=l;break;}
        if(selected==null)throw new IllegalArgumentException("Log tidak ditemukan.");
        String lokasi=selected.getDokumentasiKegiatan();
        JPanel view=panelFoto("Dokumentasi Log ID "+id,lokasi);
        JOptionPane.showMessageDialog(this,view,"Dokumentasi Kegiatan",JOptionPane.PLAIN_MESSAGE);
    }

    private void lihatFotoPresensi() {
        int id=selectedId("Presensi"); Absensi data=null;
        for(Absensi a:(staff()?staf.getRiwayatAbsensi(idUser()):supervisor.getSemuaAbsensi()))if(a.getIdAbsensi()==id){data=a;break;}
        if(data==null)throw new IllegalArgumentException("Presensi tidak ditemukan.");
        JPanel foto=new JPanel(new GridLayout(1,2,12,0));
        foto.add(panelFoto("Bukti Masuk",data.getFotoMasuk()));
        foto.add(panelFoto("Bukti Keluar",data.getFotoKeluar()));
        JOptionPane.showMessageDialog(this,foto,"Bukti Presensi ID "+id,JOptionPane.PLAIN_MESSAGE);
    }
    private JPanel panelFoto(String judul,String nama) {
        JPanel box=new JPanel(new BorderLayout(0,8));box.setPreferredSize(new Dimension(330,280));
        box.add(new JLabel(judul,SwingConstants.CENTER),BorderLayout.NORTH);
        JLabel gambar=new JLabel("Bukti foto tidak tersedia",SwingConstants.CENTER);
        gambar.setForeground(new Color(80,90,110));
        if(nama!=null && nama.startsWith("DB_FOTO:")) {
            String[] token=nama.split(":");
            try {
                byte[] raw=new com.crunchshift.dao.impl.AbsensiDAOImpl().ambilFoto(Integer.parseInt(token[1]),"MASUK".equals(token[2]));
                java.awt.image.BufferedImage img=raw==null?null:javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(raw));
                if(img!=null){
                    double scale=Math.min(300.0/img.getWidth(),225.0/img.getHeight());
                    gambar.setIcon(new ImageIcon(img.getScaledInstance(Math.max(1,(int)(img.getWidth()*scale)),Math.max(1,(int)(img.getHeight()*scale)),Image.SCALE_SMOOTH)));
                    gambar.setText("");
                } else gambar.setText("Bukti gambar tidak valid");
            }catch(Exception ex){gambar.setText("Foto gagal dibaca: "+ex.getMessage());}
        } else if(nama!=null&&!nama.trim().isEmpty()&&!nama.startsWith("TIDAK_ADA")) {
            // Resolusi hanya nama berkas, tidak menerima traversal path dari database.
            String aman=new java.io.File(nama).getName();
            java.io.File lokasi=new java.io.File(System.getProperty("user.dir"),"src/main/resources/assets/attendance/"+aman);
            if(!lokasi.isFile())lokasi=new java.io.File(System.getProperty("user.dir"),"assets/attendance/"+aman);
            if(!lokasi.isFile())lokasi=new java.io.File(System.getProperty("user.dir"),"uploads/"+aman);
            if(lokasi.isFile()) {
                javax.swing.ImageIcon asli=new javax.swing.ImageIcon(lokasi.getAbsolutePath());
                if(asli.getIconWidth()>0) gambar.setIcon(new javax.swing.ImageIcon(asli.getImage().getScaledInstance(300,225,Image.SCALE_SMOOTH)));
                gambar.setText(asli.getIconWidth()>0?"":"File tidak dapat ditampilkan");
            } else gambar.setText("File tidak ditemukan: "+aman);
        }
        box.add(gambar,BorderLayout.CENTER);return box;
    }

    private boolean pusat(){return "AdminPusat".equals(jenisPortal);}
    private boolean cabang(){return "AdminCabang".equals(jenisPortal);}
    private boolean spv(){return "Supervisor".equals(jenisPortal);}
    private boolean staff(){return "Staff".equals(jenisPortal);}
    private int idUser(){return SessionManager.getCurrentUser()==null?0:SessionManager.getCurrentUser().getIdKaryawan();}

    private void siapkanHalaman() {
        if (pusat()) {
            halaman.put("Dashboard", new DashboardAdminPanel());
            halaman.put("Karyawan", new KelolaKaryawanAdminPanel());
            halaman.put("Shift", new MasterShiftAdminPanel());
            halaman.put("Kategori", new KategoriPoinAdminPanel());
            halaman.put("Monitoring", new MonitoringAdminPanel());
            halaman.put("Laporan", new LaporanAdminPanel());
        } else if (cabang()) {
            halaman.put("Dashboard", new DashboardCabangPanel());
            halaman.put("Karyawan", new KelolaKaryawanAdminPanel());
            halaman.put("Shift", new MasterShiftAdminPanel());
            halaman.put("Kategori", new KategoriPoinAdminPanel());
            halaman.put("Laporan", new LaporanAdminPanel());
        } else if (spv()) {
            halaman.put("Dashboard", new DashboardSupervisorPanel());
            halaman.put("Karyawan", new AnggotaTimSupervisorPanel());
            halaman.put("Jadwal", new JadwalSupervisorPanel());
            halaman.put("Presensi", new VerifikasiPresensiSupervisorPanel());
            halaman.put("Log", new VerifikasiLogSupervisorPanel());
            halaman.put("Poin", new EvaluasiPoinSupervisorPanel());
            halaman.put("Laporan", new LaporanAdminPanel());
        } else if (staff()) {
            halaman.put("Dashboard", new DashboardStaffPanel());
            halaman.put("Jadwal", new JadwalStaffPanel());
            halaman.put("Presensi", new PresensiStaffPanel());
            halaman.put("Log", new LogStaffPanel());
            halaman.put("Poin", new PoinStaffPanel());
            halaman.put("Laporan", new LaporanAdminPanel());
        }
        halaman.put("Profil", new ProfilPanel());
        for (Map.Entry<String,HalamanModul> h : halaman.entrySet()) {
            // A smaller laptop window must scroll instead of cutting off fields/actions.
            javax.swing.JScrollPane gulir = new javax.swing.JScrollPane(new HalamanGulir((javax.swing.JPanel)h.getValue()),
                    javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gulir.setBorder(null);
            gulir.getViewport().setBackground(new Color(248,250,252));
            gulir.getVerticalScrollBar().setUnitIncrement(18);
            gulir.getHorizontalScrollBar().setUnitIncrement(18);
            gulir.getVerticalScrollBar().setPreferredSize(new Dimension(10,0));
            gulir.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override public void componentResized(java.awt.event.ComponentEvent ev) {
                    ((JPanel)h.getValue()).revalidate();
                }
            });
            pnlContent.add(gulir, h.getKey());
            final String key = h.getKey();
            h.getValue().tombolCari().addActionListener(e -> filter(h.getValue()));
            h.getValue().tombolSegarkan().addActionListener(e -> muat(key));
            h.getValue().pencarian().addActionListener(e -> filter(h.getValue()));
            rapikanModulResponsif((javax.swing.JPanel)h.getValue(), h.getValue());
            pasangTooltipTabel(h.getValue().tabel());
            // Semua tabel tiap role: dua kali klik atau klik kanan untuk membuka data lengkap.
            TableDetailPopup.install(h.getValue().tabel(), h.getKey());
            ResponsiveModuleLayout.apply((JPanel)h.getValue(),h.getValue());
            pasangTooltipPenjelasan((javax.swing.JPanel)h.getValue());
            seragamkanModul(h.getKey(), (javax.swing.JPanel)h.getValue(), h.getValue());
        }
        pasangAksi();
    }
    private void siapkanNavigasi() {
        java.util.Map<String,javax.swing.JButton> buttons = new java.util.LinkedHashMap<>();
        buttons.put("Dashboard",btnDashboard); buttons.put("Karyawan",btnKaryawan);
        buttons.put("Shift",btnShift); buttons.put("Kategori",btnKategori);
        buttons.put("Jadwal",btnJadwal); buttons.put("Presensi",btnPresensi);
        buttons.put("Log",btnLog); buttons.put("Poin",btnPoin);
        buttons.put("Monitoring",btnMonitoring); buttons.put("Laporan",btnLaporan);
        buttons.put("Profil",btnProfil);
        for (java.util.Map.Entry<String,javax.swing.JButton> e : buttons.entrySet()) {
            e.getValue().setVisible(halaman.containsKey(e.getKey()));
            e.getValue().addActionListener(a -> tampilkan(e.getKey()));
        }
        btnKeluar.addActionListener(e -> {
            com.crunchshift.config.SessionManager.clearSession(); dispose(); new LoginFrame().setVisible(true);
        });
        // Cukup ubah izin aksi; jangan timpa layout/desain form di runtime.
        HalamanModul k = v("Karyawan");
        if (k != null) { for (int i=0;i<3;i++) {
            javax.swing.JButton b=k.tombol(i); if(b!=null)b.setVisible(pusat()||cabang());
        } k.tampilkanForm(pusat()||cabang()); }
        if(cabang() && v("Kategori")!=null) {
            HalamanModul kategori=v("Kategori");
            kategori.tampilkanForm(false);
            for(int i=0;i<2;i++) if(kategori.tombol(i)!=null)kategori.tombol(i).setVisible(false);
        }
        HalamanModul j=v("Jadwal");
        if (j!=null) { for(int i=0;i<2;i++) {
            javax.swing.JButton b=j.tombol(i); if(b!=null)b.setVisible(cabang()||spv());
        } if(staff())j.tampilkanForm(false); }
        HalamanModul p=v("Presensi");
        if (p!=null) { for(int i=0;i<3;i++) {
            javax.swing.JButton b=p.tombol(i);
            if(b!=null)b.setVisible((i<2&&staff())||(i==2&&spv()));
        } if(spv())p.tampilkanForm(true); }
        HalamanModul lg=v("Log");
        if(lg!=null) { for(int i=0;i<3;i++) {
            javax.swing.JButton b=lg.tombol(i);
            if(b!=null)b.setVisible((i==0&&staff())||(i>=1&&spv()));
        } }
        HalamanModul c=v("Poin");
        if(c!=null) { javax.swing.JButton b=c.tombol(0);
            if(b!=null)b.setVisible(spv() || staff());
            if(staff()) { c.tampilkanForm(true); if(b!=null)b.setVisible(true); } }
        if (v("Profil") instanceof ProfilPanel) {
            ProfilPanel profil=(ProfilPanel)v("Profil");
            profil.nonaktifkanPencarian();
            if(staff())profil.siapkanEditStaff(()->jalankanAksi(()->simpanProfilStaff()),
                    ()->jalankanAksi(()->ubahPasswordStaff()));
            else profil.siapkanUbahSandi(()->jalankanAksi(()->ubahPasswordPribadi()));
        }
        if (pusat() || cabang()) {
            aksiTambahan("Shift","Batal Pilih / Bersihkan",()->batalPilih("Shift"));
        }
        if (pusat()) aksiTambahan("Kategori","Batal Pilih / Bersihkan",()->batalPilih("Kategori"));
        if (staff()) {
            ((PresensiStaffPanel)v("Presensi")).tombolAjukanIzin().addActionListener(e -> jalankanAksi(() -> ajukanIzinStaff()));
            aksiTambahan("Log","Perbarui Log Kerja",()->perbaruiLogStaff());
        }
        if (spv()) {
            aksiTambahan("Jadwal","Ubah Jadwal Shift",()->ubahJadwalSupervisor());
            aksiTambahan("Presensi","Koreksi Absensi",()->koreksiPresensiSupervisor());
            aksiTambahan("Poin","Ubah Evaluasi Poin",()->ubahEvaluasiPoin());
        }
        if (spv()) {
            EvaluasiPoinSupervisorPanel ep=(EvaluasiPoinSupervisorPanel)v("Poin");
            ep.btnSetujuiPengajuan().addActionListener(e->jalankanAksi(()->putuskanPoin("Disetujui")));
            ep.btnTolakPengajuan().addActionListener(e->jalankanAksi(()->putuskanPoin("Ditolak")));
            VerifikasiPresensiSupervisorPanel vp=(VerifikasiPresensiSupervisorPanel)v("Presensi");
            vp.btnTolakPresensi().addActionListener(e->jalankanAksi(()->{
                Absensi izinTerpilih=presensiDipilihSupervisor();
                if(!"Izin".equals(izinTerpilih.getStatusKehadiran()))
                    throw new IllegalArgumentException("Tolak Presensi hanya berlaku untuk pengajuan Izin.");
                String alasan=JOptionPane.showInputDialog(this,"Alasan penolakan:");
                if(alasan!=null && !alasan.trim().isEmpty()) {
                    if(supervisor.tolakAbsensi(selectedId("Presensi"),alasan.trim()))muat("Presensi");
                    else throw new IllegalStateException("Presensi gagal ditolak.");
                }
            }));
            VerifikasiLogSupervisorPanel vl=(VerifikasiLogSupervisorPanel)v("Log");
        }
        rapikanTampilanGlobal();
        for (HalamanModul h: halaman.values()) {
            ResponsiveModuleLayout.apply((JPanel)h,h);
            // Terapkan palet aksi terakhir setelah seluruh tombol tambahan dipasang.
            CrunchVisual.rapikanWarnaAksi((JPanel)h);
        }
    }
    private void tampilkan(String name){
        if(!halaman.containsKey(name))return;
        ((CardLayout)pnlContent.getLayout()).show(pnlContent,name);
        for (javax.swing.JButton b : new javax.swing.JButton[]{btnDashboard,btnKaryawan,btnShift,btnKategori,btnJadwal,btnPresensi,btnLog,btnPoin,btnMonitoring,btnLaporan,btnProfil}) {
            b.setBackground(new java.awt.Color(27,42,92));
            b.setForeground(java.awt.Color.WHITE);
        }
        javax.swing.JButton active = java.util.Map.ofEntries(
            java.util.Map.entry("Dashboard",btnDashboard), java.util.Map.entry("Karyawan",btnKaryawan),
            java.util.Map.entry("Shift",btnShift), java.util.Map.entry("Kategori",btnKategori),
            java.util.Map.entry("Jadwal",btnJadwal), java.util.Map.entry("Presensi",btnPresensi),
            java.util.Map.entry("Log",btnLog), java.util.Map.entry("Poin",btnPoin),
            java.util.Map.entry("Monitoring",btnMonitoring), java.util.Map.entry("Laporan",btnLaporan),
            java.util.Map.entry("Profil",btnProfil)).get(name);
        if (active != null) active.setBackground(new java.awt.Color(224,123,16));
        lblFooter.setText("  CrunchShift  /  "+name+"   |   Gunakan Segarkan untuk memuat ulang data.");
        muat(name);
    }
    private void muat(String name){
        try {
            switch(name){
                case "Dashboard":muatDashboard();break;
                case "Karyawan":muatKaryawan();break;
                case "Shift":muatShift();break;
                case "Kategori":muatKategori();break;
                case "Jadwal":muatJadwal();break;
                case "Presensi":muatPresensi();break;
                case "Log":muatLog();break;
                case "Poin":muatPoin();break;
                case "Monitoring":muatMonitoring();break;
                case "Laporan":muatLaporan();break;
                case "Profil":muatProfil();break;
                default:break;
            }
            HalamanModul p=halaman.get(name);p.pesan("Data diperbarui. Gunakan tombol yang tersedia untuk tindakan.");filter(p);
        } catch(Exception ex){gagal(name,ex);}
    }
    private void gagal(String k,Exception ex){
        if(halaman.containsKey(k))halaman.get(k).pesan("Gagal: "+ex.getMessage());
        JOptionPane.showMessageDialog(this,ex.getMessage(),"Kesalahan CrunchShift",JOptionPane.ERROR_MESSAGE);
    }
    private HalamanModul v(String key){return halaman.get(key);}
    private void tabel(String key,String[] cols,List<Object[]> data){
        DefaultTableModel m=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        for(Object[] r:data)m.addRow(r);
        JTable t=v(key).tabel();
        // Simpan kolom dan arah sorting lama saat Segarkan agar tidak kembali acak.
        java.util.List<? extends javax.swing.RowSorter.SortKey> urutanLama =
                t.getRowSorter() == null ? java.util.Collections.emptyList() :
                new java.util.ArrayList<>(t.getRowSorter().getSortKeys());
        t.setAutoCreateRowSorter(false);
        t.setModel(m);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(m);
        java.util.Comparator<Object> pembanding = MainFrame::bandingkanData;
        for (int i = 0; i < m.getColumnCount(); i++) sorter.setComparator(i, pembanding);
        t.setRowSorter(sorter);
        java.util.List<javax.swing.RowSorter.SortKey> urutanValid = new java.util.ArrayList<>();
        for (javax.swing.RowSorter.SortKey k : urutanLama) {
            if (k.getColumn() >= 0 && k.getColumn() < m.getColumnCount()) urutanValid.add(k);
        }
        if (!urutanValid.isEmpty()) sorter.setSortKeys(urutanValid);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Lebar awal berdasarkan isi/header; tidak memangkas teks panjang menjadi kolom sempit.
        for(int c=0;c<m.getColumnCount();c++){
            int width=Math.max(105,Math.min(260,m.getColumnName(c).length()*12+34));
            if(m.getColumnName(c).matches("(?i).*deskripsi.*|.*keterangan.*|.*ringkasan.*|.*divisi.*"))width=230;
            if(m.getColumnName(c).equalsIgnoreCase("ID")||m.getColumnName(c).startsWith("ID "))width=95;
            t.getColumnModel().getColumn(c).setPreferredWidth(width);
        }
        // Lebar tabel mengikuti viewport; tidak menyisakan blok abu-abu seperti Jadwal Staff.
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setResizingAllowed(false);
        ResponsiveModuleLayout.formatTable(t);
        t.revalidate();
    }
    private int selectedId(String key){
        JTable t=v(key).tabel();int r=t.getSelectedRow();
        if(r<0)throw new IllegalArgumentException("Pilih salah satu baris terlebih dahulu.");
        return Integer.parseInt(t.getModel().getValueAt(t.convertRowIndexToModel(r),0).toString());
    }
    private void filter(HalamanModul p){
        JTable t=p.tabel();if(t.getRowSorter() instanceof TableRowSorter){
            @SuppressWarnings("unchecked") TableRowSorter<TableModel> sorter=(TableRowSorter<TableModel>)t.getRowSorter();
            String q=p.pencarian().getText().trim();sorter.setRowFilter(q.isEmpty()?null:RowFilter.regexFilter("(?i)"+java.util.regex.Pattern.quote(q)));
        }
    }
    private static String str(Object o){return o==null?"-":o.toString();}
    private static String val(HalamanModul p,int i){
        JComponent c=p.masukan(i);
        if(c instanceof JTextField)return ((JTextField)c).getText().trim();
        if(c instanceof JComboBox)return str(((JComboBox<?>)c).getSelectedItem());
        return "";
    }
    private static void setVal(HalamanModul p,int i,String value){
        JComponent c=p.masukan(i);
        if(c instanceof JTextField)((JTextField)c).setText(value==null?"":value);
        else if(c instanceof JComboBox)((JComboBox<?>)c).setSelectedItem(value);
    }
    private int choiceId(HalamanModul p,int i){String s=val(p,i);if(!s.matches("\\d+\\s*-.*"))throw new IllegalArgumentException("Pilih data dari daftar.");return Integer.parseInt(s.split(" - ")[0]);}
    private void pilihComboMenurutId(HalamanModul p,int index,int id) {
        if(!(p.masukan(index) instanceof JComboBox))return;
        JComboBox<?> cb=(JComboBox<?>)p.masukan(index);
        for(int i=0;i<cb.getItemCount();i++){
            String item=String.valueOf(cb.getItemAt(i));
            if(item.startsWith(id+" - ")){cb.setSelectedIndex(i);return;}
        }
        // Jika akun sudah tidak aktif, tetap perlihatkan identitas lama agar pilihan tidak keliru.
        if(index==0 && cb instanceof JComboBox<?>) {
            for(JadwalShift j:cabang.getAllJadwal()) if(j.getIdKaryawan()==id){
                @SuppressWarnings("unchecked") JComboBox<String> combo=(JComboBox<String>)cb;
                combo.addItem(id+" - "+j.getNamaKaryawan()+" (tidak aktif)");
                combo.setSelectedIndex(combo.getItemCount()-1);
                return;
            }
        }
    }
    private void combo(HalamanModul p,int i,List<String> items){
        JComboBox<String> b=(JComboBox<String>)p.masukan(i);b.removeAllItems();
        for(String x:items)b.addItem(x);
    }
    private void run(String key,Runnable task){try{task.run();muat(key);}catch(Exception ex){gagal(key,ex);}}
    private void ok(boolean success,String msg){if(!success)throw new IllegalStateException(msg);JOptionPane.showMessageDialog(this,"Perubahan berhasil disimpan.");}
    private void bind(String key,int i,Runnable r){HalamanModul p=v(key);if(p==null)return;JButton b=p.tombol(i);if(b!=null)b.addActionListener(e->run(key,r));}
    private void bindInformasi(String key,int i,Runnable action){HalamanModul p=v(key);if(p==null)return;JButton b=p.tombol(i);if(b!=null)b.addActionListener(e->{try{action.run();}catch(Exception ex){gagal(key,ex);}});}
    private boolean confirm(String msg){return JOptionPane.showConfirmDialog(this,msg,"Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;}
    private void muatDashboard(){
        List<Object[]> rows=new ArrayList<>();
        if(staff()){
            Karyawan user=SessionManager.getCurrentUser();
            if(user!=null)rows.add(new Object[]{user.getIdKaryawan(),user.getNama(),user.getPeran(),user.isStatusAktif()?"Aktif":"Nonaktif"});
        }else{
            List<Karyawan> ks=spv()?supervisor.getAllStaff():(cabang()?cabang.getKaryawanCabang():pusat.getAllKaryawan());
            for(Karyawan k:ks)rows.add(new Object[]{k.getIdKaryawan(),k.getNama(),k.getPeran(),k.isStatusAktif()?"Aktif":"Nonaktif"});
        }
        tabel("Dashboard",new String[]{"ID","Karyawan","Peran","Status"},rows);
        if(staff()){
            JPanel main=(JPanel)v("Dashboard");
            JLabel[] hints=new JLabel[3];
            java.util.List<JLabel> ditemukan=new ArrayList<>();
            cariLabelPetunjuk(main,ditemukan);
            // Labels are selected by initial numbered hint text, not arbitrary positional components.
            for(JLabel hint:ditemukan){
                String original=hint.getText();
                if(original!=null && original.startsWith("01  "))hints[0]=hint;
                if(original!=null && original.startsWith("02  "))hints[1]=hint;
                if(original!=null && original.startsWith("03  "))hints[2]=hint;
            }
            JadwalShift shift=staf.getJadwalHariIni(idUser());
            Absensi absen=staf.getAbsensiHariIni(idUser());
            if(hints[0]!=null)hints[0].setText("Pengajuan disetujui: "+staf.getRiwayatPoin(idUser()).stream().filter(p -> "Disetujui".equals(p.getStatusVerifikasi())).count());
            if(hints[1]!=null)hints[1].setText("Shift hari ini: "+(shift==null?"Belum dijadwalkan":shift.getNamaShift()+" ("+jamSingkat(shift.getJamShift())+")"));
            if(hints[2]!=null)hints[2].setText("Presensi: "+(absen==null?"Belum absen":str(absen.getStatusKehadiran())+" / "+str(absen.getStatusVerifikasi())));
            for(JLabel lbl:hints)if(lbl!=null){lbl.setToolTipText(lbl.getText());lbl.setFont(new Font("Segoe UI",Font.PLAIN,13));}
        }
    }
    private void muatKaryawan(){
        List<Object[]> rows=new ArrayList<>();
        List<Karyawan> ks=spv()?supervisor.getAllStaff():(cabang()?cabang.getKaryawanCabang():pusat.getAllKaryawan());
        for(Karyawan k:ks){
            if(spv())rows.add(new Object[]{k.getIdKaryawan(),k.getNama(),k.getEmail(),k.getPeran(),divisiKaryawan(k),
                    k instanceof Staff?((Staff)k).getBagianKerja():"-",k.isStatusAktif()?"Aktif":"Nonaktif"});
            else rows.add(new Object[]{k.getIdKaryawan(),k.getNama(),k.getEmail(),k.getPeran(),divisiKaryawan(k),
                    k.isStatusAktif()?"Aktif":"Nonaktif"});
        }
        if(spv())tabel("Karyawan",new String[]{"ID","Nama","Email","Peran","Divisi / Area","Bagian Kerja","Status"},rows);
        else tabel("Karyawan",new String[]{"ID","Nama","Email","Peran","Divisi / Area","Status"},rows);
    }
    private void muatShift(){
        List<Object[]> rows=new ArrayList<>();
        for(Shift s:pusat.getAllShift())rows.add(new Object[]{s.getIdShift(),s.getNamaShift(),s.getJamMasuk(),s.getJamKeluar()});
        tabel("Shift",new String[]{"ID","Nama Shift","Jam Masuk","Jam Keluar"},rows);
    }
    private void muatKategori(){
        List<Object[]> rows=new ArrayList<>();
        for(KategoriPoin k:pusat.getAllKategoriPoin())rows.add(new Object[]{k.getIdKategori(),k.getNamaKategori(),k.getBatasMaksimalPoin(),k.getDeskripsi()});
        tabel("Kategori",new String[]{"ID","Nama Kategori","Nilai Acuan","Keterangan"},rows);
    }
    private void muatJadwal(){
        HalamanModul p=v("Jadwal");List<Object[]> rows=new ArrayList<>();
        List<JadwalShift> list=staff()?staf.getJadwalSaya(idUser()):cabang.getAllJadwal();
        for(JadwalShift j:list){
            if(staff())rows.add(new Object[]{j.getTanggal(),j.getNamaShift(),jamSingkat(j.getJamShift())});
            else rows.add(new Object[]{j.getIdJadwal(),j.getTanggal(),j.getNamaKaryawan(),j.getNamaShift(),jamSingkat(j.getJamShift())});
        }
        tabel("Jadwal",staff()?new String[]{"Tanggal","Shift","Jam Kerja"}:new String[]{"ID Jadwal","Tanggal","Karyawan","Shift","Jam Kerja"},rows);
        if(!staff()){
            List<String> karyawan=new ArrayList<>();
            for(Karyawan k:cabang.getKaryawanCabang())if(k.isStatusAktif())karyawan.add(k.getIdKaryawan()+" - "+k.getNama());
            combo(p,0,karyawan);
            List<String> shifts=new ArrayList<>();for(Shift s:cabang.getAllShift())shifts.add(s.getIdShift()+" - "+s.getNamaShift());combo(p,1,shifts);
        }
    }
    private static String format(ZonedDateTime dt){return dt==null?"-":dt.toLocalDateTime().toString().replace('T',' ');}
    /** Hanya tawarkan Lihat Foto bila BLOB tersedia atau berkas lama masih dapat ditemukan. */
    private static boolean buktiFotoAda(String lokasi, boolean log){
        if(lokasi==null || lokasi.isBlank() || lokasi.startsWith("TIDAK_ADA")) return false;
        if(lokasi.startsWith("DB_FOTO:")) return true;
        String nama=new java.io.File(lokasi).getName();
        java.nio.file.Path dasar=java.nio.file.Path.of(System.getProperty("user.dir"));
        java.nio.file.Path[] candidates={
            dasar.resolve("uploads").resolve(nama),
            dasar.resolve("src/main/resources/assets/attendance").resolve(nama),
            dasar.resolve("assets/attendance").resolve(nama)
        };
        for(java.nio.file.Path file:candidates)if(java.nio.file.Files.isRegularFile(file))return true;
        return false;
    }
    private void absRows(String key,List<Absensi> data){
        List<Object[]> rows=new ArrayList<>();
        for(Absensi a:data)rows.add(new Object[]{a.getIdAbsensi(),str(a.getTanggal()),str(a.getNamaKaryawan()),str(a.getNamaShift()),format(a.getWaktuMasuk()),format(a.getWaktuKeluar()),
            (buktiFotoAda(a.getFotoMasuk(),false) || buktiFotoAda(a.getFotoKeluar(),false))?"Lihat Foto":"Bukti tidak tersedia",a.getStatusKehadiran(),a.getStatusVerifikasi()});
        tabel(key,new String[]{"ID","Tanggal","Karyawan","Shift","Masuk","Keluar","Bukti Foto","Kehadiran","Verifikasi"},rows);
        if(staff() && "Presensi".equals(key)) {
            // ID tetap di TableModel untuk akses data walaupun kolomnya tidak terlihat oleh Staff.
            JTable t=v(key).tabel();
            t.removeColumn(t.getColumnModel().getColumn(0));
        }
    }
    private void muatPresensi(){
        if(staff())absRows("Presensi",staf.getRiwayatAbsensi(idUser()));
        else if(spv()){
            absRows("Presensi",supervisor.getSemuaAbsensi());
        }
        else absRows("Presensi",cabang.getRekapAbsensi());
    }
    private void muatMonitoring(){absRows("Monitoring",cabang()?cabang.getRekapAbsensi():pusat.getMonitoringAbsensi());}
    private void muatLog(){
        List<LogAktivitas> ls=staff()?staf.getRiwayatLogStaff(idUser()):supervisor.getSemuaLog();
        List<Object[]> rows=new ArrayList<>();
        for(LogAktivitas l:ls)rows.add(new Object[]{l.getIdLog(),l.getTanggal(),l.getNamaKaryawan(),l.getDeskripsiAktivitas(),l.getCatatanSupervisor(),l.getCatatanRevisi(),
            buktiFotoAda(l.getDokumentasiKegiatan(),true)?"Lihat Foto":"Bukti tidak tersedia",l.getStatusVerifikasi()});
        tabel("Log",new String[]{"ID Log","Tanggal","Karyawan","Ringkasan","Catatan Serah Terima","Catatan Revisi","Bukti Foto","Status"},rows);
    }
    private void muatPoin(){
        HalamanModul p=v("Poin"); List<CrunchPoints> ls=staff()?staf.getRiwayatPoin(idUser()):supervisor.getAllPointsHistory();
        List<Object[]> rows=new ArrayList<>();
        Map<Integer,String> deskripsi=new HashMap<>();
        List<KategoriPoin> allKategori=staff()?staf.daftarKategoriPoin():supervisor.getAllKategoriPoin();
        for(KategoriPoin cat:allKategori)deskripsi.put(cat.getIdKategori(),str(cat.getDeskripsi()));
        for(CrunchPoints k:ls) {
            Object tanggal=k.getTanggalPemberian()==null?"-":k.getTanggalPemberian().toLocalDate();
            if(staff())rows.add(new Object[]{k.getIdPoin(),tanggal,kategoriTanpaNominal(k.getNamaKategori()),k.getCatatanApresiasi(),k.getNilaiPoin(),k.getStatusVerifikasi()});
            else rows.add(new Object[]{k.getIdPoin(),tanggal,k.getNamaStaff(),k.getNamaKategori(),k.getCatatanApresiasi(),k.getNilaiPoin(),k.getStatusVerifikasi()});
        }
        if(staff())tabel("Poin",new String[]{"ID Pengajuan","Tanggal","Kategori","Deskripsi Kontribusi","Jumlah Poin","Status"},rows);
        else tabel("Poin",new String[]{"ID Poin","Tanggal","Staff","Kategori","Deskripsi Kontribusi","Poin","Status"},rows);
        if(staff()) {
            int saldo=staf.getSaldoPoinStaff(idUser());
            ((PoinStaffPanel)p).tampilkanSaldoDanBadge(saldo,staf.tentukanBadgeLevel(saldo));
        }
        if(spv()){
            List<String> users=new ArrayList<>();for(Karyawan k:supervisor.getAllStaff())users.add(k.getIdKaryawan()+" - "+k.getNama());combo(p,0,users);
            List<String> cats=new ArrayList<>();for(KategoriPoin k:supervisor.getAllKategoriPoin())cats.add(k.getIdKategori()+" - "+k.getNamaKategori());combo(p,1,cats);
        }
    }
    private void muatLaporan(){
        List<Object[]> rows=new ArrayList<>();
        if(pusat()){
            rows.add(new Object[]{"Karyawan aktif",pusat.getTotalKaryawanAktif(),"Seluruh perusahaan"});
            rows.add(new Object[]{"Total poin",pusat.getTotalPoinNasional(),"Keseluruhan poin"});
        }else if(cabang()){
            rows.add(new Object[]{"Karyawan cabang",cabang.getTotalPegawaiCabang(),"Daftar staff dan supervisor"});
            rows.add(new Object[]{"Jadwal hari ini",cabang.getJumlahShiftHariIni(),"Data cabang"});
        }else if(spv()){
            rows.add(new Object[]{"Presensi menunggu",supervisor.getJumlahPresensiPending(),"Perlu ditinjau"});
            rows.add(new Object[]{"Log menunggu",supervisor.getJumlahLogPending(),"Perlu diverifikasi"});
        }else{
            rows.add(new Object[]{"Pengajuan disetujui",staf.getRiwayatPoin(idUser()).stream().filter(p -> "Disetujui".equals(p.getStatusVerifikasi())).count(),"Jumlah pengajuan"});
            rows.add(new Object[]{"Log harian",staf.getRiwayatLogStaff(idUser()).size(),"Riwayat pribadi"});
        }
        tabel("Laporan",new String[]{"Jenis Data","Jumlah","Keterangan"},rows);
    }
    private void muatProfil(){
        List<Object[]> rows=new ArrayList<>();Karyawan u=new com.crunchshift.dao.impl.KaryawanDAOImpl().findById(idUser());
        if(u!=null){rows.add(new Object[]{"ID Karyawan",u.getIdKaryawan()});rows.add(new Object[]{"Nama",u.getNama()});rows.add(new Object[]{"Email",u.getEmail()});rows.add(new Object[]{"Peran",u.getDeskripsiPeran()});
            rows.add(new Object[]{"Divisi / Area",divisiKaryawan(u)});
            if(u instanceof Staff)rows.add(new Object[]{"Bagian Kerja",((Staff)u).getBagianKerja()});
            rows.add(new Object[]{"Status",u.isStatusAktif()?"Aktif":"Nonaktif"});
            // Kata sandi sengaja tidak dicantumkan dalam tabel Profil.
            }
        tabel("Profil",new String[]{"Atribut","Nilai"},rows);
        if(staff() && u instanceof Staff)((ProfilPanel)v("Profil")).muatDataStaff(((Staff)u).getDivisi(),((Staff)u).getBagianKerja());
    }
    private void cariLabelPetunjuk(Container parent,List<JLabel> labels){
        for(Component c:parent.getComponents()){
            if(c instanceof JLabel)labels.add((JLabel)c);
            if(c instanceof Container)cariLabelPetunjuk((Container)c,labels);
        }
    }
    private void periksaIdentitas(String nama,String email,String password,boolean wajibPassword){
        if(nama==null || !nama.trim().matches("(?U)[\\p{L}][\\p{L} .'-]{1,99}"))
            throw new IllegalArgumentException("Nama harus terdiri atas huruf, bukan angka (minimal 2 karakter).");
        if(email==null || !email.trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            throw new IllegalArgumentException("Masukkan alamat email yang valid, contoh nama@domain.com.");
        if(wajibPassword || (password!=null && !password.isEmpty())){
            if(password==null || password.length()<5)
                throw new IllegalArgumentException("Kata sandi minimal 5 karakter.");
        }
    }
    private String divisiKaryawan(Karyawan k){
        if(k instanceof Staff)return str(((Staff)k).getDivisi());
        if(k instanceof Supervisor)return str(((Supervisor)k).getAreaTanggungJawab());
        if(k instanceof Admin)return str(((Admin)k).getDepartemen());
        return "-";
    }
    private java.io.File ambilFotoPresensi(String judul) {
        PresensiStaffPanel pp = (PresensiStaffPanel) v("Presensi");
        if (pp.fotoTerpilih() != null) return pp.fotoTerpilih();
        JFileChooser fc=new JFileChooser();fc.setDialogTitle(judul);
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Foto JPG / PNG", "jpg", "jpeg", "png"));
        if(fc.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION)return null;
        pp.setFotoTerpilih(fc.getSelectedFile());
        return fc.getSelectedFile();
    }
    private boolean previewFoto(java.io.File file,String judul){
        if(file==null || !file.isFile())throw new IllegalArgumentException("Berkas foto tidak ditemukan.");
        String ext=file.getName().toLowerCase(java.util.Locale.ROOT);
        if(!(ext.endsWith(".png")||ext.endsWith(".jpg")||ext.endsWith(".jpeg")))
            throw new IllegalArgumentException("Foto harus berupa JPG, JPEG, atau PNG.");
        javax.swing.ImageIcon foto=new javax.swing.ImageIcon(file.getAbsolutePath());
        if(foto.getIconWidth()<=0)throw new IllegalArgumentException("File yang dipilih bukan gambar valid.");
        double ratio=Math.min(480.0/foto.getIconWidth(),320.0/foto.getIconHeight());
        JLabel gambar=new JLabel(new ImageIcon(foto.getImage().getScaledInstance(
            Math.max(1,(int)(foto.getIconWidth()*ratio)),Math.max(1,(int)(foto.getIconHeight()*ratio)),Image.SCALE_SMOOTH)));
        JPanel panel=new JPanel(new BorderLayout(5,8));
        panel.add(new JLabel("Periksa foto sebelum presensi dikirim:"),BorderLayout.NORTH);
        panel.add(gambar,BorderLayout.CENTER);
        return JOptionPane.showConfirmDialog(this,panel,judul,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;
    }
    private String jamSingkat(String jam){
        if(jam==null)return "-";
        return jam.replaceAll("(\\d{2}:\\d{2}):\\d{2}","$1");
    }
    private java.time.LocalDate tanggalDari(Object raw){
        if(raw==null)return null;
        String value=raw.toString();
        if(value.length()<10)return null;
        try {return java.time.LocalDate.parse(value.substring(0,10));}
        catch(Exception ex){return null;}
    }
    private java.time.LocalDate[] pilihPeriode(){
        JComboBox<String> jenis=new JComboBox<>(new String[]{"Mingguan (7 hari)","Bulanan (bulan kalender)","Semua data"});
        JSpinner tanggal=new JSpinner(new SpinnerDateModel());
        tanggal.setEditor(new JSpinner.DateEditor(tanggal,"dd/MM/yyyy"));
        JPanel panel=new JPanel(new GridLayout(0,1,5,5));
        panel.add(new JLabel("Jenis rekap:"));panel.add(jenis);
        panel.add(new JLabel("Tanggal acuan periode:"));panel.add(tanggal);
        if(JOptionPane.showConfirmDialog(this,panel,"Pilih Periode Rekap",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return null;
        LocalDate day=((java.util.Date)tanggal.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        if(jenis.getSelectedIndex()==2)return new LocalDate[]{LocalDate.MIN,LocalDate.MAX};
        if(jenis.getSelectedIndex()==1)return new LocalDate[]{day.withDayOfMonth(1),day.withDayOfMonth(day.lengthOfMonth())};
        return new LocalDate[]{day.minusDays(6),day};
    }
    private String[] laporanKolom=new String[]{"Jenis","Tanggal","Karyawan","Keterangan","Status / Nilai"};
    private java.util.List<Object[]> buatRekap(LocalDate dari,LocalDate sampai){
        List<Object[]> rows=new ArrayList<>();
        List<Absensi> presensi=staff()?staf.getRiwayatAbsensi(idUser()):spv()?supervisor.getSemuaAbsensi():cabang()?cabang.getRekapAbsensi():pusat.getMonitoringAbsensi();
        for(Absensi a:presensi){LocalDate day=a.getTanggal();if(day!=null&&!day.isBefore(dari)&&!day.isAfter(sampai))
            rows.add(new Object[]{"Presensi",day,a.getNamaKaryawan(),a.getNamaShift(),a.getStatusKehadiran()+" / "+a.getStatusVerifikasi()});}
        if(staff()||spv()||pusat()){
            List<LogAktivitas> logs=staff()?staf.getRiwayatLogStaff(idUser()):spv()?supervisor.getSemuaLog():new com.crunchshift.dao.impl.LogAktivitasDAOImpl().findAll();
            for(LogAktivitas l:logs){LocalDate day=l.getTanggal();if(day!=null&&!day.isBefore(dari)&&!day.isAfter(sampai))
                rows.add(new Object[]{"Log Aktivitas",day,l.getNamaKaryawan(),l.getDeskripsiAktivitas(),l.getStatusVerifikasi()});}
            List<CrunchPoints> points=staff()?staf.getRiwayatPoin(idUser()):spv()?supervisor.getAllPointsHistory():new com.crunchshift.dao.impl.CrunchPointsDAOImpl().findAll();
            for(CrunchPoints pt:points){LocalDate day=pt.getTanggalPemberian()==null?null:pt.getTanggalPemberian().toLocalDate();
                if(day!=null&&!day.isBefore(dari)&&!day.isAfter(sampai))
                    rows.add(new Object[]{"CrunchPoints",day,pt.getNamaStaff(),staff()?kategoriTanpaNominal(pt.getNamaKategori()):pt.getNamaKategori(),(staff()?pt.getStatusVerifikasi():pt.getNilaiPoin()+" / "+pt.getStatusVerifikasi())});}
        }
        rows.sort((a,b)->bandingkanData(b[1],a[1]));
        return rows;
    }
    private void tampilkanRekapBerperiode(){
        LocalDate[] range=pilihPeriode();if(range==null)return;
        List<Object[]> rows=buatRekap(range[0],range[1]);
        DefaultTableModel model=new DefaultTableModel(laporanKolom,0){public boolean isCellEditable(int r,int c){return false;}};
        for(Object[] r:rows)model.addRow(r);
        JTable table=new JTable(model);table.setRowHeight(32);table.setAutoCreateRowSorter(true);
        TableDetailPopup.install(table, "Rekap Periode");
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        JScrollPane sp=new JScrollPane(table);sp.setPreferredSize(new Dimension(820,430));
        JOptionPane.showMessageDialog(this,sp,"Rekap "+range[0]+" s.d. "+range[1]+" ("+rows.size()+" data)",JOptionPane.PLAIN_MESSAGE);
    }
    private void eksporRekapBerperiode(){
        LocalDate[] range=pilihPeriode();if(range==null)return;
        List<Object[]> rows=buatRekap(range[0],range[1]);
        JFileChooser fc=new JFileChooser();fc.setSelectedFile(new File("rekap_crunchshift_"+range[0]+"_"+range[1]+".csv"));
        if(fc.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        try(PrintWriter w=new PrintWriter(new java.io.OutputStreamWriter(new FileOutputStream(fc.getSelectedFile()),java.nio.charset.StandardCharsets.UTF_8))){
            w.println("sep=,");
            for(int i=0;i<laporanKolom.length;i++){if(i>0)w.print(',');w.print(esc(laporanKolom[i]));}w.println();
            for(Object[] row:rows){for(int i=0;i<row.length;i++){if(i>0)w.print(',');w.print(esc(row[i]));}w.println();}
        }catch(IOException ex){throw new IllegalStateException("Ekspor gagal: "+ex.getMessage(),ex);}
        JOptionPane.showMessageDialog(this,"Berhasil mengekspor "+rows.size()+" catatan untuk periode yang dipilih.");
    }
    private void pasangAksi(){
        bindInformasi("Dashboard",0,()->tampilkanRingkasanDashboard());
        bind("Karyawan",0,()->{
            if(!pusat()&&!cabang())return;
            HalamanModul p=v("Karyawan");
            String role=val(p,3),detail=val(p,4);
            Karyawan k;
            if("Admin".equals(role)){
                if(!java.util.Arrays.asList("administrasi","produksi","logistik").contains(detail))throw new IllegalArgumentException("Pilih departemen Admin yang valid.");
                Admin a=new Admin();a.setDepartemen(detail);a.setTingkatanAkses(cabang()?"Cabang":"Pusat");k=a;}
            else if("Supervisor".equals(role)){Supervisor s=new Supervisor();s.setAreaTanggungJawab(detail);s.setShiftSupervisi("Pagi");k=s;}
            else{Staff s=new Staff();s.setDivisi(detail);s.setBagianKerja("Operator");k=s;}
            periksaIdentitas(val(p,0),val(p,1),val(p,2),true);
            k.setNama(val(p,0));k.setEmail(val(p,1));k.setPassword(val(p,2));k.setStatusAktif(true);
            ok(pusat.tambahKaryawan(k),"Gagal menambah karyawan. Periksa email unik dan tipe role.");
        });
        bind("Karyawan",1,()->{
            if(!pusat()&&!cabang())return;HalamanModul p=v("Karyawan");Karyawan k=pusat.getKaryawanById(selectedId("Karyawan"));
            if(k==null)throw new IllegalArgumentException("Data karyawan tidak ditemukan.");
            if(!val(p,0).isEmpty())k.setNama(val(p,0));
            if(!val(p,1).isEmpty())k.setEmail(val(p,1));
            periksaIdentitas(val(p,0).isEmpty()?k.getNama():val(p,0),val(p,1).isEmpty()?k.getEmail():val(p,1),val(p,2),false);
            if(!val(p,2).isEmpty())k.setPassword(val(p,2));
            String roleBaru=val(p,3), divisiBaru=val(p,4);
            if(divisiBaru.isBlank())throw new IllegalArgumentException("Divisi / Area harus diisi.");
            if(!roleBaru.equalsIgnoreCase(k.getPeran())) {
                Karyawan rolePengganti;
                if("Admin".equalsIgnoreCase(roleBaru)){
                    Admin a=new Admin();a.setDepartemen(divisiBaru);
                    a.setTingkatanAkses(k instanceof Admin?((Admin)k).getTingkatanAkses():cabang()?"Cabang":"Pusat");rolePengganti=a;
                } else if("Supervisor".equalsIgnoreCase(roleBaru)) {
                    Supervisor s=new Supervisor();s.setAreaTanggungJawab(divisiBaru);s.setShiftSupervisi("Pagi");rolePengganti=s;
                } else if("Staff".equalsIgnoreCase(roleBaru)) {
                    Staff s=new Staff();s.setDivisi(divisiBaru);s.setBagianKerja("Operator");rolePengganti=s;
                } else throw new IllegalArgumentException("Peran tidak dikenal.");
                rolePengganti.setIdKaryawan(k.getIdKaryawan());rolePengganti.setNama(k.getNama());
                rolePengganti.setEmail(k.getEmail());rolePengganti.setPassword(k.getPassword());
                rolePengganti.setStatusAktif(k.isStatusAktif());k=rolePengganti;
            } else {
                if(k instanceof Admin)((Admin)k).setDepartemen(divisiBaru);
                if(k instanceof Staff)((Staff)k).setDivisi(divisiBaru);
                if(k instanceof Supervisor)((Supervisor)k).setAreaTanggungJawab(divisiBaru);
            }
            ok(pusat.ubahKaryawan(k),"Perubahan gagal; periksa apakah peran lama masih digunakan pada transaksi terkait.");
        });
        bind("Karyawan",2,()->{
            if(!pusat()&&!cabang())return;Karyawan k=pusat.getKaryawanById(selectedId("Karyawan"));
            if(k!=null&&confirm("Ubah status akun "+k.getNama()+"?"))ok(pusat.toggleStatusKaryawan(k.getIdKaryawan(),k.isStatusAktif()),"Gagal mengubah status akun.");
        });
        bind("Shift",0,()->{
            if(!pusat()&&!cabang())return;HalamanModul p=v("Shift");Shift s=new Shift();
            try{s.setIdShift(selectedId("Shift"));}catch(Exception ignored){}
            s.setNamaShift(val(p,0).trim());s.setJamMasuk(LocalTime.parse(val(p,1)));s.setJamKeluar(LocalTime.parse(val(p,2)));
            if(s.getNamaShift().isBlank())throw new IllegalArgumentException("Nama shift wajib diisi.");
            if(s.getJamMasuk().equals(s.getJamKeluar()))throw new IllegalArgumentException("Jam masuk dan keluar tidak boleh sama.");
            for(Shift existing:pusat.getAllShift())if(existing.getIdShift()!=s.getIdShift() && existing.getNamaShift().trim().equalsIgnoreCase(s.getNamaShift()))
                throw new IllegalArgumentException("Nama Shift sudah terdaftar.");
            ok(pusat.simpanShift(s),"Gagal menyimpan master shift.");
        });
        bind("Shift",1,()->{if((pusat()||cabang())&&confirm("Hapus shift terpilih?"))ok(pusat.hapusShift(selectedId("Shift")),"Shift mungkin masih dipakai jadwal.");});
        bind("Kategori",0,()->{
            if(!pusat())return;HalamanModul p=v("Kategori");KategoriPoin k=new KategoriPoin();
            try{k.setIdKategori(selectedId("Kategori"));}catch(Exception ignored){}
            k.setNamaKategori(val(p,0).trim());k.setBatasMaksimalPoin(Integer.parseInt(val(p,1)));k.setDeskripsi(val(p,2).trim());
            if(k.getNamaKategori().isEmpty()||k.getBatasMaksimalPoin()<0)throw new IllegalArgumentException("Nama dan nilai acuan wajib valid.");
            for(KategoriPoin existing:pusat.getAllKategoriPoin())if(existing.getIdKategori()!=k.getIdKategori() && (existing.getNamaKategori().trim().equalsIgnoreCase(k.getNamaKategori()) || str(existing.getDeskripsi()).trim().equalsIgnoreCase(k.getDeskripsi())))
                throw new IllegalArgumentException("Nama Kategori atau Keterangan sudah terdaftar.");
            ok(pusat.simpanKategoriPoin(k),"Gagal menyimpan kategori.");
        });
        bind("Kategori",1,()->{if(pusat()&&confirm("Hapus kategori terpilih?"))ok(pusat.hapusKategoriPoin(selectedId("Kategori")),"Kategori masih dipakai transaksi.");});
        bind("Jadwal",0,()->{
            if(!cabang()&&!spv())return;HalamanModul p=v("Jadwal");
            ok(cabang.jadwalkanShift(choiceId(p,0),choiceId(p,1),LocalDate.parse(val(p,2))),"Jadwal bentrok atau gagal disimpan.");
        });
        bind("Jadwal",1,()->{if((cabang()||spv())&&confirm("Batalkan jadwal terpilih?"))ok(cabang.batalkanJadwal(selectedId("Jadwal")),"Jadwal mungkin digunakan presensi.");});
        if (staff() && v("Presensi") instanceof PresensiStaffPanel) {
            PresensiStaffPanel pp = (PresensiStaffPanel) v("Presensi");
            pp.tombolPilihFoto().addActionListener(e -> {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Pilih foto presensi dari komputer");
                fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Foto JPG / PNG", "jpg", "jpeg", "png"));
                if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        java.io.File file = fc.getSelectedFile();
                        if (javax.imageio.ImageIO.read(file)==null) throw new IllegalArgumentException("Bukan gambar");
                        pp.setFotoTerpilih(file);
                    } catch(Exception ex) {
                        JOptionPane.showMessageDialog(this,"Foto tidak valid atau tidak terbaca.","Bukti Presensi",JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        bind("Presensi",0,()->{
            if(!staff())return;JadwalShift j=staf.getJadwalHariIni(idUser());if(j==null)throw new IllegalArgumentException("Belum ada jadwal kerja untuk hari ini.");
            java.io.File file = ambilFotoPresensi("Pilih bukti foto check-in"); if(file==null)return;
            if(!previewFoto(file,"Konfirmasi Foto Check-In"))return;
            try{String[] foto=staf.prosesFotoBukti(file,System.getProperty("user.dir"));ok(staf.lakukanCheckIn(idUser(),j.getIdShift(),foto[1],foto[0],val(v("Presensi"),0)),"Presensi masuk gagal.");((PresensiStaffPanel)v("Presensi")).setFotoTerpilih(null);}
            catch(Exception e){throw new IllegalStateException(e.getMessage(),e);}
        });
        bind("Presensi",1,()->{
            if(!staff())return;Absensi a=staf.getAbsensiHariIni(idUser());if(a==null)throw new IllegalArgumentException("Belum check-in hari ini.");
            java.io.File file = ambilFotoPresensi("Pilih bukti foto check-out"); if(file==null)return;
            if(!previewFoto(file,"Konfirmasi Foto Check-Out"))return;
            try{String[] foto=staf.prosesFotoBukti(file,System.getProperty("user.dir"));ok(staf.lakukanCheckOut(a.getIdAbsensi(),foto[1]),"Presensi keluar gagal.");((PresensiStaffPanel)v("Presensi")).setFotoTerpilih(null);}
            catch(Exception e){throw new IllegalStateException(e.getMessage(),e);}
        });
        bind("Presensi",2,()->{if(spv())ok(supervisor.setujuiAbsensi(selectedId("Presensi"),val(v("Presensi"),0)),"Gagal menyetujui presensi.");});
        bind("Log",0,()->{
            if(!staff())return;HalamanModul p=v("Log");
            String uraian=val(p,0);
            if(uraian.trim().isEmpty())throw new IllegalArgumentException("Ringkasan aktivitas wajib diisi.");
            LogStaffPanel formLog=(LogStaffPanel)p;
            String catatan=formLog.catatanSerahTerima();
            if(catatan.isEmpty()||catatan.length()>500||uraian.length()>500)
                throw new IllegalArgumentException("Ringkasan dan serah terima wajib diisi (maksimal 500 karakter).");
            java.io.File gambar=formLog.fotoLog();
            if(gambar==null||!gambar.isFile())throw new IllegalArgumentException("Pilih foto dokumentasi aktivitas terlebih dahulu.");
            String ext=gambar.getName().toLowerCase(java.util.Locale.ROOT);
            if(!(ext.endsWith(".jpg")||ext.endsWith(".jpeg")||ext.endsWith(".png")))throw new IllegalArgumentException("Dokumentasi harus JPG, JPEG, atau PNG.");
            if(gambar.length()>8L*1024*1024)throw new IllegalArgumentException("Foto maksimal 8 MB.");
            if(!previewFoto(gambar,"Konfirmasi foto dokumentasi"))return;
            String namaBerkas=java.util.UUID.randomUUID().toString()+ext.substring(ext.lastIndexOf('.'));
            java.nio.file.Path tujuan=java.nio.file.Path.of(System.getProperty("user.dir"),"uploads",namaBerkas);
            try {
                java.nio.file.Files.createDirectories(tujuan.getParent());
                java.nio.file.Files.copy(gambar.toPath(),tujuan);
                if(!staf.kirimLogLengkap(idUser(),uraian,catatan,"uploads/"+namaBerkas)) {
                    java.nio.file.Files.deleteIfExists(tujuan);
                    throw new IllegalStateException("Log gagal disimpan ke database.");
                }
            } catch(java.io.IOException ex){throw new IllegalStateException("Gagal menyalin dokumentasi: "+ex.getMessage(),ex);}
            formLog.resetLog();
            muat("Log");
            JOptionPane.showMessageDialog(this,"Catatan aktivitas berhasil dikirim dan menunggu verifikasi Supervisor.",
                "Log Aktivitas",JOptionPane.INFORMATION_MESSAGE);
        });
        if(v("Presensi")!=null) v("Presensi").tabel().addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){
                JTable t=v("Presensi").tabel();
                int r=t.rowAtPoint(e.getPoint()), c=t.columnAtPoint(e.getPoint());
                if(r>=0 && c>=0 && "Bukti Foto".equals(t.getColumnName(c))){
                    t.setRowSelectionInterval(r,r);
                    Object label=t.getValueAt(r,c);
                    if(!"Lihat Foto".equals(label)) return;
                    jalankanAksi(()->lihatFotoPresensi());
                }
            }
        });
        if(v("Log")!=null) v("Log").tabel().addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){
                JTable table=v("Log").tabel();int col=table.columnAtPoint(e.getPoint());int row=table.rowAtPoint(e.getPoint());
                if(col>=0 && row>=0 && "Bukti Foto".equals(table.getColumnName(col))) {
                    table.setRowSelectionInterval(row,row);
                    if(!"Lihat Foto".equals(String.valueOf(table.getValueAt(row,col))))return;
                    try{lihatDokumentasiLog();}catch(Exception ex){JOptionPane.showMessageDialog(MainFrame.this,ex.getMessage(),"Dokumentasi",JOptionPane.WARNING_MESSAGE);}
                }
            }
        });
        bind("Log",1,()->{if(spv())ok(supervisor.verifikasiLog(selectedId("Log"),val(v("Log"),0)),"Gagal memverifikasi log.");});
        bind("Log",2,()->{if(spv())ok(supervisor.mintaRevisiLog(selectedId("Log"),val(v("Log"),0)),"Gagal mengirim catatan revisi.");});
        bind("Poin",0,()->{
            if(staff()) { bukaPengajuanPoin(); return; }
            if(!spv())return;HalamanModul p=v("Poin");CrunchPoints cp=new CrunchPoints();
            cp.setIdStaff(choiceId(p,0));cp.setIdSupervisor(idUser());cp.setIdKategori(choiceId(p,1));
            for(KategoriPoin k:supervisor.getAllKategoriPoin())if(k.getIdKategori()==cp.getIdKategori())cp.setNilaiPoin(k.getBatasMaksimalPoin());
            cp.setCatatanApresiasi(val(p,2));ok(supervisor.prosesPemberianPoin(cp),"Gagal memberikan poin.");
        });
        bindInformasi("Monitoring",0,()->tampilkanDetailMonitoring());
        bindInformasi("Laporan",0,()->tampilkanRekapBerperiode());
        bindInformasi("Laporan",1,()->eksporRekapBerperiode());
        HalamanModul kp=halaman.get("Karyawan");
        if(kp!=null)
        kp.tabel().getSelectionModel().addListSelectionListener(ev->{
            if(ev.getValueIsAdjusting()||(!pusat()&&!cabang()))return;try{
                Karyawan k=pusat.getKaryawanById(selectedId("Karyawan"));if(k==null)return;
                setVal(kp,0,k.getNama());setVal(kp,1,k.getEmail());setVal(kp,2,"");setVal(kp,3,k.getPeran());setVal(kp,4,divisiKaryawan(k));
            }catch(Exception ignored){}
        });
        // Sama seperti pengelolaan Karyawan Admin: klik satu kali pada baris
        // Jadwal Supervisor untuk mengisi karyawan, shift, dan tanggal di form kiri.
        HalamanModul jadwalForm=halaman.get("Jadwal");
        if(spv() && jadwalForm!=null)
        jadwalForm.tabel().getSelectionModel().addListSelectionListener(ev->{
            if(ev.getValueIsAdjusting())return;
            try {
                int id=selectedId("Jadwal");
                for(JadwalShift j:cabang.getAllJadwal()) if(j.getIdJadwal()==id){
                    pilihComboMenurutId(jadwalForm,0,j.getIdKaryawan());
                    pilihComboMenurutId(jadwalForm,1,j.getIdShift());
                    setVal(jadwalForm,2,j.getTanggal().toString());
                    jadwalForm.pesan("Jadwal dipilih. Ubah tanggal/shift pada form kiri, lalu klik Ubah Jadwal Shift.");
                    break;
                }
            }catch(Exception ignored){}
        });
        HalamanModul sh=halaman.get("Shift");
        if(sh!=null)
        sh.tabel().getSelectionModel().addListSelectionListener(ev->{if(ev.getValueIsAdjusting())return;try{
            int id=selectedId("Shift");for(Shift s:pusat.getAllShift())if(s.getIdShift()==id){setVal(sh,0,s.getNamaShift());setVal(sh,1,str(s.getJamMasuk()));setVal(sh,2,str(s.getJamKeluar()));break;}
        }catch(Exception ignored){}});
        HalamanModul kt=halaman.get("Kategori");
        if(kt!=null)
        kt.tabel().getSelectionModel().addListSelectionListener(ev->{if(ev.getValueIsAdjusting())return;try{
            int id=selectedId("Kategori");for(KategoriPoin k:pusat.getAllKategoriPoin())if(k.getIdKategori()==id){setVal(kt,0,k.getNamaKategori());setVal(kt,1,String.valueOf(k.getBatasMaksimalPoin()));setVal(kt,2,k.getDeskripsi());break;}
        }catch(Exception ignored){}});
    }
    private static String esc(Object value){String s=value==null?"":value.toString();return "\""+s.replace("\"","\"\"")+"\"";}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents(){
        pnlTop = new javax.swing.JPanel();
        pnlBrand = new javax.swing.JPanel();
        lblBrand = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        pnlNav = new javax.swing.JPanel();
        btnDashboard = new javax.swing.JButton();
        btnKaryawan = new javax.swing.JButton();
        btnShift = new javax.swing.JButton();
        btnKategori = new javax.swing.JButton();
        btnJadwal = new javax.swing.JButton();
        btnPresensi = new javax.swing.JButton();
        btnLog = new javax.swing.JButton();
        btnPoin = new javax.swing.JButton();
        btnMonitoring = new javax.swing.JButton();
        btnLaporan = new javax.swing.JButton();
        btnProfil = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        lblFooter = new javax.swing.JLabel();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CrunchShift | Dashboard & Modul");
        getContentPane().setLayout(new java.awt.BorderLayout());
        pnlTop.setBackground(new java.awt.Color(27,42,92));
        pnlTop.setLayout(new java.awt.BorderLayout());
        pnlBrand.setBackground(new java.awt.Color(27,42,92));
        pnlBrand.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,7,8));
        lblBrand.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/icon_brand_designer.png")));
        lblBrand.setText("");
        lblBrand.setForeground(java.awt.Color.WHITE);
        lblBrand.setFont(new java.awt.Font("Segoe UI",1,17));
        pnlBrand.add(lblBrand);
        lblUser.setText("Pengguna");
        lblUser.setForeground(java.awt.Color.WHITE);
        pnlBrand.add(lblUser);
        pnlTop.add(pnlBrand,java.awt.BorderLayout.CENTER);
        pnlNav.setBackground(new java.awt.Color(27,42,92));

        javax.swing.GroupLayout pnlNavLayout = new javax.swing.GroupLayout(pnlNav);
        pnlNav.setLayout(pnlNavLayout);
        pnlNavLayout.setHorizontalGroup(
            pnlNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(10).addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(56).addComponent(btnKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(102).addComponent(btnShift, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(148).addComponent(btnKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(194).addComponent(btnJadwal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(240).addComponent(btnPresensi, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(286).addComponent(btnLog, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(332).addComponent(btnPoin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(378).addComponent(btnMonitoring, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(424).addComponent(btnLaporan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(470).addComponent(btnProfil, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(516).addComponent(btnKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlNavLayout.setVerticalGroup(
            pnlNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(10).addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(56).addComponent(btnKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(102).addComponent(btnShift, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(148).addComponent(btnKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(194).addComponent(btnJadwal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(240).addComponent(btnPresensi, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(286).addComponent(btnLog, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(332).addComponent(btnPoin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(378).addComponent(btnMonitoring, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(424).addComponent(btnLaporan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(470).addComponent(btnProfil, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlNavLayout.createSequentialGroup().addGap(516).addComponent(btnKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlNav.setPreferredSize(new java.awt.Dimension(204,630));
        pnlNav.setBorder(javax.swing.BorderFactory.createEmptyBorder(10,10,10,10));
        btnDashboard.setText("  Dashboard");
        btnDashboard.setBackground(new java.awt.Color(27,42,92));
        btnDashboard.setForeground(new java.awt.Color(255,255,255));
        btnDashboard.setFont(new java.awt.Font("Segoe UI",1,13));
        btnDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_dashboard.png")));
        btnDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDashboard.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnDashboard.setFocusPainted(false);

        btnKaryawan.setText("  Karyawan / Tim");
        btnKaryawan.setBackground(new java.awt.Color(27,42,92));
        btnKaryawan.setForeground(new java.awt.Color(255,255,255));
        btnKaryawan.setFont(new java.awt.Font("Segoe UI",1,13));
        btnKaryawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_karyawan.png")));
        btnKaryawan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnKaryawan.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnKaryawan.setFocusPainted(false);

        btnShift.setText("  Master Shift");
        btnShift.setBackground(new java.awt.Color(27,42,92));
        btnShift.setForeground(new java.awt.Color(255,255,255));
        btnShift.setFont(new java.awt.Font("Segoe UI",1,13));
        btnShift.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_shift.png")));
        btnShift.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnShift.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnShift.setFocusPainted(false);

        btnKategori.setText("  Kategori Poin");
        btnKategori.setBackground(new java.awt.Color(27,42,92));
        btnKategori.setForeground(new java.awt.Color(255,255,255));
        btnKategori.setFont(new java.awt.Font("Segoe UI",1,13));
        btnKategori.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_kategori.png")));
        btnKategori.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnKategori.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnKategori.setFocusPainted(false);

        btnJadwal.setText("  Jadwal Shift");
        btnJadwal.setBackground(new java.awt.Color(27,42,92));
        btnJadwal.setForeground(new java.awt.Color(255,255,255));
        btnJadwal.setFont(new java.awt.Font("Segoe UI",1,13));
        btnJadwal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_jadwal.png")));
        btnJadwal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnJadwal.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnJadwal.setFocusPainted(false);

        btnPresensi.setText("  Presensi");
        btnPresensi.setBackground(new java.awt.Color(27,42,92));
        btnPresensi.setForeground(new java.awt.Color(255,255,255));
        btnPresensi.setFont(new java.awt.Font("Segoe UI",1,13));
        btnPresensi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_presensi.png")));
        btnPresensi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPresensi.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnPresensi.setFocusPainted(false);

        btnLog.setText("  Log Aktivitas");
        btnLog.setBackground(new java.awt.Color(27,42,92));
        btnLog.setForeground(new java.awt.Color(255,255,255));
        btnLog.setFont(new java.awt.Font("Segoe UI",1,13));
        btnLog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_log.png")));
        btnLog.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLog.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnLog.setFocusPainted(false);

        btnPoin.setText("  CrunchPoints");
        btnPoin.setBackground(new java.awt.Color(27,42,92));
        btnPoin.setForeground(new java.awt.Color(255,255,255));
        btnPoin.setFont(new java.awt.Font("Segoe UI",1,13));
        btnPoin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_poin.png")));
        btnPoin.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPoin.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnPoin.setFocusPainted(false);

        btnMonitoring.setText("  Monitoring");
        btnMonitoring.setBackground(new java.awt.Color(27,42,92));
        btnMonitoring.setForeground(new java.awt.Color(255,255,255));
        btnMonitoring.setFont(new java.awt.Font("Segoe UI",1,13));
        btnMonitoring.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_monitor.png")));
        btnMonitoring.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMonitoring.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnMonitoring.setFocusPainted(false);

        btnLaporan.setText("  Laporan");
        btnLaporan.setBackground(new java.awt.Color(27,42,92));
        btnLaporan.setForeground(new java.awt.Color(255,255,255));
        btnLaporan.setFont(new java.awt.Font("Segoe UI",1,13));
        btnLaporan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_laporan.png")));
        btnLaporan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLaporan.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnLaporan.setFocusPainted(false);

        btnProfil.setText("  Profil");
        btnProfil.setBackground(new java.awt.Color(27,42,92));
        btnProfil.setForeground(new java.awt.Color(255,255,255));
        btnProfil.setFont(new java.awt.Font("Segoe UI",1,13));
        btnProfil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/nav_profil.png")));
        btnProfil.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProfil.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnProfil.setFocusPainted(false);

        btnKeluar.setText("  Keluar");
        btnKeluar.setBackground(new java.awt.Color(27,42,92));
        btnKeluar.setForeground(new java.awt.Color(255,255,255));
        btnKeluar.setFont(new java.awt.Font("Segoe UI",1,13));
        btnKeluar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnKeluar.setBorder(javax.swing.BorderFactory.createEmptyBorder(7,14,7,8));
        btnKeluar.setFocusPainted(false);

        getContentPane().add(pnlNav,java.awt.BorderLayout.WEST);
        getContentPane().add(pnlTop,java.awt.BorderLayout.NORTH);
        pnlContent.setBackground(new java.awt.Color(248,250,252));
        pnlContent.setLayout(new java.awt.CardLayout());
        getContentPane().add(pnlContent,java.awt.BorderLayout.CENTER);
        lblFooter.setText("  CrunchShift  /  Pilih menu di sebelah kiri untuk mengelola pekerjaan.");
        lblFooter.setFont(new java.awt.Font("Segoe UI",0,12));
        lblFooter.setForeground(new java.awt.Color(87,102,126));
        lblFooter.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,8,5,8));
        getContentPane().add(lblFooter,java.awt.BorderLayout.SOUTH);
        pack();
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JLabel lblBrand;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel pnlNav;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JLabel lblFooter;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnKaryawan;
    private javax.swing.JButton btnShift;
    private javax.swing.JButton btnKategori;
    private javax.swing.JButton btnJadwal;
    private javax.swing.JButton btnPresensi;
    private javax.swing.JButton btnLog;
    private javax.swing.JButton btnPoin;
    private javax.swing.JButton btnMonitoring;
    private javax.swing.JButton btnLaporan;
    private javax.swing.JButton btnProfil;
    private javax.swing.JButton btnKeluar;
    // End of variables declaration//GEN-END:variables
}
