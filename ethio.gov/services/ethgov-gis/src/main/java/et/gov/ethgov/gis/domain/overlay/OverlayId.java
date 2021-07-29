package et.gov.ethgov.gis.domain.overlay;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Overlay. */
public final class OverlayId {
    private final UUID value;

    private OverlayId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static OverlayId newId() {
        return new OverlayId(UUID.randomUUID());
    }

    public static OverlayId of(UUID value) {
        return new OverlayId(value);
    }

    public static OverlayId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new OverlayId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OverlayId other)) {
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
