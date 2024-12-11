package et.gov.ethgov.reporting.domain.workbook;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Workbook aggregate — bounded context root for workbook in the reporting service.
 */
public final class Workbook {
    private final WorkbookId id;
    private String referenceCode;
    private String title;
    private String description;
    private String tenantId;
    private WorkbookStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private Workbook(
            WorkbookId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            WorkbookStatus status,
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

    public static Workbook create(
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
        Workbook aggregate = new Workbook(
                WorkbookId.newId(),
                referenceCode.trim(),
                title.trim(),
                Optional.ofNullable(description).orElse("").trim(),
                tenantId.trim(),
                WorkbookStatus.DRAFT,
                now,
                now,
                0L);
        aggregate.domainEvents.add(new WorkbookCreated(aggregate.id, now));
        return aggregate;
    }

    public static Workbook rehydrate(
            WorkbookId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            WorkbookStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Workbook(
                id, referenceCode, title, description, tenantId, status, createdAt, updatedAt, version);
    }

    public void activate(Instant now) {
        transitionTo(WorkbookStatus.ACTIVE, now);
        domainEvents.add(new WorkbookActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (reason.isBlank()) {
            throw new IllegalArgumentException("suspend reason required");
        }
        transitionTo(WorkbookStatus.SUSPENDED, now);
        domainEvents.add(new WorkbookSuspended(id, now));
        this.description = appendNote(this.description, "Suspended: " + reason.trim());
    }

    public void close(Instant now) {
        transitionTo(WorkbookStatus.CLOSED, now);
        domainEvents.add(new WorkbookClosed(id, now));
    }

    public void updateDetails(String title, String description, Instant now) {
        Objects.requireNonNull(now, "now");
        if (status == WorkbookStatus.ARCHIVED || status == WorkbookStatus.CLOSED) {
            throw new IllegalStateException("cannot update " + status + " workbook");
        }
        if (title != null && !title.isBlank()) {
            this.title = title.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        this.updatedAt = now;
        this.version += 1;
        domainEvents.add(new WorkbookUpdated(id, now));
    }

    private void transitionTo(WorkbookStatus target, Instant now) {
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

    public WorkbookId id() { return id; }
    public String referenceCode() { return referenceCode; }
    public String title() { return title; }
    public String description() { return description; }
    public String tenantId() { return tenantId; }
    public WorkbookStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
