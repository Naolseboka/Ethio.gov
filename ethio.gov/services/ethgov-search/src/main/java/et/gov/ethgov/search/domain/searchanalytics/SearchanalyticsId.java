package et.gov.ethgov.search.domain.searchanalytics;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Searchanalytics. */
public final class SearchanalyticsId {
    private final UUID value;

    private SearchanalyticsId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SearchanalyticsId newId() {
        return new SearchanalyticsId(UUID.randomUUID());
    }

    public static SearchanalyticsId of(UUID value) {
        return new SearchanalyticsId(value);
    }

    public static SearchanalyticsId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SearchanalyticsId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchanalyticsId other)) {
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
