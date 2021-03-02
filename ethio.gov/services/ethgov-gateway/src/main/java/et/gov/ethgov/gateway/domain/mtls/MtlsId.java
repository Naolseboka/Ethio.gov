package et.gov.ethgov.gateway.domain.mtls;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Mtls. */
public final class MtlsId {
    private final UUID value;

    private MtlsId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static MtlsId newId() {
        return new MtlsId(UUID.randomUUID());
    }

    public static MtlsId of(UUID value) {
        return new MtlsId(value);
    }

    public static MtlsId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new MtlsId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MtlsId other)) {
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
