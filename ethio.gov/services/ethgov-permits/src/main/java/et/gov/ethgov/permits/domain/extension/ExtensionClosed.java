package et.gov.ethgov.permits.domain.extension;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ExtensionClosed. */
public final class ExtensionClosed {
    private final ExtensionId id;
    private final Instant occurredAt;

    public ExtensionClosed(ExtensionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ExtensionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
