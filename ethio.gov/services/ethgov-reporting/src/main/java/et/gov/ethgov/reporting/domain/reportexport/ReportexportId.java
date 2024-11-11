package et.gov.ethgov.reporting.domain.reportexport;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Reportexport. */
public final class ReportexportId {
    private final UUID value;

    private ReportexportId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ReportexportId newId() {
        return new ReportexportId(UUID.randomUUID());
    }

    public static ReportexportId of(UUID value) {
        return new ReportexportId(value);
    }

    public static ReportexportId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ReportexportId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportexportId other)) {
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
