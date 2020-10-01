package et.gov.ethgov.document.domain.ocrjob;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OcrjobClosed. */
public final class OcrjobClosed {
    private final OcrjobId id;
    private final Instant occurredAt;

    public OcrjobClosed(OcrjobId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public OcrjobId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
