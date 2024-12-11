package et.gov.ethgov.reporting.domain.workbook;

import java.time.Instant;
import java.util.Objects;

/** Domain event: WorkbookClosed. */
public final class WorkbookClosed {
    private final WorkbookId id;
    private final Instant occurredAt;

    public WorkbookClosed(WorkbookId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public WorkbookId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
