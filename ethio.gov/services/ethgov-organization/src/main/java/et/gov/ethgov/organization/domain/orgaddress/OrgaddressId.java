package et.gov.ethgov.organization.domain.orgaddress;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Orgaddress. */
public final class OrgaddressId {
    private final UUID value;

    private OrgaddressId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static OrgaddressId newId() {
        return new OrgaddressId(UUID.randomUUID());
    }

    public static OrgaddressId of(UUID value) {
        return new OrgaddressId(value);
    }

    public static OrgaddressId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new OrgaddressId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgaddressId other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
