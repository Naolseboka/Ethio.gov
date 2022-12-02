package et.gov.ethgov.notification.domain.campaign;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CampaignUpdated. */
public final class CampaignUpdated {
    private final CampaignId id;
    private final Instant occurredAt;

    public CampaignUpdated(CampaignId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CampaignId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
