package com.crunchshift.view;

import com.crunchshift.config.DatabaseConnection;
import com.crunchshift.controller.AuthController;
import com.crunchshift.model.Karyawan;
import com.crunchshift.util.UIThemeStyler;

import java.awt.event.KeyEvent;
import java.sql.Connection;
import javax.swing.JOptionPane;

/**
 * Frame Autentikasi Pengguna (LoginFrame) CrunchShift.
 * Menggunakan NetBeans GUI Form Builder (.java + .form).
 */
public class LoginFrame extends javax.swing.JFrame {

    private final AuthController authController;

    /**
     * Membuat form LoginFrame baru.
     */
    public LoginFrame() {
        initComponents();
        tampilkanLogoCrunchShift();
        pasangTampilSandi();
        desainLoginRapi();
        this.authController = new AuthController();
        checkDatabaseStatus();
    }

    /** Logo resmi pada panel login, diperkecil proporsional tanpa mengubah artwork. */
    private void tampilkanLogoCrunchShift() {
        java.awt.Image img = new javax.swing.ImageIcon(getClass().getResource(
                "/com/crunchshift/view/assets/icon_brand.png")).getImage();
        lblBrand.setIcon(new javax.swing.ImageIcon(img.getScaledInstance(340, 95, java.awt.Image.SCALE_SMOOTH)));
        lblBrand.setText("");
        lblBrand.setOpaque(false);
        lblBrand.setBackground(java.awt.Color.WHITE);
    }

