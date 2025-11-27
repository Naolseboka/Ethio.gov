package et.gov.ethgov.workflow.domain.comment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CommentUpdated. */
public final class CommentUpdated {
    private final CommentId id;
    private final Instant occurredAt;

    public CommentUpdated(CommentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CommentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
