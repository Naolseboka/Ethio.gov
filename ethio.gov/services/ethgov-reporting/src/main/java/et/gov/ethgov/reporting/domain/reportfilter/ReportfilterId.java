package et.gov.ethgov.reporting.domain.reportfilter;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Reportfilter. */
public final class ReportfilterId {
    private final UUID value;

    private ReportfilterId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ReportfilterId newId() {
        return new ReportfilterId(UUID.randomUUID());
    }

    public static ReportfilterId of(UUID value) {
        return new ReportfilterId(value);
    }

    public static ReportfilterId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ReportfilterId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportfilterId other)) {
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
