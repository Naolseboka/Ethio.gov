package et.gov.ethgov.organization.domain.organization;

import java.util.Locale;
import java.util.Objects;

public final class LegalName {

    private final String value;

    private LegalName(String value) {
        this.value = value;
    }

    public static LegalName of(String raw) {
        Objects.requireNonNull(raw, "legalName");
        String normalized = raw.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 2 || normalized.length() > 200) {
            throw new IllegalArgumentException("legalName length must be between 2 and 200");
        }
        return new LegalName(normalized);
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
        if (!(other instanceof LegalName that)) {
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
