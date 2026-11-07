package com.crunchshift.view;

/**
 * Form GUI Builder mandiri: semua komponen visual adalah bagian dari .form.
 * Penanganan database & tombol dilakukan oleh MainFrame; tidak mengubah layout.
 */
public class PoinStaffPanel extends javax.swing.JPanel implements HalamanModul {
    public PoinStaffPanel() {
        initComponents();
        lblReadOnly.setText("Pengajuan ditinjau oleh supervisor.");
        lblReadOnly.setText("Status pengajuan ditinjau supervisor.");
        lblHint1.setText("Total Saldo Poin: -");
        lblHint2.setText("Lencana: -");
        lblHint3.setText("Poin dihitung setelah disetujui Supervisor");
        lblFormTitle.setText("Ringkasan CrunchPoints");
        lblTableTitle.setText("Riwayat Pengajuan Saya");
        lblTitle.setText("Pengajuan CrunchPoints");
        lblSubtitle.setText("Ajukan apresiasi kontribusi kerja dan pantau status verifikasi.");
        siapkanPanduanPoin();
    }

    /** Hanya panel CrunchPoints Staff yang mendapat panduan ringkas seperti acuan desain. */
    private void siapkanPanduanPoin() {
        final java.awt.Color navy = new java.awt.Color(27, 42, 92);
        final java.awt.Color orange = new java.awt.Color(224, 123, 16);
        javax.swing.JPanel guide = new javax.swing.JPanel(new java.awt.BorderLayout(0, 9));
        guide.setOpaque(true);
        guide.setBackground(new java.awt.Color(232, 241, 253));
        guide.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(210, 225, 245),1,true),
                javax.swing.BorderFactory.createEmptyBorder(9, 10, 10, 10)));
        javax.swing.JLabel title = new javax.swing.JLabel("ⓘ  Panduan CrunchPoints");
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        title.setForeground(navy);
        title.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, orange),
                javax.swing.BorderFactory.createEmptyBorder(5, 9, 5, 0)));
        guide.add(title, java.awt.BorderLayout.NORTH);
        javax.swing.JTextArea steps = new javax.swing.JTextArea(
                "Ajukan kontribusi kepada Supervisor.\n\n"+
                "01  Klik Ajukan CrunchPoints\n"+
                "02  Pilih kategori dan isi kontribusi\n"+
                "03  Pantau status pengajuan di tabel");
        steps.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        steps.setForeground(navy);
        steps.setEditable(false);
        steps.setFocusable(false);
        steps.setLineWrap(true);
        steps.setWrapStyleWord(true);
        steps.setOpaque(false);
        steps.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        guide.add(steps,java.awt.BorderLayout.CENTER);
        guide.setPreferredSize(new java.awt.Dimension(270, 188));
        pnlLeft.add(guide,java.awt.BorderLayout.SOUTH);
    }
    public void tampilkanSaldoDanBadge(int saldo, String badge) {
        lblFormTitle.setText("Ringkasan CrunchPoints");
        lblHint1.setText("Total Saldo Poin:   " + saldo);
        lblHint2.setText("Lencana:   " + badge);
        lblHint3.setText("Poin dihitung setelah disetujui Supervisor");
    }

    @Override public javax.swing.JTable tabel() { return tblData; }
    @Override public javax.swing.JTextField pencarian() { return txtCari; }
    @Override public javax.swing.JButton tombolCari() { return btnCari; }
    @Override public javax.swing.JButton tombolSegarkan() { return btnSegarkan; }
    @Override public void pesan(String s) { lblStatus.setText(s); }
    @Override public void tampilkanForm(boolean show) { pnlLeft.setVisible(show); }
    @Override public javax.swing.JButton tombol(int i) {
        switch (i) {
            case 0: return btnAjukanPoin;
            default: return null;
        }
    }
    @Override public javax.swing.JComponent masukan(int i) {
        switch (i) {
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
        lblHint1 = new javax.swing.JLabel();
        lblHint2 = new javax.swing.JLabel();
        lblHint3 = new javax.swing.JLabel();
        pnlActions = new javax.swing.JPanel();
        lblReadOnly = new javax.swing.JLabel();
        btnAjukanPoin = new javax.swing.JButton();
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
        lblTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/icon_poin.png")));
        lblTitle.setText("Riwayat CrunchPoints");

        lblSubtitle.setForeground(new java.awt.Color(99,115,137));
        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblSubtitle.setText("Ajukan kontribusi kerja dan pantau status pengajuan.");

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
        lblFormTitle.setText("Petunjuk");

        pnlFormContent.setBackground(new java.awt.Color(255,255,255));
        pnlFormContent.setLayout(new java.awt.BorderLayout(0,3));
        pnlFormContent.add(pnlFields, java.awt.BorderLayout.NORTH);
        pnlFormContent.add(pnlActions, java.awt.BorderLayout.CENTER);

        pnlFields.setBackground(new java.awt.Color(255,255,255));
        pnlFields.setPreferredSize(new java.awt.Dimension(305,150));

        javax.swing.GroupLayout pnlFieldsLayout = new javax.swing.GroupLayout(pnlFields);
        pnlFields.setLayout(pnlFieldsLayout);
        pnlFieldsLayout.setHorizontalGroup(
            pnlFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlFieldsLayout.createSequentialGroup().addComponent(lblHint1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlFieldsLayout.createSequentialGroup().addComponent(lblHint2, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlFieldsLayout.createSequentialGroup().addComponent(lblHint3, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlFieldsLayout.setVerticalGroup(
            pnlFieldsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlFieldsLayout.createSequentialGroup().addComponent(lblHint1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlFieldsLayout.createSequentialGroup().addGap(53).addComponent(lblHint2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlFieldsLayout.createSequentialGroup().addGap(106).addComponent(lblHint3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );


        lblHint1.setForeground(new java.awt.Color(99,115,137));
        lblHint1.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblHint1.setText("01  Lihat poin yang masuk");


        lblHint2.setForeground(new java.awt.Color(99,115,137));
        lblHint2.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblHint2.setText("02  Gunakan pencarian jika perlu");


        lblHint3.setForeground(new java.awt.Color(99,115,137));
        lblHint3.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblHint3.setText("<html><div style='width:265px'>03  Tanyakan ke supervisor bila perlu</div></html>");


        pnlActions.setBackground(new java.awt.Color(255,255,255));

        pnlActions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,8,9));
        btnAjukanPoin.setText("Ajukan CrunchPoints");
        btnAjukanPoin.setFont(new java.awt.Font("Segoe UI",java.awt.Font.BOLD,13));
        btnAjukanPoin.setBackground(new java.awt.Color(224,123,16));
        btnAjukanPoin.setForeground(java.awt.Color.WHITE);
        btnAjukanPoin.setPreferredSize(new java.awt.Dimension(205,40));
        pnlActions.add(btnAjukanPoin);
        pnlActions.add(lblReadOnly);
        lblReadOnly.setForeground(new java.awt.Color(99,115,137));
        lblReadOnly.setFont(new java.awt.Font("Segoe UI", 0, 13));
        lblReadOnly.setText("Hanya tampilan data");

        pnlRight.setBackground(new java.awt.Color(255,255,255));
        pnlRight.setBorder(javax.swing.BorderFactory.createEmptyBorder(17,14,13,14));
        pnlRight.setLayout(new java.awt.BorderLayout(0,10));
        pnlRight.add(lblTableTitle, java.awt.BorderLayout.NORTH);
        pnlRight.add(scrollData, java.awt.BorderLayout.CENTER);

        lblTableTitle.setForeground(new java.awt.Color(27,42,92));
        lblTableTitle.setFont(new java.awt.Font("Segoe UI", 1, 17));
        lblTableTitle.setText("Daftar Riwayat CrunchPoints");

        scrollData.setViewportView(tblData);

        tblData.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[] {"ID Pengajuan", "Tanggal", "Kategori", "Deskripsi Kategori", "Status"}));
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
    private javax.swing.JLabel lblHint1;
    private javax.swing.JLabel lblHint2;
    private javax.swing.JLabel lblHint3;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JLabel lblReadOnly;
    private javax.swing.JButton btnAjukanPoin;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JLabel lblTableTitle;
    private javax.swing.JScrollPane scrollData;
    private javax.swing.JTable tblData;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JLabel lblStatus;
    // End of variables declaration//GEN-END:variables
}
