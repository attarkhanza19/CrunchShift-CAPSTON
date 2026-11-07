package com.crunchshift.util;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

    public static final ZoneId ZONE_WITA = ZoneId.of("Asia/Makassar");

    private static final Locale LOCALE_ID = Locale.forLanguageTag("id-ID");
    private static final DateTimeFormatter FORMAT_JAM = DateTimeFormatter.ofPattern("HH:mm:ss 'WITA'");
    private static final DateTimeFormatter FORMAT_TANGGAL_LENGKAP = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", LOCALE_ID);
    private static final DateTimeFormatter FORMAT_TANGGAL_JAM = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private DateUtil() {
    }

    public static ZonedDateTime sekarangWITA() {
        return ZonedDateTime.now(ZONE_WITA);
    }

    public static String formatJam(ZonedDateTime zdt) {
        return zdt.format(FORMAT_JAM);
    }

    public static String formatTanggalLengkap(ZonedDateTime zdt) {
        return zdt.format(FORMAT_TANGGAL_LENGKAP);
    }

    public static String formatTanggalJam(ZonedDateTime zdt) {
        return zdt.format(FORMAT_TANGGAL_JAM);
    }

    public static Timestamp toTimestamp(ZonedDateTime zdt) {
        return Timestamp.from(zdt.toInstant());
    }

    public static ZonedDateTime fromTimestamp(Timestamp ts) {
        return ts.toInstant().atZone(ZONE_WITA);
    }

    public static ZonedDateTime fromLocalDate(java.time.LocalDate date) {
        return date != null ? date.atStartOfDay(ZONE_WITA) : sekarangWITA();
    }

    public static boolean isTerlambat(ZonedDateTime waktuMasuk, LocalTime jadwalMasuk, int toleransiMenit) {
        LocalTime batasMasuk = jadwalMasuk.plusMinutes(toleransiMenit);
        LocalTime jamMasuk = waktuMasuk.toLocalTime();
        return jamMasuk.isAfter(batasMasuk);
    }
}
