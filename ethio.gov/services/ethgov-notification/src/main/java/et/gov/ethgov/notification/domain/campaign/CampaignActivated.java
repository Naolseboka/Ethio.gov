package et.gov.ethgov.notification.domain.campaign;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CampaignActivated. */
public final class CampaignActivated {
    private final CampaignId id;
    private final Instant occurredAt;

    public CampaignActivated(CampaignId id, Instant occurredAt) {
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
