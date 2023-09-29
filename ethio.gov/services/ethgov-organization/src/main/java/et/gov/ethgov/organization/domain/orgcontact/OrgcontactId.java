package et.gov.ethgov.organization.domain.orgcontact;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Orgcontact. */
public final class OrgcontactId {
    private final UUID value;

    private OrgcontactId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static OrgcontactId newId() {
        return new OrgcontactId(UUID.randomUUID());
    }

    public static OrgcontactId of(UUID value) {
        return new OrgcontactId(value);
    }

    public static OrgcontactId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new OrgcontactId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgcontactId other)) {
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
