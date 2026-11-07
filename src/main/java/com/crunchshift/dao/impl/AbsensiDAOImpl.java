package com.crunchshift.dao.impl;

import com.crunchshift.config.DatabaseConnection;
import com.crunchshift.dao.AbsensiDAO;
import com.crunchshift.model.Absensi;
import com.crunchshift.util.DateUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi DAO untuk Presensi Absensi Karyawan.
 * Selaras dengan tabel absensi (id_absensi, waktu_masuk, waktu_keluar, status_kehadiran,
 * keterangan_absen, status_verifikasi, jadwal_shift_id_jadwal, absen_masuk, absen_keluar)
 * terhubung via jadwal_shift.
 */
public class AbsensiDAOImpl implements AbsensiDAO {

    private static final String BASE_SELECT =
            "SELECT a.id_absensi, j.karyawan_id_karyawan AS id_karyawan, j.shift_id_shift AS id_shift, "
                    + "j.tanggal_jadwal AS tanggal, a.waktu_masuk, a.waktu_keluar, "
                    + "a.status_kehadiran, a.status_verifikasi, "
                    + "a.absen_masuk AS foto_masuk, a.absen_keluar AS foto_keluar, "
                    + "'' AS hash_foto, a.keterangan_absen AS catatan, "
                    + "k.nama_lengkap AS nama_karyawan, s.nama_shift "
                    + "FROM absensi a "
                    + "JOIN jadwal_shift j ON a.jadwal_shift_id_jadwal = j.id_jadwal "
                    + "JOIN karyawan k ON j.karyawan_id_karyawan = k.id_karyawan "
                    + "JOIN shift s ON j.shift_id_shift = s.id_shift ";

