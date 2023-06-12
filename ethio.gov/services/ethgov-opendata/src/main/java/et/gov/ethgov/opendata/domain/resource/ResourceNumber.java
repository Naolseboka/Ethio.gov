package et.gov.ethgov.opendata.domain.resource;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ResourceNumber {
    private static final Pattern PATTERN = Pattern.compile("^ODR-[0-9]{4}-[0-9]{6}$");
    private final String value;

    private ResourceNumber(String value) {
        this.value = value;
    }

    public static ResourceNumber of(String raw) {
        Objects.requireNonNull(raw, "resourceNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("resourceNumber must match ODR-YYYY-######");
        }
        return new ResourceNumber(n);
    }

    public static ResourceNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("invalid year");
        }
        if (sequence < 1 || sequence > 999_999) {
            throw new IllegalArgumentException("sequence out of range");
        }
        return new ResourceNumber("ODR-%d-%06d".formatted(year, sequence));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ResourceNumber that && value.equals(that.value));
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
