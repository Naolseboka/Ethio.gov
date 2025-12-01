package et.gov.ethgov.workflow.domain.definition;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Definition. */
public final class DefinitionId {
    private final UUID value;

    private DefinitionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DefinitionId newId() {
        return new DefinitionId(UUID.randomUUID());
    }

    public static DefinitionId of(UUID value) {
        return new DefinitionId(value);
    }

    public static DefinitionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DefinitionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefinitionId other)) {
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
