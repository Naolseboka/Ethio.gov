package et.gov.ethgov.notification.domain.preference;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Preference. */
public final class PreferenceId {
    private final UUID value;

    private PreferenceId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PreferenceId newId() {
        return new PreferenceId(UUID.randomUUID());
    }

    public static PreferenceId of(UUID value) {
        return new PreferenceId(value);
    }

    public static PreferenceId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PreferenceId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PreferenceId other)) {
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
