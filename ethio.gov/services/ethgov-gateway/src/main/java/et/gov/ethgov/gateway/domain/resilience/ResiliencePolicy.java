package et.gov.ethgov.gateway.domain.resilience;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ResiliencePolicy {
    private static final Pattern DEP = Pattern.compile("^[a-z0-9][a-z0-9._-]{1,126}$");

    private final ResiliencePolicyId id;
    private final TenantId tenantId;
    private final ResiliencePolicyNumber policyNumber;
    private final String dependencyName;
    private int failureRateThreshold;
    private int waitDurationSeconds;
    private int permittedCallsInHalfOpen;
    private ResiliencePolicyStatus status;
    private Instant createdAt;
    private Instant activatedAt;
    private Instant updatedAt;
    private long version;
    private final List<ResiliencePolicyDomainEvent> domainEvents = new ArrayList<>();

    private ResiliencePolicy(
            ResiliencePolicyId id,
            TenantId tenantId,
            ResiliencePolicyNumber policyNumber,
            String dependencyName,
            int failureRateThreshold,
            int waitDurationSeconds,
            int permittedCallsInHalfOpen,
            ResiliencePolicyStatus status,
            Instant createdAt,
            Instant activatedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.policyNumber = policyNumber;
        this.dependencyName = dependencyName;
        this.failureRateThreshold = failureRateThreshold;
        this.waitDurationSeconds = waitDurationSeconds;
        this.permittedCallsInHalfOpen = permittedCallsInHalfOpen;
        this.status = status;
        this.createdAt = createdAt;
        this.activatedAt = activatedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static ResiliencePolicy create(
            TenantId tenantId,
            ResiliencePolicyNumber policyNumber,
            String dependencyName,
            int failureRateThreshold,
            int waitDurationSeconds,
            int permittedCallsInHalfOpen,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(policyNumber, "policyNumber");
        Objects.requireNonNull(now, "now");
        String dep = requireDependency(dependencyName);
        ResiliencePolicy policy = new ResiliencePolicy(
                ResiliencePolicyId.generate(),
                tenantId,
                policyNumber,
                dep,
                requireThreshold(failureRateThreshold),
                requireWait(waitDurationSeconds),
                requireHalfOpen(permittedCallsInHalfOpen),
                ResiliencePolicyStatus.DRAFT,
                now,
                null,
                now,
                0L);
        policy.record(new ResiliencePolicyCreated(policy.id, tenantId, policyNumber, dep, now));
        return policy;
    }

    public static ResiliencePolicy rehydrate(
            ResiliencePolicyId id,
            TenantId tenantId,
            ResiliencePolicyNumber policyNumber,
            String dependencyName,
            int failureRateThreshold,
            int waitDurationSeconds,
            int permittedCallsInHalfOpen,
            ResiliencePolicyStatus status,
            Instant createdAt,
            Instant activatedAt,
            Instant updatedAt,
            long version) {
        return new ResiliencePolicy(
                id, tenantId, policyNumber, dependencyName, failureRateThreshold,
                waitDurationSeconds, permittedCallsInHalfOpen, status, createdAt, activatedAt, updatedAt, version);
    }

    public void update(int failureRateThreshold, int waitDurationSeconds, int permittedCallsInHalfOpen, Instant now) {
        if (status != ResiliencePolicyStatus.DRAFT && status != ResiliencePolicyStatus.ACTIVE) {
            throw new DomainException(
                    "POLICY_NOT_EDITABLE",
                    "Policy Not Editable",
                    "Only DRAFT or ACTIVE resilience policies can be updated",
                    409);
        }
        Objects.requireNonNull(now, "now");
        this.failureRateThreshold = requireThreshold(failureRateThreshold);
        this.waitDurationSeconds = requireWait(waitDurationSeconds);
        this.permittedCallsInHalfOpen = requireHalfOpen(permittedCallsInHalfOpen);
        this.updatedAt = now;
        record(new ResiliencePolicyUpdated(id, now));
    }

    public void activate(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != ResiliencePolicyStatus.DRAFT && status != ResiliencePolicyStatus.SUSPENDED) {
            throw invalidState("activate");
        }
        this.status = ResiliencePolicyStatus.ACTIVE;
        if (this.activatedAt == null) this.activatedAt = now;
        this.updatedAt = now;
        record(new ResiliencePolicyActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != ResiliencePolicyStatus.ACTIVE) throw invalidState("suspend");
        this.status = ResiliencePolicyStatus.SUSPENDED;
        this.updatedAt = now;
        record(new ResiliencePolicySuspended(id, reason.trim(), now));
    }

    public void retire(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != ResiliencePolicyStatus.ACTIVE && status != ResiliencePolicyStatus.SUSPENDED) {
            throw invalidState("retire");
        }
        this.status = ResiliencePolicyStatus.RETIRED;
        this.updatedAt = now;
        record(new ResiliencePolicyRetired(id, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == ResiliencePolicyStatus.CANCELLED) return;
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != ResiliencePolicyStatus.DRAFT) throw invalidState("cancel");
        this.status = ResiliencePolicyStatus.CANCELLED;
        this.updatedAt = now;
        record(new ResiliencePolicyCancelled(id, reason.trim(), now));
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_RESILIENCE_STATE",
                "Invalid Resilience State",
                "Cannot " + action + " resilience policy in status " + status,
                409);
    }

    private static String requireDependency(String dependencyName) {
        if (dependencyName == null || dependencyName.isBlank()) {
            throw new IllegalArgumentException("dependencyName is required");
        }
        String n = dependencyName.trim().toLowerCase(Locale.ROOT);
        if (!DEP.matcher(n).matches()) {
            throw new IllegalArgumentException("dependencyName is invalid");
        }
        return n;
    }

    private static int requireThreshold(int v) {
        if (v < 1 || v > 100) throw new IllegalArgumentException("failureRateThreshold must be 1..100");
        return v;
    }

    private static int requireWait(int v) {
        if (v < 1 || v > 3600) throw new IllegalArgumentException("waitDurationSeconds must be 1..3600");
        return v;
    }

    private static int requireHalfOpen(int v) {
        if (v < 1 || v > 100) throw new IllegalArgumentException("permittedCallsInHalfOpen must be 1..100");
        return v;
    }

    private static void requireReason(String reason) {
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("reason is required");
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException("reason length must be between 5 and 500");
        }
    }

    private void record(ResiliencePolicyDomainEvent event) { domainEvents.add(event); }

    public List<ResiliencePolicyDomainEvent> pullDomainEvents() {
        List<ResiliencePolicyDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public ResiliencePolicyId id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public ResiliencePolicyNumber policyNumber() { return policyNumber; }
    public String dependencyName() { return dependencyName; }
    public int failureRateThreshold() { return failureRateThreshold; }
    public int waitDurationSeconds() { return waitDurationSeconds; }
    public int permittedCallsInHalfOpen() { return permittedCallsInHalfOpen; }
    public ResiliencePolicyStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Optional<Instant> activatedAt() { return Optional.ofNullable(activatedAt); }
    public Instant updatedAt() { return updatedAt; }
    public long entityVersion() { return version; }
}
