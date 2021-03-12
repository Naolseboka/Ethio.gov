package et.gov.ethgov.gateway.domain.ratelimit;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record RateLimitPolicyCreated(
        RateLimitPolicyId rateLimitPolicyId,
        TenantId tenantId,
        RateLimitPolicyNumber policyNumber,
        RateLimitSubjectType subjectType,
        String subjectKey,
        Instant occurredAt) implements RateLimitPolicyDomainEvent {}
