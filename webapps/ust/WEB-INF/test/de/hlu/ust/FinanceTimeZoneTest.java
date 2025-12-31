/*
 * FinanceTimeZoneTest.java – Regression tests
 *
 * ust web application, Copyright (c) 2025 Heiko Lübbe, MIT License, https://github.com/muhme/ust
 *
 */
package de.hlu.ust;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Regression test for time zone handling of audit timestamps.
 * See https://github.com/muhme/ust/issues/1
 *
 * What it does:
 * - Simulates a container running in UTC while expecting Europe/Berlin output.
 * - Verifies formatting honors the configured timezone during display.
 *
 * Special:
 * - Temporarily sets the system property UST_TIMEZONE and then cleans it up.
 * - Safely restores the JVM default TimeZone after the test to avoid side effects.
 */
public class FinanceTimeZoneTest {

    public static void main(String[] args) {
        int failures = 0;
        try {
            shouldFormatAuditTimestampInConfiguredTimeZone();
            System.out.println("FinanceTimeZoneTest: PASS");
        } catch (AssertionError error) {
            failures++;
            System.err.println("FinanceTimeZoneTest: FAIL - " + error.getMessage());
        }

        if (failures > 0) {
            throw new AssertionError("FinanceTimeZoneTest failures: " + failures);
        }
    }

    private static void shouldFormatAuditTimestampInConfiguredTimeZone() {
        TimeZone originalDefault = TimeZone.getDefault();
        try {
            // Simulate container defaulting to UTC while the business locale is Berlin.
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            System.setProperty("UST_TIMEZONE", "Europe/Berlin");

            Booking booking = new Booking();
            GregorianCalendar auditTimestampUtc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            auditTimestampUtc.clear();
            auditTimestampUtc.set(2025, Calendar.JUNE, 15, 11, 0, 0);
            booking.setDate(auditTimestampUtc);

            ZoneId configuredZone = ZoneId.of("Europe/Berlin");
            ZonedDateTime expectedLocalTime = ZonedDateTime.ofInstant(
                    auditTimestampUtc.toInstant(), configuredZone);
            String expected = expectedLocalTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            String actual = booking.getDateAsString();
            if (!expected.equals(actual)) {
                throw new AssertionError("expected audit timestamp " + expected
                        + " when running in UTC but got " + actual);
            }
        } finally {
            TimeZone.setDefault(originalDefault);
            System.clearProperty("UST_TIMEZONE");
        }
    }
}