    @Override
    public List<Absensi> findAll() {
        List<Absensi> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY j.tanggal_jadwal DESC, a.waktu_masuk DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractAbsensi(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua absensi: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Absensi> findByKaryawan(int idKaryawan) {
        return cariAbsensi(idKaryawan);
    }

    @Override
    public List<Absensi> findByTanggal(LocalDate tanggal) {
        List<Absensi> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE j.tanggal_jadwal = ? ORDER BY a.waktu_masuk ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAbsensi(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari absensi by tanggal: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Absensi> findPendingVerifikasi() {
        List<Absensi> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE a.status_verifikasi = 'Menunggu' ORDER BY j.tanggal_jadwal DESC, a.waktu_masuk DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractAbsensi(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari absensi pending: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Absensi findHariIni(int idKaryawan, LocalDate tanggal) {
        String sql = BASE_SELECT + "WHERE j.karyawan_id_karyawan = ? AND (j.tanggal_jadwal = ? OR (j.tanggal_jadwal = DATE_SUB(?, INTERVAL 1 DAY) AND s.jam_masuk > s.jam_keluar AND a.waktu_keluar IS NULL)) ORDER BY j.tanggal_jadwal ASC, a.id_absensi DESC LIMIT 1";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            ps.setDate(2, Date.valueOf(tanggal));
            ps.setDate(3, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractAbsensi(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari absensi hari ini: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertCheckIn(Absensi absensi) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try {
            int idJadwal = -1;
            String sqlFindJadwal = "SELECT id_jadwal FROM jadwal_shift WHERE karyawan_id_karyawan = ? AND tanggal_jadwal = ? LIMIT 1";
            try (PreparedStatement psFind = conn.prepareStatement(sqlFindJadwal)) {
                psFind.setInt(1, absensi.getIdKaryawan());
                psFind.setDate(2, Date.valueOf(absensi.getTanggal()));
                try (ResultSet rsJ = psFind.executeQuery()) {
                    if (rsJ.next()) {
                        idJadwal = rsJ.getInt(1);
                    }
                }
            }

            if (idJadwal <= 0) {
                String sqlCreateJadwal = "INSERT INTO jadwal_shift (karyawan_id_karyawan, shift_id_shift, tanggal_jadwal) VALUES (?, ?, ?)";
                try (PreparedStatement psCreate = conn.prepareStatement(sqlCreateJadwal, Statement.RETURN_GENERATED_KEYS)) {
                    psCreate.setInt(1, absensi.getIdKaryawan());
                    psCreate.setInt(2, absensi.getIdShift() > 0 ? absensi.getIdShift() : 1);
                    psCreate.setDate(3, Date.valueOf(absensi.getTanggal()));
                    psCreate.executeUpdate();
                    try (ResultSet rsGen = psCreate.getGeneratedKeys()) {
                        if (rsGen.next()) {
                            idJadwal = rsGen.getInt(1);
                        }
                    }
                }
            }

            if (idJadwal <= 0) throw new SQLException("Jadwal kerja tidak ditemukan atau gagal dibuat.");
            try (PreparedStatement duplikat=conn.prepareStatement(
                    "SELECT COUNT(*) FROM absensi WHERE jadwal_shift_id_jadwal=?")) {
                duplikat.setInt(1,idJadwal);
                try(ResultSet rs=duplikat.executeQuery()){
                    if(rs.next() && rs.getInt(1)>0)
                        throw new SQLException("Anda sudah melakukan Check-In untuk jadwal ini. Gunakan Check-Out untuk pulang.");
                }
            }

            String sql = "INSERT INTO absensi (jadwal_shift_id_jadwal, waktu_masuk, "
                    + "status_kehadiran, status_verifikasi, absen_masuk, absen_keluar, keterangan_absen) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idJadwal);
                ps.setTimestamp(2, absensi.getWaktuMasuk() != null ? DateUtil.toTimestamp(absensi.getWaktuMasuk()) : new Timestamp(System.currentTimeMillis()));
                ps.setString(3, absensi.getStatusKehadiran() != null ? absensi.getStatusKehadiran() : "Hadir");
                ps.setString(4, absensi.getStatusVerifikasi() != null ? absensi.getStatusVerifikasi() : "Menunggu");
                simpanBukti(ps, 5, absensi.getFotoMasuk());
                ps.setNull(6, java.sql.Types.BLOB);
                ps.setString(7, absensi.getCatatan() != null ? absensi.getCatatan() : "Presensi Masuk");

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            absensi.setIdAbsensi(rs.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal check-in absensi: " + e.getMessage());
            throw new IllegalStateException("Check-In gagal: " + e.getMessage(),e);
        }
        return false;
    }

    /** Pengajuan izin staff berdasarkan jadwal miliknya, hanya bila belum ada presensi. */
    public boolean ajukanIzin(int idKaryawan, int idJadwal, String alasan) {
        if (idKaryawan <= 0 || idJadwal <= 0 || alasan == null || alasan.trim().isEmpty() || alasan.length() > 200) return false;
        String sql = "INSERT INTO absensi (jadwal_shift_id_jadwal, waktu_masuk, waktu_keluar, "
                + "status_kehadiran, keterangan_absen, status_verifikasi, absen_masuk, absen_keluar) "
                + "SELECT j.id_jadwal, NULL, NULL, 'Izin', ?, 'Menunggu', ?, NULL "
                + "FROM jadwal_shift j WHERE j.id_jadwal = ? AND j.karyawan_id_karyawan = ? "
                + "AND NOT EXISTS (SELECT 1 FROM absensi a WHERE a.jadwal_shift_id_jadwal = j.id_jadwal)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, alasan.trim());
            ps.setBytes(2, new byte[0]); // absen_masuk mediumblob NOT NULL; izin tidak memakai bukti foto
            ps.setInt(3, idJadwal); ps.setInt(4, idKaryawan);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { throw new IllegalStateException("Gagal mengajukan izin: " + e.getMessage(), e); }
    }

    /** Koreksi kehadiran oleh Supervisor tanpa menghilangkan histori presensi. */
    public boolean koreksiKehadiran(int idAbsensi, String status, String alasan) {
        if (idAbsensi <= 0 || !java.util.Arrays.asList("Hadir", "Terlambat", "Izin", "Tidak Hadir").contains(status)
                || alasan == null || alasan.isBlank() || alasan.length() > 200) return false;
        String sql = "UPDATE absensi SET status_kehadiran = ?, keterangan_absen = ? WHERE id_absensi = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,status); ps.setString(2,alasan.trim()); ps.setInt(3,idAbsensi);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {throw new IllegalStateException("Koreksi presensi gagal: " + e.getMessage(), e);}
    }

    /** Penolakan hanya dapat diterapkan pada baris berstatus Izin. */
    public boolean tolakIzin(int idAbsensi, String alasan) {
        if (alasan == null || alasan.isBlank() || alasan.length() > 200) return false;
        String sql = "UPDATE absensi SET status_verifikasi = 'Ditolak', keterangan_absen = ? "
                + "WHERE id_absensi = ? AND status_kehadiran = 'Izin'";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,alasan.trim()); ps.setInt(2,idAbsensi);
            return ps.executeUpdate() == 1;
        } catch(SQLException e) {throw new IllegalStateException("Gagal menolak izin: " + e.getMessage(), e);}
    }


    @Override
    public boolean updateCheckOut(int idAbsensi, ZonedDateTime waktuKeluar, String fotoKeluar) {
        String sql = "UPDATE absensi SET waktu_keluar = ?, absen_keluar = ? WHERE id_absensi = ? AND waktu_keluar IS NULL";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, waktuKeluar != null ? DateUtil.toTimestamp(waktuKeluar) : new Timestamp(System.currentTimeMillis()));
            simpanBukti(ps, 2, fotoKeluar);
            ps.setInt(3, idAbsensi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal check-out absensi: " + e.getMessage());
            throw new IllegalStateException("Check-Out gagal: " + e.getMessage(),e);
        }
    }

    @Override
    public boolean updateVerifikasi(int idAbsensi, String statusVerifikasi, String catatan) {
        String sql = "UPDATE absensi SET status_verifikasi = ?, keterangan_absen = ? WHERE id_absensi = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusVerifikasi);
            ps.setString(2, catatan);
            ps.setInt(3, idAbsensi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal update verifikasi absensi: " + e.getMessage());
            return false;
        }
    }

    @Override
    public double hitungPersentaseKehadiranHariIni() {
        String sqlTotal = "SELECT COUNT(*) FROM jadwal_shift WHERE tanggal_jadwal = CURDATE()";
        String sqlHadir = "SELECT COUNT(*) FROM absensi a JOIN jadwal_shift j ON a.jadwal_shift_id_jadwal = j.id_jadwal "
                + "WHERE j.tanggal_jadwal = CURDATE() AND a.status_kehadiran IN ('Hadir', 'Terlambat')";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0.0;

        try (Statement stmt = conn.createStatement()) {
            int total = 0;
            int hadir = 0;
            try (ResultSet rsTotal = stmt.executeQuery(sqlTotal)) {
                if (rsTotal.next()) total = rsTotal.getInt(1);
            }
            try (ResultSet rsHadir = stmt.executeQuery(sqlHadir)) {
                if (rsHadir.next()) hadir = rsHadir.getInt(1);
            }
            if (total == 0) return hadir > 0 ? 100.0 : 0.0;
            return Math.round(((double) hadir / total) * 1000.0) / 10.0;
        } catch (SQLException e) {
            System.err.println("Gagal hitung persentase kehadiran: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public List<Absensi> cariAbsensi(int idKaryawan) {
        List<Absensi> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE j.karyawan_id_karyawan = ? ORDER BY j.tanggal_jadwal DESC, a.waktu_masuk DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAbsensi(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal cari absensi karyawan: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Absensi> cariAbsensi(int idKaryawan, LocalDate tanggal) {
        List<Absensi> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE j.karyawan_id_karyawan = ? AND j.tanggal_jadwal = ? ORDER BY a.waktu_masuk DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            ps.setDate(2, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAbsensi(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal cari absensi filter tanggal: " + e.getMessage());
        }
        return list;
    }

    private Absensi extractAbsensi(ResultSet rs) throws SQLException {
        Absensi a = new Absensi();
        a.setIdAbsensi(rs.getInt("id_absensi"));
        a.setIdKaryawan(rs.getInt("id_karyawan"));
        a.setIdShift(rs.getInt("id_shift"));
        Date dt = rs.getDate("tanggal");
        if (dt != null) a.setTanggal(dt.toLocalDate());
        Timestamp tsMasuk = rs.getTimestamp("waktu_masuk");
        if (tsMasuk != null) a.setWaktuMasuk(DateUtil.fromTimestamp(tsMasuk));
        Timestamp tsKeluar = rs.getTimestamp("waktu_keluar");
        if (tsKeluar != null) a.setWaktuKeluar(DateUtil.fromTimestamp(tsKeluar));
        a.setStatusKehadiran(rs.getString("status_kehadiran"));
        a.setStatusVerifikasi(rs.getString("status_verifikasi"));
        a.setFotoMasuk(labelBukti(rs.getBytes("foto_masuk"), a.getIdAbsensi(), true));
        a.setFotoKeluar(labelBukti(rs.getBytes("foto_keluar"), a.getIdAbsensi(), false));
        a.setHashFoto(rs.getString("hash_foto"));
        a.setCatatan(rs.getString("catatan"));
        a.setNamaKaryawan(rs.getString("nama_karyawan"));
        a.setNamaShift(rs.getString("nama_shift"));
        return a;
    }
    /** Byte foto baru disimpan di MEDIUMBLOB; nama file lama tetap bisa dibaca. */
    private static void simpanBukti(PreparedStatement ps,int indeks,String nama) throws SQLException {
        if(nama == null || nama.isBlank()) throw new SQLException("Foto presensi wajib dipilih.");
        java.nio.file.Path path = java.nio.file.Path.of(System.getProperty("user.dir"),
                "src", "main", "resources", "assets", "attendance", new java.io.File(nama).getName());
        if(!java.nio.file.Files.isRegularFile(path)) throw new SQLException("File foto tidak ditemukan: "+path);
        try {
            long ukuran=java.nio.file.Files.size(path);
            if(ukuran>8*1024*1024) throw new SQLException("Maksimal ukuran foto 8 MB.");
            byte[] bytes=java.nio.file.Files.readAllBytes(path);
            if(javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(bytes))==null)
                throw new SQLException("Foto tidak valid.");
            ps.setBytes(indeks, bytes);
        } catch(java.io.IOException ex) {throw new SQLException("Gagal membaca foto",ex);}
    }
    private static String labelBukti(byte[] isi,int id,boolean masuk) {
        if(isi==null || isi.length==0)return null;
        if(isi.length>4 && ((isi[0]&255)==0xff && (isi[1]&255)==0xd8 ||
                isi[0]==(byte)0x89 && isi[1]==0x50 || isi[0]==0x47 && isi[1]==0x49))
            return "DB_FOTO:"+id+":"+(masuk?"MASUK":"KELUAR");
        return new String(isi,java.nio.charset.StandardCharsets.UTF_8);
    }
    /** Dapat dipakai Supervisor untuk membaca bukti BLOB secara langsung. */
    public byte[] ambilFoto(int id,boolean masuk) {
        String sql="SELECT "+(masuk?"absen_masuk":"absen_keluar")+" FROM absensi WHERE id_absensi=?";
        try(java.sql.Connection c=DatabaseConnection.getConnection()) {
            if(c==null)return null;
            try(PreparedStatement ps=c.prepareStatement(sql)){
                ps.setInt(1,id);
                try(ResultSet rs=ps.executeQuery()) {return rs.next()?rs.getBytes(1):null;}
            }
        }catch(SQLException ex){throw new IllegalStateException("Foto tidak bisa diambil: "+ex.getMessage(),ex);}
    }

}
