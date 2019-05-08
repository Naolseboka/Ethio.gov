package et.gov.ethgov.casemgmt.domain.casefile;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Human-readable case number unique within a tenant (e.g. CAS-2024-000123). */
public final class CaseNumber {

    private static final Pattern PATTERN = Pattern.compile("^CAS-[0-9]{4}-[0-9]{6}$");

    private final String value;

    private CaseNumber(String value) {
        this.value = value;
    }

    public static CaseNumber of(String raw) {
        Objects.requireNonNull(raw, "caseNumber");
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("caseNumber must match CAS-YYYY-######");
        }
        return new CaseNumber(normalized);
    }

    public static CaseNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("invalid year");
        }
        if (sequence < 1 || sequence > 999_999) {
            throw new IllegalArgumentException("sequence out of range");
        }
        return new CaseNumber("CAS-%d-%06d".formatted(year, sequence));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CaseNumber that)) {
            return false;
        }
        return value.equals(that.value);
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
