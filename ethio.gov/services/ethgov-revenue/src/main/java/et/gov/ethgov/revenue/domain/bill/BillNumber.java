package et.gov.ethgov.revenue.domain.bill;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Tenant-local revenue bill number, e.g. REV-2024-000001. */
public final class BillNumber {
    private static final Pattern PATTERN = Pattern.compile("^REV-[0-9]{4}-[0-9]{6}$");
    private final String value;
    private BillNumber(String value) { this.value = value; }
    public static BillNumber of(String raw) {
        Objects.requireNonNull(raw, "billNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("billNumber must match REV-YYYY-######");
        }
        return new BillNumber(n);
    }
    public static BillNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) throw new IllegalArgumentException("invalid year");
        if (sequence < 1 || sequence > 999_999) throw new IllegalArgumentException("sequence out of range");
        return new BillNumber("REV-%d-%06d".formatted(year, sequence));
    }
    public String value() { return value; }
    @Override public boolean equals(Object o) {
        return this == o || (o instanceof BillNumber that && value.equals(that.value));
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
