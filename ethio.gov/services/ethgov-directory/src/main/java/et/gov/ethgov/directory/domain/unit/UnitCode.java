package et.gov.ethgov.directory.domain.unit;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Tenant-local short code for an organizational unit (e.g. FIN-BUD). */
public final class UnitCode {

    private static final Pattern PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9._-]{1,31}$");

    private final String value;

    private UnitCode(String value) {
        this.value = value;
    }

    public static UnitCode of(String raw) {
        Objects.requireNonNull(raw, "unitCode");
        String normalized = raw.trim().toUpperCase(Locale.ROOT).replace(' ', '-');
        if (!PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "unitCode must be 2–32 chars of A-Z, 0-9, '.', '_', or '-'");
        }
        return new UnitCode(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UnitCode that)) {
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
