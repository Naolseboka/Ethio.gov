package et.gov.ethgov.reporting.domain.reportexport;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Reportexport aggregate — bounded context root for reportexport in the reporting service.
 */
public final class Reportexport {
    private final ReportexportId id;
    private String referenceCode;
    private String title;
    private String description;
    private String tenantId;
    private ReportexportStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private Reportexport(
            ReportexportId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            ReportexportStatus status,
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

    public static Reportexport create(
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
        Reportexport aggregate = new Reportexport(
                ReportexportId.newId(),
                referenceCode.trim(),
                title.trim(),
                Optional.ofNullable(description).orElse("").trim(),
                tenantId.trim(),
                ReportexportStatus.DRAFT,
                now,
                now,
                0L);
        aggregate.domainEvents.add(new ReportexportCreated(aggregate.id, now));
        return aggregate;
    }

    public static Reportexport rehydrate(
            ReportexportId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            ReportexportStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Reportexport(
                id, referenceCode, title, description, tenantId, status, createdAt, updatedAt, version);
    }

    public void activate(Instant now) {
        transitionTo(ReportexportStatus.ACTIVE, now);
        domainEvents.add(new ReportexportActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (reason.isBlank()) {
            throw new IllegalArgumentException("suspend reason required");
        }
        transitionTo(ReportexportStatus.SUSPENDED, now);
        domainEvents.add(new ReportexportSuspended(id, now));
        this.description = appendNote(this.description, "Suspended: " + reason.trim());
    }

    public void close(Instant now) {
        transitionTo(ReportexportStatus.CLOSED, now);
        domainEvents.add(new ReportexportClosed(id, now));
    }

    public void updateDetails(String title, String description, Instant now) {
        Objects.requireNonNull(now, "now");
        if (status == ReportexportStatus.ARCHIVED || status == ReportexportStatus.CLOSED) {
            throw new IllegalStateException("cannot update " + status + " reportexport");
        }
        if (title != null && !title.isBlank()) {
            this.title = title.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        this.updatedAt = now;
        this.version += 1;
        domainEvents.add(new ReportexportUpdated(id, now));
    }

    private void transitionTo(ReportexportStatus target, Instant now) {
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

    public ReportexportId id() { return id; }
    public String referenceCode() { return referenceCode; }
    public String title() { return title; }
    public String description() { return description; }
    public String tenantId() { return tenantId; }
    public ReportexportStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
