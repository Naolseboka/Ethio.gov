package et.gov.ethgov.organization.domain.organization;

import java.util.Objects;
import java.util.UUID;

public final class OrganizationId {

    private final String value;

    private OrganizationId(String value) {
        this.value = value;
    }

    public static OrganizationId generate() {
        return new OrganizationId("org_" + UUID.randomUUID());
    }

    public static OrganizationId of(String raw) {
        Objects.requireNonNull(raw, "organizationId");
        String normalized = raw.trim();
        if (!normalized.startsWith("org_") || normalized.length() < 40) {
            throw new IllegalArgumentException("Invalid OrganizationId: " + raw);
        }
        return new OrganizationId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrganizationId that)) {
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
