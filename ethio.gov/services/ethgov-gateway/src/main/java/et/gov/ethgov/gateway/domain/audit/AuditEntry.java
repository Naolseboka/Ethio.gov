package et.gov.ethgov.gateway.domain.audit;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class AuditEntry {
    private final AuditEntryId id;
    private final TenantId tenantId;
    private final String actorId;
    private final String action;
    private final String resourceType;
    private final String resourceId;
    private final AuditOutcome outcome;
    private final String detailJson;
    private final Instant occurredAt;
    private final List<AuditEntryDomainEvent> domainEvents = new ArrayList<>();

    private AuditEntry(
            AuditEntryId id,
            TenantId tenantId,
            String actorId,
            String action,
            String resourceType,
            String resourceId,
            AuditOutcome outcome,
            String detailJson,
            Instant occurredAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.actorId = actorId;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.outcome = outcome;
        this.detailJson = detailJson;
        this.occurredAt = occurredAt;
    }

    public static AuditEntry append(
            TenantId tenantId,
            String actorId,
            String action,
            String resourceType,
            String resourceId,
            AuditOutcome outcome,
            String detailJson,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(now, "now");
        AuditEntry entry = new AuditEntry(
                AuditEntryId.generate(),
                tenantId,
                requireToken(actorId, "actorId", 128),
                requireToken(action, "action", 128).toLowerCase(Locale.ROOT),
                requireToken(resourceType, "resourceType", 128).toLowerCase(Locale.ROOT),
                requireToken(resourceId, "resourceId", 128),
                outcome,
                blankToNull(detailJson, 4000),
                now);
        entry.domainEvents.add(new AuditEntryAppended(
                entry.id, tenantId, entry.action, entry.resourceType, entry.resourceId, outcome, now));
        return entry;
    }

    public static AuditEntry rehydrate(
            AuditEntryId id,
            TenantId tenantId,
            String actorId,
            String action,
            String resourceType,
            String resourceId,
            AuditOutcome outcome,
            String detailJson,
            Instant occurredAt) {
        return new AuditEntry(id, tenantId, actorId, action, resourceType, resourceId, outcome, detailJson, occurredAt);
    }

    private static String requireToken(String value, String field, int max) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        String n = value.trim();
        if (n.length() > max) throw new IllegalArgumentException(field + " length must be <= " + max);
        return n;
    }

    private static String blankToNull(String value, int max) {
        if (value == null || value.isBlank()) return null;
        String n = value.trim();
        if (n.length() > max) throw new IllegalArgumentException("detailJson length must be <= " + max);
        return n;
    }

    public List<AuditEntryDomainEvent> pullDomainEvents() {
        List<AuditEntryDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public AuditEntryId id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public String actorId() { return actorId; }
    public String action() { return action; }
    public String resourceType() { return resourceType; }
    public String resourceId() { return resourceId; }
    public AuditOutcome outcome() { return outcome; }
    public Optional<String> detailJson() { return Optional.ofNullable(detailJson); }
    public Instant occurredAt() { return occurredAt; }
}
