package et.gov.ethgov.gateway.domain.rewrite;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Rewrite. */
public final class RewriteId {
    private final UUID value;

    private RewriteId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RewriteId newId() {
        return new RewriteId(UUID.randomUUID());
    }

    public static RewriteId of(UUID value) {
        return new RewriteId(value);
    }

    public static RewriteId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RewriteId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RewriteId other)) {
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
