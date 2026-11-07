-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Nov 03, 2026 at 04:09 PM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `crunchshift_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `absensi`
--

CREATE TABLE `absensi` (
  `id_absensi` int NOT NULL,
  `waktu_masuk` timestamp NULL DEFAULT NULL,
  `waktu_keluar` timestamp NULL DEFAULT NULL,
  `status_kehadiran` enum('-','Hadir','Terlambat','Izin','Tidak Hadir') NOT NULL DEFAULT '-',
  `keterangan_absen` varchar(200) DEFAULT NULL,
  `status_verifikasi` enum('Menunggu','Disetujui','Ditolak') NOT NULL DEFAULT 'Menunggu',
  `jadwal_shift_id_jadwal` int NOT NULL,
  `absen_masuk` mediumblob NOT NULL,
  `absen_keluar` mediumblob
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `absensi`
--

INSERT INTO `absensi` (`id_absensi`, `waktu_masuk`, `waktu_keluar`, `status_kehadiran`, `keterangan_absen`, `status_verifikasi`, `jadwal_shift_id_jadwal`, `absen_masuk`, `absen_keluar`) VALUES
(1, '2026-09-30 22:50:00', '2026-10-01 07:05:00', 'Hadir', 'Hadir tepat waktu', 'Disetujui', 1, 0x62756b74695f6d6173756b5f30312e6a7067, 0x62756b74695f6b656c7561725f30312e6a7067),
(2, '2026-09-30 22:55:00', '2026-10-01 07:02:00', 'Hadir', 'Hadir tepat waktu', 'Disetujui', 2, 0x62756b74695f6d6173756b5f30322e6a7067, 0x62756b74695f6b656c7561725f30322e6a7067),
(3, '2026-10-01 07:18:00', '2026-10-01 15:05:00', 'Terlambat', 'Terjebak kemacetan jalur logistik', 'Disetujui', 3, 0x62756b74695f6d6173756b5f30332e6a7067, 0x62756b74695f6b656c7561725f30332e6a7067),
(4, '2026-10-01 06:52:00', '2026-10-01 15:01:00', 'Hadir', 'Hadir tepat waktu', 'Disetujui', 4, 0x62756b74695f6d6173756b5f30342e6a7067, 0x62756b74695f6b656c7561725f30342e6a7067),
(5, '2026-10-01 14:55:00', '2026-10-01 23:04:00', 'Hadir', 'Shift malam lancar', 'Disetujui', 5, 0x62756b74695f6d6173756b5f30352e6a7067, 0x62756b74695f6b656c7561725f30352e6a7067),
(6, '2026-10-01 22:58:00', '2026-10-02 07:00:00', 'Hadir', 'Hadir tepat waktu', 'Disetujui', 6, 0x62756b74695f6d6173756b5f30362e6a7067, 0x62756b74695f6b656c7561725f30362e6a7067),
(7, NULL, NULL, 'Izin', 'Izin pemeriksaan kesehatan rutin', 'Disetujui', 7, 0x73757261745f646f6b7465725f30372e6a7067, 0x544944414b5f4144415f42554b54495f4b454c5541525f37),
(8, '2026-10-02 06:50:00', '2026-10-02 15:03:00', 'Hadir', 'Hadir tepat waktu', 'Disetujui', 8, 0x62756b74695f6d6173756b5f30382e6a7067, 0x62756b74695f6b656c7561725f30382e6a7067),
(9, '2026-10-02 07:25:00', '2026-10-02 15:10:00', 'Terlambat', 'Ban motor bocor (Telah diverifikasi Supervisor)', 'Disetujui', 9, 0x62756b74695f6d6173756b5f30392e6a7067, 0x62756b74695f6b656c7561725f30392e6a7067),
(10, '2026-10-02 14:50:00', '2026-10-02 23:01:00', 'Hadir', 'Shift malam lancar', 'Disetujui', 10, 0x62756b74695f6d6173756b5f31302e6a7067, 0x62756b74695f6b656c7561725f31302e6a7067),
(11, '2026-10-02 22:53:00', '2026-10-03 07:04:00', 'Hadir', 'Hadir tepat waktu', 'Disetujui', 11, 0x62756b74695f6d6173756b5f31312e6a7067, 0x62756b74695f6b656c7561725f31312e6a7067),
(12, NULL, NULL, 'Izin', 'Urlaub acara keluarga mendadak', 'Ditolak', 12, 0x544944414b5f4144415f42554b54495f4d4153554b5f3132, 0x544944414b5f4144415f42554b54495f4b454c5541525f3132),
(13, '2026-10-03 06:55:00', '2026-10-03 15:00:00', 'Hadir', 'Hadir tepat waktu', 'Disetujui', 13, 0x62756b74695f6d6173756b5f31332e6a7067, 0x62756b74695f6b656c7561725f31332e6a7067),
(14, NULL, NULL, 'Tidak Hadir', 'Tanpa keterangan (Alpha)', 'Disetujui', 14, 0x544944414b5f4144415f42554b54495f4d4153554b5f3134, 0x544944414b5f4144415f42554b54495f4b454c5541525f3134),
(15, '2026-10-04 06:51:00', '2026-10-04 15:02:00', 'Hadir', 'Hadir tepat waktu', 'Disetujui', 15, 0x62756b74695f6d6173756b5f31352e6a7067, 0x62756b74695f6b656c7561725f31352e6a7067),
(16, '2026-11-03 08:16:07', '2026-11-03 08:16:34', 'Terlambat', 'Koreksi SPV (Hadir Tepat Waktu): keren', 'Disetujui', 16, 0x313739333639333736363836385f7475676173206d746b2e6a706567, 0x313739333639333739333738375f7475676173206d746b2e6a706567);

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id_karyawan` int NOT NULL,
  `departemen` enum('-','produksi','logistik','administrasi') NOT NULL DEFAULT '-',
  `tingkat_akses` enum('-','Pusat','Cabang') NOT NULL DEFAULT '-'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id_karyawan`, `departemen`, `tingkat_akses`) VALUES
(1, 'administrasi', 'Pusat'),
(2, 'produksi', 'Pusat'),
(3, 'logistik', 'Cabang');

-- --------------------------------------------------------

--
-- Table structure for table `crunch_points`
--

CREATE TABLE `crunch_points` (
  `id_poin` int NOT NULL,
  `deskripsi_kontribusi` varchar(500) NOT NULL,
  `jumlah_poin` int NOT NULL,
  `status_verifikasi` enum('Menunggu','Disetujui','Ditolak') NOT NULL DEFAULT 'Menunggu',
  `tanggal_pengajuan` date NOT NULL,
  `staff_id_karyawan` int NOT NULL,
  `supervisor_id_karyawan` int NOT NULL,
  `kategori_poin_id_kategori` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `crunch_points`
--

INSERT INTO `crunch_points` (`id_poin`, `deskripsi_kontribusi`, `jumlah_poin`, `status_verifikasi`, `tanggal_pengajuan`, `staff_id_karyawan`, `supervisor_id_karyawan`, `kategori_poin_id_kategori`) VALUES
(1, 'Hadir tepat waktu penuh dan membantu persiapan pemanasan oven lebih awal', 10, 'Disetujui', '2026-10-01', 9, 4, 1),
(2, 'Mencatat serah terima takaran adonan dengan sangat rinci untuk shift sore', 20, 'Disetujui', '2026-10-01', 10, 4, 2),
(3, 'Mendeteksi keretakan lempeng pemanas wafer sebelum adonan tumpah', 50, 'Disetujui', '2026-10-01', 11, 6, 3),
(4, 'Mengatur ulang ketegangan roll plastik sehingga reject saset kopi turun 0%', 50, 'Disetujui', '2026-10-01', 12, 5, 3),
(5, 'Membantu tim gudang menyusun palet karton saat pergantian shift malam', 20, 'Disetujui', '2026-10-02', 13, 6, 2),
(6, 'Disiplin presensi dan kerapian area kerja mesin coding', 10, 'Disetujui', '2026-10-02', 14, 5, 1),
(7, 'Menyusun jalur manuver forklift baru yang memangkas waktu muat 15 menit', 100, 'Disetujui', '2026-10-02', 15, 7, 4),
(8, 'Teliti menemukan bahan baku lembap saat proses bongkar muat', 50, 'Disetujui', '2026-10-02', 16, 7, 3),
(9, 'Koordinasi serah terima dokumen ekspedisi lintas shift dengan sangat rapi', 20, 'Disetujui', '2026-10-02', 17, 7, 2),
(10, 'Mengusulkan pola pre-heating roaster kopi yang menghemat gas 8%', 100, 'Disetujui', '2026-10-03', 18, 4, 4),
(11, 'Sigap menghentikan konveyor saat sensor suhu sealer tidak stabil', 50, 'Disetujui', '2026-10-03', 19, 5, 3),
(12, 'Merapikan pelabelan rak stok opname sehingga audit bulanan lebih cepat', 20, 'Disetujui', '2026-10-03', 20, 7, 2),
(13, 'Hadir lebih awal untuk menyelesaikan sanitasi pipa sebelum jadwal giling', 10, 'Disetujui', '2026-10-03', 21, 4, 1),
(14, 'Membantu rekan pengemasan melakukan sortir ekstra pada jam sibuk', 20, 'Disetujui', '2026-10-03', 22, 8, 2),
(15, 'Inisiatif kalibrasi mandiri sensor oven sehingga kualitas warna biskuit stabil', 50, 'Menunggu', '2026-10-04', 9, 4, 3);

-- --------------------------------------------------------

--
-- Table structure for table `jadwal_shift`
--

CREATE TABLE `jadwal_shift` (
  `id_jadwal` int NOT NULL,
  `tanggal_jadwal` date NOT NULL,
  `karyawan_id_karyawan` int NOT NULL,
  `shift_id_shift` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `jadwal_shift`
--

INSERT INTO `jadwal_shift` (`id_jadwal`, `tanggal_jadwal`, `karyawan_id_karyawan`, `shift_id_shift`) VALUES
(1, '2026-10-01', 9, 1),
(2, '2026-10-01', 10, 1),
(3, '2026-10-01', 11, 2),
(4, '2026-10-01', 12, 2),
(5, '2026-10-01', 13, 3),
(6, '2026-10-02', 14, 1),
(7, '2026-10-02', 15, 1),
(8, '2026-10-02', 16, 2),
(9, '2026-10-02', 17, 2),
(10, '2026-10-02', 18, 3),
(11, '2026-10-03', 19, 1),
(12, '2026-10-03', 20, 1),
(13, '2026-10-03', 21, 2),
(14, '2026-10-03', 22, 3),
(15, '2026-10-04', 9, 2),
(16, '2026-11-03', 5, 1),
(17, '2026-11-03', 9, 1),
(18, '2026-11-03', 10, 2),
(19, '2026-11-12', 21, 3);

-- --------------------------------------------------------

--
-- Table structure for table `karyawan`
--

CREATE TABLE `karyawan` (
  `id_karyawan` int NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(225) NOT NULL,
  `jenis_karyawan` enum('-','Admin','Supervisor','Staff') NOT NULL DEFAULT '-',
  `status_akun` enum('Aktif','Nonaktif') NOT NULL DEFAULT 'Aktif'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `karyawan`
--

INSERT INTO `karyawan` (`id_karyawan`, `nama_lengkap`, `email`, `password`, `jenis_karyawan`, `status_akun`) VALUES
(1, 'Muhammad Attar Khanza', 'attar.admin@crunch.co.id', 'pass1', 'Admin', 'Aktif'),
(2, 'Evan Setiawan', 'evan.admin@crunch.co.id', 'pass2', 'Admin', 'Aktif'),
(3, 'Clara Wijaya', 'clara.admin@crunch.co.id', 'pass3', 'Admin', 'Aktif'),
(4, 'Az-Zahra Imsawati', 'zahra.spv@crunch.co.id', 'pass4', 'Supervisor', 'Aktif'),
(5, 'Arizky Saputra', 'arizky.staff@crunch.co.id', 'pass5', 'Staff', 'Aktif'),
(6, 'Hendra Gunawan', 'hendra.spv@crunch.co.id', 'pass6', 'Supervisor', 'Aktif'),
(7, 'Surya Pratama', 'surya.spv@crunch.co.id', 'pass7', 'Supervisor', 'Aktif'),
(8, 'Dian Kusuma', 'dian.spv@crunch.co.id', 'pass8', 'Supervisor', 'Aktif'),
(9, 'Budi Santoso', 'budi.staff@crunch.co.id', 'pass9', 'Staff', 'Aktif'),
(10, 'Siti Aminah', 'siti.staff@crunch.co.id', 'pass10', 'Staff', 'Aktif'),
(11, 'Rudi Hartono', 'rudi.staff@crunch.co.id', 'pass11', 'Staff', 'Aktif'),
(12, 'Dewi Lestari', 'dewi.staff@crunch.co.id', 'pass12', 'Staff', 'Aktif'),
(13, 'Andi Wijaya', 'andi.staff@crunch.co.id', 'pass13', 'Staff', 'Aktif'),
(14, 'Maya Puspita', 'maya.staff@crunch.co.id', 'pass14', 'Staff', 'Aktif'),
(15, 'Rina Kusuma', 'rina.staff@crunch.co.id', 'pass15', 'Staff', 'Aktif'),
(16, 'Fajar Nugroho', 'fajar.staff@crunch.co.id', 'pass16', 'Staff', 'Aktif'),
(17, 'Gilang Ramadhan', 'gilang.staff@crunch.co.id', 'pass17', 'Staff', 'Aktif'),
(18, 'Putri Handayani', 'putri.staff@crunch.co.id', 'pass18', 'Staff', 'Aktif'),
(19, 'Reza Rahadian', 'reza.staff@crunch.co.id', 'pass19', 'Staff', 'Aktif'),
(20, 'Nadia Omara', 'nadia.staff@crunch.co.id', 'pass20', 'Staff', 'Aktif'),
(21, 'Tono Sudirjo', 'tono.staff@crunch.co.id', 'pass21', 'Staff', 'Aktif'),
(22, 'Wulan Guritno', 'wulan.staff@crunch.co.id', 'pass22', 'Staff', 'Aktif'),
(23, 'Zaki Yamani', 'zaki.staff@crunch.co.id', 'pass23', 'Staff', 'Aktif');

-- --------------------------------------------------------

--
-- Table structure for table `kategori_poin`
--

CREATE TABLE `kategori_poin` (
  `id_kategori` int NOT NULL,
  `nama_kategori` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nilai_acuan` int NOT NULL DEFAULT '0',
  `keterangan` varchar(225) DEFAULT NULL,
  `admin_id_karyawan` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `kategori_poin`
