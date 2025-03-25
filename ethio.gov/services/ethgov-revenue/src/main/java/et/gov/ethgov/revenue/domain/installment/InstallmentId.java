package et.gov.ethgov.revenue.domain.installment;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Installment. */
public final class InstallmentId {
    private final UUID value;

    private InstallmentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static InstallmentId newId() {
        return new InstallmentId(UUID.randomUUID());
    }

    public static InstallmentId of(UUID value) {
        return new InstallmentId(value);
    }

    public static InstallmentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new InstallmentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstallmentId other)) {
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
