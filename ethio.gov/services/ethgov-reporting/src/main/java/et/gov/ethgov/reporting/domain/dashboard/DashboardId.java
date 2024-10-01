package et.gov.ethgov.reporting.domain.dashboard;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Dashboard. */
public final class DashboardId {
    private final UUID value;

    private DashboardId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DashboardId newId() {
        return new DashboardId(UUID.randomUUID());
    }

    public static DashboardId of(UUID value) {
        return new DashboardId(value);
    }

    public static DashboardId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DashboardId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DashboardId other)) {
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
