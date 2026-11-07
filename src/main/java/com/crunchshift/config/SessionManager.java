package com.crunchshift.config;

import com.crunchshift.model.Karyawan;

public class SessionManager {

    private static Karyawan currentUser;

    private SessionManager() {
    }

    public static void setCurrentUser(Karyawan user) {
        currentUser = user;
    }

    public static Karyawan getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
