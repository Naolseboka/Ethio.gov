package et.gov.ethgov.notification.domain.emailbatch;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Emailbatch aggregate — bounded context root for emailbatch in the notification service.
 */
public final class Emailbatch {
    private final EmailbatchId id;
    private String referenceCode;
    private String title;
    private String description;
    private String tenantId;
    private EmailbatchStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private Emailbatch(
            EmailbatchId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            EmailbatchStatus status,
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

    public static Emailbatch create(
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
        Emailbatch aggregate = new Emailbatch(
                EmailbatchId.newId(),
                referenceCode.trim(),
                title.trim(),
                Optional.ofNullable(description).orElse("").trim(),
                tenantId.trim(),
                EmailbatchStatus.DRAFT,
                now,
                now,
                0L);
        aggregate.domainEvents.add(new EmailbatchCreated(aggregate.id, now));
        return aggregate;
    }

    public static Emailbatch rehydrate(
            EmailbatchId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            EmailbatchStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Emailbatch(
                id, referenceCode, title, description, tenantId, status, createdAt, updatedAt, version);
    }

    public void activate(Instant now) {
        transitionTo(EmailbatchStatus.ACTIVE, now);
        domainEvents.add(new EmailbatchActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (reason.isBlank()) {
            throw new IllegalArgumentException("suspend reason required");
        }
        transitionTo(EmailbatchStatus.SUSPENDED, now);
        domainEvents.add(new EmailbatchSuspended(id, now));
        this.description = appendNote(this.description, "Suspended: " + reason.trim());
    }

    public void close(Instant now) {
        transitionTo(EmailbatchStatus.CLOSED, now);
        domainEvents.add(new EmailbatchClosed(id, now));
    }

    public void updateDetails(String title, String description, Instant now) {
        Objects.requireNonNull(now, "now");
        if (status == EmailbatchStatus.ARCHIVED || status == EmailbatchStatus.CLOSED) {
            throw new IllegalStateException("cannot update " + status + " emailbatch");
        }
        if (title != null && !title.isBlank()) {
            this.title = title.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        this.updatedAt = now;
        this.version += 1;
        domainEvents.add(new EmailbatchUpdated(id, now));
    }

    private void transitionTo(EmailbatchStatus target, Instant now) {
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

    public EmailbatchId id() { return id; }
    public String referenceCode() { return referenceCode; }
    public String title() { return title; }
    public String description() { return description; }
    public String tenantId() { return tenantId; }
    public EmailbatchStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
