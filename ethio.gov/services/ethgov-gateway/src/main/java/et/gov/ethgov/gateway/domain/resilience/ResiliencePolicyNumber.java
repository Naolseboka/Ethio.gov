package et.gov.ethgov.gateway.domain.resilience;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ResiliencePolicyNumber {
    private static final Pattern PATTERN = Pattern.compile("^RSP-[0-9]{4}-[0-9]{6}$");
    private final String value;

    private ResiliencePolicyNumber(String value) { this.value = value; }

    public static ResiliencePolicyNumber of(String raw) {
        Objects.requireNonNull(raw, "policyNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("policyNumber must match RSP-YYYY-######");
        }
        return new ResiliencePolicyNumber(n);
    }

    public static ResiliencePolicyNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) throw new IllegalArgumentException("invalid year");
        if (sequence < 1 || sequence > 999_999) throw new IllegalArgumentException("sequence out of range");
        return new ResiliencePolicyNumber("RSP-%d-%06d".formatted(year, sequence));
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ResiliencePolicyNumber that && value.equals(that.value));
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
