package et.gov.ethgov.workflow.domain.comment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CommentActivated. */
public final class CommentActivated {
    private final CommentId id;
    private final Instant occurredAt;

    public CommentActivated(CommentId id, Instant occurredAt) {
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
