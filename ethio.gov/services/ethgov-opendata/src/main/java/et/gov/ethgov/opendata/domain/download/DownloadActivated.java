package et.gov.ethgov.opendata.domain.download;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DownloadActivated. */
public final class DownloadActivated {
    private final DownloadId id;
    private final Instant occurredAt;

    public DownloadActivated(DownloadId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DownloadId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
