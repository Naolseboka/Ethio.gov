package et.gov.ethgov.opendata.domain.download;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DownloadUpdated. */
public final class DownloadUpdated {
    private final DownloadId id;
    private final Instant occurredAt;

    public DownloadUpdated(DownloadId id, Instant occurredAt) {
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
