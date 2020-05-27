package et.gov.ethgov.directory.domain.queue;

import java.time.Instant;
import java.util.Objects;

/** Domain event: QueueActivated. */
public final class QueueActivated {
    private final QueueId id;
    private final Instant occurredAt;

    public QueueActivated(QueueId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public QueueId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
