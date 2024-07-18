package et.gov.ethgov.permits.domain.temporary;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Temporary aggregate — bounded context root for temporary in the permits service.
 */
public final class Temporary {
    private final TemporaryId id;
    private String referenceCode;
    private String title;
    private String description;
    private String tenantId;
    private TemporaryStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private Temporary(
            TemporaryId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            TemporaryStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.referenceCode = referenceCode;
        this.title = title;
        this.description = description;
        this.tenantId = tenantId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static Temporary create(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            Instant now) {
        Objects.requireNonNull(referenceCode, "referenceCode");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(now, "now");
        if (referenceCode.isBlank()) {
            throw new IllegalArgumentException("referenceCode must not be blank");
        }
        if (title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        Temporary aggregate = new Temporary(
                TemporaryId.newId(),
                referenceCode.trim(),
                title.trim(),
                Optional.ofNullable(description).orElse("").trim(),
                tenantId.trim(),
                TemporaryStatus.DRAFT,
                now,
                now,
                0L);
        aggregate.domainEvents.add(new TemporaryCreated(aggregate.id, now));
        return aggregate;
    }

    public static Temporary rehydrate(
            TemporaryId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            TemporaryStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Temporary(
                id, referenceCode, title, description, tenantId, status, createdAt, updatedAt, version);
    }

    public void activate(Instant now) {
        transitionTo(TemporaryStatus.ACTIVE, now);
        domainEvents.add(new TemporaryActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (reason.isBlank()) {
            throw new IllegalArgumentException("suspend reason required");
        }
        transitionTo(TemporaryStatus.SUSPENDED, now);
        domainEvents.add(new TemporarySuspended(id, now));
        this.description = appendNote(this.description, "Suspended: " + reason.trim());
    }

    public void close(Instant now) {
        transitionTo(TemporaryStatus.CLOSED, now);
        domainEvents.add(new TemporaryClosed(id, now));
    }

    public void updateDetails(String title, String description, Instant now) {
        Objects.requireNonNull(now, "now");
        if (status == TemporaryStatus.ARCHIVED || status == TemporaryStatus.CLOSED) {
            throw new IllegalStateException("cannot update " + status + " temporary");
        }
        if (title != null && !title.isBlank()) {
            this.title = title.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        this.updatedAt = now;
        this.version += 1;
        domainEvents.add(new TemporaryUpdated(id, now));
    }

    private void transitionTo(TemporaryStatus target, Instant now) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(now, "now");
        if (!status.canTransitionTo(target)) {
            throw new IllegalStateException("illegal transition " + status + " -> " + target);
        }
        this.status = target;
        this.updatedAt = now;
        this.version += 1;
    }

    private static String appendNote(String current, String note) {
        if (current == null || current.isBlank()) {
            return note;
        }
        return current + " | " + note;
    }

    public List<Object> pullDomainEvents() {
        List<Object> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public TemporaryId id() { return id; }
    public String referenceCode() { return referenceCode; }
    public String title() { return title; }
    public String description() { return description; }
    public String tenantId() { return tenantId; }
    public TemporaryStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
