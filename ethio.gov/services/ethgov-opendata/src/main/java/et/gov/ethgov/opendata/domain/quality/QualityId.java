package et.gov.ethgov.opendata.domain.quality;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Quality. */
public final class QualityId {
    private final UUID value;

    private QualityId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static QualityId newId() {
        return new QualityId(UUID.randomUUID());
    }

    public static QualityId of(UUID value) {
        return new QualityId(value);
    }

    public static QualityId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new QualityId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QualityId other)) {
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
