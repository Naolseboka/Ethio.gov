package et.gov.ethgov.reporting.domain.report;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Report. */
public final class ReportId {
    private final UUID value;

    private ReportId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ReportId newId() {
        return new ReportId(UUID.randomUUID());
    }

    public static ReportId of(UUID value) {
        return new ReportId(value);
    }

    public static ReportId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ReportId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportId other)) {
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
