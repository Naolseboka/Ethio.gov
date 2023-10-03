package et.gov.ethgov.organization.domain.orgunit;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Orgunit. */
public final class OrgunitId {
    private final UUID value;

    private OrgunitId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static OrgunitId newId() {
        return new OrgunitId(UUID.randomUUID());
    }

    public static OrgunitId of(UUID value) {
        return new OrgunitId(value);
    }

    public static OrgunitId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new OrgunitId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgunitId other)) {
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
