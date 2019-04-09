package et.gov.ethgov.case.domain.party;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Party. */
public final class PartyId {
    private final UUID value;

    private PartyId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PartyId newId() {
        return new PartyId(UUID.randomUUID());
    }

    public static PartyId of(UUID value) {
        return new PartyId(value);
    }

    public static PartyId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PartyId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartyId other)) {
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
