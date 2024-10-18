package et.gov.ethgov.reporting.domain.definition;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Tenant-local report definition number, e.g. RPT-2024-000001. */
public final class ReportNumber {
    private static final Pattern PATTERN = Pattern.compile("^RPT-[0-9]{4}-[0-9]{6}$");
    private final String value;

    private ReportNumber(String value) {
        this.value = value;
    }

    public static ReportNumber of(String raw) {
        Objects.requireNonNull(raw, "reportNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("reportNumber must match RPT-YYYY-######");
        }
        return new ReportNumber(n);
    }

    public static ReportNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("invalid year");
        }
        if (sequence < 1 || sequence > 999_999) {
            throw new IllegalArgumentException("sequence out of range");
        }
        return new ReportNumber("RPT-%d-%06d".formatted(year, sequence));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ReportNumber that && value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
