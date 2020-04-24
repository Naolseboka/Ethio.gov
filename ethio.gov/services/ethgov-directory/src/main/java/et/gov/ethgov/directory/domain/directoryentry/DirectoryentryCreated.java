package et.gov.ethgov.directory.domain.directoryentry;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DirectoryentryCreated. */
public final class DirectoryentryCreated {
    private final DirectoryentryId id;
    private final Instant occurredAt;

    public DirectoryentryCreated(DirectoryentryId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DirectoryentryId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
