package et.gov.ethgov.citizen.domain.citizen;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Optional Fayda (national digital ID) number. Format is validated when present;
 * live Fayda federation remains a later integration phase.
 */
public final class FaydaId {

    private static final Pattern PATTERN = Pattern.compile("^[0-9]{12,16}$");

    private final String value;

    private FaydaId(String value) {
        this.value = value;
    }

    public static FaydaId of(String raw) {
        Objects.requireNonNull(raw, "faydaId");
        String normalized = raw.trim().replace(" ", "");
        if (!PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Fayda ID must be 12-16 digits");
        }
        return new FaydaId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FaydaId that)) {
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
