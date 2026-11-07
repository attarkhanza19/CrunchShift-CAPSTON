package com.crunchshift.controller;

import com.crunchshift.config.DatabaseConnection;
import com.crunchshift.dao.AbsensiDAO;
import com.crunchshift.dao.CrunchPointsDAO;
import com.crunchshift.dao.JadwalShiftDAO;
import com.crunchshift.dao.LogAktivitasDAO;
import com.crunchshift.dao.ShiftDAO;
import com.crunchshift.dao.impl.AbsensiDAOImpl;
import com.crunchshift.dao.impl.CrunchPointsDAOImpl;
import com.crunchshift.dao.impl.KaryawanDAOImpl;
import com.crunchshift.dao.impl.KategoriPoinDAOImpl;
import com.crunchshift.model.Karyawan;
import com.crunchshift.model.KategoriPoin;
import com.crunchshift.dao.impl.JadwalShiftDAOImpl;
import com.crunchshift.dao.impl.LogAktivitasDAOImpl;
import com.crunchshift.dao.impl.ShiftDAOImpl;
import com.crunchshift.model.Absensi;
import com.crunchshift.model.CrunchPoints;
import com.crunchshift.model.JadwalShift;
import com.crunchshift.model.LogAktivitas;
import com.crunchshift.model.Shift;
import com.crunchshift.util.DateUtil;
import com.crunchshift.util.FileHashUtil;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Controller untuk modul Staff Mandiri: Presensi digital WITA, anti-fraud foto SHA-256,
 * handover log harian, dan pantauan Crunch Points gamifikasi.
 */
public class StaffController {

    private final JadwalShiftDAO jadwalShiftDAO;
    private final ShiftDAO shiftDAO;
    private final AbsensiDAO absensiDAO;
    private final LogAktivitasDAO logAktivitasDAO;
    private final CrunchPointsDAO crunchPointsDAO;

    public static final String CARD_DASHBOARD = "CARD_DASHBOARD";
    public static final String CARD_MESIN_PRESENSI = "CARD_MESIN_PRESENSI";
    public static final String CARD_JADWAL_SAYA = "CARD_JADWAL_SAYA";
    public static final String CARD_RIWAYAT_PRESENSI = "CARD_RIWAYAT_PRESENSI";
    public static final String CARD_LOG_HARIAN = "CARD_LOG_HARIAN";
    public static final String CARD_CRUNCH_POINTS_SAYA = "CARD_CRUNCH_POINTS_SAYA";

    public void switchCard(java.awt.Container container, String cardName) {
        if (container != null && container.getLayout() instanceof java.awt.CardLayout) {
            ((java.awt.CardLayout) container.getLayout()).show(container, cardName);
        }
    }

    public StaffController() {
        this.jadwalShiftDAO = new JadwalShiftDAOImpl();
        this.shiftDAO = new ShiftDAOImpl();
        this.absensiDAO = new AbsensiDAOImpl();
        this.logAktivitasDAO = new LogAktivitasDAOImpl();
        this.crunchPointsDAO = new CrunchPointsDAOImpl();
    }

    public JadwalShift getJadwalHariIni(int idKaryawan) {
        return jadwalShiftDAO.findJadwalHariIni(idKaryawan, LocalDate.now());
    }

    public Absensi getAbsensiHariIni(int idKaryawan) {
        return absensiDAO.findHariIni(idKaryawan, LocalDate.now());
    }

    /**
     * Memproses file foto bukti dan menghitung SHA-256 anti-fraud.
     * Mengembalikan array String [hashFoto, pathTersimpan].
     * Melempar Exception jika foto terdeteksi duplikat atau gagal dibaca.
     */
    public String[] prosesFotoBukti(File fileFoto, String projectBasePath) throws Exception {
        if (fileFoto == null || !fileFoto.exists()) {
            throw new IllegalArgumentException("Berkas foto tidak ditemukan.");
        }

        // 1. Generate SHA-256 hash
        String hash = FileHashUtil.generateSHA256(fileFoto);

        // 2. Decision Check: Apakah hash duplikat di DB?
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null && FileHashUtil.isDuplicateHash(hash, conn)) {
            throw new IllegalStateException("DUPLICATE_PHOTO");
        }

