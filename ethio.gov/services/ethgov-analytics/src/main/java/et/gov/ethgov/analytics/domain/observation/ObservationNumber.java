package et.gov.ethgov.analytics.domain.observation;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ObservationNumber {
    private static final Pattern PATTERN = Pattern.compile("^MOB-[0-9]{4}-[0-9]{6}$");
    private final String value;

    private ObservationNumber(String value) {
        this.value = value;
    }

    public static ObservationNumber of(String raw) {
        Objects.requireNonNull(raw, "observationNumber");
        String n = raw.trim().toUpperCase(Locale.ROOT);
        if (!PATTERN.matcher(n).matches()) {
            throw new IllegalArgumentException("observationNumber must match MOB-YYYY-######");
        }
        return new ObservationNumber(n);
    }

    public static ObservationNumber allocate(int year, long sequence) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("invalid year");
        }
        if (sequence < 1 || sequence > 999_999) {
            throw new IllegalArgumentException("sequence out of range");
        }
        return new ObservationNumber("MOB-%d-%06d".formatted(year, sequence));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ObservationNumber that && value.equals(that.value));
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
