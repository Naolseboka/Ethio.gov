package et.gov.ethgov.search.domain.searchdocument;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Searchdocument aggregate — bounded context root for searchdocument in the search service.
 */
public final class Searchdocument {
    private final SearchdocumentId id;
    private String referenceCode;
    private String title;
    private String description;
    private String tenantId;
    private SearchdocumentStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private Searchdocument(
            SearchdocumentId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            SearchdocumentStatus status,
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

    public static Searchdocument create(
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
        Searchdocument aggregate = new Searchdocument(
                SearchdocumentId.newId(),
                referenceCode.trim(),
                title.trim(),
                Optional.ofNullable(description).orElse("").trim(),
                tenantId.trim(),
                SearchdocumentStatus.DRAFT,
                now,
                now,
                0L);
        aggregate.domainEvents.add(new SearchdocumentCreated(aggregate.id, now));
        return aggregate;
    }

    public static Searchdocument rehydrate(
            SearchdocumentId id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            SearchdocumentStatus status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Searchdocument(
                id, referenceCode, title, description, tenantId, status, createdAt, updatedAt, version);
    }

    public void activate(Instant now) {
        transitionTo(SearchdocumentStatus.ACTIVE, now);
        domainEvents.add(new SearchdocumentActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (reason.isBlank()) {
            throw new IllegalArgumentException("suspend reason required");
        }
        transitionTo(SearchdocumentStatus.SUSPENDED, now);
        domainEvents.add(new SearchdocumentSuspended(id, now));
        this.description = appendNote(this.description, "Suspended: " + reason.trim());
    }

    public void close(Instant now) {
        transitionTo(SearchdocumentStatus.CLOSED, now);
        domainEvents.add(new SearchdocumentClosed(id, now));
    }

    public void updateDetails(String title, String description, Instant now) {
        Objects.requireNonNull(now, "now");
        if (status == SearchdocumentStatus.ARCHIVED || status == SearchdocumentStatus.CLOSED) {
            throw new IllegalStateException("cannot update " + status + " searchdocument");
        }
        if (title != null && !title.isBlank()) {
            this.title = title.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        this.updatedAt = now;
        this.version += 1;
        domainEvents.add(new SearchdocumentUpdated(id, now));
    }

    private void transitionTo(SearchdocumentStatus target, Instant now) {
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

    public SearchdocumentId id() { return id; }
    public String referenceCode() { return referenceCode; }
    public String title() { return title; }
    public String description() { return description; }
    public String tenantId() { return tenantId; }
    public SearchdocumentStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
