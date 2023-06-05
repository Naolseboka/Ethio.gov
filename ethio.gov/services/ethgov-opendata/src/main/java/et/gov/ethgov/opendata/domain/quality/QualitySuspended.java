package et.gov.ethgov.opendata.domain.quality;

import java.time.Instant;
import java.util.Objects;

/** Domain event: QualitySuspended. */
public final class QualitySuspended {
    private final QualityId id;
    private final Instant occurredAt;

    public QualitySuspended(QualityId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public QualityId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
