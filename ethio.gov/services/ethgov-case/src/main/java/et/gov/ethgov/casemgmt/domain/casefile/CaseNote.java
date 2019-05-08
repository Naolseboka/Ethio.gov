package et.gov.ethgov.casemgmt.domain.casefile;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class CaseNote {

    private final String noteId;
    private final String authorAccountId;
    private final String body;
    private final Instant createdAt;

    private CaseNote(String noteId, String authorAccountId, String body, Instant createdAt) {
        this.noteId = noteId;
        this.authorAccountId = authorAccountId;
        this.body = body;
        this.createdAt = createdAt;
    }

    public static CaseNote of(String authorAccountId, String body, Instant now) {
        Objects.requireNonNull(authorAccountId, "authorAccountId");
        Objects.requireNonNull(body, "body");
        Objects.requireNonNull(now, "now");
        String author = authorAccountId.trim();
        if (!author.startsWith("usr_") || author.length() < 40) {
            throw new IllegalArgumentException("authorAccountId must be a UserAccountId");
        }
        String normalized = body.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 2 || normalized.length() > 2000) {
            throw new IllegalArgumentException("note body length must be between 2 and 2000");
        }
        return new CaseNote("note_" + UUID.randomUUID(), author, normalized, now);
    }

    public static CaseNote rehydrate(String noteId, String authorAccountId, String body, Instant createdAt) {
        return new CaseNote(noteId, authorAccountId, body, createdAt);
    }

    public String noteId() {
        return noteId;
    }

    public String authorAccountId() {
        return authorAccountId;
    }

    public String body() {
        return body;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
