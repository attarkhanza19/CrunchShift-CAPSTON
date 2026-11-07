package com.crunchshift.view;

/**
 * Form GUI Builder mandiri: semua komponen visual adalah bagian dari .form.
 * Penanganan database & tombol dilakukan oleh MainFrame; tidak mengubah layout.
 */
public class LogStaffPanel extends javax.swing.JPanel implements HalamanModul {
    private java.io.File fotoLog;
    public LogStaffPanel() {
        initComponents();
        lblSubtitle.setText("Isi ringkasan, serah terima, pilih foto, lalu klik Kirim Aktivitas.");
        btnPilihDokumentasi.addActionListener(e -> pilihFoto());
        // Tinggi ringkas agar kedua aksi asli dan aksi pembaruan tetap terlihat.
        pnlFields.setPreferredSize(new java.awt.Dimension(280,214));
        pnlActions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,5,3));
        btnPilihDokumentasi.setPreferredSize(new java.awt.Dimension(115,32));
        btnAksi1.setPreferredSize(new java.awt.Dimension(132,32));
    }
    public java.io.File fotoLog(){return fotoLog;}
    public String catatanSerahTerima(){return input2.getText().trim();}
    public void resetLog(){input1.setText("");input2.setText("");input3.setText("Belum memilih foto");fotoLog=null;}
    private void pilihFoto(){
        javax.swing.JFileChooser chooser=new javax.swing.JFileChooser();
        chooser.setDialogTitle("Pilih foto dokumentasi aktivitas");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Foto JPG / PNG","jpg","jpeg","png"));
        if(chooser.showOpenDialog(this)!=javax.swing.JFileChooser.APPROVE_OPTION)return;
        java.io.File f=chooser.getSelectedFile();
        try {
            if(!f.isFile()||f.length()>8L*1024*1024||f.length()==0)
                throw new IllegalArgumentException("Ukuran foto harus 1 byte sampai 8 MB.");
            if(javax.imageio.ImageIO.read(f)==null)throw new IllegalArgumentException("Berkas bukan gambar yang valid.");
            fotoLog=f;input3.setText(f.getName());input3.setToolTipText(f.getAbsolutePath());
        }catch(Exception ex){javax.swing.JOptionPane.showMessageDialog(this,ex.getMessage(),"Foto tidak valid",javax.swing.JOptionPane.WARNING_MESSAGE);}
    }
    @Override public javax.swing.JTable tabel() { return tblData; }
    @Override public javax.swing.JTextField pencarian() { return txtCari; }
    @Override public javax.swing.JButton tombolCari() { return btnCari; }
    @Override public javax.swing.JButton tombolSegarkan() { return btnSegarkan; }
    @Override public void pesan(String s) { lblStatus.setText(s); }
    @Override public void tampilkanForm(boolean show) { pnlLeft.setVisible(show); }
    @Override public javax.swing.JButton tombol(int i) {
        switch (i) {
            case 0: return btnAksi1;
            default: return null;
        }
    }
    @Override public javax.swing.JComponent masukan(int i) {
        switch (i) {
            case 0: return input1;
            case 1: return input2;
            case 2: return input3;
            default: return null;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlHeader = new javax.swing.JPanel();
        pnlHeading = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        pnlToolbar = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        btnSegarkan = new javax.swing.JButton();
        pnlBody = new javax.swing.JPanel();
        pnlLeft = new javax.swing.JPanel();
        lblFormTitle = new javax.swing.JLabel();
        pnlFormContent = new javax.swing.JPanel();
        pnlFields = new javax.swing.JPanel();
        lblField1 = new javax.swing.JLabel();
        input1 = new javax.swing.JTextField();
        lblField2 = new javax.swing.JLabel();
        input2 = new javax.swing.JTextField();
        lblField3 = new javax.swing.JLabel();
        input3 = new javax.swing.JTextField();
        pnlActions = new javax.swing.JPanel();
        btnAksi1 = new javax.swing.JButton();
        btnPilihDokumentasi = new javax.swing.JButton();
        pnlRight = new javax.swing.JPanel();
        lblTableTitle = new javax.swing.JLabel();
        scrollData = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        pnlStatus = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();

        this.setBackground(new java.awt.Color(248,250,252));
        this.setPreferredSize(new java.awt.Dimension(1020,620));
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(15,19,12,19));
        this.setLayout(new java.awt.BorderLayout(11,11));

        pnlHeader.setBackground(new java.awt.Color(248,250,252));
        pnlHeader.setLayout(new java.awt.BorderLayout(0,9));
        pnlHeader.add(pnlHeading, java.awt.BorderLayout.CENTER);
        pnlHeader.add(pnlToolbar, java.awt.BorderLayout.SOUTH);

        pnlHeading.setBackground(new java.awt.Color(248,250,252));

        javax.swing.GroupLayout pnlHeadingLayout = new javax.swing.GroupLayout(pnlHeading);
        pnlHeading.setLayout(pnlHeadingLayout);
        pnlHeadingLayout.setHorizontalGroup(
            pnlHeadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlHeadingLayout.createSequentialGroup().addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlHeadingLayout.createSequentialGroup().addComponent(lblSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlHeadingLayout.setVerticalGroup(
            pnlHeadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlHeadingLayout.createSequentialGroup().addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlHeadingLayout.createSequentialGroup().addGap(48).addComponent(lblSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );


        lblTitle.setForeground(new java.awt.Color(27,42,92));
        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 25));
        lblTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/icon_log.png")));
        lblTitle.setText("Catatan Aktivitas Harian");

        lblSubtitle.setForeground(new java.awt.Color(99,115,137));
        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblSubtitle.setText("Catat ringkasan pekerjaan serta rentang waktu aktivitas.");

        pnlToolbar.setBackground(new java.awt.Color(248,250,252));

        // GUI Builder and runtime use the same compact, editable toolbar.
        pnlToolbar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 4));
        pnlToolbar.add(lblSearch);
        txtCari.setPreferredSize(new java.awt.Dimension(190, 32));
        pnlToolbar.add(txtCari);
        btnCari.setPreferredSize(new java.awt.Dimension(72, 32));
        pnlToolbar.add(btnCari);
        btnSegarkan.setPreferredSize(new java.awt.Dimension(108, 32));
        pnlToolbar.add(btnSegarkan);


        lblSearch.setForeground(new java.awt.Color(29,42,68));
        lblSearch.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblSearch.setText("Cari:");

        txtCari.setText("");
        txtCari.setColumns(24);

        btnCari.setBackground(new java.awt.Color(255,255,255));
        btnCari.setForeground(new java.awt.Color(27,42,92));
        btnCari.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnCari.setText("Cari");

        btnSegarkan.setBackground(new java.awt.Color(255,255,255));
        btnSegarkan.setForeground(new java.awt.Color(27,42,92));
        btnSegarkan.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnSegarkan.setText("Segarkan");

        pnlBody.setBackground(new java.awt.Color(248,250,252));
        pnlBody.setLayout(new java.awt.BorderLayout(15,0));
        pnlBody.add(pnlLeft, java.awt.BorderLayout.WEST);
        pnlBody.add(pnlRight, java.awt.BorderLayout.CENTER);

        pnlLeft.setBackground(new java.awt.Color(255,255,255));
        pnlLeft.setPreferredSize(new java.awt.Dimension(340,480));
        pnlLeft.setBorder(javax.swing.BorderFactory.createEmptyBorder(17,17,13,17));
        pnlLeft.setLayout(new java.awt.BorderLayout(0,10));
        pnlLeft.add(lblFormTitle, java.awt.BorderLayout.NORTH);
        pnlLeft.add(pnlFormContent, java.awt.BorderLayout.CENTER);

        lblFormTitle.setForeground(new java.awt.Color(27,42,92));
        lblFormTitle.setFont(new java.awt.Font("Segoe UI", 1, 17));
        lblFormTitle.setText("Form Aktivitas");

        pnlFormContent.setBackground(new java.awt.Color(255,255,255));
        pnlFormContent.setLayout(new java.awt.BorderLayout(0,3));
        pnlFormContent.add(pnlFields, java.awt.BorderLayout.NORTH);
        pnlFormContent.add(pnlActions, java.awt.BorderLayout.CENTER);

        pnlFields.setBackground(new java.awt.Color(255,255,255));
        pnlFields.setPreferredSize(new java.awt.Dimension(305,260));
        pnlFields.setLayout(new java.awt.GridLayout(0,1,3,5));
        pnlFields.add(lblField1);
        pnlFields.add(input1);
        pnlFields.add(lblField2);
        pnlFields.add(input2);
        pnlFields.add(lblField3);
        pnlFields.add(input3);

        lblField1.setForeground(new java.awt.Color(29,42,68));
        lblField1.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblField1.setText("Ringkasan kegiatan");

        input1.setFont(new java.awt.Font("Segoe UI", 0, 13));
        input1.setText("");
        input1.setColumns(15);

        lblField2.setForeground(new java.awt.Color(29,42,68));
        lblField2.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblField2.setText("Catatan Serah Terima");

        input2.setFont(new java.awt.Font("Segoe UI", 0, 13));
        input2.setText("");
        input2.setColumns(15);

        lblField3.setForeground(new java.awt.Color(29,42,68));
        lblField3.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblField3.setText("Bukti Foto Aktivitas");

        input3.setFont(new java.awt.Font("Segoe UI", 0, 13));
        input3.setText("Belum memilih foto");
        input3.setEditable(false);
        input3.setColumns(15);

        pnlActions.setBackground(new java.awt.Color(255,255,255));

        pnlActions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,8,9));
        pnlActions.add(btnPilihDokumentasi);
        pnlActions.add(btnAksi1);
        btnPilihDokumentasi.setBackground(new java.awt.Color(240,246,255));
        btnPilihDokumentasi.setForeground(new java.awt.Color(27,42,92));
        btnPilihDokumentasi.setFont(new java.awt.Font("Segoe UI",java.awt.Font.BOLD,12));
        btnPilihDokumentasi.setText("Pilih Foto...");
        btnPilihDokumentasi.setPreferredSize(new java.awt.Dimension(125,36));
        btnAksi1.setBackground(new java.awt.Color(224,123,16));
        btnAksi1.setForeground(new java.awt.Color(255,255,255));
        btnAksi1.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnAksi1.setText("Kirim Aktivitas");
        btnAksi1.setPreferredSize(new java.awt.Dimension(145,36));

        pnlRight.setBackground(new java.awt.Color(255,255,255));
        pnlRight.setBorder(javax.swing.BorderFactory.createEmptyBorder(17,14,13,14));
        pnlRight.setLayout(new java.awt.BorderLayout(0,10));
        pnlRight.add(lblTableTitle, java.awt.BorderLayout.NORTH);
        pnlRight.add(scrollData, java.awt.BorderLayout.CENTER);

        lblTableTitle.setForeground(new java.awt.Color(27,42,92));
        lblTableTitle.setFont(new java.awt.Font("Segoe UI", 1, 17));
        lblTableTitle.setText("Daftar Catatan Aktivitas Harian");

        scrollData.setViewportView(tblData);

        tblData.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[] {"ID Log", "Tanggal", "Karyawan", "Ringkasan", "Bukti Foto", "Status"}));
        tblData.setFont(new java.awt.Font("Segoe UI", 0, 13));
        tblData.setForeground(new java.awt.Color(39, 52, 76));
        tblData.setGridColor(new java.awt.Color(231, 235, 241));
        tblData.setSelectionBackground(new java.awt.Color(228, 236, 248));
        tblData.setSelectionForeground(new java.awt.Color(27, 42, 92));
        tblData.setShowVerticalLines(false);
        tblData.setRowHeight(34);
        tblData.setFillsViewportHeight(true);

        pnlStatus.setBackground(new java.awt.Color(248,250,252));

        javax.swing.GroupLayout pnlStatusLayout = new javax.swing.GroupLayout(pnlStatus);
        pnlStatus.setLayout(pnlStatusLayout);
        pnlStatusLayout.setHorizontalGroup(
            pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlStatusLayout.createSequentialGroup().addGap(9).addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlStatusLayout.setVerticalGroup(
            pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlStatusLayout.createSequentialGroup().addGap(6).addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        );


        lblStatus.setForeground(new java.awt.Color(99,115,137));
        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblStatus.setText("Pilih Segarkan untuk mengambil data dari basis data.");
        this.add(pnlHeader, java.awt.BorderLayout.NORTH);
        this.add(pnlBody, java.awt.BorderLayout.CENTER);
        this.add(pnlStatus, java.awt.BorderLayout.SOUTH);
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlHeading;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JPanel pnlToolbar;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JTextField txtCari;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnSegarkan;
    private javax.swing.JPanel pnlBody;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JLabel lblFormTitle;
    private javax.swing.JPanel pnlFormContent;
    private javax.swing.JPanel pnlFields;
    private javax.swing.JLabel lblField1;
    private javax.swing.JTextField input1;
    private javax.swing.JLabel lblField2;
    private javax.swing.JTextField input2;
    private javax.swing.JLabel lblField3;
    private javax.swing.JTextField input3;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JButton btnAksi1;
    private javax.swing.JButton btnPilihDokumentasi;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JLabel lblTableTitle;
    private javax.swing.JScrollPane scrollData;
    private javax.swing.JTable tblData;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JLabel lblStatus;
    // End of variables declaration//GEN-END:variables
}
