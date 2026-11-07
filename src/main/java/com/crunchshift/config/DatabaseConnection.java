package com.crunchshift.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/crunchshift_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Makassar&connectTimeout=3000";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static Connection instance;

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Gagal membuat koneksi database: " + e.getMessage());
        }
        return instance;
    }
}
