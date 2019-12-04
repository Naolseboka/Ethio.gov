package et.gov.ethgov.developer.domain.app;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class AppNumber {
    private static final Pattern PATTERN = Pattern.compile("^DAP-[0-9]{4}-[0-9]{6}$");
    private final String value;

    private AppNumber(String value) {
        this.value = value;
    }

    public static AppNumber of(String raw) {
        Objects.requireNonNull(raw, "appNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("appNumber must match DAP-YYYY-######");
        }
        return new AppNumber(n);
    }

    public static AppNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("invalid year");
        }
        if (sequence < 1 || sequence > 999_999) {
            throw new IllegalArgumentException("sequence out of range");
        }
        return new AppNumber("DAP-%d-%06d".formatted(year, sequence));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof AppNumber that && value.equals(that.value));
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
