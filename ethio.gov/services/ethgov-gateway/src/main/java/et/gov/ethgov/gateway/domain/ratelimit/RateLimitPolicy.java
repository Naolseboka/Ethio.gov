package et.gov.ethgov.gateway.domain.ratelimit;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class RateLimitPolicy {
    private final RateLimitPolicyId id;
    private final TenantId tenantId;
    private final RateLimitPolicyNumber policyNumber;
    private String name;
    private RateLimitSubjectType subjectType;
    private String subjectKey;
    private int limitPerWindow;
    private int windowSeconds;
    private RateLimitPolicyStatus status;
    private Instant createdAt;
    private Instant activatedAt;
    private Instant updatedAt;
    private long version;
    private final List<RateLimitPolicyDomainEvent> domainEvents = new ArrayList<>();

    private RateLimitPolicy(
            RateLimitPolicyId id,
            TenantId tenantId,
            RateLimitPolicyNumber policyNumber,
            String name,
            RateLimitSubjectType subjectType,
            String subjectKey,
            int limitPerWindow,
            int windowSeconds,
            RateLimitPolicyStatus status,
            Instant createdAt,
            Instant activatedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.policyNumber = policyNumber;
        this.name = name;
        this.subjectType = subjectType;
        this.subjectKey = subjectKey;
        this.limitPerWindow = limitPerWindow;
        this.windowSeconds = windowSeconds;
        this.status = status;
        this.createdAt = createdAt;
        this.activatedAt = activatedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static RateLimitPolicy create(
            TenantId tenantId,
            RateLimitPolicyNumber policyNumber,
            String name,
            RateLimitSubjectType subjectType,
            String subjectKey,
            int limitPerWindow,
            int windowSeconds,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(policyNumber, "policyNumber");
        Objects.requireNonNull(subjectType, "subjectType");
        Objects.requireNonNull(now, "now");
        RateLimitPolicy policy = new RateLimitPolicy(
                RateLimitPolicyId.generate(),
                tenantId,
                policyNumber,
                requireName(name),
                subjectType,
                requireSubjectKey(subjectType, subjectKey),
                requireLimit(limitPerWindow),
                requireWindow(windowSeconds),
                RateLimitPolicyStatus.DRAFT,
                now,
                null,
                now,
                0L);
        policy.record(new RateLimitPolicyCreated(
                policy.id, tenantId, policyNumber, subjectType, policy.subjectKey, now));
        return policy;
    }

    public static RateLimitPolicy rehydrate(
            RateLimitPolicyId id,
            TenantId tenantId,
            RateLimitPolicyNumber policyNumber,
            String name,
            RateLimitSubjectType subjectType,
            String subjectKey,
            int limitPerWindow,
            int windowSeconds,
            RateLimitPolicyStatus status,
            Instant createdAt,
            Instant activatedAt,
            Instant updatedAt,
            long version) {
        return new RateLimitPolicy(
                id,
                tenantId,
                policyNumber,
                name,
                subjectType,
                subjectKey,
                limitPerWindow,
                windowSeconds,
                status,
                createdAt,
                activatedAt,
                updatedAt,
                version);
    }

    public void update(String name, int limitPerWindow, int windowSeconds, Instant now) {
        if (status != RateLimitPolicyStatus.DRAFT && status != RateLimitPolicyStatus.ACTIVE) {
            throw new DomainException(
                    "POLICY_NOT_EDITABLE",
                    "Policy Not Editable",
                    "Only DRAFT or ACTIVE rate-limit policies can be updated",
                    409);
        }
        Objects.requireNonNull(now, "now");
        this.name = requireName(name);
        this.limitPerWindow = requireLimit(limitPerWindow);
        this.windowSeconds = requireWindow(windowSeconds);
        this.updatedAt = now;
        record(new RateLimitPolicyUpdated(id, now));
    }

    public void activate(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != RateLimitPolicyStatus.DRAFT && status != RateLimitPolicyStatus.SUSPENDED) {
            throw invalidState("activate");
        }
        this.status = RateLimitPolicyStatus.ACTIVE;
        if (this.activatedAt == null) {
            this.activatedAt = now;
        }
        this.updatedAt = now;
        record(new RateLimitPolicyActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != RateLimitPolicyStatus.ACTIVE) {
            throw invalidState("suspend");
        }
        this.status = RateLimitPolicyStatus.SUSPENDED;
        this.updatedAt = now;
        record(new RateLimitPolicySuspended(id, reason.trim(), now));
    }

    public void retire(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != RateLimitPolicyStatus.ACTIVE && status != RateLimitPolicyStatus.SUSPENDED) {
            throw invalidState("retire");
        }
        this.status = RateLimitPolicyStatus.RETIRED;
        this.updatedAt = now;
        record(new RateLimitPolicyRetired(id, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == RateLimitPolicyStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != RateLimitPolicyStatus.DRAFT) {
            throw invalidState("cancel");
        }
        this.status = RateLimitPolicyStatus.CANCELLED;
        this.updatedAt = now;
        record(new RateLimitPolicyCancelled(id, reason.trim(), now));
    }

    public Instant windowStart(Instant now) {
        long epoch = now.getEpochSecond();
        long aligned = epoch - (epoch % windowSeconds);
        return Instant.ofEpochSecond(aligned);
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_RATE_LIMIT_STATE",
                "Invalid Rate Limit State",
                "Cannot " + action + " rate-limit policy in status " + status,
                409);
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        String n = name.trim();
        if (n.length() > 200) {
            throw new IllegalArgumentException("name length must be <= 200");
        }
        return n;
    }

    private static String requireSubjectKey(RateLimitSubjectType type, String subjectKey) {
        if (subjectKey == null || subjectKey.isBlank()) {
            throw new IllegalArgumentException("subjectKey is required");
        }
        String key = subjectKey.trim();
        if (key.length() > 256) {
            throw new IllegalArgumentException("subjectKey length must be <= 256");
        }
        return switch (type) {
            case APP -> {
                if (!key.startsWith("dap_") || key.length() < 40) {
                    throw new IllegalArgumentException("APP subjectKey must be a dap_ identifier");
                }
                yield key;
            }
            case CREDENTIAL -> {
                if (!key.startsWith("ack_") || key.length() < 40) {
                    throw new IllegalArgumentException("CREDENTIAL subjectKey must be an ack_ identifier");
                }
                yield key;
            }
            case TENANT -> {
                if (!key.startsWith("tnt_") || key.length() < 40) {
                    throw new IllegalArgumentException("TENANT subjectKey must be a tnt_ identifier");
                }
                yield key;
            }
            case ROUTE -> {
                if (!key.startsWith("/")) {
                    throw new IllegalArgumentException("ROUTE subjectKey must start with /");
                }
                yield key.toLowerCase(Locale.ROOT);
            }
        };
    }

    private static int requireLimit(int limitPerWindow) {
        if (limitPerWindow < 1 || limitPerWindow > 1_000_000) {
            throw new IllegalArgumentException("limitPerWindow must be between 1 and 1000000");
        }
        return limitPerWindow;
    }

    private static int requireWindow(int windowSeconds) {
        if (windowSeconds < 1 || windowSeconds > 86_400) {
            throw new IllegalArgumentException("windowSeconds must be between 1 and 86400");
        }
        return windowSeconds;
    }

    private static void requireReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException("reason length must be between 5 and 500");
        }
    }

    private void record(RateLimitPolicyDomainEvent event) {
        domainEvents.add(event);
    }

    public List<RateLimitPolicyDomainEvent> pullDomainEvents() {
        List<RateLimitPolicyDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public RateLimitPolicyId id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public RateLimitPolicyNumber policyNumber() { return policyNumber; }
    public String name() { return name; }
    public RateLimitSubjectType subjectType() { return subjectType; }
    public String subjectKey() { return subjectKey; }
    public int limitPerWindow() { return limitPerWindow; }
    public int windowSeconds() { return windowSeconds; }
    public RateLimitPolicyStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Optional<Instant> activatedAt() { return Optional.ofNullable(activatedAt); }
    public Instant updatedAt() { return updatedAt; }
    public long entityVersion() { return version; }
}
