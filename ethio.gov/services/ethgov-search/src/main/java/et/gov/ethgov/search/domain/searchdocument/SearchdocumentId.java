package et.gov.ethgov.search.domain.searchdocument;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Searchdocument. */
public final class SearchdocumentId {
    private final UUID value;

    private SearchdocumentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SearchdocumentId newId() {
        return new SearchdocumentId(UUID.randomUUID());
    }

    public static SearchdocumentId of(UUID value) {
        return new SearchdocumentId(value);
    }

    public static SearchdocumentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SearchdocumentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchdocumentId other)) {
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
