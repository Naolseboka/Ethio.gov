package et.gov.ethgov.directory.domain.directoryentry;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DirectoryentryActivated. */
public final class DirectoryentryActivated {
    private final DirectoryentryId id;
    private final Instant occurredAt;

    public DirectoryentryActivated(DirectoryentryId id, Instant occurredAt) {
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
