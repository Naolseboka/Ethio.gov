package et.gov.ethgov.directory.domain.unit;

import java.util.Objects;
import java.util.UUID;

public final class OrganizationalUnitId {

    private final String value;

    private OrganizationalUnitId(String value) {
        this.value = value;
    }

    public static OrganizationalUnitId generate() {
        return new OrganizationalUnitId("ou_" + UUID.randomUUID());
    }

    public static OrganizationalUnitId of(String raw) {
        Objects.requireNonNull(raw, "organizationalUnitId");
        String normalized = raw.trim();
        if (!normalized.startsWith("ou_") || normalized.length() < 39) {
            throw new IllegalArgumentException("Invalid OrganizationalUnitId: " + raw);
        }
        return new OrganizationalUnitId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrganizationalUnitId that)) {
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
