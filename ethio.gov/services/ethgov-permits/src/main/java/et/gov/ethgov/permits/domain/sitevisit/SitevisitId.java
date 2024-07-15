package et.gov.ethgov.permits.domain.sitevisit;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Sitevisit. */
public final class SitevisitId {
    private final UUID value;

    private SitevisitId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SitevisitId newId() {
        return new SitevisitId(UUID.randomUUID());
    }

    public static SitevisitId of(UUID value) {
        return new SitevisitId(value);
    }

    public static SitevisitId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SitevisitId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SitevisitId other)) {
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
