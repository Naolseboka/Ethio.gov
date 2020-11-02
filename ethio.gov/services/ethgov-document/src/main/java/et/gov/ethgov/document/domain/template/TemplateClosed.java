package et.gov.ethgov.document.domain.template;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TemplateClosed. */
public final class TemplateClosed {
    private final TemplateId id;
    private final Instant occurredAt;

    public TemplateClosed(TemplateId id, Instant occurredAt) {
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
