package et.gov.ethgov.organization.domain.budgetcode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Budgetcode aggregate — bounded context root for budgetcode in the organization service.
 */
public final class Budgetcode {
    private final BudgetcodeId id;
    private String referenceCode;
    private String title;
    private String description;
    private String tenantId;
    private BudgetcodeStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private Budgetcode(
            BudgetcodeId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            BudgetcodeStatus status,
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

    public static Budgetcode create(
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
        Budgetcode aggregate = new Budgetcode(
                BudgetcodeId.newId(),
                referenceCode.trim(),
                title.trim(),
                Optional.ofNullable(description).orElse("").trim(),
                tenantId.trim(),
                BudgetcodeStatus.DRAFT,
                now,
                now,
                0L);
        aggregate.domainEvents.add(new BudgetcodeCreated(aggregate.id, now));
        return aggregate;
    }

    public static Budgetcode rehydrate(
            BudgetcodeId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            BudgetcodeStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Budgetcode(
                id, referenceCode, title, description, tenantId, status, createdAt, updatedAt, version);
    }

    public void activate(Instant now) {
        transitionTo(BudgetcodeStatus.ACTIVE, now);
        domainEvents.add(new BudgetcodeActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (reason.isBlank()) {
            throw new IllegalArgumentException("suspend reason required");
        }
        transitionTo(BudgetcodeStatus.SUSPENDED, now);
        domainEvents.add(new BudgetcodeSuspended(id, now));
        this.description = appendNote(this.description, "Suspended: " + reason.trim());
    }

    public void close(Instant now) {
        transitionTo(BudgetcodeStatus.CLOSED, now);
        domainEvents.add(new BudgetcodeClosed(id, now));
    }

    public void updateDetails(String title, String description, Instant now) {
        Objects.requireNonNull(now, "now");
        if (status == BudgetcodeStatus.ARCHIVED || status == BudgetcodeStatus.CLOSED) {
            throw new IllegalStateException("cannot update " + status + " budgetcode");
        }
        if (title != null && !title.isBlank()) {
            this.title = title.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        this.updatedAt = now;
        this.version += 1;
        domainEvents.add(new BudgetcodeUpdated(id, now));
    }

    private void transitionTo(BudgetcodeStatus target, Instant now) {
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

    public BudgetcodeId id() { return id; }
    public String referenceCode() { return referenceCode; }
    public String title() { return title; }
    public String description() { return description; }
    public String tenantId() { return tenantId; }
    public BudgetcodeStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
