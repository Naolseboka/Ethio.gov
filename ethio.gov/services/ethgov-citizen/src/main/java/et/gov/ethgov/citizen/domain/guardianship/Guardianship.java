package et.gov.ethgov.citizen.domain.guardianship;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Guardianship aggregate — bounded context root for guardianship in the citizen service.
 */
public final class Guardianship {
    private final GuardianshipId id;
    private String referenceCode;
    private String title;
    private String description;
    private String tenantId;
    private GuardianshipStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private Guardianship(
            GuardianshipId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            GuardianshipStatus status,
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

    public static Guardianship create(
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
        Guardianship aggregate = new Guardianship(
                GuardianshipId.newId(),
                referenceCode.trim(),
                title.trim(),
                Optional.ofNullable(description).orElse("").trim(),
                tenantId.trim(),
                GuardianshipStatus.DRAFT,
                now,
                now,
                0L);
        aggregate.domainEvents.add(new GuardianshipCreated(aggregate.id, now));
        return aggregate;
    }

    public static Guardianship rehydrate(
            GuardianshipId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            GuardianshipStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Guardianship(
                id, referenceCode, title, description, tenantId, status, createdAt, updatedAt, version);
    }

    public void activate(Instant now) {
        transitionTo(GuardianshipStatus.ACTIVE, now);
        domainEvents.add(new GuardianshipActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (reason.isBlank()) {
            throw new IllegalArgumentException("suspend reason required");
        }
        transitionTo(GuardianshipStatus.SUSPENDED, now);
        domainEvents.add(new GuardianshipSuspended(id, now));
        this.description = appendNote(this.description, "Suspended: " + reason.trim());
    }

    public void close(Instant now) {
        transitionTo(GuardianshipStatus.CLOSED, now);
        domainEvents.add(new GuardianshipClosed(id, now));
    }

    public void updateDetails(String title, String description, Instant now) {
        Objects.requireNonNull(now, "now");
        if (status == GuardianshipStatus.ARCHIVED || status == GuardianshipStatus.CLOSED) {
            throw new IllegalStateException("cannot update " + status + " guardianship");
        }
        if (title != null && !title.isBlank()) {
            this.title = title.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        this.updatedAt = now;
        this.version += 1;
        domainEvents.add(new GuardianshipUpdated(id, now));
    }

    private void transitionTo(GuardianshipStatus target, Instant now) {
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

    public GuardianshipId id() { return id; }
    public String referenceCode() { return referenceCode; }
    public String title() { return title; }
    public String description() { return description; }
    public String tenantId() { return tenantId; }
    public GuardianshipStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
