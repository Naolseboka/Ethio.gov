package et.gov.ethgov.developer.domain.sandbox;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Sandbox. */
public final class SandboxId {
    private final UUID value;

    private SandboxId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SandboxId newId() {
        return new SandboxId(UUID.randomUUID());
    }

    public static SandboxId of(UUID value) {
        return new SandboxId(value);
    }

    public static SandboxId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SandboxId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SandboxId other)) {
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
