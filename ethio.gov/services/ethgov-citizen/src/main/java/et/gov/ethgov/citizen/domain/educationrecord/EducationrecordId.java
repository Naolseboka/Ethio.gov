package et.gov.ethgov.citizen.domain.educationrecord;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Educationrecord. */
public final class EducationrecordId {
    private final UUID value;

    private EducationrecordId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static EducationrecordId newId() {
        return new EducationrecordId(UUID.randomUUID());
    }

    public static EducationrecordId of(UUID value) {
        return new EducationrecordId(value);
    }

    public static EducationrecordId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new EducationrecordId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EducationrecordId other)) {
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
