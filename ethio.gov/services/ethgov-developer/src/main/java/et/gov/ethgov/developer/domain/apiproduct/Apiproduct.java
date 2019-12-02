package et.gov.ethgov.developer.domain.apiproduct;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Apiproduct aggregate — bounded context root for apiproduct in the developer service.
 */
public final class Apiproduct {
    private final ApiproductId id;
    private String referenceCode;
    private String title;
    private String description;
    private String tenantId;
    private ApiproductStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private Apiproduct(
            ApiproductId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            ApiproductStatus status,
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

    public static Apiproduct create(
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
        Apiproduct aggregate = new Apiproduct(
                ApiproductId.newId(),
                referenceCode.trim(),
                title.trim(),
                Optional.ofNullable(description).orElse("").trim(),
                tenantId.trim(),
                ApiproductStatus.DRAFT,
                now,
                now,
                0L);
        aggregate.domainEvents.add(new ApiproductCreated(aggregate.id, now));
        return aggregate;
    }

    public static Apiproduct rehydrate(
            ApiproductId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            ApiproductStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Apiproduct(
                id, referenceCode, title, description, tenantId, status, createdAt, updatedAt, version);
    }

    public void activate(Instant now) {
        transitionTo(ApiproductStatus.ACTIVE, now);
        domainEvents.add(new ApiproductActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (reason.isBlank()) {
            throw new IllegalArgumentException("suspend reason required");
        }
        transitionTo(ApiproductStatus.SUSPENDED, now);
        domainEvents.add(new ApiproductSuspended(id, now));
        this.description = appendNote(this.description, "Suspended: " + reason.trim());
    }

    public void close(Instant now) {
        transitionTo(ApiproductStatus.CLOSED, now);
        domainEvents.add(new ApiproductClosed(id, now));
    }

    public void updateDetails(String title, String description, Instant now) {
        Objects.requireNonNull(now, "now");
        if (status == ApiproductStatus.ARCHIVED || status == ApiproductStatus.CLOSED) {
            throw new IllegalStateException("cannot update " + status + " apiproduct");
        }
        if (title != null && !title.isBlank()) {
            this.title = title.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        this.updatedAt = now;
        this.version += 1;
        domainEvents.add(new ApiproductUpdated(id, now));
    }

    private void transitionTo(ApiproductStatus target, Instant now) {
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

    public ApiproductId id() { return id; }
    public String referenceCode() { return referenceCode; }
    public String title() { return title; }
    public String description() { return description; }
    public String tenantId() { return tenantId; }
    public ApiproductStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
