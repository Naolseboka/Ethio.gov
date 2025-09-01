package et.gov.ethgov.search.domain.suggestion;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Suggestion. */
public final class SuggestionId {
    private final UUID value;

    private SuggestionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SuggestionId newId() {
        return new SuggestionId(UUID.randomUUID());
    }

    public static SuggestionId of(UUID value) {
        return new SuggestionId(value);
    }

    public static SuggestionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SuggestionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SuggestionId other)) {
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
