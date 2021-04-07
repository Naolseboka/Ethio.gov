package et.gov.ethgov.gateway.domain.upstream;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Upstream. */
public final class UpstreamId {
    private final UUID value;

    private UpstreamId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static UpstreamId newId() {
        return new UpstreamId(UUID.randomUUID());
    }

    public static UpstreamId of(UUID value) {
        return new UpstreamId(value);
    }

    public static UpstreamId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new UpstreamId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UpstreamId other)) {
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
