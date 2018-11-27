package et.gov.ethgov.analytics.domain.metric;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class MetricCode {
    private static final Pattern PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]{2,63}$");
    private final String value;

    private MetricCode(String value) {
        this.value = value;
    }

    public static MetricCode of(String raw) {
        Objects.requireNonNull(raw, "code");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("code must be UPPER_SNAKE_CASE (3-64 chars)");
        }
        return new MetricCode(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof MetricCode that && value.equals(that.value));
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
