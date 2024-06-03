package et.gov.ethgov.permits.domain.extension;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ExtensionCreated. */
public final class ExtensionCreated {
    private final ExtensionId id;
    private final Instant occurredAt;

    public ExtensionCreated(ExtensionId id, Instant occurredAt) {
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
