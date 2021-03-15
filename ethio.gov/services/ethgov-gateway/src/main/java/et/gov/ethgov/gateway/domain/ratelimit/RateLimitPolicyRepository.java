package et.gov.ethgov.gateway.domain.ratelimit;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface RateLimitPolicyRepository {
    boolean existsByTenantAndSubject(TenantId tenantId, RateLimitSubjectType subjectType, String subjectKey);

    Optional<RateLimitPolicy> findById(RateLimitPolicyId id);

    Optional<RateLimitPolicy> findByTenantAndNumber(TenantId tenantId, RateLimitPolicyNumber number);

    Optional<RateLimitPolicy> findActiveByTenantAndSubject(
            TenantId tenantId, RateLimitSubjectType subjectType, String subjectKey);

    void save(RateLimitPolicy policy);
}