        // 3. Salin foto ke direktori assets/attendance
        File savedFile = FileHashUtil.copyToAttendanceDir(fileFoto, projectBasePath != null ? projectBasePath : ".");
        return new String[]{hash, savedFile.getName()};
    }

    public boolean lakukanCheckIn(int idKaryawan, int idShift, String fotoFilename, String hashFoto, String catatan) {
        Shift shift = shiftDAO.findById(idShift);
        ZonedDateTime sekarang = DateUtil.sekarangWITA();

        String statusKehadiran = "Hadir";
        if (shift != null && shift.getJamMasuk() != null) {
            // Toleransi keterlambatan 15 menit
            if (DateUtil.isTerlambat(sekarang, shift.getJamMasuk(), 15)) {
                statusKehadiran = "Terlambat";
            }
        }

        Absensi absensi = new Absensi();
        absensi.setIdKaryawan(idKaryawan);
        absensi.setIdShift(idShift);
        absensi.setTanggal(sekarang.toLocalDate());
        absensi.setWaktuMasuk(sekarang);
        absensi.setStatusKehadiran(statusKehadiran);
        absensi.setStatusVerifikasi("Menunggu");
        absensi.setFotoMasuk(fotoFilename);
        absensi.setHashFoto(hashFoto);
        absensi.setCatatan(catatan);

        return absensiDAO.insertCheckIn(absensi);
    }

    public boolean lakukanCheckOut(int idAbsensi, String fotoFilename) {
        ZonedDateTime sekarang = DateUtil.sekarangWITA();
        return absensiDAO.updateCheckOut(idAbsensi, sekarang, fotoFilename);
    }

    public boolean ajukanIzin(int idKaryawan, int idJadwal, String alasan) {
        return ((AbsensiDAOImpl)absensiDAO).ajukanIzin(idKaryawan, idJadwal, alasan);
    }


    public List<JadwalShift> getJadwalSaya(int idKaryawan) {
        return jadwalShiftDAO.findByKaryawan(idKaryawan);
    }

    public List<Absensi> getRiwayatAbsensi(int idKaryawan) {
        return absensiDAO.findByKaryawan(idKaryawan);
    }

    public List<LogAktivitas> getRiwayatLogStaff(int idKaryawan) {
        return logAktivitasDAO.findByKaryawan(idKaryawan);
    }

    public boolean kirimLogAktivitas(int idKaryawan, LocalTime jamMulai, LocalTime jamSelesai, String deskripsi) {
        if (idKaryawan <= 0 || jamMulai == null || jamSelesai == null || deskripsi == null || deskripsi.trim().isEmpty()) {
            return false;
        }

        LogAktivitas log = new LogAktivitas();
        log.setIdKaryawan(idKaryawan);
        log.setTanggal(LocalDate.now());
        log.setJamMulai(jamMulai);
        log.setJamSelesai(jamSelesai);
        log.setDeskripsiAktivitas(deskripsi.trim());
        log.setStatusVerifikasi("Menunggu");

        return logAktivitasDAO.insert(log);
    }

    public List<Karyawan> daftarSupervisor() {return new KaryawanDAOImpl().findByPeran("Supervisor");}
    public List<KategoriPoin> daftarKategoriPoin() {return new KategoriPoinDAOImpl().findAll();}
    public boolean ajukanCrunchPoints(int idStaff,int idSupervisor,int idKategori,String deskripsi,int nilaiAcuan) {
        return ((CrunchPointsDAOImpl)crunchPointsDAO).ajukanPoin(idStaff,idSupervisor,idKategori,deskripsi,nilaiAcuan);
    }

    /** Simpan uraian kerja, catatan handover dan dokumentasi nyata di database. */
    public boolean kirimLogLengkap(int idKaryawan,String uraian,String handover,String dokumentasi) {
        if(idKaryawan<=0||uraian==null||uraian.trim().isEmpty()||handover==null||handover.trim().isEmpty()||dokumentasi==null||dokumentasi.isBlank()) return false;
        LogAktivitas log=new LogAktivitas();
        log.setIdKaryawan(idKaryawan);log.setTanggal(LocalDate.now());
        log.setDeskripsiAktivitas(uraian.trim());log.setCatatanSupervisor(handover.trim());
        log.setDokumentasiKegiatan(dokumentasi);
        log.setStatusVerifikasi("Menunggu");
        return logAktivitasDAO.insert(log);
    }

    public boolean perbaruiLogStaff(int idLog, int idKaryawan, String ringkasan,
                                     String catatanSerahTerima, String dokumentasi) {
        return ((LogAktivitasDAOImpl)logAktivitasDAO)
                .perbaruiLogStaff(idLog, idKaryawan, ringkasan, catatanSerahTerima, dokumentasi);
    }


    public List<CrunchPoints> getRiwayatPoin(int idKaryawan) {
        return crunchPointsDAO.findByKaryawan(idKaryawan);
    }

    public int getSaldoPoinStaff(int idKaryawan) {
        List<CrunchPoints> points = crunchPointsDAO.cariPoin(idKaryawan, "Apresiasi");
        int total = 0;
        for (CrunchPoints cp : points) {
            if ("Disetujui".equals(cp.getStatusVerifikasi())) total += cp.getNilaiPoin();
        }
        return total;
    }

    public String tentukanBadgeLevel(int totalPoin) {
        if (totalPoin >= 500) return "Platinum 💎";
        if (totalPoin >= 250) return "Gold 🥇";
        if (totalPoin >= 100) return "Silver 🥈";
        return "Bronze 🥉";
    }
}