    /** Menata ulang visual login tanpa mengubah autentikasi, listener, dan nama field. */
    private void desainLoginRapi() {
        final java.awt.Color navy = new java.awt.Color(27,42,92);
        final java.awt.Color orange = new java.awt.Color(226,121,9);
        final java.awt.Color canvas = new java.awt.Color(247,249,253);
        final java.awt.Color border = new java.awt.Color(215,225,239);
        getContentPane().setBackground(canvas);
        setMinimumSize(new java.awt.Dimension(850,575));
        setPreferredSize(new java.awt.Dimension(990,620));
        panelTitleBar.setPreferredSize(new java.awt.Dimension(0,49));
        panelTitleBar.setLayout(new java.awt.BorderLayout());
        lblTitle.setFont(new java.awt.Font("Segoe UI",java.awt.Font.BOLD,16));
        lblTitle.setText("  CrunchShift  /  Portal Karyawan");
        lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,21,0,0));
        panelTitleBar.add(lblTitle,java.awt.BorderLayout.WEST);
        panelBrand.removeAll();
        panelBrand.setLayout(new java.awt.BorderLayout());
        panelBrand.setPreferredSize(new java.awt.Dimension(345,0));
        panelBrand.setBackground(navy);
        javax.swing.JPanel logoArea=new javax.swing.JPanel(new java.awt.GridBagLayout());
        logoArea.setOpaque(false);
        lblBrand.setIcon(null);
        java.net.URL brandUrl=getClass().getResource("/com/crunchshift/view/assets/icon_brand_header.png");
        if(brandUrl!=null){
            javax.swing.ImageIcon brand=new javax.swing.ImageIcon(brandUrl);
            int w=285;
            int h=Math.max(1,w*brand.getIconHeight()/Math.max(1,brand.getIconWidth()));
            lblBrand.setIcon(new javax.swing.ImageIcon(brand.getImage().getScaledInstance(w,h,java.awt.Image.SCALE_SMOOTH)));
        }
        lblBrand.setText("");
        lblBrand.setOpaque(false);
        lblBrand.setBorder(null);
        logoArea.add(lblBrand);
        panelBrand.add(logoArea,java.awt.BorderLayout.CENTER);
        lblSubBrand.setText("<html><div style='text-align:center;line-height:1.6'>"
                +"MANAJEMEN SHIFT &bull; PRESENSI<br>CATATAN AKTIVITAS &bull; CRUNCHPOINTS"
                +"<br><br><b>Kerja teratur, apresiasi terukur.</b></div></html>");
        lblSubBrand.setFont(new java.awt.Font("Segoe UI",0,14));
        lblSubBrand.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubBrand.setBorder(javax.swing.BorderFactory.createEmptyBorder(14,12,25,12));
        panelBrand.add(lblSubBrand,java.awt.BorderLayout.SOUTH);
        panelFormContainer.removeAll();
        panelFormContainer.setLayout(new java.awt.GridBagLayout());
        panelFormContainer.setBackground(canvas);
        panelFormKredensial.removeAll();
        panelFormKredensial.setBackground(java.awt.Color.WHITE);
        panelFormKredensial.setLayout(new java.awt.GridBagLayout());
        panelFormKredensial.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(border,1,true),
                javax.swing.BorderFactory.createEmptyBorder(18,25,20,25)));
        java.awt.GridBagConstraints gc=new java.awt.GridBagConstraints();
        gc.gridx=0;gc.gridwidth=2;gc.weightx=1;gc.fill=java.awt.GridBagConstraints.HORIZONTAL;
        gc.insets=new java.awt.Insets(0,0,3,0);
        lblWelcome.setFont(new java.awt.Font("Segoe UI",java.awt.Font.BOLD,23));
        lblWelcome.setForeground(navy);
        gc.gridy=0;panelFormKredensial.add(lblWelcome,gc);
        lblWelcomeSub.setFont(new java.awt.Font("Segoe UI",0,13));
        lblWelcomeSub.setForeground(new java.awt.Color(107,120,143));
        gc.gridy=1;gc.insets=new java.awt.Insets(0,0,16,0);panelFormKredensial.add(lblWelcomeSub,gc);
        lblEmail.setFont(new java.awt.Font("Segoe UI",java.awt.Font.BOLD,13));
        gc.gridy=2;gc.insets=new java.awt.Insets(0,0,9,0);panelFormKredensial.add(lblEmail,gc);
        txtEmail.setPreferredSize(new java.awt.Dimension(360,37));
        txtEmail.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(161,177,199)),
                javax.swing.BorderFactory.createEmptyBorder(0,13,0,13)));
        gc.gridy=3;gc.ipady=5;gc.insets=new java.awt.Insets(0,0,13,0);panelFormKredensial.add(txtEmail,gc);gc.ipady=0;
        lblPassword.setFont(new java.awt.Font("Segoe UI",java.awt.Font.BOLD,13));
        gc.gridy=4;gc.insets=new java.awt.Insets(0,0,9,0);panelFormKredensial.add(lblPassword,gc);
        txtPassword.setPreferredSize(new java.awt.Dimension(360,37));
        txtPassword.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(161,177,199)),
                javax.swing.BorderFactory.createEmptyBorder(0,13,0,13)));
        gc.gridy=5;gc.ipady=5;gc.insets=new java.awt.Insets(0,0,7,0);panelFormKredensial.add(txtPassword,gc);gc.ipady=0;
        chkTampilkanSandi.setBackground(java.awt.Color.WHITE);
        gc.gridy=6;gc.insets=new java.awt.Insets(0,0,10,0);panelFormKredensial.add(chkTampilkanSandi,gc);
        lblDbStatus.setFont(new java.awt.Font("Segoe UI",0,12));
        lblDbStatus.setPreferredSize(new java.awt.Dimension(360,36));
        gc.gridy=7;gc.insets=new java.awt.Insets(0,0,12,0);panelFormKredensial.add(lblDbStatus,gc);
        panelAction.removeAll();
        panelAction.setLayout(new java.awt.GridLayout(1,2,14,0));
        panelAction.setBackground(java.awt.Color.WHITE);
        btnReset.setPreferredSize(new java.awt.Dimension(160,40));
        btnReset.setFocusPainted(false);
        btnReset.setBorder(javax.swing.BorderFactory.createLineBorder(border));
        btnMasuk.setPreferredSize(new java.awt.Dimension(160,40));
        btnMasuk.setBackground(orange);btnMasuk.setForeground(java.awt.Color.WHITE);
        btnMasuk.setFocusPainted(false);
        panelAction.add(btnReset);panelAction.add(btnMasuk);
        gc.gridy=8;gc.insets=new java.awt.Insets(0,0,0,0);panelFormKredensial.add(panelAction,gc);
        java.awt.GridBagConstraints outer=new java.awt.GridBagConstraints();
        outer.insets=new java.awt.Insets(14,18,14,18);
        outer.fill=java.awt.GridBagConstraints.NONE;
        panelFormContainer.add(panelFormKredensial,outer);
        panelStatusBar.setLayout(new java.awt.BorderLayout());
        panelStatusBar.setPreferredSize(new java.awt.Dimension(0,32));
        lblStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,18,0,8));
        panelStatusBar.add(lblStatus,java.awt.BorderLayout.WEST);
        panelMain.revalidate();panelMain.repaint();
        pack();setLocationRelativeTo(null);
    }

    /** Checkbox asli GUI Builder; bisa diseleksi pada tab Design. */
    private void pasangTampilSandi() {
        final char echo=txtPassword.getEchoChar();
        chkTampilkanSandi.addActionListener(e -> txtPassword.setEchoChar(chkTampilkanSandi.isSelected()?(char)0:echo));
    }

    private void checkDatabaseStatus() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                lblDbStatus.setText("Koneksi DB: Terhubung (MySQL crunchshift_db)");
                lblStatus.setText("Status: Terhubung ke database, siap autentikasi.");
            } else {
                lblDbStatus.setText("Koneksi DB: Terputus");
                lblStatus.setText("Status: Gagal terhubung ke MySQL crunchshift_db");
            }
        } catch (Exception e) {
            lblDbStatus.setText("Koneksi DB: Error (" + e.getMessage() + ")");
            lblStatus.setText("Status: Periksa layanan MySQL lokal port 3306");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTitleBar = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panelMain = new javax.swing.JPanel();
        panelBrand = new javax.swing.JPanel();
        lblBrand = new javax.swing.JLabel();
        lblSubBrand = new javax.swing.JLabel();
        panelFormContainer = new javax.swing.JPanel();
        panelFormKredensial = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        lblWelcomeSub = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblDbStatus = new javax.swing.JLabel();
        panelAction = new javax.swing.JPanel();
        btnReset = new javax.swing.JButton();
        btnMasuk = new javax.swing.JButton();
        chkTampilkanSandi = new javax.swing.JCheckBox();
        panelStatusBar = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CrunchShift | Masuk");
        setPreferredSize(new java.awt.Dimension(980, 580));
        setResizable(true);

        panelTitleBar.setBackground(new java.awt.Color(27, 42, 92));
        panelTitleBar.setPreferredSize(new java.awt.Dimension(980, 52));

        javax.swing.GroupLayout panelTitleBarLayout = new javax.swing.GroupLayout(panelTitleBar);
        panelTitleBar.setLayout(panelTitleBarLayout);
        panelTitleBarLayout.setHorizontalGroup(
            panelTitleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelTitleBarLayout.createSequentialGroup().addGap(5).addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelTitleBarLayout.setVerticalGroup(
            panelTitleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelTitleBarLayout.createSequentialGroup().addGap(5).addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(248, 250, 252));
        lblTitle.setText("  CrunchShift  /  Portal Karyawan");


        getContentPane().add(panelTitleBar, java.awt.BorderLayout.NORTH);

        panelMain.setBackground(new java.awt.Color(248, 250, 252));
        panelMain.setLayout(new java.awt.BorderLayout());

        panelBrand.setBackground(new java.awt.Color(27, 42, 92));
        panelBrand.setPreferredSize(new java.awt.Dimension(345, 470));

        javax.swing.GroupLayout panelBrandLayout = new javax.swing.GroupLayout(panelBrand);
        panelBrand.setLayout(panelBrandLayout);
        panelBrandLayout.setHorizontalGroup(
            panelBrandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(lblBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblSubBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelBrandLayout.setVerticalGroup(
            panelBrandLayout.createSequentialGroup()
                .addGap(90)
                .addComponent(lblBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(168)
                .addComponent(lblSubBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        lblBrand.setFont(new java.awt.Font("Segoe UI", 1, 29)); // NOI18N
        lblBrand.setForeground(java.awt.Color.WHITE);
        lblBrand.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/crunchshift/view/assets/icon_brand_designer.png")));
        lblBrand.setText("");
        lblBrand.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);


        lblSubBrand.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSubBrand.setForeground(java.awt.Color.WHITE);
        lblSubBrand.setText("<html><div style='text-align:center'>SHIFT MANAGEMENT  &bull;  PRESENSI<br>CATATAN AKTIVITAS  &bull;  CRUNCHPOINTS<br><br>Kerja teratur, apresiasi terukur.</div></html>");
        lblSubBrand.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);



        panelMain.add(panelBrand, java.awt.BorderLayout.WEST);

        panelFormContainer.setBackground(new java.awt.Color(248, 250, 252));

        javax.swing.GroupLayout panelFormContainerLayout = new javax.swing.GroupLayout(panelFormContainer);
        panelFormContainer.setLayout(panelFormContainerLayout);
        panelFormContainerLayout.setHorizontalGroup(
            panelFormContainerLayout.createSequentialGroup().addGap(75)
                .addComponent(panelFormKredensial, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelFormContainerLayout.setVerticalGroup(
            panelFormContainerLayout.createSequentialGroup().addGap(23)
                .addComponent(panelFormKredensial, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        panelFormKredensial.setBackground(java.awt.Color.WHITE);
        panelFormKredensial.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(221, 228, 238), 1), javax.swing.BorderFactory.createEmptyBorder(20, 23, 22, 23)));
        panelFormKredensial.setPreferredSize(new java.awt.Dimension(450, 416));

        javax.swing.GroupLayout panelFormKredensialLayout = new javax.swing.GroupLayout(panelFormKredensial);
        panelFormKredensial.setLayout(panelFormKredensialLayout);
        panelFormKredensialLayout.setHorizontalGroup(
            panelFormKredensialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblWelcomeSub, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(chkTampilkanSandi, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblDbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(panelAction, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelFormKredensialLayout.setVerticalGroup(
            panelFormKredensialLayout.createSequentialGroup()
                .addGap(5)
                .addComponent(lblWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblWelcomeSub, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14)
                .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10)
                .addComponent(lblPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(chkTampilkanSandi, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(lblDbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13)
                .addComponent(panelAction, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        lblWelcome.setText("Selamat datang kembali");
        lblWelcome.setFont(new java.awt.Font("Segoe UI",java.awt.Font.BOLD,23));
        lblWelcome.setForeground(new java.awt.Color(27,42,92));
        lblWelcomeSub.setText("Masuk dengan akun karyawan kamu.");
        lblWelcomeSub.setFont(new java.awt.Font("Segoe UI",java.awt.Font.PLAIN,13));
        lblWelcomeSub.setForeground(new java.awt.Color(107,120,143));

        lblEmail.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(27, 42, 92));
        lblEmail.setText("Email");


        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtEmail.setText("");


        lblPassword.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(27, 42, 92));
        lblPassword.setText("Kata Sandi");


        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtPassword.setText("");
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPasswordKeyPressed(evt);
            }
        });


        lblDbStatus.setBackground(new java.awt.Color(240, 246, 255));
        lblDbStatus.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblDbStatus.setForeground(new java.awt.Color(27, 42, 92));
        lblDbStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDbStatus.setText("Koneksi: MySQL crunchshift_db");
        lblDbStatus.setOpaque(true);


        panelAction.setBackground(java.awt.Color.WHITE);

        javax.swing.GroupLayout panelActionLayout = new javax.swing.GroupLayout(panelAction);
        panelAction.setLayout(panelActionLayout);
        panelActionLayout.setHorizontalGroup(
            panelActionLayout.createSequentialGroup()
                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14)
                .addComponent(btnMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelActionLayout.setVerticalGroup(
            panelActionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        chkTampilkanSandi.setText("Tampilkan kata sandi");
        chkTampilkanSandi.setBackground(java.awt.Color.WHITE);
        chkTampilkanSandi.setFont(new java.awt.Font("Segoe UI",0,12));
        chkTampilkanSandi.setForeground(new java.awt.Color(27,42,92));
        btnReset.setBackground(new java.awt.Color(240, 246, 255));
        btnReset.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnReset.setForeground(new java.awt.Color(27, 42, 92));
        btnReset.setText("Bersihkan");
        btnReset.setPreferredSize(new java.awt.Dimension(155, 43));
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });


        btnMasuk.setBackground(new java.awt.Color(224, 123, 16));
        btnMasuk.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnMasuk.setForeground(java.awt.Color.WHITE);
        btnMasuk.setText("Masuk");
        btnMasuk.setPreferredSize(new java.awt.Dimension(160, 43));
        btnMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMasukActionPerformed(evt);
            }
        });




        panelMain.add(panelFormContainer, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelMain, java.awt.BorderLayout.CENTER);

        panelStatusBar.setBackground(new java.awt.Color(27, 42, 92));
        panelStatusBar.setPreferredSize(new java.awt.Dimension(980, 30));

        javax.swing.GroupLayout panelStatusBarLayout = new javax.swing.GroupLayout(panelStatusBar);
        panelStatusBar.setLayout(panelStatusBarLayout);
        panelStatusBarLayout.setHorizontalGroup(
            panelStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelStatusBarLayout.createSequentialGroup().addGap(5).addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelStatusBarLayout.setVerticalGroup(
            panelStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelStatusBarLayout.createSequentialGroup().addGap(3).addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(248, 250, 252));
        lblStatus.setText("Status: Siap autentikasi role MVC");


        getContentPane().add(panelStatusBar, java.awt.BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        txtEmail.setText("");
        txtPassword.setText("");
        txtEmail.requestFocus();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMasukActionPerformed
        prosesLogin();
    }//GEN-LAST:event_btnMasukActionPerformed

    private void txtPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPasswordKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            prosesLogin();
        }
    }//GEN-LAST:event_txtPasswordKeyPressed

    private void prosesLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Email dan password tidak boleh kosong!",
                    "Validasi Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        lblStatus.setText("Status: Memverifikasi kredensial...");
        Karyawan karyawan;
        try { karyawan = authController.authenticate(email, password); }
        catch (IllegalStateException ex) {
            lblStatus.setText("Status: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Autentikasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (karyawan == null) {
            lblStatus.setText("Status: Gagal login. Kredensial tidak valid.");
            JOptionPane.showMessageDialog(this,
                    "Email atau kata sandi salah!",
                    "Autentikasi Gagal",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String targetFrame = authController.determineFrame(karyawan);
        lblStatus.setText("Status: Login berhasil sebagai " + karyawan.getNama());

        MainFrame portal = new MainFrame(targetFrame);
        portal.setLocationRelativeTo(null);
        portal.setVisible(true);
        this.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMasuk;
    private javax.swing.JCheckBox chkTampilkanSandi;
    private javax.swing.JButton btnReset;
    private javax.swing.JLabel lblBrand;
    private javax.swing.JLabel lblDbStatus;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JLabel lblWelcomeSub;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSubBrand;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panelAction;
    private javax.swing.JPanel panelBrand;
    private javax.swing.JPanel panelFormContainer;
    private javax.swing.JPanel panelFormKredensial;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelStatusBar;
    private javax.swing.JPanel panelTitleBar;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
