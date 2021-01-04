package et.gov.ethgov.gateway.application.ratelimit;

import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicy;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyId;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyNumber;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetRateLimitPolicyUseCase {
    private final RateLimitPolicyRepository policies;

    public GetRateLimitPolicyUseCase(RateLimitPolicyRepository policies) {
        this.policies = policies;
    }

    @Transactional(readOnly = true)
    public RateLimitPolicy byId(String rateLimitPolicyId) {
        return policies
                .findById(RateLimitPolicyId.of(rateLimitPolicyId))
                .orElseThrow(() -> new DomainException(
                        "RATE_LIMIT_NOT_FOUND", "Rate Limit Not Found", "Unknown policy " + rateLimitPolicyId, 404));
    }

    @Transactional(readOnly = true)
    public RateLimitPolicy byNumber(String tenantId, String policyNumber) {
        return policies
                .findByTenantAndNumber(TenantId.of(tenantId), RateLimitPolicyNumber.of(policyNumber))
                .orElseThrow(() -> new DomainException(
                        "RATE_LIMIT_NOT_FOUND", "Rate Limit Not Found", "Unknown policy number " + policyNumber, 404));
    }
}
