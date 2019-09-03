package et.gov.ethgov.citizen.domain.household;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Household. */
public final class HouseholdId {
    private final UUID value;

    private HouseholdId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static HouseholdId newId() {
        return new HouseholdId(UUID.randomUUID());
    }

    public static HouseholdId of(UUID value) {
        return new HouseholdId(value);
    }

    public static HouseholdId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new HouseholdId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HouseholdId other)) {
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
