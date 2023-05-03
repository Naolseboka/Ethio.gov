package et.gov.ethgov.opendata.domain.dataupdate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Dataupdate aggregate — bounded context root for dataupdate in the opendata service.
 */
public final class Dataupdate {
    private final DataupdateId id;
    private String referenceCode;
    private String title;
    private String description;
    private String tenantId;
    private DataupdateStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private Dataupdate(
            DataupdateId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            DataupdateStatus status,
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

    public static Dataupdate create(
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
        Dataupdate aggregate = new Dataupdate(
                DataupdateId.newId(),
                referenceCode.trim(),
                title.trim(),
                Optional.ofNullable(description).orElse("").trim(),
                tenantId.trim(),
                DataupdateStatus.DRAFT,
                now,
                now,
                0L);
        aggregate.domainEvents.add(new DataupdateCreated(aggregate.id, now));
        return aggregate;
    }

    public static Dataupdate rehydrate(
            DataupdateId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            DataupdateStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Dataupdate(
                id, referenceCode, title, description, tenantId, status, createdAt, updatedAt, version);
    }

    public void activate(Instant now) {
        transitionTo(DataupdateStatus.ACTIVE, now);
        domainEvents.add(new DataupdateActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (reason.isBlank()) {
            throw new IllegalArgumentException("suspend reason required");
        }
        transitionTo(DataupdateStatus.SUSPENDED, now);
        domainEvents.add(new DataupdateSuspended(id, now));
        this.description = appendNote(this.description, "Suspended: " + reason.trim());
    }

    public void close(Instant now) {
        transitionTo(DataupdateStatus.CLOSED, now);
        domainEvents.add(new DataupdateClosed(id, now));
    }

    public void updateDetails(String title, String description, Instant now) {
        Objects.requireNonNull(now, "now");
        if (status == DataupdateStatus.ARCHIVED || status == DataupdateStatus.CLOSED) {
            throw new IllegalStateException("cannot update " + status + " dataupdate");
        }
        if (title != null && !title.isBlank()) {
            this.title = title.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        this.updatedAt = now;
        this.version += 1;
        domainEvents.add(new DataupdateUpdated(id, now));
    }

    private void transitionTo(DataupdateStatus target, Instant now) {
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

    public DataupdateId id() { return id; }
    public String referenceCode() { return referenceCode; }
    public String title() { return title; }
    public String description() { return description; }
    public String tenantId() { return tenantId; }
    public DataupdateStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
