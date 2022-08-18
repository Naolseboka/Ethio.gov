package et.gov.ethgov.licensing.domain.inspection;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Inspection. */
public final class InspectionId {
    private final UUID value;

    private InspectionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static InspectionId newId() {
        return new InspectionId(UUID.randomUUID());
    }

    public static InspectionId of(UUID value) {
        return new InspectionId(value);
    }

    public static InspectionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new InspectionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InspectionId other)) {
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
