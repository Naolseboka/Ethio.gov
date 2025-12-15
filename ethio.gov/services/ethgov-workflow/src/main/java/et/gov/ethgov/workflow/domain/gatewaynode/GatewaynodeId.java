package et.gov.ethgov.workflow.domain.gatewaynode;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Gatewaynode. */
public final class GatewaynodeId {
    private final UUID value;

    private GatewaynodeId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static GatewaynodeId newId() {
        return new GatewaynodeId(UUID.randomUUID());
    }

    public static GatewaynodeId of(UUID value) {
        return new GatewaynodeId(value);
    }

    public static GatewaynodeId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new GatewaynodeId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GatewaynodeId other)) {
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
