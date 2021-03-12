package et.gov.ethgov.gateway.domain.ratelimit;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class RateLimitPolicyNumber {
    private static final Pattern PATTERN = Pattern.compile("^RLP-[0-9]{4}-[0-9]{6}$");
    private final String value;

    private RateLimitPolicyNumber(String value) {
        this.value = value;
    }

    public static RateLimitPolicyNumber of(String raw) {
        Objects.requireNonNull(raw, "policyNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("policyNumber must match RLP-YYYY-######");
        }
        return new RateLimitPolicyNumber(n);
    }

    public static RateLimitPolicyNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("invalid year");
        }
        if (sequence < 1 || sequence > 999_999) {
            throw new IllegalArgumentException("sequence out of range");
        }
        return new RateLimitPolicyNumber("RLP-%d-%06d".formatted(year, sequence));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof RateLimitPolicyNumber that && value.equals(that.value));
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
