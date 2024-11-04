package et.gov.ethgov.reporting.domain.reportalert;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Reportalert. */
public final class ReportalertId {
    private final UUID value;

    private ReportalertId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ReportalertId newId() {
        return new ReportalertId(UUID.randomUUID());
    }

    public static ReportalertId of(UUID value) {
        return new ReportalertId(value);
    }

    public static ReportalertId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ReportalertId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportalertId other)) {
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
