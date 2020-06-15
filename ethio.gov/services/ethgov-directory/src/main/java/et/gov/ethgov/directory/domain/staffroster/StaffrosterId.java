package et.gov.ethgov.directory.domain.staffroster;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Staffroster. */
public final class StaffrosterId {
    private final UUID value;

    private StaffrosterId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static StaffrosterId newId() {
        return new StaffrosterId(UUID.randomUUID());
    }

    public static StaffrosterId of(UUID value) {
        return new StaffrosterId(value);
    }

    public static StaffrosterId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new StaffrosterId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StaffrosterId other)) {
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
