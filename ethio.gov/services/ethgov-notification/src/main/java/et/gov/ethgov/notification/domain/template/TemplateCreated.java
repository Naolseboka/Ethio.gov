package et.gov.ethgov.notification.domain.template;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TemplateCreated. */
public final class TemplateCreated {
    private final TemplateId id;
    private final Instant occurredAt;

    public TemplateCreated(TemplateId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TemplateId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
