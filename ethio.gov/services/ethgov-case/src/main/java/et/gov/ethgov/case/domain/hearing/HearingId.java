package et.gov.ethgov.case.domain.hearing;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Hearing. */
public final class HearingId {
    private final UUID value;

    private HearingId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static HearingId newId() {
        return new HearingId(UUID.randomUUID());
    }

    public static HearingId of(UUID value) {
        return new HearingId(value);
    }

    public static HearingId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new HearingId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HearingId other)) {
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
