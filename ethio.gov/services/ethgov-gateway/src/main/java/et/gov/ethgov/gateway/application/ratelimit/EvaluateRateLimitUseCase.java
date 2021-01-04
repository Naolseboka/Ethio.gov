package et.gov.ethgov.gateway.application.ratelimit;

import et.gov.ethgov.gateway.domain.audit.AuditEntry;
import et.gov.ethgov.gateway.domain.audit.AuditEntryRepository;
import et.gov.ethgov.gateway.domain.audit.AuditOutcome;
import et.gov.ethgov.gateway.application.GatewayEventPublisher;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitCounterStore;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicy;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyRepository;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitSubjectType;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvaluateRateLimitUseCase {
    private final RateLimitPolicyRepository policies;
    private final RateLimitCounterStore counters;
    private final AuditEntryRepository audits;
    private final GatewayEventPublisher eventPublisher;
    private final Clock clock;

    public EvaluateRateLimitUseCase(
            RateLimitPolicyRepository policies,
            RateLimitCounterStore counters,
            AuditEntryRepository audits,
            GatewayEventPublisher eventPublisher,
            Clock clock) {
        this.policies = policies;
        this.counters = counters;
        this.audits = audits;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public Result execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        RateLimitSubjectType subjectType =
                RateLimitSubjectType.valueOf(command.subjectType().trim().toUpperCase());
        String subjectKey = command.subjectKey().trim();
        if (subjectType == RateLimitSubjectType.ROUTE) {
            subjectKey = subjectKey.toLowerCase();
        }
        RateLimitPolicy policy = policies
                .findActiveByTenantAndSubject(tenantId, subjectType, subjectKey)
                .orElseThrow(() -> new DomainException(
                        "RATE_LIMIT_NOT_FOUND",
                        "Rate Limit Not Found",
                        "No ACTIVE rate-limit policy for subject",
                        404));
        Instant now = clock.instant();
        Instant windowStart = policy.windowStart(now);
        long hits = counters.incrementAndGet(policy.id(), windowStart);
        boolean allowed = hits <= policy.limitPerWindow();
        if (!allowed) {
            AuditEntry denied = AuditEntry.append(
                    tenantId,
                    command.actorId() == null || command.actorId().isBlank() ? "gateway" : command.actorId(),
                    "rate_limit.evaluate",
                    "rate_limit_policy",
                    policy.id().value(),
                    AuditOutcome.DENIED,
                    "{\"hits\":%d,\"limit\":%d}".formatted(hits, policy.limitPerWindow()),
                    now);
            audits.append(denied);
            eventPublisher.publishAuditEvents(denied.pullDomainEvents());
        }
        return new Result(
                policy.id().value(), allowed, hits, policy.limitPerWindow(), policy.windowSeconds(), windowStart);
    }

    public record Command(String tenantId, String subjectType, String subjectKey, String actorId) {}

    public record Result(
            String rateLimitPolicyId,
            boolean allowed,
            long hits,
            int limitPerWindow,
            int windowSeconds,
            Instant windowStart) {}
}
