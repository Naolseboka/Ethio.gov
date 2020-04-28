package et.gov.ethgov.directory.domain.directoryentry;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DirectoryentryUpdated. */
public final class DirectoryentryUpdated {
    private final DirectoryentryId id;
    private final Instant occurredAt;

    public DirectoryentryUpdated(DirectoryentryId id, Instant occurredAt) {
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
