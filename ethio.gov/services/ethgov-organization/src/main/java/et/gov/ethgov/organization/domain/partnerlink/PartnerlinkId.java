package et.gov.ethgov.organization.domain.partnerlink;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Partnerlink. */
public final class PartnerlinkId {
    private final UUID value;

    private PartnerlinkId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PartnerlinkId newId() {
        return new PartnerlinkId(UUID.randomUUID());
    }

    public static PartnerlinkId of(UUID value) {
        return new PartnerlinkId(value);
    }

    public static PartnerlinkId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PartnerlinkId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartnerlinkId other)) {
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
