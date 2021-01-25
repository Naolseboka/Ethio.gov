package et.gov.ethgov.gateway.domain.auditlog;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Auditlog. */
public final class AuditlogId {
    private final UUID value;

    private AuditlogId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AuditlogId newId() {
        return new AuditlogId(UUID.randomUUID());
    }

    public static AuditlogId of(UUID value) {
        return new AuditlogId(value);
    }

    public static AuditlogId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AuditlogId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditlogId other)) {
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
