package et.gov.ethgov.organization.domain.membership;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Membership. */
public final class MembershipId {
    private final UUID value;

    private MembershipId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static MembershipId newId() {
        return new MembershipId(UUID.randomUUID());
    }

    public static MembershipId of(UUID value) {
        return new MembershipId(value);
    }

    public static MembershipId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new MembershipId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MembershipId other)) {
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
