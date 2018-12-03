package et.gov.ethgov.analytics.domain.metric;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class MetricNumber {
    private static final Pattern PATTERN = Pattern.compile("^MET-[0-9]{4}-[0-9]{6}$");
    private final String value;

    private MetricNumber(String value) {
        this.value = value;
    }

    public static MetricNumber of(String raw) {
        Objects.requireNonNull(raw, "metricNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("metricNumber must match MET-YYYY-######");
        }
        return new MetricNumber(n);
    }

    public static MetricNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("invalid year");
        }
        if (sequence < 1 || sequence > 999_999) {
            throw new IllegalArgumentException("sequence out of range");
        }
        return new MetricNumber("MET-%d-%06d".formatted(year, sequence));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof MetricNumber that && value.equals(that.value));
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
