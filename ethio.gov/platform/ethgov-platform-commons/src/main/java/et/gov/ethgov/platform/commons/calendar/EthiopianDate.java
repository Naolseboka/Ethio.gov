package et.gov.ethgov.platform.commons.calendar;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Ethiopian (Ge'ez) calendar date.
 *
 * <p>Algorithm adapted from the widely used civil conversion used by Ethiopian institutions:
 * Ethiopian year 1 starts on 11 September 8 CE (Gregorian) in the proleptic calendar used here.
 */
public final class EthiopianDate {

    private final int year;
    private final int month;
    private final int day;

    private EthiopianDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public static EthiopianDate of(int year, int month, int day) {
        validate(year, month, day);
        return new EthiopianDate(year, month, day);
    }

    public static EthiopianDate fromGregorian(LocalDate gregorian) {
        Objects.requireNonNull(gregorian, "gregorian");
        int jdn = toJulianDayNumber(gregorian.getYear(), gregorian.getMonthValue(), gregorian.getDayOfMonth());
        return fromJulianDayNumber(jdn);
    }

    public LocalDate toGregorian() {
        int jdn = toJulianDayNumberEthiopian(year, month, day);
        return fromJulianDayNumberGregorian(jdn);
    }

    public int year() {
        return year;
    }

    public int month() {
        return month;
    }

    public int day() {
        return day;
    }

    public boolean isLeapYear() {
        return isEthiopianLeapYear(year);
    }

    public static boolean isEthiopianLeapYear(int year) {
        return Math.floorMod(year, 4) == 3;
    }

    private static void validate(int year, int month, int day) {
        if (year < 1) {
            throw new DateTimeException("Ethiopian year must be >= 1");
        }
        if (month < 1 || month > 13) {
            throw new DateTimeException("Ethiopian month must be between 1 and 13");
        }
        int maxDay = month == 13 ? (isEthiopianLeapYear(year) ? 6 : 5) : 30;
        if (day < 1 || day > maxDay) {
            throw new DateTimeException(
                    "Invalid Ethiopian date " + year + "-" + month + "-" + day + " (max day " + maxDay + ")");
        }
    }

    private static EthiopianDate fromJulianDayNumber(int jdn) {
        int r = (jdn - 1723856) % 1461;
        int n = r % 365 + 365 * (r / 1460);
        int year = 4 * ((jdn - 1723856) / 1461) + r / 365 - r / 1460;
        int month = n / 30 + 1;
        int day = n % 30 + 1;
        return of(year, month, day);
    }

    private static int toJulianDayNumberEthiopian(int year, int month, int day) {
        // Amete Mihret epoch offset 1723856 (Geez.org / civil conversion)
        return (1723856 + 365) + 365 * (year - 1) + year / 4 + 30 * month + day - 31;
    }

    private static int toJulianDayNumber(int year, int month, int day) {
        int a = (14 - month) / 12;
        int y = year + 4800 - a;
        int m = month + 12 * a - 3;
        return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
    }

    private static LocalDate fromJulianDayNumberGregorian(int jdn) {
        int a = jdn + 32044;
        int b = (4 * a + 3) / 146097;
        int c = a - (146097 * b) / 4;
        int d = (4 * c + 3) / 1461;
        int e = c - (1461 * d) / 4;
        int m = (5 * e + 2) / 153;
        int day = e - (153 * m + 2) / 5 + 1;
        int month = m + 3 - 12 * (m / 10);
        int year = 100 * b + d - 4800 + m / 10;
        return LocalDate.of(year, month, day);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EthiopianDate that)) {
            return false;
        }
        return year == that.year && month == that.month && day == that.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day);
    }

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d E.C.", year, month, day);
    }
}