--

INSERT INTO `kategori_poin` (`id_kategori`, `nama_kategori`, `nilai_acuan`, `keterangan`, `admin_id_karyawan`) VALUES
(1, 'Evaluasi / Pelanggaran (Poin 0)', 0, 'Karyawan terlambat >15 menit tanpa force majeure, atau melanggar SOP ringan yang butuh peneguran.', 1),
(2, 'Kontribusi Dasar (Poin 25)', 25, 'Karyawan presensi tepat waktu 100% penuh selama 1 minggu shift berjalan dan menyelesaikan target produksi sesuai standar minimal.', 1),
(3, 'Kolaborasi & Handover (Poin 50)', 50, 'Karyawan proaktif membantu lini lain yang mengalami bottleneck & mencatat laporan serah terima (handover) mesin dengan sangat detail.', 2),
(4, 'Problem Solving (Poin 75)', 75, 'Karyawan berhasil mendeteksi anomali/kerusakan minor mesin sebelum terjadi downtime panjang dan melakukan perbaikan darurat mandiri.', 2),
(5, 'Inovasi & Efisiensi (Poin 100)', 100, 'Karyawan memberikan gagasan modifikasi alat bantu atau metode kerja baru yang terbukti menurunkan jumlah barang reject (waste) di atas 5%.', 2);

