package et.gov.ethgov.permits.domain.permitinspection;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Permitinspection. */
public final class PermitinspectionId {
    private final UUID value;

    private PermitinspectionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PermitinspectionId newId() {
        return new PermitinspectionId(UUID.randomUUID());
    }

    public static PermitinspectionId of(UUID value) {
        return new PermitinspectionId(value);
    }

    public static PermitinspectionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PermitinspectionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermitinspectionId other)) {
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
