package et.gov.ethgov.notification.domain.campaign;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Campaign. */
public final class CampaignId {
    private final UUID value;

    private CampaignId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CampaignId newId() {
        return new CampaignId(UUID.randomUUID());
    }

    public static CampaignId of(UUID value) {
        return new CampaignId(value);
    }

    public static CampaignId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CampaignId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CampaignId other)) {
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
