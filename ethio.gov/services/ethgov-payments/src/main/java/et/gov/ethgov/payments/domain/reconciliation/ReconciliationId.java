package et.gov.ethgov.payments.domain.reconciliation;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Reconciliation. */
public final class ReconciliationId {
    private final UUID value;

    private ReconciliationId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ReconciliationId newId() {
        return new ReconciliationId(UUID.randomUUID());
    }

    public static ReconciliationId of(UUID value) {
        return new ReconciliationId(value);
    }

    public static ReconciliationId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ReconciliationId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReconciliationId other)) {
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
