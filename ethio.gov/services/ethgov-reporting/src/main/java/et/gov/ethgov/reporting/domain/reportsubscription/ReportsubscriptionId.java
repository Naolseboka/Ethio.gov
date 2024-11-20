package et.gov.ethgov.reporting.domain.reportsubscription;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Reportsubscription. */
public final class ReportsubscriptionId {
    private final UUID value;

    private ReportsubscriptionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ReportsubscriptionId newId() {
        return new ReportsubscriptionId(UUID.randomUUID());
    }

    public static ReportsubscriptionId of(UUID value) {
        return new ReportsubscriptionId(value);
    }

    public static ReportsubscriptionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ReportsubscriptionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportsubscriptionId other)) {
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
