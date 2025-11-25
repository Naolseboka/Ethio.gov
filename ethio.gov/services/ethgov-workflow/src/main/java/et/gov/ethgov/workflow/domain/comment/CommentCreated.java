package et.gov.ethgov.workflow.domain.comment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CommentCreated. */
public final class CommentCreated {
    private final CommentId id;
    private final Instant occurredAt;

    public CommentCreated(CommentId id, Instant occurredAt) {
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
