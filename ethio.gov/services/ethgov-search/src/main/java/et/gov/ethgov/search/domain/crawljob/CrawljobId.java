package et.gov.ethgov.search.domain.crawljob;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Crawljob. */
public final class CrawljobId {
    private final UUID value;

    private CrawljobId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CrawljobId newId() {
        return new CrawljobId(UUID.randomUUID());
    }

    public static CrawljobId of(UUID value) {
        return new CrawljobId(value);
    }

    public static CrawljobId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CrawljobId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrawljobId other)) {
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
