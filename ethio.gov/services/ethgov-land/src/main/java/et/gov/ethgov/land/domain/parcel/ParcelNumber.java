package et.gov.ethgov.land.domain.parcel;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Tenant-local parcel number, e.g. LND-2024-000001. */
public final class ParcelNumber {
    private static final Pattern PATTERN = Pattern.compile("^LND-[0-9]{4}-[0-9]{6}$");
    private final String value;
    private ParcelNumber(String value) { this.value = value; }
    public static ParcelNumber of(String raw) {
        Objects.requireNonNull(raw, "parcelNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("parcelNumber must match LND-YYYY-######");
        }
        return new ParcelNumber(n);
    }
    public static ParcelNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) throw new IllegalArgumentException("invalid year");
        if (sequence < 1 || sequence > 999_999) throw new IllegalArgumentException("sequence out of range");
        return new ParcelNumber("LND-%d-%06d".formatted(year, sequence));
    }
    public String value() { return value; }
    @Override public boolean equals(Object o) {
        return this == o || (o instanceof ParcelNumber that && value.equals(that.value));
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
