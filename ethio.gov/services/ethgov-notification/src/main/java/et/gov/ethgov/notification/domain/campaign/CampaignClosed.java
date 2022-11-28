package et.gov.ethgov.notification.domain.campaign;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CampaignClosed. */
public final class CampaignClosed {
    private final CampaignId id;
    private final Instant occurredAt;

    public CampaignClosed(CampaignId id, Instant occurredAt) {
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
