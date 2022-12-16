package et.gov.ethgov.notification.domain.emailbatch;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EmailbatchClosed. */
public final class EmailbatchClosed {
    private final EmailbatchId id;
    private final Instant occurredAt;

    public EmailbatchClosed(EmailbatchId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public EmailbatchId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