-- --------------------------------------------------------

--
-- Table structure for table `log_aktivitas`
--

CREATE TABLE `log_aktivitas` (
  `id_log` int NOT NULL,
  `tanggal_log` date NOT NULL,
  `ringkasan_kegiatan` varchar(500) NOT NULL,
  `catatan_serah_terima` varchar(500) NOT NULL,
  `dokumentasi_kegiatan` varchar(255) NOT NULL,
  `status_verifikasi` enum('Menunggu','Revisi','Terverifikasi') NOT NULL DEFAULT 'Menunggu',
  `karyawan_id_karyawan` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `log_aktivitas`
--

INSERT INTO `log_aktivitas` (`id_log`, `tanggal_log`, `ringkasan_kegiatan`, `catatan_serah_terima`, `dokumentasi_kegiatan`, `status_verifikasi`, `karyawan_id_karyawan`) VALUES
(1, '2026-10-01', 'Memanggang 500 batch biskuit kelapa lini A', 'Suhu oven zona 2 perlu dipantau shift sore', 'uploads/log_oven_01.jpg', 'Terverifikasi', 9),
(2, '2026-10-01', 'Mencampur 300 kg adonan biskuit krim cokelat', 'Stok tepung terigu silo B tersisa 40%', 'uploads/log_adonan_02.jpg', 'Terverifikasi', 10),
(3, '2026-10-01', 'Mencetak 450 loyang wafer cokelat berlapis', 'Cetakan nomor 4 sudah dibersihkan', 'uploads/log_wafer_03.jpg', 'Terverifikasi', 11),
(4, '2026-10-01', 'Mengemas 12.000 saset kopi instan 3-in-1', 'Roll plastik kemasan baru dipasang pada mesin 2', 'uploads/log_kopi_04.jpg', 'Terverifikasi', 12),
(5, '2026-10-01', 'Menyusun 400 karton biskuit ke atas palet kayu', 'Palet jalur C siap diangkut forklift pagi', 'uploads/log_karton_05.jpg', 'Terverifikasi', 13),
(6, '2026-10-02', 'Mencetak kode produksi & expired date 8.000 pak', 'Tinta printer ink-jet tinggal 25%', 'uploads/log_label_06.jpg', 'Terverifikasi', 14),
(7, '2026-10-02', 'Memindahkan 60 palet barang jadi ke gudang blok D', 'Baterai forklift unit 2 sedang di-charge', 'uploads/log_forklift_07.jpg', 'Terverifikasi', 15),
(8, '2026-10-02', 'Menerima dan mengecek 10 ton gula rafinasi', '2 karung lembap dipisahkan ke area karantina', 'uploads/log_bahan_08.jpg', 'Terverifikasi', 16),
(9, '2026-10-02', 'Memuat 5 truk kontainer distribusi wilayah Kaltim', 'Surat jalan kontainer ke-5 menunggu tanda tangan', 'uploads/log_distribusi_09.jpg', 'Terverifikasi', 17),
(10, '2026-10-02', 'Menyangrai 800 kg biji kopi robusta gelombang malam', 'Katup pembuangan asap roaster sedikit berbunyi', 'uploads/log_roaster_10.jpg', 'Terverifikasi', 18),
(11, '2026-10-03', 'Melakukan uji kebocoran vakum kemasan biskuit', 'Ditemukan 12 kemasan bocor pada awal shift', 'uploads/log_sealing_11.jpg', 'Terverifikasi', 19),
(12, '2026-10-03', 'Melakukan stok opname bahan kemas kardus & foil', 'Selisih 3 roll aluminium foil masih ditelusuri', 'uploads/log_opname_12.jpg', 'Menunggu', 20),
(13, '2026-10-03', 'Sanitasi menyeluruh pipa krim dan conveyor lini B', 'Pipa sudah kering dan siap dipakai shift malam', 'uploads/log_sanitasi_13.jpg', 'Terverifikasi', 21),
(14, '2026-10-03', 'Menyortir biskuit patah sebelum masuk mesin kemas', 'Total 15 kg biskuit patah dicatat di form reject', 'uploads/log_sortir_14.jpg', 'Revisi', 22),
(15, '2026-10-04', 'Kalibrasi suhu pemanggangan oven biskuit shift sore', 'Terverifikasi oleh Supervisor', 'uploads/log_oven_15.jpg', 'Terverifikasi', 9);

-- --------------------------------------------------------

--
-- Table structure for table `shift`
--

CREATE TABLE `shift` (
  `id_shift` int NOT NULL,
  `nama_shift` varchar(30) NOT NULL,
  `jam_masuk` time NOT NULL,
  `jam_keluar` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `shift`
--

INSERT INTO `shift` (`id_shift`, `nama_shift`, `jam_masuk`, `jam_keluar`) VALUES
(1, 'Shift Pagi', '07:00:00', '15:00:00'),
(2, 'Shift Sore', '15:00:00', '23:00:00'),
(3, 'Shift Malam', '23:00:00', '07:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `id_karyawan` int NOT NULL,
  `divisi` varchar(50) NOT NULL,
  `bagian_kerja` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`id_karyawan`, `divisi`, `bagian_kerja`) VALUES
(5, 'Produksi', 'Operator Lini Utama'),
(9, 'Produksi', 'Operator Oven Biskuit'),
(10, 'Produksi', 'Pencampuran Adonan'),
(11, 'Produksi', 'Pencetakan Wafer'),
(12, 'Pengemasan', 'Mesin Saset Kopi'),
(13, 'Pengemasan', 'Pengepakan Karton'),
(14, 'Pengemasan', 'Labeling & Coding'),
(15, 'Gudang', 'Forklift Operator'),
(16, 'Gudang', 'Penerimaan Bahan Baku'),
(17, 'Gudang', 'Distribusi Barang Jadi'),
(18, 'Produksi', 'Operator Roaster Kopi'),
(19, 'Pengemasan', 'Sealing Kemasan Biskuit'),
(20, 'Produksi', 'Stok Opname Harian'),
(21, 'Produksi', 'Sanitasi Mesin Produksi'),
(22, 'Pengemasan', 'Sortir Produk Reject'),
(23, 'Gudang', 'Logistik Palet');

-- --------------------------------------------------------

--
-- Table structure for table `supervisor`
--

CREATE TABLE `supervisor` (
  `id_karyawan` int NOT NULL,
  `area_supervisi` varchar(50) NOT NULL,
  `shift_supervisi` enum('-','Pagi','Sore','Malam') NOT NULL DEFAULT '-'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `supervisor`
--

INSERT INTO `supervisor` (`id_karyawan`, `area_supervisi`, `shift_supervisi`) VALUES
(4, 'Lini Biskuit', 'Pagi'),
(6, 'Lini Wafer Cokelat', 'Malam'),
(7, 'Gudang Bahan Baku', 'Pagi'),
(8, 'Quality Control', 'Sore');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `absensi`
--
ALTER TABLE `absensi`
  ADD PRIMARY KEY (`id_absensi`),
  ADD KEY `fk_absensi_jadwal` (`jadwal_shift_id_jadwal`);

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_karyawan`);

--
-- Indexes for table `crunch_points`
--
ALTER TABLE `crunch_points`
  ADD PRIMARY KEY (`id_poin`),
  ADD KEY `fk_poin_staff` (`staff_id_karyawan`),
  ADD KEY `fk_poin_supervisor` (`supervisor_id_karyawan`),
  ADD KEY `fk_poin_kategori` (`kategori_poin_id_kategori`);

--
-- Indexes for table `jadwal_shift`
--
ALTER TABLE `jadwal_shift`
  ADD PRIMARY KEY (`id_jadwal`),
  ADD KEY `fk_jadwal_karyawan` (`karyawan_id_karyawan`),
  ADD KEY `fk_jadwal_shift` (`shift_id_shift`);

--
-- Indexes for table `karyawan`
--
ALTER TABLE `karyawan`
  ADD PRIMARY KEY (`id_karyawan`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `kategori_poin`
--
ALTER TABLE `kategori_poin`
  ADD PRIMARY KEY (`id_kategori`),
  ADD KEY `fk_kategori_admin` (`admin_id_karyawan`);

--
-- Indexes for table `log_aktivitas`
--
ALTER TABLE `log_aktivitas`
  ADD PRIMARY KEY (`id_log`),
  ADD UNIQUE KEY `uq_dokumentasi_kegiatan` (`dokumentasi_kegiatan`),
  ADD KEY `fk_log_karyawan` (`karyawan_id_karyawan`);

--
-- Indexes for table `shift`
--
ALTER TABLE `shift`
  ADD PRIMARY KEY (`id_shift`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`id_karyawan`);

--
-- Indexes for table `supervisor`
--
ALTER TABLE `supervisor`
  ADD PRIMARY KEY (`id_karyawan`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `absensi`
--
ALTER TABLE `absensi`
  MODIFY `id_absensi` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `crunch_points`
--
ALTER TABLE `crunch_points`
  MODIFY `id_poin` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `jadwal_shift`
--
ALTER TABLE `jadwal_shift`
  MODIFY `id_jadwal` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `karyawan`
--
ALTER TABLE `karyawan`
  MODIFY `id_karyawan` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `kategori_poin`
--
ALTER TABLE `kategori_poin`
  MODIFY `id_kategori` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `log_aktivitas`
--
ALTER TABLE `log_aktivitas`
  MODIFY `id_log` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `shift`
--
ALTER TABLE `shift`
  MODIFY `id_shift` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `absensi`
--
ALTER TABLE `absensi`
  ADD CONSTRAINT `fk_absensi_jadwal` FOREIGN KEY (`jadwal_shift_id_jadwal`) REFERENCES `jadwal_shift` (`id_jadwal`);

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `fk_admin_karyawan` FOREIGN KEY (`id_karyawan`) REFERENCES `karyawan` (`id_karyawan`);

--
-- Constraints for table `crunch_points`
--
ALTER TABLE `crunch_points`
  ADD CONSTRAINT `fk_poin_kategori` FOREIGN KEY (`kategori_poin_id_kategori`) REFERENCES `kategori_poin` (`id_kategori`),
  ADD CONSTRAINT `fk_poin_staff` FOREIGN KEY (`staff_id_karyawan`) REFERENCES `staff` (`id_karyawan`),
  ADD CONSTRAINT `fk_poin_supervisor` FOREIGN KEY (`supervisor_id_karyawan`) REFERENCES `supervisor` (`id_karyawan`);

--
-- Constraints for table `jadwal_shift`
--
ALTER TABLE `jadwal_shift`
  ADD CONSTRAINT `fk_jadwal_karyawan` FOREIGN KEY (`karyawan_id_karyawan`) REFERENCES `karyawan` (`id_karyawan`),
  ADD CONSTRAINT `fk_jadwal_shift` FOREIGN KEY (`shift_id_shift`) REFERENCES `shift` (`id_shift`);

--
-- Constraints for table `kategori_poin`
--
ALTER TABLE `kategori_poin`
  ADD CONSTRAINT `fk_kategori_admin` FOREIGN KEY (`admin_id_karyawan`) REFERENCES `admin` (`id_karyawan`);

--
-- Constraints for table `log_aktivitas`
--
ALTER TABLE `log_aktivitas`
  ADD CONSTRAINT `fk_log_karyawan` FOREIGN KEY (`karyawan_id_karyawan`) REFERENCES `karyawan` (`id_karyawan`);

--
-- Constraints for table `staff`
--
ALTER TABLE `staff`
  ADD CONSTRAINT `fk_staff_karyawan` FOREIGN KEY (`id_karyawan`) REFERENCES `karyawan` (`id_karyawan`);

--
-- Constraints for table `supervisor`
--
ALTER TABLE `supervisor`
  ADD CONSTRAINT `fk_supervisor_karyawan` FOREIGN KEY (`id_karyawan`) REFERENCES `karyawan` (`id_karyawan`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
