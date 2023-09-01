package et.gov.ethgov.organization.domain.hierarchy;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Hierarchy. */
public final class HierarchyId {
    private final UUID value;

    private HierarchyId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static HierarchyId newId() {
        return new HierarchyId(UUID.randomUUID());
    }

    public static HierarchyId of(UUID value) {
        return new HierarchyId(value);
    }

    public static HierarchyId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new HierarchyId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HierarchyId other)) {
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
