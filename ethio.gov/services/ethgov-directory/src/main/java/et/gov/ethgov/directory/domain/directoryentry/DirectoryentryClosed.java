package et.gov.ethgov.directory.domain.directoryentry;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DirectoryentryClosed. */
public final class DirectoryentryClosed {
    private final DirectoryentryId id;
    private final Instant occurredAt;

    public DirectoryentryClosed(DirectoryentryId id, Instant occurredAt) {
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
