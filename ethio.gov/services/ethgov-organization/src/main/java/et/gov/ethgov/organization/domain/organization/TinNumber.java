package et.gov.ethgov.organization.domain.organization;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Ethiopian Taxpayer Identification Number (TIN).
 * Accepts 10-digit TINs used by ERCA/MoR systems.
 */
public final class TinNumber {

    private static final Pattern PATTERN = Pattern.compile("^[0-9]{10}$");

    private final String value;

    private TinNumber(String value) {
        this.value = value;
    }

    public static TinNumber of(String raw) {
        Objects.requireNonNull(raw, "tin");
        String normalized = raw.trim().replace(" ", "");
        if (!PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("TIN must be exactly 10 digits");
        }
        return new TinNumber(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TinNumber that)) {
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
