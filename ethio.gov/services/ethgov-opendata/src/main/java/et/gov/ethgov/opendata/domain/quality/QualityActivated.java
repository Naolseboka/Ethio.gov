package et.gov.ethgov.opendata.domain.quality;

import java.time.Instant;
import java.util.Objects;

/** Domain event: QualityActivated. */
public final class QualityActivated {
    private final QualityId id;
    private final Instant occurredAt;

    public QualityActivated(QualityId id, Instant occurredAt) {
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
