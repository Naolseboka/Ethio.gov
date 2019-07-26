package et.gov.ethgov.citizen.domain.biometricref;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Biometricref. */
public final class BiometricrefId {
    private final UUID value;

    private BiometricrefId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static BiometricrefId newId() {
        return new BiometricrefId(UUID.randomUUID());
    }

    public static BiometricrefId of(UUID value) {
        return new BiometricrefId(value);
    }

    public static BiometricrefId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new BiometricrefId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BiometricrefId other)) {
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
