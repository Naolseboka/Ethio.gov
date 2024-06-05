package et.gov.ethgov.permits.domain.extension;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ExtensionUpdated. */
public final class ExtensionUpdated {
    private final ExtensionId id;
    private final Instant occurredAt;

    public ExtensionUpdated(ExtensionId id, Instant occurredAt) {
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
