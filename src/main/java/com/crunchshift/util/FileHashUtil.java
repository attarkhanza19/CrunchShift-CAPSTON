package com.crunchshift.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileHashUtil {

    private static final String ATTENDANCE_DIR = "src/main/resources/assets/attendance/";

    private FileHashUtil() {
    }

    public static String generateSHA256(File file) throws Exception {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static boolean isDuplicateHash(String hash, Connection conn) {
        if (conn == null || hash == null) return false;
        String sql = "SELECT COUNT(*) FROM absensi WHERE SHA2(absen_masuk, 256) = ? OR SHA2(absen_keluar, 256) = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, hash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            // Fallback aman jika query gagal
        }
        return false;
    }

    public static File copyToAttendanceDir(File sourceFile, String projectBasePath) throws IOException {
        Path destinationDir = Path.of(projectBasePath, ATTENDANCE_DIR);
        Files.createDirectories(destinationDir);

        String originalName = sourceFile.getName();
        String uniqueName = System.currentTimeMillis() + "_" + originalName;
        Path destinationPath = destinationDir.resolve(uniqueName);

        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toFile();
    }
}
