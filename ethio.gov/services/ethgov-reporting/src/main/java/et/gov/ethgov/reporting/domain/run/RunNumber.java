package et.gov.ethgov.reporting.domain.run;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Tenant-local report run number, e.g. RRN-2024-000001. */
public final class RunNumber {
    private static final Pattern PATTERN = Pattern.compile("^RRN-[0-9]{4}-[0-9]{6}$");
    private final String value;

    private RunNumber(String value) {
        this.value = value;
    }

    public static RunNumber of(String raw) {
        Objects.requireNonNull(raw, "runNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("runNumber must match RRN-YYYY-######");
        }
        return new RunNumber(n);
    }

    public static RunNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("invalid year");
        }
        if (sequence < 1 || sequence > 999_999) {
            throw new IllegalArgumentException("sequence out of range");
        }
        return new RunNumber("RRN-%d-%06d".formatted(year, sequence));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof RunNumber that && value.equals(that.value));
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
