package et.gov.ethgov.directory.domain.unit;

import java.util.Locale;
import java.util.Objects;

public final class UnitName {

    private final String value;

    private UnitName(String value) {
        this.value = value;
    }

    public static UnitName of(String raw) {
        Objects.requireNonNull(raw, "unitName");
        String normalized = raw.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 2 || normalized.length() > 200) {
            throw new IllegalArgumentException("unitName length must be between 2 and 200");
        }
        return new UnitName(normalized);
    }

    public String value() {
        return value;
    }

    public String searchKey() {
        return value.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UnitName that)) {
            return false;
        }
        return value.equalsIgnoreCase(that.value);
    }

    @Override
    public int hashCode() {
        return value.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
