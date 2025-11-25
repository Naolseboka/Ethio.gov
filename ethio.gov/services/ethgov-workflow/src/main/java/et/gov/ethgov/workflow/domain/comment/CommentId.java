package et.gov.ethgov.workflow.domain.comment;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Comment. */
public final class CommentId {
    private final UUID value;

    private CommentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CommentId newId() {
        return new CommentId(UUID.randomUUID());
    }

    public static CommentId of(UUID value) {
        return new CommentId(value);
    }

    public static CommentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CommentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommentId other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
